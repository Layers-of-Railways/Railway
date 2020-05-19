package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.ZombieModel;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZombieRenderer extends AbstractZombieRenderer<ZombieEntity, ZombieModel<ZombieEntity>> {
   public ZombieRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new ZombieModel<>(0.0F, false), new ZombieModel<>(0.5F, true), new ZombieModel<>(1.0F, true));
   }
}