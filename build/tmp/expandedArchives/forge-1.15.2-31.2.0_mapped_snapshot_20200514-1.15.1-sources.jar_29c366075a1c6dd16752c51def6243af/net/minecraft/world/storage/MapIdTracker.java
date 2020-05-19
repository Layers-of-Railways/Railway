package net.minecraft.world.storage;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.nbt.CompoundNBT;

public class MapIdTracker extends WorldSavedData {
   private final Object2IntMap<String> usedIds = new Object2IntOpenHashMap<>();

   public MapIdTracker() {
      super("idcounts");
      this.usedIds.defaultReturnValue(-1);
   }

   /**
    * reads in data from the NBTTagCompound into this MapDataBase
    */
   public void read(CompoundNBT nbt) {
      this.usedIds.clear();

      for(String s : nbt.keySet()) {
         if (nbt.contains(s, 99)) {
            this.usedIds.put(s, nbt.getInt(s));
         }
      }

   }

   public CompoundNBT write(CompoundNBT compound) {
      for(Entry<String> entry : this.usedIds.object2IntEntrySet()) {
         compound.putInt(entry.getKey(), entry.getIntValue());
      }

      return compound;
   }

   public int getNextId() {
      int i = this.usedIds.getInt("map") + 1;
      this.usedIds.put("map", i);
      this.markDirty();
      return i;
   }
}