package net.minecraft.item.crafting;

import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.potion.Effect;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class SuspiciousStewRecipe extends SpecialRecipe {
   public SuspiciousStewRecipe(ResourceLocation idIn) {
      super(idIn);
   }

   /**
    * Used to check if a recipe matches current crafting inventory
    */
   public boolean matches(CraftingInventory inv, World worldIn) {
      boolean flag = false;
      boolean flag1 = false;
      boolean flag2 = false;
      boolean flag3 = false;

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack itemstack = inv.getStackInSlot(i);
         if (!itemstack.isEmpty()) {
            if (itemstack.getItem() == Blocks.BROWN_MUSHROOM.asItem() && !flag2) {
               flag2 = true;
            } else if (itemstack.getItem() == Blocks.RED_MUSHROOM.asItem() && !flag1) {
               flag1 = true;
            } else if (itemstack.getItem().isIn(ItemTags.SMALL_FLOWERS) && !flag) {
               flag = true;
            } else {
               if (itemstack.getItem() != Items.BOWL || flag3) {
                  return false;
               }

               flag3 = true;
            }
         }
      }

      return flag && flag2 && flag1 && flag3;
   }

   /**
    * Returns an Item that is the result of this recipe
    */
   public ItemStack getCraftingResult(CraftingInventory inv) {
      ItemStack itemstack = ItemStack.EMPTY;

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack itemstack1 = inv.getStackInSlot(i);
         if (!itemstack1.isEmpty() && itemstack1.getItem().isIn(ItemTags.SMALL_FLOWERS)) {
            itemstack = itemstack1;
            break;
         }
      }

      ItemStack itemstack2 = new ItemStack(Items.SUSPICIOUS_STEW, 1);
      if (itemstack.getItem() instanceof BlockItem && ((BlockItem)itemstack.getItem()).getBlock() instanceof FlowerBlock) {
         FlowerBlock flowerblock = (FlowerBlock)((BlockItem)itemstack.getItem()).getBlock();
         Effect effect = flowerblock.getStewEffect();
         SuspiciousStewItem.addEffect(itemstack2, effect, flowerblock.getStewEffectDuration());
      }

      return itemstack2;
   }

   /**
    * Used to determine if this recipe can fit in a grid of the given width/height
    */
   public boolean canFit(int width, int height) {
      return width >= 2 && height >= 2;
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.CRAFTING_SPECIAL_SUSPICIOUSSTEW;
   }
}