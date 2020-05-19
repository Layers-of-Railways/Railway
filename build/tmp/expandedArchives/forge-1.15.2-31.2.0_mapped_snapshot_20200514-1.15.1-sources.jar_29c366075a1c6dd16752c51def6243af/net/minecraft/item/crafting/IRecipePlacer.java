package net.minecraft.item.crafting;

import java.util.Iterator;
import net.minecraft.util.math.MathHelper;

public interface IRecipePlacer<T> {
   default void placeRecipe(int width, int height, int outputSlot, IRecipe<?> recipe, Iterator<T> ingredients, int maxAmount) {
      int i = width;
      int j = height;
      if (recipe instanceof net.minecraftforge.common.crafting.IShapedRecipe) {
         net.minecraftforge.common.crafting.IShapedRecipe shapedrecipe = (net.minecraftforge.common.crafting.IShapedRecipe)recipe;
         i = shapedrecipe.getRecipeWidth();
         j = shapedrecipe.getRecipeHeight();
      }

      int k1 = 0;

      for(int k = 0; k < height; ++k) {
         if (k1 == outputSlot) {
            ++k1;
         }

         boolean flag = (float)j < (float)height / 2.0F;
         int l = MathHelper.floor((float)height / 2.0F - (float)j / 2.0F);
         if (flag && l > k) {
            k1 += width;
            ++k;
         }

         for(int i1 = 0; i1 < width; ++i1) {
            if (!ingredients.hasNext()) {
               return;
            }

            flag = (float)i < (float)width / 2.0F;
            l = MathHelper.floor((float)width / 2.0F - (float)i / 2.0F);
            int j1 = i;
            boolean flag1 = i1 < i;
            if (flag) {
               j1 = l + i;
               flag1 = l <= i1 && i1 < l + i;
            }

            if (flag1) {
               this.setSlotContents(ingredients, k1, maxAmount, k, i1);
            } else if (j1 == i1) {
               k1 += width - i1;
               break;
            }

            ++k1;
         }
      }

   }

   void setSlotContents(Iterator<T> ingredients, int slotIn, int maxAmount, int y, int x);
}