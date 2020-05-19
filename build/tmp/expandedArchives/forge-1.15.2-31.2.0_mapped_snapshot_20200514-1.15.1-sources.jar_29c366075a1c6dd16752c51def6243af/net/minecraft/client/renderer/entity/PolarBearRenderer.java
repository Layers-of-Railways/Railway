package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.model.PolarBearModel;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PolarBearRenderer extends MobRenderer<PolarBearEntity, PolarBearModel<PolarBearEntity>> {
   private static final ResourceLocation POLAR_BEAR_TEXTURE = new ResourceLocation("textures/entity/bear/polarbear.png");

   public PolarBearRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new PolarBearModel<>(), 0.9F);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(PolarBearEntity entity) {
      return POLAR_BEAR_TEXTURE;
   }

   protected void preRenderCallback(PolarBearEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
      matrixStackIn.scale(1.2F, 1.2F, 1.2F);
      super.preRenderCallback(entitylivingbaseIn, matrixStackIn, partialTickTime);
   }
}