package net.minecraft.entity.player;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PlayerAbilities {
   public boolean disableDamage;
   public boolean isFlying;
   public boolean allowFlying;
   public boolean isCreativeMode;
   public boolean allowEdit = true;
   private float flySpeed = 0.05F;
   private float walkSpeed = 0.1F;

   public void write(CompoundNBT tagCompound) {
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putBoolean("invulnerable", this.disableDamage);
      compoundnbt.putBoolean("flying", this.isFlying);
      compoundnbt.putBoolean("mayfly", this.allowFlying);
      compoundnbt.putBoolean("instabuild", this.isCreativeMode);
      compoundnbt.putBoolean("mayBuild", this.allowEdit);
      compoundnbt.putFloat("flySpeed", this.flySpeed);
      compoundnbt.putFloat("walkSpeed", this.walkSpeed);
      tagCompound.put("abilities", compoundnbt);
   }

   public void read(CompoundNBT tagCompound) {
      if (tagCompound.contains("abilities", 10)) {
         CompoundNBT compoundnbt = tagCompound.getCompound("abilities");
         this.disableDamage = compoundnbt.getBoolean("invulnerable");
         this.isFlying = compoundnbt.getBoolean("flying");
         this.allowFlying = compoundnbt.getBoolean("mayfly");
         this.isCreativeMode = compoundnbt.getBoolean("instabuild");
         if (compoundnbt.contains("flySpeed", 99)) {
            this.flySpeed = compoundnbt.getFloat("flySpeed");
            this.walkSpeed = compoundnbt.getFloat("walkSpeed");
         }

         if (compoundnbt.contains("mayBuild", 1)) {
            this.allowEdit = compoundnbt.getBoolean("mayBuild");
         }
      }

   }

   public float getFlySpeed() {
      return this.flySpeed;
   }

   @OnlyIn(Dist.CLIENT)
   public void setFlySpeed(float speed) {
      this.flySpeed = speed;
   }

   public float getWalkSpeed() {
      return this.walkSpeed;
   }

   @OnlyIn(Dist.CLIENT)
   public void setWalkSpeed(float speed) {
      this.walkSpeed = speed;
   }
}