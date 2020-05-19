package net.minecraft.client.gui.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GhostRecipe {
   private IRecipe<?> recipe;
   private final List<GhostRecipe.GhostIngredient> ingredients = Lists.newArrayList();
   private float time;

   public void clear() {
      this.recipe = null;
      this.ingredients.clear();
      this.time = 0.0F;
   }

   public void addIngredient(Ingredient p_194187_1_, int p_194187_2_, int p_194187_3_) {
      this.ingredients.add(new GhostRecipe.GhostIngredient(p_194187_1_, p_194187_2_, p_194187_3_));
   }

   public GhostRecipe.GhostIngredient get(int p_192681_1_) {
      return this.ingredients.get(p_192681_1_);
   }

   public int size() {
      return this.ingredients.size();
   }

   @Nullable
   public IRecipe<?> getRecipe() {
      return this.recipe;
   }

   public void setRecipe(IRecipe<?> p_192685_1_) {
      this.recipe = p_192685_1_;
   }

   public void render(Minecraft p_194188_1_, int p_194188_2_, int p_194188_3_, boolean p_194188_4_, float p_194188_5_) {
      if (!Screen.hasControlDown()) {
         this.time += p_194188_5_;
      }

      for(int i = 0; i < this.ingredients.size(); ++i) {
         GhostRecipe.GhostIngredient ghostrecipe$ghostingredient = this.ingredients.get(i);
         int j = ghostrecipe$ghostingredient.getX() + p_194188_2_;
         int k = ghostrecipe$ghostingredient.getY() + p_194188_3_;
         if (i == 0 && p_194188_4_) {
            AbstractGui.fill(j - 4, k - 4, j + 20, k + 20, 822018048);
         } else {
            AbstractGui.fill(j, k, j + 16, k + 16, 822018048);
         }

         ItemStack itemstack = ghostrecipe$ghostingredient.getItem();
         ItemRenderer itemrenderer = p_194188_1_.getItemRenderer();
         itemrenderer.renderItemAndEffectIntoGUI(p_194188_1_.player, itemstack, j, k);
         RenderSystem.depthFunc(516);
         AbstractGui.fill(j, k, j + 16, k + 16, 822083583);
         RenderSystem.depthFunc(515);
         if (i == 0) {
            itemrenderer.renderItemOverlays(p_194188_1_.fontRenderer, itemstack, j, k);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public class GhostIngredient {
      private final Ingredient ingredient;
      private final int x;
      private final int y;

      public GhostIngredient(Ingredient p_i47604_2_, int p_i47604_3_, int p_i47604_4_) {
         this.ingredient = p_i47604_2_;
         this.x = p_i47604_3_;
         this.y = p_i47604_4_;
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }

      public ItemStack getItem() {
         ItemStack[] aitemstack = this.ingredient.getMatchingStacks();
         return aitemstack[MathHelper.floor(GhostRecipe.this.time / 30.0F) % aitemstack.length];
      }
   }
}