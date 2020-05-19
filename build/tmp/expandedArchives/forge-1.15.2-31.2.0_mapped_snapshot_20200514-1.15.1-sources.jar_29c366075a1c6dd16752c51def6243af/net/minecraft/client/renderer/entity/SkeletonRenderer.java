package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.SkeletonModel;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkeletonRenderer extends BipedRenderer<AbstractSkeletonEntity, SkeletonModel<AbstractSkeletonEntity>> {
   private static final ResourceLocation SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/skeleton.png");

   public SkeletonRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new SkeletonModel<>(), 0.5F);
      this.addLayer(new HeldItemLayer<>(this));
      this.addLayer(new BipedArmorLayer<>(this, new SkeletonModel(0.5F, true), new SkeletonModel(1.0F, true)));
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(AbstractSkeletonEntity entity) {
      return SKELETON_TEXTURES;
   }
}