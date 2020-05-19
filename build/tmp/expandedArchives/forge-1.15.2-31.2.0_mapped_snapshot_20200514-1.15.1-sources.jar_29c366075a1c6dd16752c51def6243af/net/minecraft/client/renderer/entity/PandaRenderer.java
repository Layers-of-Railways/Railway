package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Map;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.layers.PandaHeldItemLayer;
import net.minecraft.client.renderer.entity.model.PandaModel;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PandaRenderer extends MobRenderer<PandaEntity, PandaModel<PandaEntity>> {
   private static final Map<PandaEntity.Type, ResourceLocation> field_217777_a = Util.make(Maps.newEnumMap(PandaEntity.Type.class), (p_217776_0_) -> {
      p_217776_0_.put(PandaEntity.Type.NORMAL, new ResourceLocation("textures/entity/panda/panda.png"));
      p_217776_0_.put(PandaEntity.Type.LAZY, new ResourceLocation("textures/entity/panda/lazy_panda.png"));
      p_217776_0_.put(PandaEntity.Type.WORRIED, new ResourceLocation("textures/entity/panda/worried_panda.png"));
      p_217776_0_.put(PandaEntity.Type.PLAYFUL, new ResourceLocation("textures/entity/panda/playful_panda.png"));
      p_217776_0_.put(PandaEntity.Type.BROWN, new ResourceLocation("textures/entity/panda/brown_panda.png"));
      p_217776_0_.put(PandaEntity.Type.WEAK, new ResourceLocation("textures/entity/panda/weak_panda.png"));
      p_217776_0_.put(PandaEntity.Type.AGGRESSIVE, new ResourceLocation("textures/entity/panda/aggressive_panda.png"));
   });

   public PandaRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new PandaModel<>(9, 0.0F), 0.9F);
      this.addLayer(new PandaHeldItemLayer(this));
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(PandaEntity entity) {
      return field_217777_a.getOrDefault(entity.func_213590_ei(), field_217777_a.get(PandaEntity.Type.NORMAL));
   }

   protected void applyRotations(PandaEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
      if (entityLiving.rollCounter > 0) {
         int i = entityLiving.rollCounter;
         int j = i + 1;
         float f = 7.0F;
         float f1 = entityLiving.isChild() ? 0.3F : 0.8F;
         if (i < 8) {
            float f3 = (float)(90 * i) / 7.0F;
            float f4 = (float)(90 * j) / 7.0F;
            float f2 = this.func_217775_a(f3, f4, j, partialTicks, 8.0F);
            matrixStackIn.translate(0.0D, (double)((f1 + 0.2F) * (f2 / 90.0F)), 0.0D);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-f2));
         } else if (i < 16) {
            float f13 = ((float)i - 8.0F) / 7.0F;
            float f16 = 90.0F + 90.0F * f13;
            float f5 = 90.0F + 90.0F * ((float)j - 8.0F) / 7.0F;
            float f10 = this.func_217775_a(f16, f5, j, partialTicks, 16.0F);
            matrixStackIn.translate(0.0D, (double)(f1 + 0.2F + (f1 - 0.2F) * (f10 - 90.0F) / 90.0F), 0.0D);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-f10));
         } else if ((float)i < 24.0F) {
            float f14 = ((float)i - 16.0F) / 7.0F;
            float f17 = 180.0F + 90.0F * f14;
            float f19 = 180.0F + 90.0F * ((float)j - 16.0F) / 7.0F;
            float f11 = this.func_217775_a(f17, f19, j, partialTicks, 24.0F);
            matrixStackIn.translate(0.0D, (double)(f1 + f1 * (270.0F - f11) / 90.0F), 0.0D);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-f11));
         } else if (i < 32) {
            float f15 = ((float)i - 24.0F) / 7.0F;
            float f18 = 270.0F + 90.0F * f15;
            float f20 = 270.0F + 90.0F * ((float)j - 24.0F) / 7.0F;
            float f12 = this.func_217775_a(f18, f20, j, partialTicks, 32.0F);
            matrixStackIn.translate(0.0D, (double)(f1 * ((360.0F - f12) / 90.0F)), 0.0D);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-f12));
         }
      }

      float f6 = entityLiving.func_213561_v(partialTicks);
      if (f6 > 0.0F) {
         matrixStackIn.translate(0.0D, (double)(0.8F * f6), 0.0D);
         matrixStackIn.rotate(Vector3f.XP.rotationDegrees(MathHelper.lerp(f6, entityLiving.rotationPitch, entityLiving.rotationPitch + 90.0F)));
         matrixStackIn.translate(0.0D, (double)(-1.0F * f6), 0.0D);
         if (entityLiving.func_213566_eo()) {
            float f7 = (float)(Math.cos((double)entityLiving.ticksExisted * 1.25D) * Math.PI * (double)0.05F);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f7));
            if (entityLiving.isChild()) {
               matrixStackIn.translate(0.0D, (double)0.8F, (double)0.55F);
            }
         }
      }

      float f8 = entityLiving.func_213583_w(partialTicks);
      if (f8 > 0.0F) {
         float f9 = entityLiving.isChild() ? 0.5F : 1.3F;
         matrixStackIn.translate(0.0D, (double)(f9 * f8), 0.0D);
         matrixStackIn.rotate(Vector3f.XP.rotationDegrees(MathHelper.lerp(f8, entityLiving.rotationPitch, entityLiving.rotationPitch + 180.0F)));
      }

   }

   private float func_217775_a(float p_217775_1_, float p_217775_2_, int p_217775_3_, float p_217775_4_, float p_217775_5_) {
      return (float)p_217775_3_ < p_217775_5_ ? MathHelper.lerp(p_217775_4_, p_217775_1_, p_217775_2_) : p_217775_1_;
   }
}