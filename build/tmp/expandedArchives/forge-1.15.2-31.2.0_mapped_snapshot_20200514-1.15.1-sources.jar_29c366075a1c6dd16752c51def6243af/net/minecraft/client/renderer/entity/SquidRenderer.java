package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.SquidModel;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SquidRenderer extends MobRenderer<SquidEntity, SquidModel<SquidEntity>> {
   private static final ResourceLocation SQUID_TEXTURES = new ResourceLocation("textures/entity/squid.png");

   public SquidRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new SquidModel<>(), 0.7F);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(SquidEntity entity) {
      return SQUID_TEXTURES;
   }

   protected void applyRotations(SquidEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
      float f = MathHelper.lerp(partialTicks, entityLiving.prevSquidPitch, entityLiving.squidPitch);
      float f1 = MathHelper.lerp(partialTicks, entityLiving.prevSquidYaw, entityLiving.squidYaw);
      matrixStackIn.translate(0.0D, 0.5D, 0.0D);
      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F - rotationYaw));
      matrixStackIn.rotate(Vector3f.XP.rotationDegrees(f));
      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f1));
      matrixStackIn.translate(0.0D, (double)-1.2F, 0.0D);
   }

   /**
    * Defines what float the third param in setRotationAngles of ModelBase is
    */
   protected float handleRotationFloat(SquidEntity livingBase, float partialTicks) {
      return MathHelper.lerp(partialTicks, livingBase.lastTentacleAngle, livingBase.tentacleAngle);
   }
}