package net.minecraft.entity.passive;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class WaterMobEntity extends CreatureEntity {
   protected WaterMobEntity(EntityType<? extends WaterMobEntity> type, World p_i48565_2_) {
      super(type, p_i48565_2_);
      this.setPathPriority(PathNodeType.WATER, 0.0F);
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.WATER;
   }

   public boolean isNotColliding(IWorldReader worldIn) {
      return worldIn.checkNoEntityCollision(this);
   }

   /**
    * Get number of ticks, at least during which the living entity will be silent.
    */
   public int getTalkInterval() {
      return 120;
   }

   /**
    * Get the experience points the entity currently has.
    */
   protected int getExperiencePoints(PlayerEntity player) {
      return 1 + this.world.rand.nextInt(3);
   }

   protected void updateAir(int p_209207_1_) {
      if (this.isAlive() && !this.isInWaterOrBubbleColumn()) {
         this.setAir(p_209207_1_ - 1);
         if (this.getAir() == -20) {
            this.setAir(0);
            this.attackEntityFrom(DamageSource.DROWN, 2.0F);
         }
      } else {
         this.setAir(300);
      }

   }

   /**
    * Gets called every tick from main Entity class
    */
   public void baseTick() {
      int i = this.getAir();
      super.baseTick();
      this.updateAir(i);
   }

   public boolean isPushedByWater() {
      return false;
   }

   public boolean canBeLeashedTo(PlayerEntity player) {
      return false;
   }
}