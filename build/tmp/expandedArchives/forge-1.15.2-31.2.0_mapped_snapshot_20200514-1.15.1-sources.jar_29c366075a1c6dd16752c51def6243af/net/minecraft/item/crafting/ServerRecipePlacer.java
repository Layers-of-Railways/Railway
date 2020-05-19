package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPlaceGhostRecipePacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerRecipePlacer<C extends IInventory> implements IRecipePlacer<Integer> {
   protected static final Logger LOGGER = LogManager.getLogger();
   protected final RecipeItemHelper recipeItemHelper = new RecipeItemHelper();
   protected PlayerInventory playerInventory;
   protected RecipeBookContainer<C> recipeBookContainer;

   public ServerRecipePlacer(RecipeBookContainer<C> p_i50752_1_) {
      this.recipeBookContainer = p_i50752_1_;
   }

   public void place(ServerPlayerEntity player, @Nullable IRecipe<C> recipe, boolean placeAll) {
      if (recipe != null && player.getRecipeBook().isUnlocked(recipe)) {
         this.playerInventory = player.inventory;
         if (this.placeIntoInventory() || player.isCreative()) {
            this.recipeItemHelper.clear();
            player.inventory.accountStacks(this.recipeItemHelper);
            this.recipeBookContainer.fillStackedContents(this.recipeItemHelper);
            if (this.recipeItemHelper.canCraft(recipe, (IntList)null)) {
               this.tryPlaceRecipe(recipe, placeAll);
            } else {
               this.clear();
               player.connection.sendPacket(new SPlaceGhostRecipePacket(player.openContainer.windowId, recipe));
            }

            player.inventory.markDirty();
         }
      }
   }

   protected void clear() {
      for(int i = 0; i < this.recipeBookContainer.getWidth() * this.recipeBookContainer.getHeight() + 1; ++i) {
         if (i != this.recipeBookContainer.getOutputSlot() || !(this.recipeBookContainer instanceof WorkbenchContainer) && !(this.recipeBookContainer instanceof PlayerContainer)) {
            this.giveToPlayer(i);
         }
      }

      this.recipeBookContainer.clear();
   }

   protected void giveToPlayer(int slotIn) {
      ItemStack itemstack = this.recipeBookContainer.getSlot(slotIn).getStack();
      if (!itemstack.isEmpty()) {
         for(; itemstack.getCount() > 0; this.recipeBookContainer.getSlot(slotIn).decrStackSize(1)) {
            int i = this.playerInventory.storeItemStack(itemstack);
            if (i == -1) {
               i = this.playerInventory.getFirstEmptyStack();
            }

            ItemStack itemstack1 = itemstack.copy();
            itemstack1.setCount(1);
            if (!this.playerInventory.add(i, itemstack1)) {
               LOGGER.error("Can't find any space for item in the inventory");
            }
         }

      }
   }

   protected void tryPlaceRecipe(IRecipe<C> recipe, boolean placeAll) {
      boolean flag = this.recipeBookContainer.matches(recipe);
      int i = this.recipeItemHelper.getBiggestCraftableStack(recipe, (IntList)null);
      if (flag) {
         for(int j = 0; j < this.recipeBookContainer.getHeight() * this.recipeBookContainer.getWidth() + 1; ++j) {
            if (j != this.recipeBookContainer.getOutputSlot()) {
               ItemStack itemstack = this.recipeBookContainer.getSlot(j).getStack();
               if (!itemstack.isEmpty() && Math.min(i, itemstack.getMaxStackSize()) < itemstack.getCount() + 1) {
                  return;
               }
            }
         }
      }

      int j1 = this.getMaxAmount(placeAll, i, flag);
      IntList intlist = new IntArrayList();
      if (this.recipeItemHelper.canCraft(recipe, intlist, j1)) {
         int k = j1;

         for(int l : intlist) {
            int i1 = RecipeItemHelper.unpack(l).getMaxStackSize();
            if (i1 < k) {
               k = i1;
            }
         }

         if (this.recipeItemHelper.canCraft(recipe, intlist, k)) {
            this.clear();
            this.placeRecipe(this.recipeBookContainer.getWidth(), this.recipeBookContainer.getHeight(), this.recipeBookContainer.getOutputSlot(), recipe, intlist.iterator(), k);
         }
      }

   }

   public void setSlotContents(Iterator<Integer> ingredients, int slotIn, int maxAmount, int y, int x) {
      Slot slot = this.recipeBookContainer.getSlot(slotIn);
      ItemStack itemstack = RecipeItemHelper.unpack(ingredients.next());
      if (!itemstack.isEmpty()) {
         for(int i = 0; i < maxAmount; ++i) {
            this.consumeIngredient(slot, itemstack);
         }
      }

   }

   protected int getMaxAmount(boolean placeAll, int maxPossible, boolean recipeMatches) {
      int i = 1;
      if (placeAll) {
         i = maxPossible;
      } else if (recipeMatches) {
         i = 64;

         for(int j = 0; j < this.recipeBookContainer.getWidth() * this.recipeBookContainer.getHeight() + 1; ++j) {
            if (j != this.recipeBookContainer.getOutputSlot()) {
               ItemStack itemstack = this.recipeBookContainer.getSlot(j).getStack();
               if (!itemstack.isEmpty() && i > itemstack.getCount()) {
                  i = itemstack.getCount();
               }
            }
         }

         if (i < 64) {
            ++i;
         }
      }

      return i;
   }

   protected void consumeIngredient(Slot slotToFill, ItemStack ingredientIn) {
      int i = this.playerInventory.findSlotMatchingUnusedItem(ingredientIn);
      if (i != -1) {
         ItemStack itemstack = this.playerInventory.getStackInSlot(i).copy();
         if (!itemstack.isEmpty()) {
            if (itemstack.getCount() > 1) {
               this.playerInventory.decrStackSize(i, 1);
            } else {
               this.playerInventory.removeStackFromSlot(i);
            }

            itemstack.setCount(1);
            if (slotToFill.getStack().isEmpty()) {
               slotToFill.putStack(itemstack);
            } else {
               slotToFill.getStack().grow(1);
            }

         }
      }
   }

   /**
    * Places the output of the recipe into the player's inventory.
    */
   private boolean placeIntoInventory() {
      List<ItemStack> list = Lists.newArrayList();
      int i = this.getEmptyPlayerSlots();

      for(int j = 0; j < this.recipeBookContainer.getWidth() * this.recipeBookContainer.getHeight() + 1; ++j) {
         if (j != this.recipeBookContainer.getOutputSlot()) {
            ItemStack itemstack = this.recipeBookContainer.getSlot(j).getStack().copy();
            if (!itemstack.isEmpty()) {
               int k = this.playerInventory.storeItemStack(itemstack);
               if (k == -1 && list.size() <= i) {
                  for(ItemStack itemstack1 : list) {
                     if (itemstack1.isItemEqual(itemstack) && itemstack1.getCount() != itemstack1.getMaxStackSize() && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize()) {
                        itemstack1.grow(itemstack.getCount());
                        itemstack.setCount(0);
                        break;
                     }
                  }

                  if (!itemstack.isEmpty()) {
                     if (list.size() >= i) {
                        return false;
                     }

                     list.add(itemstack);
                  }
               } else if (k == -1) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private int getEmptyPlayerSlots() {
      int i = 0;

      for(ItemStack itemstack : this.playerInventory.mainInventory) {
         if (itemstack.isEmpty()) {
            ++i;
         }
      }

      return i;
   }
}