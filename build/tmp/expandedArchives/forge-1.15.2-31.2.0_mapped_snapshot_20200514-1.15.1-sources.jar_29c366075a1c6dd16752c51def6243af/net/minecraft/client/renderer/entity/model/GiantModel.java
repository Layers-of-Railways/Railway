package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.monster.GiantEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GiantModel extends AbstractZombieModel<GiantEntity> {
   public GiantModel() {
      this(0.0F, false);
   }

   public GiantModel(float modelSize, boolean p_i51066_2_) {
      super(modelSize, 0.0F, 64, p_i51066_2_ ? 32 : 64);
   }

   public boolean isAggressive(GiantEntity entityIn) {
      return false;
   }
}