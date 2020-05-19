package net.minecraft.client.gui.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipePlacer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeOverlayGui extends AbstractGui implements IRenderable, IGuiEventListener {
   private static final ResourceLocation RECIPE_BOOK_TEXTURE = new ResourceLocation("textures/gui/recipe_book.png");
   private final List<RecipeOverlayGui.RecipeButtonWidget> buttonList = Lists.newArrayList();
   private boolean visible;
   private int x;
   private int y;
   private Minecraft mc;
   private RecipeList recipeList;
   private IRecipe<?> lastRecipeClicked;
   private float time;
   private boolean field_201704_n;

   public void func_201703_a(Minecraft p_201703_1_, RecipeList p_201703_2_, int p_201703_3_, int p_201703_4_, int p_201703_5_, int p_201703_6_, float p_201703_7_) {
      this.mc = p_201703_1_;
      this.recipeList = p_201703_2_;
      if (p_201703_1_.player.openContainer instanceof AbstractFurnaceContainer) {
         this.field_201704_n = true;
      }

      boolean flag = p_201703_1_.player.getRecipeBook().isFilteringCraftable((RecipeBookContainer)p_201703_1_.player.openContainer);
      List<IRecipe<?>> list = p_201703_2_.getDisplayRecipes(true);
      List<IRecipe<?>> list1 = flag ? Collections.emptyList() : p_201703_2_.getDisplayRecipes(false);
      int i = list.size();
      int j = i + list1.size();
      int k = j <= 16 ? 4 : 5;
      int l = (int)Math.ceil((double)((float)j / (float)k));
      this.x = p_201703_3_;
      this.y = p_201703_4_;
      int i1 = 25;
      float f = (float)(this.x + Math.min(j, k) * 25);
      float f1 = (float)(p_201703_5_ + 50);
      if (f > f1) {
         this.x = (int)((float)this.x - p_201703_7_ * (float)((int)((f - f1) / p_201703_7_)));
      }

      float f2 = (float)(this.y + l * 25);
      float f3 = (float)(p_201703_6_ + 50);
      if (f2 > f3) {
         this.y = (int)((float)this.y - p_201703_7_ * (float)MathHelper.ceil((f2 - f3) / p_201703_7_));
      }

      float f4 = (float)this.y;
      float f5 = (float)(p_201703_6_ - 100);
      if (f4 < f5) {
         this.y = (int)((float)this.y - p_201703_7_ * (float)MathHelper.ceil((f4 - f5) / p_201703_7_));
      }

      this.visible = true;
      this.buttonList.clear();

      for(int j1 = 0; j1 < j; ++j1) {
         boolean flag1 = j1 < i;
         IRecipe<?> irecipe = flag1 ? list.get(j1) : list1.get(j1 - i);
         int k1 = this.x + 4 + 25 * (j1 % k);
         int l1 = this.y + 5 + 25 * (j1 / k);
         if (this.field_201704_n) {
            this.buttonList.add(new RecipeOverlayGui.FurnaceRecipeButtonWidget(k1, l1, irecipe, flag1));
         } else {
            this.buttonList.add(new RecipeOverlayGui.RecipeButtonWidget(k1, l1, irecipe, flag1));
         }
      }

      this.lastRecipeClicked = null;
   }

   public boolean changeFocus(boolean p_changeFocus_1_) {
      return false;
   }

   public RecipeList getRecipeList() {
      return this.recipeList;
   }

   public IRecipe<?> getLastRecipeClicked() {
      return this.lastRecipeClicked;
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (p_mouseClicked_5_ != 0) {
         return false;
      } else {
         for(RecipeOverlayGui.RecipeButtonWidget recipeoverlaygui$recipebuttonwidget : this.buttonList) {
            if (recipeoverlaygui$recipebuttonwidget.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
               this.lastRecipeClicked = recipeoverlaygui$recipebuttonwidget.recipe;
               return true;
            }
         }

         return false;
      }
   }

   public boolean isMouseOver(double p_isMouseOver_1_, double p_isMouseOver_3_) {
      return false;
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      if (this.visible) {
         this.time += p_render_3_;
         RenderSystem.enableBlend();
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(RECIPE_BOOK_TEXTURE);
         RenderSystem.pushMatrix();
         RenderSystem.translatef(0.0F, 0.0F, 170.0F);
         int i = this.buttonList.size() <= 16 ? 4 : 5;
         int j = Math.min(this.buttonList.size(), i);
         int k = MathHelper.ceil((float)this.buttonList.size() / (float)i);
         int l = 24;
         int i1 = 4;
         int j1 = 82;
         int k1 = 208;
         this.nineInchSprite(j, k, 24, 4, 82, 208);
         RenderSystem.disableBlend();

         for(RecipeOverlayGui.RecipeButtonWidget recipeoverlaygui$recipebuttonwidget : this.buttonList) {
            recipeoverlaygui$recipebuttonwidget.render(p_render_1_, p_render_2_, p_render_3_);
         }

         RenderSystem.popMatrix();
      }
   }

   private void nineInchSprite(int p_191846_1_, int p_191846_2_, int p_191846_3_, int p_191846_4_, int p_191846_5_, int p_191846_6_) {
      this.blit(this.x, this.y, p_191846_5_, p_191846_6_, p_191846_4_, p_191846_4_);
      this.blit(this.x + p_191846_4_ * 2 + p_191846_1_ * p_191846_3_, this.y, p_191846_5_ + p_191846_3_ + p_191846_4_, p_191846_6_, p_191846_4_, p_191846_4_);
      this.blit(this.x, this.y + p_191846_4_ * 2 + p_191846_2_ * p_191846_3_, p_191846_5_, p_191846_6_ + p_191846_3_ + p_191846_4_, p_191846_4_, p_191846_4_);
      this.blit(this.x + p_191846_4_ * 2 + p_191846_1_ * p_191846_3_, this.y + p_191846_4_ * 2 + p_191846_2_ * p_191846_3_, p_191846_5_ + p_191846_3_ + p_191846_4_, p_191846_6_ + p_191846_3_ + p_191846_4_, p_191846_4_, p_191846_4_);

      for(int i = 0; i < p_191846_1_; ++i) {
         this.blit(this.x + p_191846_4_ + i * p_191846_3_, this.y, p_191846_5_ + p_191846_4_, p_191846_6_, p_191846_3_, p_191846_4_);
         this.blit(this.x + p_191846_4_ + (i + 1) * p_191846_3_, this.y, p_191846_5_ + p_191846_4_, p_191846_6_, p_191846_4_, p_191846_4_);

         for(int j = 0; j < p_191846_2_; ++j) {
            if (i == 0) {
               this.blit(this.x, this.y + p_191846_4_ + j * p_191846_3_, p_191846_5_, p_191846_6_ + p_191846_4_, p_191846_4_, p_191846_3_);
               this.blit(this.x, this.y + p_191846_4_ + (j + 1) * p_191846_3_, p_191846_5_, p_191846_6_ + p_191846_4_, p_191846_4_, p_191846_4_);
            }

            this.blit(this.x + p_191846_4_ + i * p_191846_3_, this.y + p_191846_4_ + j * p_191846_3_, p_191846_5_ + p_191846_4_, p_191846_6_ + p_191846_4_, p_191846_3_, p_191846_3_);
            this.blit(this.x + p_191846_4_ + (i + 1) * p_191846_3_, this.y + p_191846_4_ + j * p_191846_3_, p_191846_5_ + p_191846_4_, p_191846_6_ + p_191846_4_, p_191846_4_, p_191846_3_);
            this.blit(this.x + p_191846_4_ + i * p_191846_3_, this.y + p_191846_4_ + (j + 1) * p_191846_3_, p_191846_5_ + p_191846_4_, p_191846_6_ + p_191846_4_, p_191846_3_, p_191846_4_);
            this.blit(this.x + p_191846_4_ + (i + 1) * p_191846_3_ - 1, this.y + p_191846_4_ + (j + 1) * p_191846_3_ - 1, p_191846_5_ + p_191846_4_, p_191846_6_ + p_191846_4_, p_191846_4_ + 1, p_191846_4_ + 1);
            if (i == p_191846_1_ - 1) {
               this.blit(this.x + p_191846_4_ * 2 + p_191846_1_ * p_191846_3_, this.y + p_191846_4_ + j * p_191846_3_, p_191846_5_ + p_191846_3_ + p_191846_4_, p_191846_6_ + p_191846_4_, p_191846_4_, p_191846_3_);
               this.blit(this.x + p_191846_4_ * 2 + p_191846_1_ * p_191846_3_, this.y + p_191846_4_ + (j + 1) * p_191846_3_, p_191846_5_ + p_191846_3_ + p_191846_4_, p_191846_6_ + p_191846_4_, p_191846_4_, p_191846_4_);
            }
         }

         this.blit(this.x + p_191846_4_ + i * p_191846_3_, this.y + p_191846_4_ * 2 + p_191846_2_ * p_191846_3_, p_191846_5_ + p_191846_4_, p_191846_6_ + p_191846_3_ + p_191846_4_, p_191846_3_, p_191846_4_);
         this.blit(this.x + p_191846_4_ + (i + 1) * p_191846_3_, this.y + p_191846_4_ * 2 + p_191846_2_ * p_191846_3_, p_191846_5_ + p_191846_4_, p_191846_6_ + p_191846_3_ + p_191846_4_, p_191846_4_, p_191846_4_);
      }

   }

   public void setVisible(boolean p_192999_1_) {
      this.visible = p_192999_1_;
   }

   public boolean isVisible() {
      return this.visible;
   }

   @OnlyIn(Dist.CLIENT)
   class FurnaceRecipeButtonWidget extends RecipeOverlayGui.RecipeButtonWidget {
      public FurnaceRecipeButtonWidget(int p_i48747_2_, int p_i48747_3_, IRecipe<?> p_i48747_4_, boolean p_i48747_5_) {
         super(p_i48747_2_, p_i48747_3_, p_i48747_4_, p_i48747_5_);
      }

      protected void func_201505_a(IRecipe<?> p_201505_1_) {
         ItemStack[] aitemstack = p_201505_1_.getIngredients().get(0).getMatchingStacks();
         this.field_201506_o.add(new RecipeOverlayGui.RecipeButtonWidget.Child(10, 10, aitemstack));
      }
   }

   @OnlyIn(Dist.CLIENT)
   class RecipeButtonWidget extends Widget implements IRecipePlacer<Ingredient> {
      private final IRecipe<?> recipe;
      private final boolean isCraftable;
      protected final List<RecipeOverlayGui.RecipeButtonWidget.Child> field_201506_o = Lists.newArrayList();

      public RecipeButtonWidget(int p_i47594_2_, int p_i47594_3_, IRecipe<?> p_i47594_4_, boolean p_i47594_5_) {
         super(p_i47594_2_, p_i47594_3_, 200, 20, "");
         this.width = 24;
         this.height = 24;
         this.recipe = p_i47594_4_;
         this.isCraftable = p_i47594_5_;
         this.func_201505_a(p_i47594_4_);
      }

      protected void func_201505_a(IRecipe<?> p_201505_1_) {
         this.placeRecipe(3, 3, -1, p_201505_1_, p_201505_1_.getIngredients().iterator(), 0);
      }

      public void setSlotContents(Iterator<Ingredient> ingredients, int slotIn, int maxAmount, int y, int x) {
         ItemStack[] aitemstack = ingredients.next().getMatchingStacks();
         if (aitemstack.length != 0) {
            this.field_201506_o.add(new RecipeOverlayGui.RecipeButtonWidget.Child(3 + x * 7, 3 + y * 7, aitemstack));
         }

      }

      public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
         RenderSystem.enableAlphaTest();
         RecipeOverlayGui.this.mc.getTextureManager().bindTexture(RecipeOverlayGui.RECIPE_BOOK_TEXTURE);
         int i = 152;
         if (!this.isCraftable) {
            i += 26;
         }

         int j = RecipeOverlayGui.this.field_201704_n ? 130 : 78;
         if (this.isHovered()) {
            j += 26;
         }

         this.blit(this.x, this.y, i, j, this.width, this.height);

         for(RecipeOverlayGui.RecipeButtonWidget.Child recipeoverlaygui$recipebuttonwidget$child : this.field_201506_o) {
            RenderSystem.pushMatrix();
            float f = 0.42F;
            int k = (int)((float)(this.x + recipeoverlaygui$recipebuttonwidget$child.field_201706_b) / 0.42F - 3.0F);
            int l = (int)((float)(this.y + recipeoverlaygui$recipebuttonwidget$child.field_201707_c) / 0.42F - 3.0F);
            RenderSystem.scalef(0.42F, 0.42F, 1.0F);
            RecipeOverlayGui.this.mc.getItemRenderer().renderItemAndEffectIntoGUI(recipeoverlaygui$recipebuttonwidget$child.field_201705_a[MathHelper.floor(RecipeOverlayGui.this.time / 30.0F) % recipeoverlaygui$recipebuttonwidget$child.field_201705_a.length], k, l);
            RenderSystem.popMatrix();
         }

         RenderSystem.disableAlphaTest();
      }

      @OnlyIn(Dist.CLIENT)
      public class Child {
         public final ItemStack[] field_201705_a;
         public final int field_201706_b;
         public final int field_201707_c;

         public Child(int p_i48748_2_, int p_i48748_3_, ItemStack[] p_i48748_4_) {
            this.field_201706_b = p_i48748_2_;
            this.field_201707_c = p_i48748_3_;
            this.field_201705_a = p_i48748_4_;
         }
      }
   }
}