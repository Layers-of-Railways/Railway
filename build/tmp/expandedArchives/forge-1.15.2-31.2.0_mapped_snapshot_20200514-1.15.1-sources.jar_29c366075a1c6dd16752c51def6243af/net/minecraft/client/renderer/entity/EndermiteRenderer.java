package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.EndermiteModel;
import net.minecraft.entity.monster.EndermiteEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EndermiteRenderer extends MobRenderer<EndermiteEntity, EndermiteModel<EndermiteEntity>> {
   private static final ResourceLocation ENDERMITE_TEXTURES = new ResourceLocation("textures/entity/endermite.png");

   public EndermiteRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new EndermiteModel<>(), 0.3F);
   }

   protected float getDeathMaxRotation(EndermiteEntity entityLivingBaseIn) {
      return 180.0F;
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(EndermiteEntity entity) {
      return ENDERMITE_TEXTURES;
   }
}