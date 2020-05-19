package com.mojang.blaze3d.platform;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.minecraft.client.MainWindow;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlDebugTextUtils;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import oshi.SystemInfo;
import oshi.hardware.Processor;

@OnlyIn(Dist.CLIENT)
public class GLX {
   private static final Logger LOGGER = LogManager.getLogger();
   private static String capsString = "";
   private static String cpuInfo;
   private static final Map<Integer, String> LOOKUP_MAP = make(Maps.newHashMap(), (p_229878_0_) -> {
      p_229878_0_.put(0, "No error");
      p_229878_0_.put(1280, "Enum parameter is invalid for this function");
      p_229878_0_.put(1281, "Parameter is invalid for this function");
      p_229878_0_.put(1282, "Current state is invalid for this function");
      p_229878_0_.put(1283, "Stack overflow");
      p_229878_0_.put(1284, "Stack underflow");
      p_229878_0_.put(1285, "Out of memory");
      p_229878_0_.put(1286, "Operation on incomplete framebuffer");
      p_229878_0_.put(1286, "Operation on incomplete framebuffer");
   });

   public static String getOpenGLVersionString() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GLFW.glfwGetCurrentContext() == 0L ? "NO CONTEXT" : GlStateManager.getString(7937) + " GL version " + GlStateManager.getString(7938) + ", " + GlStateManager.getString(7936);
   }

   public static int _getRefreshRate(MainWindow p__getRefreshRate_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      long i = GLFW.glfwGetWindowMonitor(p__getRefreshRate_0_.getHandle());
      if (i == 0L) {
         i = GLFW.glfwGetPrimaryMonitor();
      }

      GLFWVidMode glfwvidmode = i == 0L ? null : GLFW.glfwGetVideoMode(i);
      return glfwvidmode == null ? 0 : glfwvidmode.refreshRate();
   }

   public static String _getLWJGLVersion() {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      return Version.getVersion();
   }

   public static LongSupplier _initGlfw() {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      MainWindow.checkGlfwError((p_229879_0_, p_229879_1_) -> {
         throw new IllegalStateException(String.format("GLFW error before init: [0x%X]%s", p_229879_0_, p_229879_1_));
      });
      List<String> list = Lists.newArrayList();
      GLFWErrorCallback glfwerrorcallback = GLFW.glfwSetErrorCallback((p_229880_1_, p_229880_2_) -> {
         list.add(String.format("GLFW error during init: [0x%X]%s", p_229880_1_, p_229880_2_));
      });
      if (!GLFW.glfwInit()) {
         throw new IllegalStateException("Failed to initialize GLFW, errors: " + Joiner.on(",").join(list));
      } else {
         LongSupplier longsupplier = () -> {
            return (long)(GLFW.glfwGetTime() * 1.0E9D);
         };

         for(String s : list) {
            LOGGER.error("GLFW error collected during initialization: {}", (Object)s);
         }

         RenderSystem.setErrorCallback(glfwerrorcallback);
         return longsupplier;
      }
   }

   public static void _setGlfwErrorCallback(GLFWErrorCallbackI p__setGlfwErrorCallback_0_) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      GLFWErrorCallback glfwerrorcallback = GLFW.glfwSetErrorCallback(p__setGlfwErrorCallback_0_);
      if (glfwerrorcallback != null) {
         glfwerrorcallback.free();
      }

   }

   public static boolean _shouldClose(MainWindow p__shouldClose_0_) {
      return GLFW.glfwWindowShouldClose(p__shouldClose_0_.getHandle());
   }

   public static void _setupNvFogDistance() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (GL.getCapabilities().GL_NV_fog_distance) {
         GlStateManager.fogi(34138, 34139);
      }

   }

   public static void _init(int p__init_0_, boolean p__init_1_) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      GLCapabilities glcapabilities = GL.getCapabilities();
      capsString = "Using framebuffer using " + GlStateManager.init(glcapabilities);

      try {
         Processor[] aprocessor = (new SystemInfo()).getHardware().getProcessors();
         cpuInfo = String.format("%dx %s", aprocessor.length, aprocessor[0]).replaceAll("\\s+", " ");
      } catch (Throwable var4) {
         ;
      }

      GlDebugTextUtils.setDebugVerbosity(p__init_0_, p__init_1_);
   }

   public static String _getCapsString() {
      return capsString;
   }

   public static String _getCpuInfo() {
      return cpuInfo == null ? "<unknown>" : cpuInfo;
   }

   public static void _renderCrosshair(int p__renderCrosshair_0_, boolean p__renderCrosshair_1_, boolean p__renderCrosshair_2_, boolean p__renderCrosshair_3_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GlStateManager.disableTexture();
      GlStateManager.depthMask(false);
      Tessellator tessellator = RenderSystem.renderThreadTesselator();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      GL11.glLineWidth(4.0F);
      bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
      if (p__renderCrosshair_1_) {
         bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.pos((double)p__renderCrosshair_0_, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
      }

      if (p__renderCrosshair_2_) {
         bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.pos(0.0D, (double)p__renderCrosshair_0_, 0.0D).color(0, 0, 0, 255).endVertex();
      }

      if (p__renderCrosshair_3_) {
         bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.pos(0.0D, 0.0D, (double)p__renderCrosshair_0_).color(0, 0, 0, 255).endVertex();
      }

      tessellator.draw();
      GL11.glLineWidth(2.0F);
      bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
      if (p__renderCrosshair_1_) {
         bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(255, 0, 0, 255).endVertex();
         bufferbuilder.pos((double)p__renderCrosshair_0_, 0.0D, 0.0D).color(255, 0, 0, 255).endVertex();
      }

      if (p__renderCrosshair_2_) {
         bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(0, 255, 0, 255).endVertex();
         bufferbuilder.pos(0.0D, (double)p__renderCrosshair_0_, 0.0D).color(0, 255, 0, 255).endVertex();
      }

      if (p__renderCrosshair_3_) {
         bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(127, 127, 255, 255).endVertex();
         bufferbuilder.pos(0.0D, 0.0D, (double)p__renderCrosshair_0_).color(127, 127, 255, 255).endVertex();
      }

      tessellator.draw();
      GL11.glLineWidth(1.0F);
      GlStateManager.depthMask(true);
      GlStateManager.enableTexture();
   }

   public static String getErrorString(int p_getErrorString_0_) {
      return LOOKUP_MAP.get(p_getErrorString_0_);
   }

   public static <T> T make(Supplier<T> p_make_0_) {
      return p_make_0_.get();
   }

   public static <T> T make(T p_make_0_, Consumer<T> p_make_1_) {
      p_make_1_.accept(p_make_0_);
      return p_make_0_;
   }
}