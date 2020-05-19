package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.SilverfishModel;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SilverfishRenderer extends MobRenderer<SilverfishEntity, SilverfishModel<SilverfishEntity>> {
   private static final ResourceLocation SILVERFISH_TEXTURES = new ResourceLocation("textures/entity/silverfish.png");

   public SilverfishRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new SilverfishModel<>(), 0.3F);
   }

   protected float getDeathMaxRotation(SilverfishEntity entityLivingBaseIn) {
      return 180.0F;
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(SilverfishEntity entity) {
      return SILVERFISH_TEXTURES;
   }
}