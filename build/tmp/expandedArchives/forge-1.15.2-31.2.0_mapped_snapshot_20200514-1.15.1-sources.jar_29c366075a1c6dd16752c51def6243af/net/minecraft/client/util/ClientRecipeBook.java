package net.minecraft.client.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.inventory.container.BlastFurnaceContainer;
import net.minecraft.inventory.container.FurnaceContainer;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.SmokerContainer;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientRecipeBook extends RecipeBook {
   private final RecipeManager recipeManager;
   private final Map<RecipeBookCategories, List<RecipeList>> recipesByCategory = Maps.newHashMap();
   private final List<RecipeList> allRecipes = Lists.newArrayList();

   public ClientRecipeBook(RecipeManager p_i48186_1_) {
      this.recipeManager = p_i48186_1_;
   }

   public void rebuildTable() {
      this.allRecipes.clear();
      this.recipesByCategory.clear();
      Table<RecipeBookCategories, String, RecipeList> table = HashBasedTable.create();

      for(IRecipe<?> irecipe : this.recipeManager.getRecipes()) {
         if (!irecipe.isDynamic()) {
            RecipeBookCategories recipebookcategories = getCategory(irecipe);
            String s = irecipe.getGroup();
            RecipeList recipelist;
            if (s.isEmpty()) {
               recipelist = this.newRecipeList(recipebookcategories);
            } else {
               recipelist = table.get(recipebookcategories, s);
               if (recipelist == null) {
                  recipelist = this.newRecipeList(recipebookcategories);
                  table.put(recipebookcategories, s, recipelist);
               }
            }

            recipelist.add(irecipe);
         }
      }

   }

   private RecipeList newRecipeList(RecipeBookCategories p_202889_1_) {
      RecipeList recipelist = new RecipeList();
      this.allRecipes.add(recipelist);
      this.recipesByCategory.computeIfAbsent(p_202889_1_, (p_202890_0_) -> {
         return Lists.newArrayList();
      }).add(recipelist);
      if (p_202889_1_ != RecipeBookCategories.FURNACE_BLOCKS && p_202889_1_ != RecipeBookCategories.FURNACE_FOOD && p_202889_1_ != RecipeBookCategories.FURNACE_MISC) {
         if (p_202889_1_ != RecipeBookCategories.BLAST_FURNACE_BLOCKS && p_202889_1_ != RecipeBookCategories.BLAST_FURNACE_MISC) {
            if (p_202889_1_ == RecipeBookCategories.SMOKER_FOOD) {
               this.func_216767_a(RecipeBookCategories.SMOKER_SEARCH, recipelist);
            } else if (p_202889_1_ == RecipeBookCategories.STONECUTTER) {
               this.func_216767_a(RecipeBookCategories.STONECUTTER, recipelist);
            } else if (p_202889_1_ == RecipeBookCategories.CAMPFIRE) {
               this.func_216767_a(RecipeBookCategories.CAMPFIRE, recipelist);
            } else {
               this.func_216767_a(RecipeBookCategories.SEARCH, recipelist);
            }
         } else {
            this.func_216767_a(RecipeBookCategories.BLAST_FURNACE_SEARCH, recipelist);
         }
      } else {
         this.func_216767_a(RecipeBookCategories.FURNACE_SEARCH, recipelist);
      }

      return recipelist;
   }

   private void func_216767_a(RecipeBookCategories p_216767_1_, RecipeList p_216767_2_) {
      this.recipesByCategory.computeIfAbsent(p_216767_1_, (p_216768_0_) -> {
         return Lists.newArrayList();
      }).add(p_216767_2_);
   }

   private static RecipeBookCategories getCategory(IRecipe<?> recipe) {
      IRecipeType<?> irecipetype = recipe.getType();
      if (irecipetype == IRecipeType.SMELTING) {
         if (recipe.getRecipeOutput().getItem().isFood()) {
            return RecipeBookCategories.FURNACE_FOOD;
         } else {
            return recipe.getRecipeOutput().getItem() instanceof BlockItem ? RecipeBookCategories.FURNACE_BLOCKS : RecipeBookCategories.FURNACE_MISC;
         }
      } else if (irecipetype == IRecipeType.BLASTING) {
         return recipe.getRecipeOutput().getItem() instanceof BlockItem ? RecipeBookCategories.BLAST_FURNACE_BLOCKS : RecipeBookCategories.BLAST_FURNACE_MISC;
      } else if (irecipetype == IRecipeType.SMOKING) {
         return RecipeBookCategories.SMOKER_FOOD;
      } else if (irecipetype == IRecipeType.STONECUTTING) {
         return RecipeBookCategories.STONECUTTER;
      } else if (irecipetype == IRecipeType.CAMPFIRE_COOKING) {
         return RecipeBookCategories.CAMPFIRE;
      } else {
         ItemStack itemstack = recipe.getRecipeOutput();
         ItemGroup itemgroup = itemstack.getItem().getGroup();
         if (itemgroup == ItemGroup.BUILDING_BLOCKS) {
            return RecipeBookCategories.BUILDING_BLOCKS;
         } else if (itemgroup != ItemGroup.TOOLS && itemgroup != ItemGroup.COMBAT) {
            return itemgroup == ItemGroup.REDSTONE ? RecipeBookCategories.REDSTONE : RecipeBookCategories.MISC;
         } else {
            return RecipeBookCategories.EQUIPMENT;
         }
      }
   }

   public static List<RecipeBookCategories> func_216769_b(RecipeBookContainer<?> p_216769_0_) {
      if (!(p_216769_0_ instanceof WorkbenchContainer) && !(p_216769_0_ instanceof PlayerContainer)) {
         if (p_216769_0_ instanceof FurnaceContainer) {
            return Lists.newArrayList(RecipeBookCategories.FURNACE_SEARCH, RecipeBookCategories.FURNACE_FOOD, RecipeBookCategories.FURNACE_BLOCKS, RecipeBookCategories.FURNACE_MISC);
         } else if (p_216769_0_ instanceof BlastFurnaceContainer) {
            return Lists.newArrayList(RecipeBookCategories.BLAST_FURNACE_SEARCH, RecipeBookCategories.BLAST_FURNACE_BLOCKS, RecipeBookCategories.BLAST_FURNACE_MISC);
         } else {
            return p_216769_0_ instanceof SmokerContainer ? Lists.newArrayList(RecipeBookCategories.SMOKER_SEARCH, RecipeBookCategories.SMOKER_FOOD) : Lists.newArrayList();
         }
      } else {
         return Lists.newArrayList(RecipeBookCategories.SEARCH, RecipeBookCategories.EQUIPMENT, RecipeBookCategories.BUILDING_BLOCKS, RecipeBookCategories.MISC, RecipeBookCategories.REDSTONE);
      }
   }

   public List<RecipeList> getRecipes() {
      return this.allRecipes;
   }

   public List<RecipeList> getRecipes(RecipeBookCategories p_202891_1_) {
      return this.recipesByCategory.getOrDefault(p_202891_1_, Collections.emptyList());
   }
}