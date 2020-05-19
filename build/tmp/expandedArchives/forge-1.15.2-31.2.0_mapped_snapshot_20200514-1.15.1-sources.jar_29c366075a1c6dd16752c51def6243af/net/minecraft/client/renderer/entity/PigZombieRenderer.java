package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.ZombieModel;
import net.minecraft.entity.monster.ZombiePigmanEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PigZombieRenderer extends BipedRenderer<ZombiePigmanEntity, ZombieModel<ZombiePigmanEntity>> {
   private static final ResourceLocation ZOMBIE_PIGMAN_TEXTURE = new ResourceLocation("textures/entity/zombie_pigman.png");

   public PigZombieRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new ZombieModel<>(0.0F, false), 0.5F);
      this.addLayer(new BipedArmorLayer<>(this, new ZombieModel(0.5F, true), new ZombieModel(1.0F, true)));
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(ZombiePigmanEntity entity) {
      return ZOMBIE_PIGMAN_TEXTURE;
   }
}