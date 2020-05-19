package net.minecraft.client.gui.advancements;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancementTabGui extends AbstractGui {
   private final Minecraft minecraft;
   private final AdvancementsScreen screen;
   private final AdvancementTabType type;
   private final int index;
   private final Advancement advancement;
   private final DisplayInfo display;
   private final ItemStack icon;
   private final String title;
   private final AdvancementEntryGui root;
   private final Map<Advancement, AdvancementEntryGui> guis = Maps.newLinkedHashMap();
   private double scrollX;
   private double scrollY;
   private int minX = Integer.MAX_VALUE;
   private int minY = Integer.MAX_VALUE;
   private int maxX = Integer.MIN_VALUE;
   private int maxY = Integer.MIN_VALUE;
   private float fade;
   private boolean centered;
   private int page;

   public AdvancementTabGui(Minecraft p_i47589_1_, AdvancementsScreen p_i47589_2_, AdvancementTabType p_i47589_3_, int p_i47589_4_, Advancement p_i47589_5_, DisplayInfo p_i47589_6_) {
      this.minecraft = p_i47589_1_;
      this.screen = p_i47589_2_;
      this.type = p_i47589_3_;
      this.index = p_i47589_4_;
      this.advancement = p_i47589_5_;
      this.display = p_i47589_6_;
      this.icon = p_i47589_6_.getIcon();
      this.title = p_i47589_6_.getTitle().getFormattedText();
      this.root = new AdvancementEntryGui(this, p_i47589_1_, p_i47589_5_, p_i47589_6_);
      this.addGuiAdvancement(this.root, p_i47589_5_);
   }

   public AdvancementTabGui(Minecraft mc, AdvancementsScreen screen, AdvancementTabType type, int index, int page, Advancement adv, DisplayInfo info) {
      this(mc, screen, type, index, adv, info);
      this.page = page;
   }

   public int getPage() {
      return page;
   }

   public Advancement getAdvancement() {
      return this.advancement;
   }

   public String getTitle() {
      return this.title;
   }

   public void drawTab(int p_191798_1_, int p_191798_2_, boolean p_191798_3_) {
      this.type.draw(this, p_191798_1_, p_191798_2_, p_191798_3_, this.index);
   }

   public void drawIcon(int p_191796_1_, int p_191796_2_, ItemRenderer p_191796_3_) {
      this.type.drawIcon(p_191796_1_, p_191796_2_, this.index, p_191796_3_, this.icon);
   }

   public void drawContents() {
      if (!this.centered) {
         this.scrollX = (double)(117 - (this.maxX + this.minX) / 2);
         this.scrollY = (double)(56 - (this.maxY + this.minY) / 2);
         this.centered = true;
      }

      RenderSystem.pushMatrix();
      RenderSystem.enableDepthTest();
      RenderSystem.translatef(0.0F, 0.0F, 950.0F);
      RenderSystem.colorMask(false, false, false, false);
      fill(4680, 2260, -4680, -2260, -16777216);
      RenderSystem.colorMask(true, true, true, true);
      RenderSystem.translatef(0.0F, 0.0F, -950.0F);
      RenderSystem.depthFunc(518);
      fill(234, 113, 0, 0, -16777216);
      RenderSystem.depthFunc(515);
      ResourceLocation resourcelocation = this.display.getBackground();
      if (resourcelocation != null) {
         this.minecraft.getTextureManager().bindTexture(resourcelocation);
      } else {
         this.minecraft.getTextureManager().bindTexture(TextureManager.RESOURCE_LOCATION_EMPTY);
      }

      int i = MathHelper.floor(this.scrollX);
      int j = MathHelper.floor(this.scrollY);
      int k = i % 16;
      int l = j % 16;

      for(int i1 = -1; i1 <= 15; ++i1) {
         for(int j1 = -1; j1 <= 8; ++j1) {
            blit(k + 16 * i1, l + 16 * j1, 0.0F, 0.0F, 16, 16, 16, 16);
         }
      }

      this.root.drawConnectivity(i, j, true);
      this.root.drawConnectivity(i, j, false);
      this.root.draw(i, j);
      RenderSystem.depthFunc(518);
      RenderSystem.translatef(0.0F, 0.0F, -950.0F);
      RenderSystem.colorMask(false, false, false, false);
      fill(4680, 2260, -4680, -2260, -16777216);
      RenderSystem.colorMask(true, true, true, true);
      RenderSystem.translatef(0.0F, 0.0F, 950.0F);
      RenderSystem.depthFunc(515);
      RenderSystem.popMatrix();
   }

   public void drawToolTips(int p_192991_1_, int p_192991_2_, int p_192991_3_, int p_192991_4_) {
      RenderSystem.pushMatrix();
      RenderSystem.translatef(0.0F, 0.0F, 200.0F);
      fill(0, 0, 234, 113, MathHelper.floor(this.fade * 255.0F) << 24);
      boolean flag = false;
      int i = MathHelper.floor(this.scrollX);
      int j = MathHelper.floor(this.scrollY);
      if (p_192991_1_ > 0 && p_192991_1_ < 234 && p_192991_2_ > 0 && p_192991_2_ < 113) {
         for(AdvancementEntryGui advancemententrygui : this.guis.values()) {
            if (advancemententrygui.isMouseOver(i, j, p_192991_1_, p_192991_2_)) {
               flag = true;
               advancemententrygui.drawHover(i, j, this.fade, p_192991_3_, p_192991_4_);
               break;
            }
         }
      }

      RenderSystem.popMatrix();
      if (flag) {
         this.fade = MathHelper.clamp(this.fade + 0.02F, 0.0F, 0.3F);
      } else {
         this.fade = MathHelper.clamp(this.fade - 0.04F, 0.0F, 1.0F);
      }

   }

   public boolean func_195627_a(int p_195627_1_, int p_195627_2_, double p_195627_3_, double p_195627_5_) {
      return this.type.func_198891_a(p_195627_1_, p_195627_2_, this.index, p_195627_3_, p_195627_5_);
   }

   @Nullable
   public static AdvancementTabGui create(Minecraft p_193936_0_, AdvancementsScreen p_193936_1_, int p_193936_2_, Advancement p_193936_3_) {
      if (p_193936_3_.getDisplay() == null) {
         return null;
      } else {
         for(AdvancementTabType advancementtabtype : AdvancementTabType.values()) {
            if ((p_193936_2_ % AdvancementTabType.MAX_TABS) < advancementtabtype.getMax()) {
               return new AdvancementTabGui(p_193936_0_, p_193936_1_, advancementtabtype, p_193936_2_ % AdvancementTabType.MAX_TABS, p_193936_2_ / AdvancementTabType.MAX_TABS, p_193936_3_, p_193936_3_.getDisplay());
            }

            p_193936_2_ -= advancementtabtype.getMax();
         }

         return null;
      }
   }

   public void func_195626_a(double p_195626_1_, double p_195626_3_) {
      if (this.maxX - this.minX > 234) {
         this.scrollX = MathHelper.clamp(this.scrollX + p_195626_1_, (double)(-(this.maxX - 234)), 0.0D);
      }

      if (this.maxY - this.minY > 113) {
         this.scrollY = MathHelper.clamp(this.scrollY + p_195626_3_, (double)(-(this.maxY - 113)), 0.0D);
      }

   }

   public void addAdvancement(Advancement p_191800_1_) {
      if (p_191800_1_.getDisplay() != null) {
         AdvancementEntryGui advancemententrygui = new AdvancementEntryGui(this, this.minecraft, p_191800_1_, p_191800_1_.getDisplay());
         this.addGuiAdvancement(advancemententrygui, p_191800_1_);
      }
   }

   private void addGuiAdvancement(AdvancementEntryGui p_193937_1_, Advancement p_193937_2_) {
      this.guis.put(p_193937_2_, p_193937_1_);
      int i = p_193937_1_.getX();
      int j = i + 28;
      int k = p_193937_1_.getY();
      int l = k + 27;
      this.minX = Math.min(this.minX, i);
      this.maxX = Math.max(this.maxX, j);
      this.minY = Math.min(this.minY, k);
      this.maxY = Math.max(this.maxY, l);

      for(AdvancementEntryGui advancemententrygui : this.guis.values()) {
         advancemententrygui.attachToParent();
      }

   }

   @Nullable
   public AdvancementEntryGui getAdvancementGui(Advancement p_191794_1_) {
      return this.guis.get(p_191794_1_);
   }

   public AdvancementsScreen getScreen() {
      return this.screen;
   }
}