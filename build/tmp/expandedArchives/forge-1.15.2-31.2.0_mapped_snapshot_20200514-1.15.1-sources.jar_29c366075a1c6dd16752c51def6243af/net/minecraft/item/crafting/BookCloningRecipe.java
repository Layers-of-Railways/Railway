package net.minecraft.item.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BookCloningRecipe extends SpecialRecipe {
   public BookCloningRecipe(ResourceLocation idIn) {
      super(idIn);
   }

   /**
    * Used to check if a recipe matches current crafting inventory
    */
   public boolean matches(CraftingInventory inv, World worldIn) {
      int i = 0;
      ItemStack itemstack = ItemStack.EMPTY;

      for(int j = 0; j < inv.getSizeInventory(); ++j) {
         ItemStack itemstack1 = inv.getStackInSlot(j);
         if (!itemstack1.isEmpty()) {
            if (itemstack1.getItem() == Items.WRITTEN_BOOK) {
               if (!itemstack.isEmpty()) {
                  return false;
               }

               itemstack = itemstack1;
            } else {
               if (itemstack1.getItem() != Items.WRITABLE_BOOK) {
                  return false;
               }

               ++i;
            }
         }
      }

      return !itemstack.isEmpty() && itemstack.hasTag() && i > 0;
   }

   /**
    * Returns an Item that is the result of this recipe
    */
   public ItemStack getCraftingResult(CraftingInventory inv) {
      int i = 0;
      ItemStack itemstack = ItemStack.EMPTY;

      for(int j = 0; j < inv.getSizeInventory(); ++j) {
         ItemStack itemstack1 = inv.getStackInSlot(j);
         if (!itemstack1.isEmpty()) {
            if (itemstack1.getItem() == Items.WRITTEN_BOOK) {
               if (!itemstack.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               itemstack = itemstack1;
            } else {
               if (itemstack1.getItem() != Items.WRITABLE_BOOK) {
                  return ItemStack.EMPTY;
               }

               ++i;
            }
         }
      }

      if (!itemstack.isEmpty() && itemstack.hasTag() && i >= 1 && WrittenBookItem.getGeneration(itemstack) < 2) {
         ItemStack itemstack2 = new ItemStack(Items.WRITTEN_BOOK, i);
         CompoundNBT compoundnbt = itemstack.getTag().copy();
         compoundnbt.putInt("generation", WrittenBookItem.getGeneration(itemstack) + 1);
         itemstack2.setTag(compoundnbt);
         return itemstack2;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

      for(int i = 0; i < nonnulllist.size(); ++i) {
         ItemStack itemstack = inv.getStackInSlot(i);
         if (itemstack.hasContainerItem()) {
            nonnulllist.set(i, itemstack.getContainerItem());
         } else if (itemstack.getItem() instanceof WrittenBookItem) {
            ItemStack itemstack1 = itemstack.copy();
            itemstack1.setCount(1);
            nonnulllist.set(i, itemstack1);
            break;
         }
      }

      return nonnulllist;
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.CRAFTING_SPECIAL_BOOKCLONING;
   }

   /**
    * Used to determine if this recipe can fit in a grid of the given width/height
    */
   public boolean canFit(int width, int height) {
      return width >= 3 && height >= 3;
   }
}