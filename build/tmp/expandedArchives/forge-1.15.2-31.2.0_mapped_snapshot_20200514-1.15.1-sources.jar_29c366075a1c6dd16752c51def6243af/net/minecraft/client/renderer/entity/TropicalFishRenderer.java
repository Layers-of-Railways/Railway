package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.layers.TropicalFishPatternLayer;
import net.minecraft.client.renderer.entity.model.AbstractTropicalFishModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.TropicalFishAModel;
import net.minecraft.client.renderer.entity.model.TropicalFishBModel;
import net.minecraft.entity.passive.fish.TropicalFishEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TropicalFishRenderer extends MobRenderer<TropicalFishEntity, EntityModel<TropicalFishEntity>> {
   /** Breaking recompile intentionally since modelA/B incorrectly mapped. */
   private final TropicalFishAModel<TropicalFishEntity> aModel = new TropicalFishAModel<>(0.0F);
   /** Breaking recompile intentionally since modelA/B incorrectly mapped. */
   private final TropicalFishBModel<TropicalFishEntity> bModel = new TropicalFishBModel<>(0.0F);

   public TropicalFishRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new TropicalFishAModel<>(0.0F), 0.15F);
      this.addLayer(new TropicalFishPatternLayer(this));
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(TropicalFishEntity entity) {
      return entity.getBodyTexture();
   }

   public void render(TropicalFishEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
      AbstractTropicalFishModel<TropicalFishEntity> abstracttropicalfishmodel = (AbstractTropicalFishModel<TropicalFishEntity>)(entityIn.getSize() == 0 ? this.aModel : this.bModel);
      this.entityModel = abstracttropicalfishmodel;
      float[] afloat = entityIn.func_204219_dC();
      abstracttropicalfishmodel.func_228257_a_(afloat[0], afloat[1], afloat[2]);
      super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
      abstracttropicalfishmodel.func_228257_a_(1.0F, 1.0F, 1.0F);
   }

   protected void applyRotations(TropicalFishEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
      float f = 4.3F * MathHelper.sin(0.6F * ageInTicks);
      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f));
      if (!entityLiving.isInWater()) {
         matrixStackIn.translate((double)0.2F, (double)0.1F, 0.0D);
         matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(90.0F));
      }

   }
}