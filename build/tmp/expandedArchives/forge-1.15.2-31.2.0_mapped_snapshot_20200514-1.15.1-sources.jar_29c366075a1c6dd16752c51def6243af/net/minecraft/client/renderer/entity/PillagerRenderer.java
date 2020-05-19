package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PillagerRenderer extends IllagerRenderer<PillagerEntity> {
   private static final ResourceLocation field_217772_a = new ResourceLocation("textures/entity/illager/pillager.png");

   public PillagerRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new IllagerModel<>(0.0F, 0.0F, 64, 64), 0.5F);
      this.addLayer(new HeldItemLayer<>(this));
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(PillagerEntity entity) {
      return field_217772_a;
   }
}