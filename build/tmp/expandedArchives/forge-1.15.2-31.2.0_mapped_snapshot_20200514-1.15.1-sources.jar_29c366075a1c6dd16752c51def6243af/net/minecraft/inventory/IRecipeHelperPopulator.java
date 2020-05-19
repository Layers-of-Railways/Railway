package net.minecraft.inventory;

import net.minecraft.item.crafting.RecipeItemHelper;

public interface IRecipeHelperPopulator {
   void fillStackedContents(RecipeItemHelper helper);
}