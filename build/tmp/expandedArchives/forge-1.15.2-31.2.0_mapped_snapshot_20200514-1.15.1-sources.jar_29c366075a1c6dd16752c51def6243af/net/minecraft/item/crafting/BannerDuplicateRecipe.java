package net.minecraft.item.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BannerDuplicateRecipe extends SpecialRecipe {
   public BannerDuplicateRecipe(ResourceLocation idIn) {
      super(idIn);
   }

   /**
    * Used to check if a recipe matches current crafting inventory
    */
   public boolean matches(CraftingInventory inv, World worldIn) {
      DyeColor dyecolor = null;
      ItemStack itemstack = null;
      ItemStack itemstack1 = null;

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack itemstack2 = inv.getStackInSlot(i);
         Item item = itemstack2.getItem();
         if (item instanceof BannerItem) {
            BannerItem banneritem = (BannerItem)item;
            if (dyecolor == null) {
               dyecolor = banneritem.getColor();
            } else if (dyecolor != banneritem.getColor()) {
               return false;
            }

            int j = BannerTileEntity.getPatterns(itemstack2);
            if (j > 6) {
               return false;
            }

            if (j > 0) {
               if (itemstack != null) {
                  return false;
               }

               itemstack = itemstack2;
            } else {
               if (itemstack1 != null) {
                  return false;
               }

               itemstack1 = itemstack2;
            }
         }
      }

      return itemstack != null && itemstack1 != null;
   }

   /**
    * Returns an Item that is the result of this recipe
    */
   public ItemStack getCraftingResult(CraftingInventory inv) {
      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack itemstack = inv.getStackInSlot(i);
         if (!itemstack.isEmpty()) {
            int j = BannerTileEntity.getPatterns(itemstack);
            if (j > 0 && j <= 6) {
               ItemStack itemstack1 = itemstack.copy();
               itemstack1.setCount(1);
               return itemstack1;
            }
         }
      }

      return ItemStack.EMPTY;
   }

   public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

      for(int i = 0; i < nonnulllist.size(); ++i) {
         ItemStack itemstack = inv.getStackInSlot(i);
         if (!itemstack.isEmpty()) {
            if (itemstack.hasContainerItem()) {
               nonnulllist.set(i, itemstack.getContainerItem());
            } else if (itemstack.hasTag() && BannerTileEntity.getPatterns(itemstack) > 0) {
               ItemStack itemstack1 = itemstack.copy();
               itemstack1.setCount(1);
               nonnulllist.set(i, itemstack1);
            }
         }
      }

      return nonnulllist;
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.CRAFTING_SPECIAL_BANNERDUPLICATE;
   }

   /**
    * Used to determine if this recipe can fit in a grid of the given width/height
    */
   public boolean canFit(int width, int height) {
      return width * height >= 2;
   }
}