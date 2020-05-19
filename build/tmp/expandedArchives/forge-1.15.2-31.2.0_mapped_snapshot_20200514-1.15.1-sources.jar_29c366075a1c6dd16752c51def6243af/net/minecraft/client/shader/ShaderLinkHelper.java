package net.minecraft.client.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ShaderLinkHelper {
   private static final Logger LOGGER = LogManager.getLogger();

   public static void func_227804_a_(int p_227804_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GlStateManager.useProgram(p_227804_0_);
   }

   public static void deleteShader(IShaderManager p_148077_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      p_148077_0_.getFragmentShaderLoader().detachShader();
      p_148077_0_.getVertexShaderLoader().detachShader();
      GlStateManager.deleteProgram(p_148077_0_.getProgram());
   }

   public static int createProgram() throws IOException {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      int i = GlStateManager.createProgram();
      if (i <= 0) {
         throw new IOException("Could not create shader program (returned program ID " + i + ")");
      } else {
         return i;
      }
   }

   public static void linkProgram(IShaderManager p_148075_0_) throws IOException {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      p_148075_0_.getFragmentShaderLoader().attachShader(p_148075_0_);
      p_148075_0_.getVertexShaderLoader().attachShader(p_148075_0_);
      GlStateManager.linkProgram(p_148075_0_.getProgram());
      int i = GlStateManager.getProgram(p_148075_0_.getProgram(), 35714);
      if (i == 0) {
         LOGGER.warn("Error encountered when linking program containing VS {} and FS {}. Log output:", p_148075_0_.getVertexShaderLoader().getShaderFilename(), p_148075_0_.getFragmentShaderLoader().getShaderFilename());
         LOGGER.warn(GlStateManager.getProgramInfoLog(p_148075_0_.getProgram(), 32768));
      }

   }
}