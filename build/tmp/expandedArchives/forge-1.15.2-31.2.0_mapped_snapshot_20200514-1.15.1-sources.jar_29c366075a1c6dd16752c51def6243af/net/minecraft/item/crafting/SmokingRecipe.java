package net.minecraft.item.crafting;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SmokingRecipe extends AbstractCookingRecipe {
   public SmokingRecipe(ResourceLocation p_i50022_1_, String p_i50022_2_, Ingredient p_i50022_3_, ItemStack p_i50022_4_, float p_i50022_5_, int p_i50022_6_) {
      super(IRecipeType.SMOKING, p_i50022_1_, p_i50022_2_, p_i50022_3_, p_i50022_4_, p_i50022_5_, p_i50022_6_);
   }

   public ItemStack getIcon() {
      return new ItemStack(Blocks.SMOKER);
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.SMOKING;
   }
}