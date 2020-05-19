package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.WitchHeldItemLayer;
import net.minecraft.client.renderer.entity.model.WitchModel;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WitchRenderer extends MobRenderer<WitchEntity, WitchModel<WitchEntity>> {
   private static final ResourceLocation WITCH_TEXTURES = new ResourceLocation("textures/entity/witch.png");

   public WitchRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new WitchModel<>(0.0F), 0.5F);
      this.addLayer(new WitchHeldItemLayer<>(this));
   }

   public void render(WitchEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
      this.entityModel.func_205074_a(!entityIn.getHeldItemMainhand().isEmpty());
      super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(WitchEntity entity) {
      return WITCH_TEXTURES;
   }

   protected void preRenderCallback(WitchEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
      float f = 0.9375F;
      matrixStackIn.scale(0.9375F, 0.9375F, 0.9375F);
   }
}