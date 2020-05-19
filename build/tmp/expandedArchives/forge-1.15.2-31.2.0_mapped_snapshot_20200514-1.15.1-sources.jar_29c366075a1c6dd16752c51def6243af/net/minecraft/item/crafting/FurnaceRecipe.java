package net.minecraft.item.crafting;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class FurnaceRecipe extends AbstractCookingRecipe {
   public FurnaceRecipe(ResourceLocation idIn, String groupIn, Ingredient ingredientIn, ItemStack resultIn, float experienceIn, int cookTimeIn) {
      super(IRecipeType.SMELTING, idIn, groupIn, ingredientIn, resultIn, experienceIn, cookTimeIn);
   }

   public ItemStack getIcon() {
      return new ItemStack(Blocks.FURNACE);
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.SMELTING;
   }
}