package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

public class RecipeItemHelper {
   public final Int2IntMap itemToCount = new Int2IntOpenHashMap();

   public void accountPlainStack(ItemStack stack) {
      if (!stack.isDamaged() && !stack.isEnchanted() && !stack.hasDisplayName()) {
         this.accountStack(stack);
      }

   }

   public void accountStack(ItemStack stack) {
      this.func_221264_a(stack, 64);
   }

   public void func_221264_a(ItemStack stack, int minCount) {
      if (!stack.isEmpty()) {
         int i = pack(stack);
         int j = Math.min(minCount, stack.getCount());
         this.increment(i, j);
      }

   }

   public static int pack(ItemStack stack) {
      return Registry.ITEM.getId(stack.getItem());
   }

   private boolean containsItem(int packedItem) {
      return this.itemToCount.get(packedItem) > 0;
   }

   private int tryTake(int packedItem, int maximum) {
      int i = this.itemToCount.get(packedItem);
      if (i >= maximum) {
         this.itemToCount.put(packedItem, i - maximum);
         return packedItem;
      } else {
         return 0;
      }
   }

   private void increment(int packedItem, int amount) {
      this.itemToCount.put(packedItem, this.itemToCount.get(packedItem) + amount);
   }

   public boolean canCraft(IRecipe<?> recipe, @Nullable IntList packedItemList) {
      return this.canCraft(recipe, packedItemList, 1);
   }

   public boolean canCraft(IRecipe<?> recipe, @Nullable IntList packedItemList, int maxAmount) {
      return (new RecipeItemHelper.RecipePicker(recipe)).tryPick(maxAmount, packedItemList);
   }

   public int getBiggestCraftableStack(IRecipe<?> recipe, @Nullable IntList packedItemList) {
      return this.getBiggestCraftableStack(recipe, Integer.MAX_VALUE, packedItemList);
   }

   public int getBiggestCraftableStack(IRecipe<?> recipe, int maxAmount, @Nullable IntList packedItemList) {
      return (new RecipeItemHelper.RecipePicker(recipe)).tryPickAll(maxAmount, packedItemList);
   }

   public static ItemStack unpack(int packedItem) {
      return packedItem == 0 ? ItemStack.EMPTY : new ItemStack(Item.getItemById(packedItem));
   }

   public void clear() {
      this.itemToCount.clear();
   }

   class RecipePicker {
      private final IRecipe<?> recipe;
      private final List<Ingredient> ingredients = Lists.newArrayList();
      private final int ingredientCount;
      private final int[] possessedIngredientStacks;
      private final int possessedIngredientStackCount;
      private final BitSet data;
      private final IntList path = new IntArrayList();

      public RecipePicker(IRecipe<?> recipeIn) {
         this.recipe = recipeIn;
         this.ingredients.addAll(recipeIn.getIngredients());
         this.ingredients.removeIf(Ingredient::hasNoMatchingItems);
         this.ingredientCount = this.ingredients.size();
         this.possessedIngredientStacks = this.getUniqueAvailIngredientItems();
         this.possessedIngredientStackCount = this.possessedIngredientStacks.length;
         this.data = new BitSet(this.ingredientCount + this.possessedIngredientStackCount + this.ingredientCount + this.ingredientCount * this.possessedIngredientStackCount);

         for(int i = 0; i < this.ingredients.size(); ++i) {
            IntList intlist = this.ingredients.get(i).getValidItemStacksPacked();

            for(int j = 0; j < this.possessedIngredientStackCount; ++j) {
               if (intlist.contains(this.possessedIngredientStacks[j])) {
                  this.data.set(this.getIndex(true, j, i));
               }
            }
         }

      }

      public boolean tryPick(int maxAmount, @Nullable IntList listIn) {
         if (maxAmount <= 0) {
            return true;
         } else {
            int i;
            for(i = 0; this.dfs(maxAmount); ++i) {
               RecipeItemHelper.this.tryTake(this.possessedIngredientStacks[this.path.getInt(0)], maxAmount);
               int j = this.path.size() - 1;
               this.setSatisfied(this.path.getInt(j));

               for(int k = 0; k < j; ++k) {
                  this.toggleResidual((k & 1) == 0, this.path.get(k), this.path.get(k + 1));
               }

               this.path.clear();
               this.data.clear(0, this.ingredientCount + this.possessedIngredientStackCount);
            }

            boolean flag = i == this.ingredientCount;
            boolean flag1 = flag && listIn != null;
            if (flag1) {
               listIn.clear();
            }

            this.data.clear(0, this.ingredientCount + this.possessedIngredientStackCount + this.ingredientCount);
            int l = 0;
            List<Ingredient> list = this.recipe.getIngredients();

            for(int i1 = 0; i1 < list.size(); ++i1) {
               if (flag1 && list.get(i1).hasNoMatchingItems()) {
                  listIn.add(0);
               } else {
                  for(int j1 = 0; j1 < this.possessedIngredientStackCount; ++j1) {
                     if (this.hasResidual(false, l, j1)) {
                        this.toggleResidual(true, j1, l);
                        RecipeItemHelper.this.increment(this.possessedIngredientStacks[j1], maxAmount);
                        if (flag1) {
                           listIn.add(this.possessedIngredientStacks[j1]);
                        }
                     }
                  }

                  ++l;
               }
            }

            return flag;
         }
      }

