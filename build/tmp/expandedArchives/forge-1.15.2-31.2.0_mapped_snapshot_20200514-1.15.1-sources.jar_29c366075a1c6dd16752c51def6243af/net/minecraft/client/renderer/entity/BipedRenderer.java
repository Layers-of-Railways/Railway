package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BipedRenderer<T extends MobEntity, M extends BipedModel<T>> extends MobRenderer<T, M> {
   private static final ResourceLocation DEFAULT_RES_LOC = new ResourceLocation("textures/entity/steve.png");

   public BipedRenderer(EntityRendererManager renderManagerIn, M modelBipedIn, float shadowSize) {
      super(renderManagerIn, modelBipedIn, shadowSize);
      this.addLayer(new HeadLayer<>(this));
      this.addLayer(new ElytraLayer<>(this));
      this.addLayer(new HeldItemLayer<>(this));
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(T entity) {
      return DEFAULT_RES_LOC;
   }
}