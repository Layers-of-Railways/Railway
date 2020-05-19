package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class FireworkStarFadeRecipe extends SpecialRecipe {
   private static final Ingredient INGREDIENT_FIREWORK_STAR = Ingredient.fromItems(Items.FIREWORK_STAR);

   public FireworkStarFadeRecipe(ResourceLocation idIn) {
      super(idIn);
   }

   /**
    * Used to check if a recipe matches current crafting inventory
    */
   public boolean matches(CraftingInventory inv, World worldIn) {
      boolean flag = false;
      boolean flag1 = false;

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack itemstack = inv.getStackInSlot(i);
         if (!itemstack.isEmpty()) {
            if (itemstack.getItem() instanceof DyeItem) {
               flag = true;
            } else {
               if (!INGREDIENT_FIREWORK_STAR.test(itemstack)) {
                  return false;
               }

               if (flag1) {
                  return false;
               }

               flag1 = true;
            }
         }
      }

      return flag1 && flag;
   }

   /**
    * Returns an Item that is the result of this recipe
    */
   public ItemStack getCraftingResult(CraftingInventory inv) {
      List<Integer> list = Lists.newArrayList();
      ItemStack itemstack = null;

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack itemstack1 = inv.getStackInSlot(i);
         Item item = itemstack1.getItem();
         if (item instanceof DyeItem) {
            list.add(((DyeItem)item).getDyeColor().getFireworkColor());
         } else if (INGREDIENT_FIREWORK_STAR.test(itemstack1)) {
            itemstack = itemstack1.copy();
            itemstack.setCount(1);
         }
      }

      if (itemstack != null && !list.isEmpty()) {
         itemstack.getOrCreateChildTag("Explosion").putIntArray("FadeColors", list);
         return itemstack;
      } else {
         return ItemStack.EMPTY;
      }
   }

   /**
    * Used to determine if this recipe can fit in a grid of the given width/height
    */
   public boolean canFit(int width, int height) {
      return width * height >= 2;
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.CRAFTING_SPECIAL_FIREWORK_STAR_FADE;
   }
}