package net.minecraft.item;

import java.util.List;
import net.minecraft.nbt.CompoundNBT;

public interface IDyeableArmorItem {
   default boolean hasColor(ItemStack stack) {
      CompoundNBT compoundnbt = stack.getChildTag("display");
      return compoundnbt != null && compoundnbt.contains("color", 99);
   }

   default int getColor(ItemStack stack) {
      CompoundNBT compoundnbt = stack.getChildTag("display");
      return compoundnbt != null && compoundnbt.contains("color", 99) ? compoundnbt.getInt("color") : 10511680;
   }

   default void removeColor(ItemStack stack) {
      CompoundNBT compoundnbt = stack.getChildTag("display");
      if (compoundnbt != null && compoundnbt.contains("color")) {
         compoundnbt.remove("color");
      }

   }

   default void setColor(ItemStack stack, int color) {
      stack.getOrCreateChildTag("display").putInt("color", color);
   }

   static ItemStack dyeItem(ItemStack stack, List<DyeItem> dyes) {
      ItemStack itemstack = ItemStack.EMPTY;
      int[] aint = new int[3];
      int i = 0;
      int j = 0;
      IDyeableArmorItem idyeablearmoritem = null;
      Item item = stack.getItem();
      if (item instanceof IDyeableArmorItem) {
         idyeablearmoritem = (IDyeableArmorItem)item;
         itemstack = stack.copy();
         itemstack.setCount(1);
         if (idyeablearmoritem.hasColor(stack)) {
            int k = idyeablearmoritem.getColor(itemstack);
            float f = (float)(k >> 16 & 255) / 255.0F;
            float f1 = (float)(k >> 8 & 255) / 255.0F;
            float f2 = (float)(k & 255) / 255.0F;
            i = (int)((float)i + Math.max(f, Math.max(f1, f2)) * 255.0F);
            aint[0] = (int)((float)aint[0] + f * 255.0F);
            aint[1] = (int)((float)aint[1] + f1 * 255.0F);
            aint[2] = (int)((float)aint[2] + f2 * 255.0F);
            ++j;
         }

         for(DyeItem dyeitem : dyes) {
            float[] afloat = dyeitem.getDyeColor().getColorComponentValues();
            int i2 = (int)(afloat[0] * 255.0F);
            int l = (int)(afloat[1] * 255.0F);
            int i1 = (int)(afloat[2] * 255.0F);
            i += Math.max(i2, Math.max(l, i1));
            aint[0] += i2;
            aint[1] += l;
            aint[2] += i1;
            ++j;
         }
      }

      if (idyeablearmoritem == null) {
         return ItemStack.EMPTY;
      } else {
         int j1 = aint[0] / j;
         int k1 = aint[1] / j;
         int l1 = aint[2] / j;
         float f3 = (float)i / (float)j;
         float f4 = (float)Math.max(j1, Math.max(k1, l1));
         j1 = (int)((float)j1 * f3 / f4);
         k1 = (int)((float)k1 * f3 / f4);
         l1 = (int)((float)l1 * f3 / f4);
         int j2 = (j1 << 8) + k1;
         j2 = (j2 << 8) + l1;
         idyeablearmoritem.setColor(itemstack, j2);
         return itemstack;
      }
   }
}