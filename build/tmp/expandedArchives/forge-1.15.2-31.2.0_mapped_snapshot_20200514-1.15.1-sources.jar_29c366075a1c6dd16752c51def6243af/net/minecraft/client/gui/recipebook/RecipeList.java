package net.minecraft.client.gui.recipebook;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeList {
   private final List<IRecipe<?>> recipes = Lists.newArrayList();
   private final Set<IRecipe<?>> craftable = Sets.newHashSet();
   private final Set<IRecipe<?>> canFit = Sets.newHashSet();
   private final Set<IRecipe<?>> inBook = Sets.newHashSet();
   private boolean singleResultItem = true;

   /**
    * Checks if recipebook is not empty
    */
   public boolean isNotEmpty() {
      return !this.inBook.isEmpty();
   }

   public void updateKnownRecipes(RecipeBook book) {
      for(IRecipe<?> irecipe : this.recipes) {
         if (book.isUnlocked(irecipe)) {
            this.inBook.add(irecipe);
         }
      }

   }

   public void canCraft(RecipeItemHelper handler, int width, int height, RecipeBook book) {
      for(int i = 0; i < this.recipes.size(); ++i) {
         IRecipe<?> irecipe = this.recipes.get(i);
         boolean flag = irecipe.canFit(width, height) && book.isUnlocked(irecipe);
         if (flag) {
            this.canFit.add(irecipe);
         } else {
            this.canFit.remove(irecipe);
         }

         if (flag && handler.canCraft(irecipe, (IntList)null)) {
            this.craftable.add(irecipe);
         } else {
            this.craftable.remove(irecipe);
         }
      }

   }

   public boolean isCraftable(IRecipe<?> recipe) {
      return this.craftable.contains(recipe);
   }

   public boolean containsCraftableRecipes() {
      return !this.craftable.isEmpty();
   }

   public boolean containsValidRecipes() {
      return !this.canFit.isEmpty();
   }

   public List<IRecipe<?>> getRecipes() {
      return this.recipes;
   }

   public List<IRecipe<?>> getRecipes(boolean onlyCraftable) {
      List<IRecipe<?>> list = Lists.newArrayList();
      Set<IRecipe<?>> set = onlyCraftable ? this.craftable : this.canFit;

      for(IRecipe<?> irecipe : this.recipes) {
         if (set.contains(irecipe)) {
            list.add(irecipe);
         }
      }

      return list;
   }

   public List<IRecipe<?>> getDisplayRecipes(boolean onlyCraftable) {
      List<IRecipe<?>> list = Lists.newArrayList();

      for(IRecipe<?> irecipe : this.recipes) {
         if (this.canFit.contains(irecipe) && this.craftable.contains(irecipe) == onlyCraftable) {
            list.add(irecipe);
         }
      }

      return list;
   }

   public void add(IRecipe<?> recipe) {
      this.recipes.add(recipe);
      if (this.singleResultItem) {
         ItemStack itemstack = this.recipes.get(0).getRecipeOutput();
         ItemStack itemstack1 = recipe.getRecipeOutput();
         this.singleResultItem = ItemStack.areItemsEqual(itemstack, itemstack1) && ItemStack.areItemStackTagsEqual(itemstack, itemstack1);
      }

   }

   public boolean hasSingleResultItem() {
      return this.singleResultItem;
   }
}