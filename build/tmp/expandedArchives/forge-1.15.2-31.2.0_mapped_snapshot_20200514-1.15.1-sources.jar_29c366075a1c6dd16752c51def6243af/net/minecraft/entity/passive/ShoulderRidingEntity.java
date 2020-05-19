package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public abstract class ShoulderRidingEntity extends TameableEntity {
   private int rideCooldownCounter;

   protected ShoulderRidingEntity(EntityType<? extends ShoulderRidingEntity> type, World worldIn) {
      super(type, worldIn);
   }

   public boolean func_213439_d(ServerPlayerEntity p_213439_1_) {
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putString("id", this.getEntityString());
      this.writeWithoutTypeId(compoundnbt);
      if (p_213439_1_.addShoulderEntity(compoundnbt)) {
         this.remove();
         return true;
      } else {
         return false;
      }
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      ++this.rideCooldownCounter;
      super.tick();
   }

   public boolean canSitOnShoulder() {
      return this.rideCooldownCounter > 100;
   }
}