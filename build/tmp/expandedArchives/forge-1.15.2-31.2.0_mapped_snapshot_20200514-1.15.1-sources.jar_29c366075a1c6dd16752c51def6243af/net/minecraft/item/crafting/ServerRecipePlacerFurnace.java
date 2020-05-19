package net.minecraft.item.crafting;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class ServerRecipePlacerFurnace<C extends IInventory> extends ServerRecipePlacer<C> {
   private boolean matches;

   public ServerRecipePlacerFurnace(RecipeBookContainer<C> p_i50751_1_) {
      super(p_i50751_1_);
   }

   protected void tryPlaceRecipe(IRecipe<C> recipe, boolean placeAll) {
      this.matches = this.recipeBookContainer.matches(recipe);
      int i = this.recipeItemHelper.getBiggestCraftableStack(recipe, (IntList)null);
      if (this.matches) {
         ItemStack itemstack = this.recipeBookContainer.getSlot(0).getStack();
         if (itemstack.isEmpty() || i <= itemstack.getCount()) {
            return;
         }
      }

      int j = this.getMaxAmount(placeAll, i, this.matches);
      IntList intlist = new IntArrayList();
      if (this.recipeItemHelper.canCraft(recipe, intlist, j)) {
         if (!this.matches) {
            this.giveToPlayer(this.recipeBookContainer.getOutputSlot());
            this.giveToPlayer(0);
         }

         this.func_201516_a(j, intlist);
      }
   }

   protected void clear() {
      this.giveToPlayer(this.recipeBookContainer.getOutputSlot());
      super.clear();
   }

   protected void func_201516_a(int p_201516_1_, IntList p_201516_2_) {
      Iterator<Integer> iterator = p_201516_2_.iterator();
      Slot slot = this.recipeBookContainer.getSlot(0);
      ItemStack itemstack = RecipeItemHelper.unpack(iterator.next());
      if (!itemstack.isEmpty()) {
         int i = Math.min(itemstack.getMaxStackSize(), p_201516_1_);
         if (this.matches) {
            i -= slot.getStack().getCount();
         }

         for(int j = 0; j < i; ++j) {
            this.consumeIngredient(slot, itemstack);
         }

      }
   }
}