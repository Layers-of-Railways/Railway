package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.ChickenModel;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChickenRenderer extends MobRenderer<ChickenEntity, ChickenModel<ChickenEntity>> {
   private static final ResourceLocation CHICKEN_TEXTURES = new ResourceLocation("textures/entity/chicken.png");

   public ChickenRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new ChickenModel<>(), 0.3F);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(ChickenEntity entity) {
      return CHICKEN_TEXTURES;
   }

   /**
    * Defines what float the third param in setRotationAngles of ModelBase is
    */
   protected float handleRotationFloat(ChickenEntity livingBase, float partialTicks) {
      float f = MathHelper.lerp(partialTicks, livingBase.oFlap, livingBase.wingRotation);
      float f1 = MathHelper.lerp(partialTicks, livingBase.oFlapSpeed, livingBase.destPos);
      return (MathHelper.sin(f) + 1.0F) * f1;
   }
}