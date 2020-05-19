package net.minecraft.item.crafting;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.inventory.container.BlastFurnaceContainer;
import net.minecraft.inventory.container.FurnaceContainer;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.SmokerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RecipeBook {
   protected final Set<ResourceLocation> recipes = Sets.newHashSet();
   protected final Set<ResourceLocation> newRecipes = Sets.newHashSet();
   protected boolean isGuiOpen;
   protected boolean isFilteringCraftable;
   protected boolean isFurnaceGuiOpen;
   protected boolean isFurnaceFilteringCraftable;
   protected boolean field_216763_g;
   protected boolean field_216764_h;
   protected boolean field_216765_i;
   protected boolean field_216766_j;

   public void copyFrom(RecipeBook that) {
      this.recipes.clear();
      this.newRecipes.clear();
      this.recipes.addAll(that.recipes);
      this.newRecipes.addAll(that.newRecipes);
   }

   public void unlock(IRecipe<?> recipe) {
      if (!recipe.isDynamic()) {
         this.unlock(recipe.getId());
      }

   }

   protected void unlock(ResourceLocation p_209118_1_) {
      this.recipes.add(p_209118_1_);
   }

   public boolean isUnlocked(@Nullable IRecipe<?> recipe) {
      return recipe == null ? false : this.recipes.contains(recipe.getId());
   }

   public boolean func_226144_b_(ResourceLocation p_226144_1_) {
      return this.recipes.contains(p_226144_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void lock(IRecipe<?> recipe) {
      this.lock(recipe.getId());
   }

   protected void lock(ResourceLocation p_209119_1_) {
      this.recipes.remove(p_209119_1_);
      this.newRecipes.remove(p_209119_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isNew(IRecipe<?> recipe) {
      return this.newRecipes.contains(recipe.getId());
   }

   public void markSeen(IRecipe<?> recipe) {
      this.newRecipes.remove(recipe.getId());
   }

   public void markNew(IRecipe<?> recipe) {
      this.markNew(recipe.getId());
   }

   protected void markNew(ResourceLocation p_209120_1_) {
      this.newRecipes.add(p_209120_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isGuiOpen() {
      return this.isGuiOpen;
   }

   public void setGuiOpen(boolean open) {
      this.isGuiOpen = open;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isFilteringCraftable(RecipeBookContainer<?> p_203432_1_) {
      if (p_203432_1_ instanceof FurnaceContainer) {
         return this.isFurnaceFilteringCraftable;
      } else if (p_203432_1_ instanceof BlastFurnaceContainer) {
         return this.field_216764_h;
      } else {
         return p_203432_1_ instanceof SmokerContainer ? this.field_216766_j : this.isFilteringCraftable;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isFilteringCraftable() {
      return this.isFilteringCraftable;
   }

   public void setFilteringCraftable(boolean shouldFilter) {
      this.isFilteringCraftable = shouldFilter;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isFurnaceGuiOpen() {
      return this.isFurnaceGuiOpen;
   }

   public void setFurnaceGuiOpen(boolean p_202881_1_) {
      this.isFurnaceGuiOpen = p_202881_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isFurnaceFilteringCraftable() {
      return this.isFurnaceFilteringCraftable;
   }

   public void setFurnaceFilteringCraftable(boolean p_202882_1_) {
      this.isFurnaceFilteringCraftable = p_202882_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_216758_e() {
      return this.field_216763_g;
   }

   public void func_216755_e(boolean p_216755_1_) {
      this.field_216763_g = p_216755_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_216761_f() {
      return this.field_216764_h;
   }

   public void func_216756_f(boolean p_216756_1_) {
      this.field_216764_h = p_216756_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_216759_g() {
      return this.field_216765_i;
   }

   public void func_216757_g(boolean p_216757_1_) {
      this.field_216765_i = p_216757_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_216762_h() {
      return this.field_216766_j;
   }

   public void func_216760_h(boolean p_216760_1_) {
      this.field_216766_j = p_216760_1_;
   }
}