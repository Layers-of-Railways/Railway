package net.minecraft.item.crafting;

import net.minecraft.inventory.CraftingInventory;

public interface ICraftingRecipe extends IRecipe<CraftingInventory> {
   default IRecipeType<?> getType() {
      return IRecipeType.CRAFTING;
   }
}