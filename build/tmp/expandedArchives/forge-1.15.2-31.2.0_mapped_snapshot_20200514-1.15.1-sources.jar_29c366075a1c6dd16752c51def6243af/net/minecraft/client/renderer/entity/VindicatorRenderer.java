package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.entity.monster.VindicatorEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VindicatorRenderer extends IllagerRenderer<VindicatorEntity> {
   private static final ResourceLocation VINDICATOR_TEXTURE = new ResourceLocation("textures/entity/illager/vindicator.png");

   public VindicatorRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new IllagerModel<>(0.0F, 0.0F, 64, 64), 0.5F);
      this.addLayer(new HeldItemLayer<VindicatorEntity, IllagerModel<VindicatorEntity>>(this) {
         public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, VindicatorEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entitylivingbaseIn.isAggressive()) {
               super.render(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }

         }
      });
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(VindicatorEntity entity) {
      return VINDICATOR_TEXTURE;
   }
}