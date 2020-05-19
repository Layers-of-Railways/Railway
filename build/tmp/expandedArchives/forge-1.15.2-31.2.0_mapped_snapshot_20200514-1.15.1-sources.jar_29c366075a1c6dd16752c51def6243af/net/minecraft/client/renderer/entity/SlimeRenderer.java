package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.SlimeGelLayer;
import net.minecraft.client.renderer.entity.model.SlimeModel;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SlimeRenderer extends MobRenderer<SlimeEntity, SlimeModel<SlimeEntity>> {
   private static final ResourceLocation SLIME_TEXTURES = new ResourceLocation("textures/entity/slime/slime.png");

   public SlimeRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new SlimeModel<>(16), 0.25F);
      this.addLayer(new SlimeGelLayer<>(this));
   }

   public void render(SlimeEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
      this.shadowSize = 0.25F * (float)entityIn.getSlimeSize();
      super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   protected void preRenderCallback(SlimeEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
      float f = 0.999F;
      matrixStackIn.scale(0.999F, 0.999F, 0.999F);
      matrixStackIn.translate(0.0D, (double)0.001F, 0.0D);
      float f1 = (float)entitylivingbaseIn.getSlimeSize();
      float f2 = MathHelper.lerp(partialTickTime, entitylivingbaseIn.prevSquishFactor, entitylivingbaseIn.squishFactor) / (f1 * 0.5F + 1.0F);
      float f3 = 1.0F / (f2 + 1.0F);
      matrixStackIn.scale(f3 * f1, 1.0F / f3 * f1, f3 * f1);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(SlimeEntity entity) {
      return SLIME_TEXTURES;
   }
}