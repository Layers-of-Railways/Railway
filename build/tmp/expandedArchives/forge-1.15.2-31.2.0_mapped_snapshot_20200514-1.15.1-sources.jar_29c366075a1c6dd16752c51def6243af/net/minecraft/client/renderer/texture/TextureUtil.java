package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class TextureUtil {
   private static final Logger LOGGER = LogManager.getLogger();

   public static int generateTextureId() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      return GlStateManager.genTexture();
   }

   public static void releaseTextureId(int textureId) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GlStateManager.deleteTexture(textureId);
   }

   public static void prepareImage(int textureId, int width, int height) {
      prepareImage(NativeImage.PixelFormatGLCode.RGBA, textureId, 0, width, height);
   }

   public static void prepareImage(NativeImage.PixelFormatGLCode pixelFormat, int textureId, int width, int height) {
      prepareImage(pixelFormat, textureId, 0, width, height);
   }

   public static void prepareImage(int textureId, int mipmapLevel, int width, int height) {
      prepareImage(NativeImage.PixelFormatGLCode.RGBA, textureId, mipmapLevel, width, height);
   }

   public static void prepareImage(NativeImage.PixelFormatGLCode pixelFormat, int textureId, int mipmapLevel, int width, int height) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      bindTexture(textureId);
      if (mipmapLevel >= 0) {
         GlStateManager.texParameter(3553, 33085, mipmapLevel);
         GlStateManager.texParameter(3553, 33082, 0);
         GlStateManager.texParameter(3553, 33083, mipmapLevel);
         GlStateManager.texParameter(3553, 34049, 0.0F);
      }

      for(int i = 0; i <= mipmapLevel; ++i) {
         GlStateManager.texImage2D(3553, i, pixelFormat.getGlFormat(), width >> i, height >> i, 0, 6408, 5121, (IntBuffer)null);
      }

   }

   private static void bindTexture(int textureId) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GlStateManager.bindTexture(textureId);
   }

   public static ByteBuffer readToBuffer(InputStream inputStreamIn) throws IOException {
      ByteBuffer bytebuffer;
      if (inputStreamIn instanceof FileInputStream) {
         FileInputStream fileinputstream = (FileInputStream)inputStreamIn;
         FileChannel filechannel = fileinputstream.getChannel();
         bytebuffer = MemoryUtil.memAlloc((int)filechannel.size() + 1);

         while(filechannel.read(bytebuffer) != -1) {
            ;
         }
      } else {
         bytebuffer = MemoryUtil.memAlloc(8192);
         ReadableByteChannel readablebytechannel = Channels.newChannel(inputStreamIn);

         while(readablebytechannel.read(bytebuffer) != -1) {
            if (bytebuffer.remaining() == 0) {
               bytebuffer = MemoryUtil.memRealloc(bytebuffer, bytebuffer.capacity() * 2);
            }
         }
      }

      return bytebuffer;
   }

   public static String readResourceAsString(InputStream inputStreamIn) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      ByteBuffer bytebuffer = null;

      try {
         bytebuffer = readToBuffer(inputStreamIn);
         int i = bytebuffer.position();
         ((Buffer)bytebuffer).rewind();
         String s = MemoryUtil.memASCII(bytebuffer, i);
         return s;
      } catch (IOException var7) {
         ;
      } finally {
         if (bytebuffer != null) {
            MemoryUtil.memFree(bytebuffer);
         }

      }

      return null;
   }

   public static void initTexture(IntBuffer bufferIn, int width, int height) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glPixelStorei(3312, 0);
      GL11.glPixelStorei(3313, 0);
      GL11.glPixelStorei(3314, 0);
      GL11.glPixelStorei(3315, 0);
      GL11.glPixelStorei(3316, 0);
      GL11.glPixelStorei(3317, 4);
      GL11.glTexImage2D(3553, 0, 6408, width, height, 0, 32993, 33639, bufferIn);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glTexParameteri(3553, 10240, 9728);
      GL11.glTexParameteri(3553, 10241, 9729);
   }
}