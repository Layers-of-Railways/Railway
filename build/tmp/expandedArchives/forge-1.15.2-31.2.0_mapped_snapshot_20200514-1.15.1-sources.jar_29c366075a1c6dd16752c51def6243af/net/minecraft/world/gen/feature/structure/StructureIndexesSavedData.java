package net.minecraft.world.gen.feature.structure;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;

public class StructureIndexesSavedData extends WorldSavedData {
   private LongSet all = new LongOpenHashSet();
   private LongSet remaining = new LongOpenHashSet();

   public StructureIndexesSavedData(String p_i48654_1_) {
      super(p_i48654_1_);
   }

   /**
    * reads in data from the NBTTagCompound into this MapDataBase
    */
   public void read(CompoundNBT nbt) {
      this.all = new LongOpenHashSet(nbt.getLongArray("All"));
      this.remaining = new LongOpenHashSet(nbt.getLongArray("Remaining"));
   }

   public CompoundNBT write(CompoundNBT compound) {
      compound.putLongArray("All", this.all.toLongArray());
      compound.putLongArray("Remaining", this.remaining.toLongArray());
      return compound;
   }

   public void func_201763_a(long p_201763_1_) {
      this.all.add(p_201763_1_);
      this.remaining.add(p_201763_1_);
   }

   public boolean func_208024_b(long p_208024_1_) {
      return this.all.contains(p_208024_1_);
   }

   public boolean func_208023_c(long p_208023_1_) {
      return this.remaining.contains(p_208023_1_);
   }

   public void func_201762_c(long p_201762_1_) {
      this.remaining.remove(p_201762_1_);
   }

   public LongSet getAll() {
      return this.all;
   }
}