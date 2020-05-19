package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WitherSkeletonRenderer extends SkeletonRenderer {
   private static final ResourceLocation WITHER_SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");

   public WitherSkeletonRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(AbstractSkeletonEntity entity) {
      return WITHER_SKELETON_TEXTURES;
   }

   protected void preRenderCallback(AbstractSkeletonEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
      matrixStackIn.scale(1.2F, 1.2F, 1.2F);
   }
}