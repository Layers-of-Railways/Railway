package net.minecraft.client.gui.widget.list;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ResourcePacksScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractResourcePackList extends ExtendedList<AbstractResourcePackList.ResourcePackEntry> {
   private static final ResourceLocation field_214367_b = new ResourceLocation("textures/gui/resource_packs.png");
   private static final ITextComponent field_214368_c = new TranslationTextComponent("resourcePack.incompatible");
   private static final ITextComponent field_214369_d = new TranslationTextComponent("resourcePack.incompatible.confirm.title");
   protected final Minecraft mc;
   private final ITextComponent field_214370_e;

   public AbstractResourcePackList(Minecraft p_i51074_1_, int p_i51074_2_, int p_i51074_3_, ITextComponent p_i51074_4_) {
      super(p_i51074_1_, p_i51074_2_, p_i51074_3_, 32, p_i51074_3_ - 55 + 4, 36);
      this.mc = p_i51074_1_;
      this.centerListVertically = false;
      this.setRenderHeader(true, (int)(9.0F * 1.5F));
      this.field_214370_e = p_i51074_4_;
   }

   protected void renderHeader(int p_renderHeader_1_, int p_renderHeader_2_, Tessellator p_renderHeader_3_) {
      ITextComponent itextcomponent = (new StringTextComponent("")).appendSibling(this.field_214370_e).applyTextStyles(TextFormatting.UNDERLINE, TextFormatting.BOLD);
      this.mc.fontRenderer.drawString(itextcomponent.getFormattedText(), (float)(p_renderHeader_1_ + this.width / 2 - this.mc.fontRenderer.getStringWidth(itextcomponent.getFormattedText()) / 2), (float)Math.min(this.y0 + 3, p_renderHeader_2_), 16777215);
   }

   public int getRowWidth() {
      return this.width;
   }

   protected int getScrollbarPosition() {
      return this.x1 - 6;
   }

   public void func_214365_a(AbstractResourcePackList.ResourcePackEntry p_214365_1_) {
      this.addEntry(p_214365_1_);
      p_214365_1_.field_214430_c = this;
   }

   @OnlyIn(Dist.CLIENT)
   public static class ResourcePackEntry extends ExtendedList.AbstractListEntry<AbstractResourcePackList.ResourcePackEntry> {
      private AbstractResourcePackList field_214430_c;
      protected final Minecraft field_214428_a;
      protected final ResourcePacksScreen field_214429_b;
      private final ClientResourcePackInfo field_214431_d;

      public ResourcePackEntry(AbstractResourcePackList p_i50749_1_, ResourcePacksScreen p_i50749_2_, ClientResourcePackInfo p_i50749_3_) {
         this.field_214429_b = p_i50749_2_;
         this.field_214428_a = Minecraft.getInstance();
         this.field_214431_d = p_i50749_3_;
         this.field_214430_c = p_i50749_1_;
      }

      public void func_214422_a(SelectedResourcePackList p_214422_1_) {
         this.func_214418_e().getPriority().insert(p_214422_1_.children(), this, AbstractResourcePackList.ResourcePackEntry::func_214418_e, true);
         this.func_230009_b_(p_214422_1_);
      }

      public void func_230009_b_(SelectedResourcePackList p_230009_1_) {
         this.field_214430_c = p_230009_1_;
      }

      protected void func_214419_a() {
         this.field_214431_d.func_195808_a(this.field_214428_a.getTextureManager());
      }

      protected PackCompatibility func_214423_b() {
         return this.field_214431_d.getCompatibility();
      }

      protected String func_214420_c() {
         return this.field_214431_d.getDescription().getFormattedText();
      }

      protected String func_214416_d() {
         return this.field_214431_d.getTitle().getFormattedText();
      }

      public ClientResourcePackInfo func_214418_e() {
         return this.field_214431_d;
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         PackCompatibility packcompatibility = this.func_214423_b();
         if (!packcompatibility.isCompatible()) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            AbstractGui.fill(p_render_3_ - 1, p_render_2_ - 1, p_render_3_ + p_render_4_ - 9, p_render_2_ + p_render_5_ + 1, -8978432);
         }

         this.func_214419_a();
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         AbstractGui.blit(p_render_3_, p_render_2_, 0.0F, 0.0F, 32, 32, 32, 32);
         String s = this.func_214416_d();
         String s1 = this.func_214420_c();
         if (this.func_214424_f() && (this.field_214428_a.gameSettings.touchscreen || p_render_8_)) {
            this.field_214428_a.getTextureManager().bindTexture(AbstractResourcePackList.field_214367_b);
            AbstractGui.fill(p_render_3_, p_render_2_, p_render_3_ + 32, p_render_2_ + 32, -1601138544);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int i = p_render_6_ - p_render_3_;
            int j = p_render_7_ - p_render_2_;
            if (!packcompatibility.isCompatible()) {
               s = AbstractResourcePackList.field_214368_c.getFormattedText();
               s1 = packcompatibility.getDescription().getFormattedText();
            }

            if (this.func_214425_g()) {
               if (i < 32) {
                  AbstractGui.blit(p_render_3_, p_render_2_, 0.0F, 32.0F, 32, 32, 256, 256);
               } else {
                  AbstractGui.blit(p_render_3_, p_render_2_, 0.0F, 0.0F, 32, 32, 256, 256);
               }
            } else {
               if (this.func_214426_h()) {
                  if (i < 16) {
                     AbstractGui.blit(p_render_3_, p_render_2_, 32.0F, 32.0F, 32, 32, 256, 256);
                  } else {
                     AbstractGui.blit(p_render_3_, p_render_2_, 32.0F, 0.0F, 32, 32, 256, 256);
                  }
               }

               if (this.func_214414_i()) {
                  if (i < 32 && i > 16 && j < 16) {
                     AbstractGui.blit(p_render_3_, p_render_2_, 96.0F, 32.0F, 32, 32, 256, 256);
                  } else {
                     AbstractGui.blit(p_render_3_, p_render_2_, 96.0F, 0.0F, 32, 32, 256, 256);
                  }
               }

               if (this.func_214427_j()) {
                  if (i < 32 && i > 16 && j > 16) {
                     AbstractGui.blit(p_render_3_, p_render_2_, 64.0F, 32.0F, 32, 32, 256, 256);
                  } else {
                     AbstractGui.blit(p_render_3_, p_render_2_, 64.0F, 0.0F, 32, 32, 256, 256);
                  }
               }
            }
         }

         int l = this.field_214428_a.fontRenderer.getStringWidth(s);
         if (l > 157) {
            s = this.field_214428_a.fontRenderer.trimStringToWidth(s, 157 - this.field_214428_a.fontRenderer.getStringWidth("...")) + "...";
         }

         this.field_214428_a.fontRenderer.drawStringWithShadow(s, (float)(p_render_3_ + 32 + 2), (float)(p_render_2_ + 1), 16777215);
         List<String> list = this.field_214428_a.fontRenderer.listFormattedStringToWidth(s1, 157);

         for(int k = 0; k < 2 && k < list.size(); ++k) {
            this.field_214428_a.fontRenderer.drawStringWithShadow(list.get(k), (float)(p_render_3_ + 32 + 2), (float)(p_render_2_ + 12 + 10 * k), 8421504);
         }

      }

      protected boolean func_214424_f() {
         return !this.field_214431_d.isOrderLocked() || !this.field_214431_d.isAlwaysEnabled();
      }

      protected boolean func_214425_g() {
         return !this.field_214429_b.func_214299_c(this);
      }

      protected boolean func_214426_h() {
         return this.field_214429_b.func_214299_c(this) && !this.field_214431_d.isAlwaysEnabled();
      }

      protected boolean func_214414_i() {
         List<AbstractResourcePackList.ResourcePackEntry> list = this.field_214430_c.children();
         int i = list.indexOf(this);
         return i > 0 && !(list.get(i - 1)).field_214431_d.isOrderLocked();
      }

      protected boolean func_214427_j() {
         List<AbstractResourcePackList.ResourcePackEntry> list = this.field_214430_c.children();
         int i = list.indexOf(this);
         return i >= 0 && i < list.size() - 1 && !(list.get(i + 1)).field_214431_d.isOrderLocked();
      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         double d0 = p_mouseClicked_1_ - (double)this.field_214430_c.getRowLeft();
         double d1 = p_mouseClicked_3_ - (double)this.field_214430_c.getRowTop(this.field_214430_c.children().indexOf(this));
         if (this.func_214424_f() && d0 <= 32.0D) {
            if (this.func_214425_g()) {
               this.func_214415_k().markChanged();
               PackCompatibility packcompatibility = this.func_214423_b();
               if (packcompatibility.isCompatible()) {
                  this.func_214415_k().func_214300_a(this);
               } else {
                  ITextComponent itextcomponent = packcompatibility.getConfirmMessage();
                  this.field_214428_a.displayGuiScreen(new ConfirmScreen((p_214417_1_) -> {
                     this.field_214428_a.displayGuiScreen(this.func_214415_k());
                     if (p_214417_1_) {
                        this.func_214415_k().func_214300_a(this);
                     }

                  }, AbstractResourcePackList.field_214369_d, itextcomponent));
               }

               return true;
            }

            if (d0 < 16.0D && this.func_214426_h()) {
               this.func_214415_k().func_214297_b(this);
               return true;
            }

            if (d0 > 16.0D && d1 < 16.0D && this.func_214414_i()) {
               List<AbstractResourcePackList.ResourcePackEntry> list1 = this.field_214430_c.children();
               int j = list1.indexOf(this);
               list1.remove(j);
               list1.add(j - 1, this);
               this.func_214415_k().markChanged();
               return true;
            }

            if (d0 > 16.0D && d1 > 16.0D && this.func_214427_j()) {
               List<AbstractResourcePackList.ResourcePackEntry> list = this.field_214430_c.children();
               int i = list.indexOf(this);
               list.remove(i);
               list.add(i + 1, this);
               this.func_214415_k().markChanged();
               return true;
            }
         }

         return false;
      }

      public ResourcePacksScreen func_214415_k() {
         return this.field_214429_b;
      }
   }
}