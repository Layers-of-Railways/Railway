package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.layers.CatCollarLayer;
import net.minecraft.client.renderer.entity.model.CatModel;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CatRenderer extends MobRenderer<CatEntity, CatModel<CatEntity>> {
   public CatRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new CatModel<>(0.0F), 0.4F);
      this.addLayer(new CatCollarLayer(this));
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(CatEntity entity) {
      return entity.getCatTypeName();
   }

   protected void preRenderCallback(CatEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
      super.preRenderCallback(entitylivingbaseIn, matrixStackIn, partialTickTime);
      matrixStackIn.scale(0.8F, 0.8F, 0.8F);
   }

   protected void applyRotations(CatEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
      float f = entityLiving.func_213408_v(partialTicks);
      if (f > 0.0F) {
         matrixStackIn.translate((double)(0.4F * f), (double)(0.15F * f), (double)(0.1F * f));
         matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(MathHelper.interpolateAngle(f, 0.0F, 90.0F)));
         BlockPos blockpos = new BlockPos(entityLiving);

         for(PlayerEntity playerentity : entityLiving.world.getEntitiesWithinAABB(PlayerEntity.class, (new AxisAlignedBB(blockpos)).grow(2.0D, 2.0D, 2.0D))) {
            if (playerentity.isSleeping()) {
               matrixStackIn.translate((double)(0.15F * f), 0.0D, 0.0D);
               break;
            }
         }
      }

   }
}