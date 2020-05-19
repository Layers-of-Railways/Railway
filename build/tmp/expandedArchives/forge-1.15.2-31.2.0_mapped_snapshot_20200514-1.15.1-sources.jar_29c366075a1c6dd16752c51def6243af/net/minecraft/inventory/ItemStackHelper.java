package net.minecraft.inventory;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;

public class ItemStackHelper {
   public static ItemStack getAndSplit(List<ItemStack> stacks, int index, int amount) {
      return index >= 0 && index < stacks.size() && !stacks.get(index).isEmpty() && amount > 0 ? stacks.get(index).split(amount) : ItemStack.EMPTY;
   }

   public static ItemStack getAndRemove(List<ItemStack> stacks, int index) {
      return index >= 0 && index < stacks.size() ? stacks.set(index, ItemStack.EMPTY) : ItemStack.EMPTY;
   }

   public static CompoundNBT saveAllItems(CompoundNBT tag, NonNullList<ItemStack> list) {
      return saveAllItems(tag, list, true);
   }

   public static CompoundNBT saveAllItems(CompoundNBT tag, NonNullList<ItemStack> list, boolean saveEmpty) {
      ListNBT listnbt = new ListNBT();

      for(int i = 0; i < list.size(); ++i) {
         ItemStack itemstack = list.get(i);
         if (!itemstack.isEmpty()) {
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.putByte("Slot", (byte)i);
            itemstack.write(compoundnbt);
            listnbt.add(compoundnbt);
         }
      }

      if (!listnbt.isEmpty() || saveEmpty) {
         tag.put("Items", listnbt);
      }

      return tag;
   }

   public static void loadAllItems(CompoundNBT tag, NonNullList<ItemStack> list) {
      ListNBT listnbt = tag.getList("Items", 10);

      for(int i = 0; i < listnbt.size(); ++i) {
         CompoundNBT compoundnbt = listnbt.getCompound(i);
         int j = compoundnbt.getByte("Slot") & 255;
         if (j >= 0 && j < list.size()) {
            list.set(j, ItemStack.read(compoundnbt));
         }
      }

   }
}