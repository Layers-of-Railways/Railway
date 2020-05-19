package net.minecraft.entity.item.minecart;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class MinecartEntity extends AbstractMinecartEntity {
   public MinecartEntity(EntityType<?> p_i50126_1_, World p_i50126_2_) {
      super(p_i50126_1_, p_i50126_2_);
   }

   public MinecartEntity(World worldIn, double x, double y, double z) {
      super(EntityType.MINECART, worldIn, x, y, z);
   }

   public boolean processInitialInteract(PlayerEntity player, Hand hand) {
      if (super.processInitialInteract(player, hand)) return true;
      if (player.isSecondaryUseActive()) {
         return false;
      } else if (this.isBeingRidden()) {
         return true;
      } else {
         if (!this.world.isRemote) {
            player.startRiding(this);
         }

         return true;
      }
   }

   /**
    * Called every tick the minecart is on an activator rail.
    */
   public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
      if (receivingPower) {
         if (this.isBeingRidden()) {
            this.removePassengers();
         }

         if (this.getRollingAmplitude() == 0) {
            this.setRollingDirection(-this.getRollingDirection());
            this.setRollingAmplitude(10);
            this.setDamage(50.0F);
            this.markVelocityChanged();
         }
      }

   }

   public AbstractMinecartEntity.Type getMinecartType() {
      return AbstractMinecartEntity.Type.RIDEABLE;
   }
}