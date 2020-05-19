package net.minecraft.item.crafting;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class CampfireCookingRecipe extends AbstractCookingRecipe {
   public CampfireCookingRecipe(ResourceLocation p_i50030_1_, String p_i50030_2_, Ingredient p_i50030_3_, ItemStack p_i50030_4_, float p_i50030_5_, int p_i50030_6_) {
      super(IRecipeType.CAMPFIRE_COOKING, p_i50030_1_, p_i50030_2_, p_i50030_3_, p_i50030_4_, p_i50030_5_, p_i50030_6_);
   }

   public ItemStack getIcon() {
      return new ItemStack(Blocks.CAMPFIRE);
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.CAMPFIRE_COOKING;
   }
}