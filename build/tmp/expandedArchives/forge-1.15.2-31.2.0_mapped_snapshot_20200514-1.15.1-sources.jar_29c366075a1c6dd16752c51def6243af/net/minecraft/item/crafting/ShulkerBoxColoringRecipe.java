package net.minecraft.item.crafting;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ShulkerBoxColoringRecipe extends SpecialRecipe {
   public ShulkerBoxColoringRecipe(ResourceLocation idIn) {
      super(idIn);
   }

   /**
    * Used to check if a recipe matches current crafting inventory
    */
   public boolean matches(CraftingInventory inv, World worldIn) {
      int i = 0;
      int j = 0;

      for(int k = 0; k < inv.getSizeInventory(); ++k) {
         ItemStack itemstack = inv.getStackInSlot(k);
         if (!itemstack.isEmpty()) {
            if (Block.getBlockFromItem(itemstack.getItem()) instanceof ShulkerBoxBlock) {
               ++i;
            } else {
               if (!itemstack.getItem().isIn(net.minecraftforge.common.Tags.Items.DYES)) {
                  return false;
               }

               ++j;
            }

            if (j > 1 || i > 1) {
               return false;
            }
         }
      }

      return i == 1 && j == 1;
   }

   /**
    * Returns an Item that is the result of this recipe
    */
   public ItemStack getCraftingResult(CraftingInventory inv) {
      ItemStack itemstack = ItemStack.EMPTY;
      net.minecraft.item.DyeColor dyecolor = net.minecraft.item.DyeColor.WHITE;

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack itemstack1 = inv.getStackInSlot(i);
         if (!itemstack1.isEmpty()) {
            Item item = itemstack1.getItem();
            if (Block.getBlockFromItem(item) instanceof ShulkerBoxBlock) {
               itemstack = itemstack1;
            } else {
               net.minecraft.item.DyeColor tmp = net.minecraft.item.DyeColor.getColor(itemstack1);
               if (tmp != null) dyecolor = tmp;
            }
         }
      }

      ItemStack itemstack2 = ShulkerBoxBlock.getColoredItemStack(dyecolor);
      if (itemstack.hasTag()) {
         itemstack2.setTag(itemstack.getTag().copy());
      }

      return itemstack2;
   }

   /**
    * Used to determine if this recipe can fit in a grid of the given width/height
    */
   public boolean canFit(int width, int height) {
      return width * height >= 2;
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.CRAFTING_SPECIAL_SHULKERBOXCOLORING;
   }
}