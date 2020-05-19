package net.minecraft.client.gui.recipebook;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractRecipeBookGui extends RecipeBookGui {
   private Iterator<Item> field_212964_i;
   private Set<Item> field_212965_j;
   private Slot field_212966_k;
   private Item field_212967_l;
   private float field_212968_m;

   protected boolean toggleCraftableFilter() {
      boolean flag = !this.func_212962_b();
      this.func_212959_a(flag);
      return flag;
   }

   protected abstract boolean func_212962_b();

   protected abstract void func_212959_a(boolean p_212959_1_);

   public boolean isVisible() {
      return this.func_212963_d();
   }

   protected abstract boolean func_212963_d();

   protected void setVisible(boolean p_193006_1_) {
      this.func_212957_c(p_193006_1_);
      if (!p_193006_1_) {
         this.recipeBookPage.setInvisible();
      }

      this.sendUpdateSettings();
   }

   protected abstract void func_212957_c(boolean p_212957_1_);

   protected void func_205702_a() {
      this.toggleRecipesBtn.initTextureValues(152, 182, 28, 18, RECIPE_BOOK);
   }

   protected String func_205703_f() {
      return I18n.format(this.toggleRecipesBtn.isStateTriggered() ? this.func_212960_g() : "gui.recipebook.toggleRecipes.all");
   }

   protected abstract String func_212960_g();

   public void slotClicked(@Nullable Slot slotIn) {
      super.slotClicked(slotIn);
      if (slotIn != null && slotIn.slotNumber < this.field_201522_g.getSize()) {
         this.field_212966_k = null;
      }

   }

   public void setupGhostRecipe(IRecipe<?> p_193951_1_, List<Slot> p_193951_2_) {
      ItemStack itemstack = p_193951_1_.getRecipeOutput();
      this.ghostRecipe.setRecipe(p_193951_1_);
      this.ghostRecipe.addIngredient(Ingredient.fromStacks(itemstack), (p_193951_2_.get(2)).xPos, (p_193951_2_.get(2)).yPos);
      NonNullList<Ingredient> nonnulllist = p_193951_1_.getIngredients();
      this.field_212966_k = p_193951_2_.get(1);
      if (this.field_212965_j == null) {
         this.field_212965_j = this.func_212958_h();
      }

      this.field_212964_i = this.field_212965_j.iterator();
      this.field_212967_l = null;
      Iterator<Ingredient> iterator = nonnulllist.iterator();

      for(int i = 0; i < 2; ++i) {
         if (!iterator.hasNext()) {
            return;
         }

         Ingredient ingredient = iterator.next();
         if (!ingredient.hasNoMatchingItems()) {
            Slot slot = p_193951_2_.get(i);
            this.ghostRecipe.addIngredient(ingredient, slot.xPos, slot.yPos);
         }
      }

   }

   protected abstract Set<Item> func_212958_h();

   public void renderGhostRecipe(int p_191864_1_, int p_191864_2_, boolean p_191864_3_, float p_191864_4_) {
      super.renderGhostRecipe(p_191864_1_, p_191864_2_, p_191864_3_, p_191864_4_);
      if (this.field_212966_k != null) {
         if (!Screen.hasControlDown()) {
            this.field_212968_m += p_191864_4_;
         }

         int i = this.field_212966_k.xPos + p_191864_1_;
         int j = this.field_212966_k.yPos + p_191864_2_;
         AbstractGui.fill(i, j, i + 16, j + 16, 822018048);
         this.mc.getItemRenderer().renderItemAndEffectIntoGUI(this.mc.player, this.func_212961_n().getDefaultInstance(), i, j);
         RenderSystem.depthFunc(516);
         AbstractGui.fill(i, j, i + 16, j + 16, 822083583);
         RenderSystem.depthFunc(515);
      }
   }

   private Item func_212961_n() {
      if (this.field_212967_l == null || this.field_212968_m > 30.0F) {
         this.field_212968_m = 0.0F;
         if (this.field_212964_i == null || !this.field_212964_i.hasNext()) {
            if (this.field_212965_j == null) {
               this.field_212965_j = this.func_212958_h();
            }

            this.field_212964_i = this.field_212965_j.iterator();
         }

         this.field_212967_l = this.field_212964_i.next();
      }

      return this.field_212967_l;
   }
}