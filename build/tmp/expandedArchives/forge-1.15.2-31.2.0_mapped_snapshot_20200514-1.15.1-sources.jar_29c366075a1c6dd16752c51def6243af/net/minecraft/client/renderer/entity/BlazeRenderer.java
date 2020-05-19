package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.BlazeModel;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlazeRenderer extends MobRenderer<BlazeEntity, BlazeModel<BlazeEntity>> {
   private static final ResourceLocation BLAZE_TEXTURES = new ResourceLocation("textures/entity/blaze.png");

   public BlazeRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new BlazeModel<>(), 0.5F);
   }

   protected int getBlockLight(BlazeEntity entityIn, float partialTicks) {
      return 15;
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(BlazeEntity entity) {
      return BLAZE_TEXTURES;
   }
}