      private int[] getUniqueAvailIngredientItems() {
         IntCollection intcollection = new IntAVLTreeSet();

         for(Ingredient ingredient : this.ingredients) {
            intcollection.addAll(ingredient.getValidItemStacksPacked());
         }

         IntIterator intiterator = intcollection.iterator();

         while(intiterator.hasNext()) {
            if (!RecipeItemHelper.this.containsItem(intiterator.nextInt())) {
               intiterator.remove();
            }
         }

         return intcollection.toIntArray();
      }

      private boolean dfs(int amount) {
         int i = this.possessedIngredientStackCount;

         for(int j = 0; j < i; ++j) {
            if (RecipeItemHelper.this.itemToCount.get(this.possessedIngredientStacks[j]) >= amount) {
               this.visit(false, j);

               while(!this.path.isEmpty()) {
                  int k = this.path.size();
                  boolean flag = (k & 1) == 1;
                  int l = this.path.getInt(k - 1);
                  if (!flag && !this.isSatisfied(l)) {
                     break;
                  }

                  int i1 = flag ? this.ingredientCount : i;

                  for(int j1 = 0; j1 < i1; ++j1) {
                     if (!this.hasVisited(flag, j1) && this.hasConnection(flag, l, j1) && this.hasResidual(flag, l, j1)) {
                        this.visit(flag, j1);
                        break;
                     }
                  }

                  int k1 = this.path.size();
                  if (k1 == k) {
                     this.path.removeInt(k1 - 1);
                  }
               }

               if (!this.path.isEmpty()) {
                  return true;
               }
            }
         }

         return false;
      }

      private boolean isSatisfied(int p_194091_1_) {
         return this.data.get(this.getSatisfiedIndex(p_194091_1_));
      }

      private void setSatisfied(int p_194096_1_) {
         this.data.set(this.getSatisfiedIndex(p_194096_1_));
      }

      private int getSatisfiedIndex(int p_194094_1_) {
         return this.ingredientCount + this.possessedIngredientStackCount + p_194094_1_;
      }

      private boolean hasConnection(boolean p_194093_1_, int p_194093_2_, int p_194093_3_) {
         return this.data.get(this.getIndex(p_194093_1_, p_194093_2_, p_194093_3_));
      }

      private boolean hasResidual(boolean p_194100_1_, int p_194100_2_, int p_194100_3_) {
         return p_194100_1_ != this.data.get(1 + this.getIndex(p_194100_1_, p_194100_2_, p_194100_3_));
      }

      private void toggleResidual(boolean p_194089_1_, int p_194089_2_, int p_194089_3_) {
         this.data.flip(1 + this.getIndex(p_194089_1_, p_194089_2_, p_194089_3_));
      }

      private int getIndex(boolean p_194095_1_, int p_194095_2_, int p_194095_3_) {
         int i = p_194095_1_ ? p_194095_2_ * this.ingredientCount + p_194095_3_ : p_194095_3_ * this.ingredientCount + p_194095_2_;
         return this.ingredientCount + this.possessedIngredientStackCount + this.ingredientCount + 2 * i;
      }

      private void visit(boolean p_194088_1_, int p_194088_2_) {
         this.data.set(this.getVisitedIndex(p_194088_1_, p_194088_2_));
         this.path.add(p_194088_2_);
      }

      private boolean hasVisited(boolean p_194101_1_, int p_194101_2_) {
         return this.data.get(this.getVisitedIndex(p_194101_1_, p_194101_2_));
      }

      private int getVisitedIndex(boolean p_194099_1_, int p_194099_2_) {
         return (p_194099_1_ ? 0 : this.ingredientCount) + p_194099_2_;
      }

      public int tryPickAll(int p_194102_1_, @Nullable IntList list) {
         int i = 0;
         int j = Math.min(p_194102_1_, this.getMinIngredientCount()) + 1;

         while(true) {
            int k = (i + j) / 2;
            if (this.tryPick(k, (IntList)null)) {
               if (j - i <= 1) {
                  if (k > 0) {
                     this.tryPick(k, list);
                  }

                  return k;
               }

               i = k;
            } else {
               j = k;
            }
         }
      }

      private int getMinIngredientCount() {
         int i = Integer.MAX_VALUE;

         for(Ingredient ingredient : this.ingredients) {
            int j = 0;

            for(int k : ingredient.getValidItemStacksPacked()) {
               j = Math.max(j, RecipeItemHelper.this.itemToCount.get(k));
            }

            if (i > 0) {
               i = Math.min(i, j);
            }
         }

         return i;
      }
   }
}