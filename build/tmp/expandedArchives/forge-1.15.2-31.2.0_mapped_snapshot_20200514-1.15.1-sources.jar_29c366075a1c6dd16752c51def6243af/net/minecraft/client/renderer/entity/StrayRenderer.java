package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.StayClothingLayer;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StrayRenderer extends SkeletonRenderer {
   private static final ResourceLocation STRAY_SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/stray.png");

   public StrayRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn);
      this.addLayer(new StayClothingLayer<>(this));
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(AbstractSkeletonEntity entity) {
      return STRAY_SKELETON_TEXTURES;
   }
}