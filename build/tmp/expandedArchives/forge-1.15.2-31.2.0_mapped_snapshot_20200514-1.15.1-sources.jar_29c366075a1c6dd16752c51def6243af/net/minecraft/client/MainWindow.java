package net.minecraft.client;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Optional;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.IWindowEventListener;
import net.minecraft.client.renderer.MonitorHandler;
import net.minecraft.client.renderer.ScreenSize;
import net.minecraft.client.renderer.VideoMode;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.UndeclaredException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

@OnlyIn(Dist.CLIENT)
public final class MainWindow implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final GLFWErrorCallback loggingErrorCallback = GLFWErrorCallback.create(this::logGlError);
   private final IWindowEventListener mc;
   private final MonitorHandler monitorHandler;
   private final long handle;
   private int prevWindowX;
   private int prevWindowY;
   private int prevWindowWidth;
   private int prevWindowHeight;
   private Optional<VideoMode> videoMode;
   private boolean fullscreen;
   private boolean lastFullscreen;
   private int windowX;
   private int windowY;
   private int width;
   private int height;
   private int framebufferWidth;
   private int framebufferHeight;
   private int scaledWidth;
   private int scaledHeight;
   private double guiScaleFactor;
   private String renderPhase = "";
   private boolean videoModeChanged;
   private int framerateLimit;
   private boolean vsync;

   public MainWindow(IWindowEventListener p_i51170_1_, MonitorHandler p_i51170_2_, ScreenSize p_i51170_3_, @Nullable String videoModeName, String titleIn) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      this.monitorHandler = p_i51170_2_;
      this.setThrowExceptionOnGlError();
      this.setRenderPhase("Pre startup");
      this.mc = p_i51170_1_;
      Optional<VideoMode> optional = VideoMode.parseFromSettings(videoModeName);
      if (optional.isPresent()) {
         this.videoMode = optional;
      } else if (p_i51170_3_.fullscreenWidth.isPresent() && p_i51170_3_.fullscreenHeight.isPresent()) {
         this.videoMode = Optional.of(new VideoMode(p_i51170_3_.fullscreenWidth.getAsInt(), p_i51170_3_.fullscreenHeight.getAsInt(), 8, 8, 8, 60));
      } else {
         this.videoMode = Optional.empty();
      }

      this.lastFullscreen = this.fullscreen = p_i51170_3_.fullscreen;
      Monitor monitor = p_i51170_2_.getMonitor(GLFW.glfwGetPrimaryMonitor());
      this.prevWindowWidth = this.width = p_i51170_3_.width > 0 ? p_i51170_3_.width : 1;
      this.prevWindowHeight = this.height = p_i51170_3_.height > 0 ? p_i51170_3_.height : 1;
      GLFW.glfwDefaultWindowHints();
      GLFW.glfwWindowHint(139265, 196609);
      GLFW.glfwWindowHint(139275, 221185);
      GLFW.glfwWindowHint(139266, 2);
      GLFW.glfwWindowHint(139267, 0);
      GLFW.glfwWindowHint(139272, 0);
      this.handle = net.minecraftforge.fml.loading.progress.EarlyProgressVisualization.INSTANCE.handOffWindow(()->this.width, ()->this.height, ()->titleIn, ()->this.fullscreen && monitor != null ? monitor.getMonitorPointer() : 0L);
      if (monitor != null) {
         VideoMode videomode = monitor.getVideoModeOrDefault(this.fullscreen ? this.videoMode : Optional.empty());
         this.prevWindowX = this.windowX = monitor.getVirtualPosX() + videomode.getWidth() / 2 - this.width / 2;
         this.prevWindowY = this.windowY = monitor.getVirtualPosY() + videomode.getHeight() / 2 - this.height / 2;
      } else {
         int[] aint1 = new int[1];
         int[] aint = new int[1];
         GLFW.glfwGetWindowPos(this.handle, aint1, aint);
         this.prevWindowX = this.windowX = aint1[0];
         this.prevWindowY = this.windowY = aint[0];
      }

      GLFW.glfwMakeContextCurrent(this.handle);
      GL.createCapabilities();
      this.updateVideoMode();
      this.updateFramebufferSize();
      GLFW.glfwSetFramebufferSizeCallback(this.handle, this::onFramebufferSizeUpdate);
      GLFW.glfwSetWindowPosCallback(this.handle, this::onWindowPosUpdate);
      GLFW.glfwSetWindowSizeCallback(this.handle, this::onWindowSizeUpdate);
      GLFW.glfwSetWindowFocusCallback(this.handle, this::onWindowFocusUpdate);
   }

   public int getRefreshRate() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GLX._getRefreshRate(this);
   }

   public boolean shouldClose() {
      return GLX._shouldClose(this);
   }

   public static void checkGlfwError(BiConsumer<Integer, String> p_211162_0_) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);

      try (MemoryStack memorystack = MemoryStack.stackPush()) {
         PointerBuffer pointerbuffer = memorystack.mallocPointer(1);
         int i = GLFW.glfwGetError(pointerbuffer);
         if (i != 0) {
            long j = pointerbuffer.get();
            String s = j == 0L ? "" : MemoryUtil.memUTF8(j);
            p_211162_0_.accept(i, s);
         }
      }

   }

   public void setWindowIcon(InputStream p_216529_1_, InputStream p_216529_2_) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);

      try (MemoryStack memorystack = MemoryStack.stackPush()) {
         if (p_216529_1_ == null) {
            throw new FileNotFoundException("icons/icon_16x16.png");
         }

         if (p_216529_2_ == null) {
            throw new FileNotFoundException("icons/icon_32x32.png");
         }

         IntBuffer intbuffer = memorystack.mallocInt(1);
         IntBuffer intbuffer1 = memorystack.mallocInt(1);
         IntBuffer intbuffer2 = memorystack.mallocInt(1);
         Buffer buffer = GLFWImage.mallocStack(2, memorystack);
         ByteBuffer bytebuffer = this.loadIcon(p_216529_1_, intbuffer, intbuffer1, intbuffer2);
         if (bytebuffer == null) {
            throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
         }

         buffer.position(0);
         buffer.width(intbuffer.get(0));
         buffer.height(intbuffer1.get(0));
         buffer.pixels(bytebuffer);
         ByteBuffer bytebuffer1 = this.loadIcon(p_216529_2_, intbuffer, intbuffer1, intbuffer2);
         if (bytebuffer1 == null) {
            throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
         }

         buffer.position(1);
         buffer.width(intbuffer.get(0));
         buffer.height(intbuffer1.get(0));
         buffer.pixels(bytebuffer1);
         buffer.position(0);
         GLFW.glfwSetWindowIcon(this.handle, buffer);
         STBImage.stbi_image_free(bytebuffer);
         STBImage.stbi_image_free(bytebuffer1);
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't set icon", (Throwable)ioexception);
      }

   }

   @Nullable
   private ByteBuffer loadIcon(InputStream p_198111_1_, IntBuffer p_198111_2_, IntBuffer p_198111_3_, IntBuffer p_198111_4_) throws IOException {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      ByteBuffer bytebuffer = null;

      ByteBuffer bytebuffer1;
      try {
         bytebuffer = TextureUtil.readToBuffer(p_198111_1_);
         ((java.nio.Buffer)bytebuffer).rewind();
         bytebuffer1 = STBImage.stbi_load_from_memory(bytebuffer, p_198111_2_, p_198111_3_, p_198111_4_, 0);
      } finally {
         if (bytebuffer != null) {
            MemoryUtil.memFree(bytebuffer);
         }

      }

      return bytebuffer1;
   }

   public void setRenderPhase(String renderPhaseIn) {
      this.renderPhase = renderPhaseIn;
   }

   private void setThrowExceptionOnGlError() {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      GLFW.glfwSetErrorCallback(MainWindow::throwExceptionForGlError);
   }

   private static void throwExceptionForGlError(int error, long description) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      String s = "GLFW error " + error + ": " + MemoryUtil.memUTF8(description);
      TinyFileDialogs.tinyfd_messageBox("Minecraft", s + ".\n\nPlease make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).", "ok", "error", false);
      throw new MainWindow.GlException(s);
   }

   public void logGlError(int error, long description) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      String s = MemoryUtil.memUTF8(description);
      LOGGER.error("########## GL ERROR ##########");
      LOGGER.error("@ {}", (Object)this.renderPhase);
      LOGGER.error("{}: {}", error, s);
   }

   public void setLogOnGlError() {
      GLFWErrorCallback glfwerrorcallback = GLFW.glfwSetErrorCallback(this.loggingErrorCallback);
      if (glfwerrorcallback != null) {
         glfwerrorcallback.free();
      }

   }

   public void setVsync(boolean vsyncEnabled) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      this.vsync = vsyncEnabled;
      GLFW.glfwSwapInterval(vsyncEnabled ? 1 : 0);
   }

   public void close() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      Callbacks.glfwFreeCallbacks(this.handle);
      this.loggingErrorCallback.close();
      GLFW.glfwDestroyWindow(this.handle);
      GLFW.glfwTerminate();
   }

   private void onWindowPosUpdate(long windowPointer, int windowXIn, int windowYIn) {
      this.windowX = windowXIn;
      this.windowY = windowYIn;
   }

   private void onFramebufferSizeUpdate(long windowPointer, int framebufferWidth, int framebufferHeight) {
      if (windowPointer == this.handle) {
         int i = this.getFramebufferWidth();
         int j = this.getFramebufferHeight();
         if (framebufferWidth != 0 && framebufferHeight != 0) {
            this.framebufferWidth = framebufferWidth;
            this.framebufferHeight = framebufferHeight;
            if (this.getFramebufferWidth() != i || this.getFramebufferHeight() != j) {
               this.mc.updateWindowSize();
            }

         }
      }
   }

   private void updateFramebufferSize() {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      int[] aint = new int[1];
      int[] aint1 = new int[1];
      GLFW.glfwGetFramebufferSize(this.handle, aint, aint1);
      this.framebufferWidth = aint[0];
      this.framebufferHeight = aint1[0];
   }

   private void onWindowSizeUpdate(long windowPointer, int windowWidthIn, int windowHeightIn) {
      this.width = windowWidthIn;
      this.height = windowHeightIn;
   }

   private void onWindowFocusUpdate(long windowPointer, boolean hasFocus) {
      if (windowPointer == this.handle) {
         this.mc.setGameFocused(hasFocus);
      }

   }

   public void setFramerateLimit(int p_216526_1_) {
      this.framerateLimit = p_216526_1_;
   }

   public int getLimitFramerate() {
      return this.framerateLimit;
   }

   public void flipFrame() {
      RenderSystem.flipFrame(this.handle);
      if (this.fullscreen != this.lastFullscreen) {
         this.lastFullscreen = this.fullscreen;
         this.toggleFullscreen(this.vsync);
      }

   }

   public Optional<VideoMode> getVideoMode() {
      return this.videoMode;
   }

   public void setVideoMode(Optional<VideoMode> fullscreenModeIn) {
      boolean flag = !fullscreenModeIn.equals(this.videoMode);
      this.videoMode = fullscreenModeIn;
      if (flag) {
         this.videoModeChanged = true;
      }

   }

   public void update() {
      if (this.fullscreen && this.videoModeChanged) {
         this.videoModeChanged = false;
         this.updateVideoMode();
         this.mc.updateWindowSize();
      }

   }

   private void updateVideoMode() {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      boolean flag = GLFW.glfwGetWindowMonitor(this.handle) != 0L;
      if (this.fullscreen) {
         Monitor monitor = this.monitorHandler.getMonitor(this);
         if (monitor == null) {
            LOGGER.warn("Failed to find suitable monitor for fullscreen mode");
            this.fullscreen = false;
         } else {
            VideoMode videomode = monitor.getVideoModeOrDefault(this.videoMode);
            if (!flag) {
               this.prevWindowX = this.windowX;
               this.prevWindowY = this.windowY;
               this.prevWindowWidth = this.width;
               this.prevWindowHeight = this.height;
            }

            this.windowX = 0;
            this.windowY = 0;
            this.width = videomode.getWidth();
            this.height = videomode.getHeight();
            GLFW.glfwSetWindowMonitor(this.handle, monitor.getMonitorPointer(), this.windowX, this.windowY, this.width, this.height, videomode.getRefreshRate());
         }
      } else {
         this.windowX = this.prevWindowX;
         this.windowY = this.prevWindowY;
         this.width = this.prevWindowWidth;
         this.height = this.prevWindowHeight;
         GLFW.glfwSetWindowMonitor(this.handle, 0L, this.windowX, this.windowY, this.width, this.height, -1);
      }

   }

   public void toggleFullscreen() {
      this.fullscreen = !this.fullscreen;
   }

   private void toggleFullscreen(boolean vsyncEnabled) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);

      try {
         this.updateVideoMode();
         this.mc.updateWindowSize();
         this.setVsync(vsyncEnabled);
         this.flipFrame();
      } catch (Exception exception) {
         LOGGER.error("Couldn't toggle fullscreen", (Throwable)exception);
      }

   }

   public int calcGuiScale(int guiScaleIn, boolean forceUnicode) {
      int i;
      for(i = 1; i != guiScaleIn && i < this.framebufferWidth && i < this.framebufferHeight && this.framebufferWidth / (i + 1) >= 320 && this.framebufferHeight / (i + 1) >= 240; ++i) {
         ;
      }

      if (forceUnicode && i % 2 != 0) {
         ++i;
      }

      return i;
   }

   public void setGuiScale(double scaleFactor) {
      this.guiScaleFactor = scaleFactor;
      int i = (int)((double)this.framebufferWidth / scaleFactor);
      this.scaledWidth = (double)this.framebufferWidth / scaleFactor > (double)i ? i + 1 : i;
      int j = (int)((double)this.framebufferHeight / scaleFactor);
      this.scaledHeight = (double)this.framebufferHeight / scaleFactor > (double)j ? j + 1 : j;
   }

   public void func_230148_b_(String p_230148_1_) {
      GLFW.glfwSetWindowTitle(this.handle, p_230148_1_);
   }

   /**
    * Gets a pointer to the native window object that is passed to GLFW.
    */
   public long getHandle() {
      return this.handle;
   }

   public boolean isFullscreen() {
      return this.fullscreen;
   }

   public int getFramebufferWidth() {
      return this.framebufferWidth;
   }

   public int getFramebufferHeight() {
      return this.framebufferHeight;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getScaledWidth() {
      return this.scaledWidth;
   }

   public int getScaledHeight() {
      return this.scaledHeight;
   }

   public int getWindowX() {
      return this.windowX;
   }

   public int getWindowY() {
      return this.windowY;
   }

   public double getGuiScaleFactor() {
      return this.guiScaleFactor;
   }

   @Nullable
   public Monitor getMonitor() {
      return this.monitorHandler.getMonitor(this);
   }

   public void setRawMouseInput(boolean valueIn) {
      InputMappings.setRawMouseInput(this.handle, valueIn);
   }

   @OnlyIn(Dist.CLIENT)
   public static class GlException extends UndeclaredException {
      private GlException(String messageIn) {
         super(messageIn);
      }
   }
}