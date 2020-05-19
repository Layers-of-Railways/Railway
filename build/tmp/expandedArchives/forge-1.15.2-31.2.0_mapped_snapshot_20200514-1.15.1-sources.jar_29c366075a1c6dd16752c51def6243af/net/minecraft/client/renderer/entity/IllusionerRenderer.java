package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.entity.monster.IllusionerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IllusionerRenderer extends IllagerRenderer<IllusionerEntity> {
   private static final ResourceLocation ILLUSIONIST = new ResourceLocation("textures/entity/illager/illusioner.png");

   public IllusionerRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new IllagerModel<>(0.0F, 0.0F, 64, 64), 0.5F);
      this.addLayer(new HeldItemLayer<IllusionerEntity, IllagerModel<IllusionerEntity>>(this) {
         public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, IllusionerEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entitylivingbaseIn.isSpellcasting() || entitylivingbaseIn.isAggressive()) {
               super.render(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }

         }
      });
      this.entityModel.func_205062_a().showModel = true;
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(IllusionerEntity entity) {
      return ILLUSIONIST;
   }

   public void render(IllusionerEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
      if (entityIn.isInvisible()) {
         Vec3d[] avec3d = entityIn.getRenderLocations(partialTicks);
         float f = this.handleRotationFloat(entityIn, partialTicks);

         for(int i = 0; i < avec3d.length; ++i) {
            matrixStackIn.push();
            matrixStackIn.translate(avec3d[i].x + (double)MathHelper.cos((float)i + f * 0.5F) * 0.025D, avec3d[i].y + (double)MathHelper.cos((float)i + f * 0.75F) * 0.0125D, avec3d[i].z + (double)MathHelper.cos((float)i + f * 0.7F) * 0.025D);
            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.pop();
         }
      } else {
         super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
      }

   }

   protected boolean isVisible(IllusionerEntity livingEntityIn) {
      return true;
   }
}