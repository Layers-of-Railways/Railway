package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderHelper {
   public static void enableStandardItemLighting() {
      RenderSystem.enableLighting();
      RenderSystem.enableColorMaterial();
      RenderSystem.colorMaterial(1032, 5634);
   }

   /**
    * Disables the OpenGL lighting properties enabled by enableStandardItemLighting
    */
   public static void disableStandardItemLighting() {
      RenderSystem.disableLighting();
      RenderSystem.disableColorMaterial();
   }

   public static void setupLevelDiffuseLighting(Matrix4f matrixIn) {
      RenderSystem.setupLevelDiffuseLighting(matrixIn);
   }

   public static void setupGuiFlatDiffuseLighting() {
      RenderSystem.setupGuiFlatDiffuseLighting();
   }

   public static void setupGui3DDiffuseLighting() {
      RenderSystem.setupGui3DDiffuseLighting();
   }
}