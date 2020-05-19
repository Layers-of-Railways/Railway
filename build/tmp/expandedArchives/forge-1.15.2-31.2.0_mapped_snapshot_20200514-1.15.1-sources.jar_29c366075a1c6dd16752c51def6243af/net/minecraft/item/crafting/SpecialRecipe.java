package net.minecraft.item.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class SpecialRecipe implements ICraftingRecipe {
   private final ResourceLocation id;

   public SpecialRecipe(ResourceLocation idIn) {
      this.id = idIn;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   /**
    * If true, this recipe does not appear in the recipe book and does not respect recipe unlocking (and the
    * doLimitedCrafting gamerule)
    */
   public boolean isDynamic() {
      return true;
   }

   /**
    * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
    * possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
    */
   public ItemStack getRecipeOutput() {
      return ItemStack.EMPTY;
   }
}