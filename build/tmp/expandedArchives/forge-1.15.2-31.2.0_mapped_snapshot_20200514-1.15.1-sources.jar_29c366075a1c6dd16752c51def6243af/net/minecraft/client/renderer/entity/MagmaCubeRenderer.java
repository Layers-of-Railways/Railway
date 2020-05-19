package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.model.MagmaCubeModel;
import net.minecraft.entity.monster.MagmaCubeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MagmaCubeRenderer extends MobRenderer<MagmaCubeEntity, MagmaCubeModel<MagmaCubeEntity>> {
   private static final ResourceLocation MAGMA_CUBE_TEXTURES = new ResourceLocation("textures/entity/slime/magmacube.png");

   public MagmaCubeRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new MagmaCubeModel<>(), 0.25F);
   }

   protected int getBlockLight(MagmaCubeEntity entityIn, float partialTicks) {
      return 15;
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(MagmaCubeEntity entity) {
      return MAGMA_CUBE_TEXTURES;
   }

   protected void preRenderCallback(MagmaCubeEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
      int i = entitylivingbaseIn.getSlimeSize();
      float f = MathHelper.lerp(partialTickTime, entitylivingbaseIn.prevSquishFactor, entitylivingbaseIn.squishFactor) / ((float)i * 0.5F + 1.0F);
      float f1 = 1.0F / (f + 1.0F);
      matrixStackIn.scale(f1 * (float)i, 1.0F / f1 * (float)i, f1 * (float)i);
   }
}