package net.minecraft.item.crafting;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class BlastingRecipe extends AbstractCookingRecipe {
   public BlastingRecipe(ResourceLocation p_i50031_1_, String p_i50031_2_, Ingredient p_i50031_3_, ItemStack p_i50031_4_, float p_i50031_5_, int p_i50031_6_) {
      super(IRecipeType.BLASTING, p_i50031_1_, p_i50031_2_, p_i50031_3_, p_i50031_4_, p_i50031_5_, p_i50031_6_);
   }

   public ItemStack getIcon() {
      return new ItemStack(Blocks.BLAST_FURNACE);
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.BLASTING;
   }
}