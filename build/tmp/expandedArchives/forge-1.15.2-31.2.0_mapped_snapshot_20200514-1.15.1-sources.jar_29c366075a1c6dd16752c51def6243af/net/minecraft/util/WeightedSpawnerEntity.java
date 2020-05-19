package net.minecraft.util;

import net.minecraft.nbt.CompoundNBT;

public class WeightedSpawnerEntity extends WeightedRandom.Item {
   private final CompoundNBT nbt;

   public WeightedSpawnerEntity() {
      super(1);
      this.nbt = new CompoundNBT();
      this.nbt.putString("id", "minecraft:pig");
   }

   public WeightedSpawnerEntity(CompoundNBT nbtIn) {
      this(nbtIn.contains("Weight", 99) ? nbtIn.getInt("Weight") : 1, nbtIn.getCompound("Entity"));
   }

   public WeightedSpawnerEntity(int itemWeightIn, CompoundNBT nbtIn) {
      super(itemWeightIn);
      this.nbt = nbtIn;
   }

   public CompoundNBT toCompoundTag() {
      CompoundNBT compoundnbt = new CompoundNBT();
      if (!this.nbt.contains("id", 8)) {
         this.nbt.putString("id", "minecraft:pig");
      } else if (!this.nbt.getString("id").contains(":")) {
         this.nbt.putString("id", (new ResourceLocation(this.nbt.getString("id"))).toString());
      }

      compoundnbt.put("Entity", this.nbt);
      compoundnbt.putInt("Weight", this.itemWeight);
      return compoundnbt;
   }

   public CompoundNBT getNbt() {
      return this.nbt;
   }
}