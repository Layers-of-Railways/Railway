package net.minecraft.item.crafting;

import net.minecraft.block.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class StonecuttingRecipe extends SingleItemRecipe {
   public StonecuttingRecipe(ResourceLocation p_i50021_1_, String p_i50021_2_, Ingredient p_i50021_3_, ItemStack p_i50021_4_) {
      super(IRecipeType.STONECUTTING, IRecipeSerializer.STONECUTTING, p_i50021_1_, p_i50021_2_, p_i50021_3_, p_i50021_4_);
   }

   /**
    * Used to check if a recipe matches current crafting inventory
    */
   public boolean matches(IInventory inv, World worldIn) {
      return this.ingredient.test(inv.getStackInSlot(0));
   }

   public ItemStack getIcon() {
      return new ItemStack(Blocks.STONECUTTER);
   }
}