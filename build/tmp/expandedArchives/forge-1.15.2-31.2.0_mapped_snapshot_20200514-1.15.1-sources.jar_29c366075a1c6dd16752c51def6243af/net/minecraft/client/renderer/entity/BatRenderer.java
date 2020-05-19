package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.model.BatModel;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BatRenderer extends MobRenderer<BatEntity, BatModel> {
   private static final ResourceLocation BAT_TEXTURES = new ResourceLocation("textures/entity/bat.png");

   public BatRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new BatModel(), 0.25F);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(BatEntity entity) {
      return BAT_TEXTURES;
   }

   protected void preRenderCallback(BatEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
      matrixStackIn.scale(0.35F, 0.35F, 0.35F);
   }

   protected void applyRotations(BatEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
      if (entityLiving.getIsBatHanging()) {
         matrixStackIn.translate(0.0D, (double)-0.1F, 0.0D);
      } else {
         matrixStackIn.translate(0.0D, (double)(MathHelper.cos(ageInTicks * 0.3F) * 0.1F), 0.0D);
      }

      super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
   }
}