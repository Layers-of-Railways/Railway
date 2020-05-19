package net.minecraft.inventory;

import java.util.Collections;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public interface IRecipeHolder {
   void setRecipeUsed(@Nullable IRecipe<?> recipe);

   @Nullable
   IRecipe<?> getRecipeUsed();

   default void onCrafting(PlayerEntity player) {
      IRecipe<?> irecipe = this.getRecipeUsed();
      if (irecipe != null && !irecipe.isDynamic()) {
         player.unlockRecipes(Collections.singleton(irecipe));
         this.setRecipeUsed((IRecipe<?>)null);
      }

   }

   default boolean canUseRecipe(World worldIn, ServerPlayerEntity player, IRecipe<?> recipe) {
      if (!recipe.isDynamic() && worldIn.getGameRules().getBoolean(GameRules.DO_LIMITED_CRAFTING) && !player.getRecipeBook().isUnlocked(recipe)) {
         return false;
      } else {
         this.setRecipeUsed(recipe);
         return true;
      }
   }
}