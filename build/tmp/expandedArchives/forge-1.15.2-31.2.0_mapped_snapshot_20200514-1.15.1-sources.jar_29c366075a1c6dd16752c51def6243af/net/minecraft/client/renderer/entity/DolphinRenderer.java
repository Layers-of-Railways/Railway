package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.DolphinCarriedItemLayer;
import net.minecraft.client.renderer.entity.model.DolphinModel;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DolphinRenderer extends MobRenderer<DolphinEntity, DolphinModel<DolphinEntity>> {
   private static final ResourceLocation DOLPHIN_LOCATION = new ResourceLocation("textures/entity/dolphin.png");

   public DolphinRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new DolphinModel<>(), 0.7F);
      this.addLayer(new DolphinCarriedItemLayer(this));
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(DolphinEntity entity) {
      return DOLPHIN_LOCATION;
   }
}