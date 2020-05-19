package net.minecraft.client.gui.advancements;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.CSeenAdvancementsPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancementsScreen extends Screen implements ClientAdvancementManager.IListener {
   private static final ResourceLocation WINDOW = new ResourceLocation("textures/gui/advancements/window.png");
   private static final ResourceLocation TABS = new ResourceLocation("textures/gui/advancements/tabs.png");
   private final ClientAdvancementManager clientAdvancementManager;
   private final Map<Advancement, AdvancementTabGui> tabs = Maps.newLinkedHashMap();
   private AdvancementTabGui selectedTab;
   private boolean isScrolling;
   private static int tabPage, maxPages;

   public AdvancementsScreen(ClientAdvancementManager p_i47383_1_) {
      super(NarratorChatListener.EMPTY);
      this.clientAdvancementManager = p_i47383_1_;
   }

   protected void init() {
      this.tabs.clear();
      this.selectedTab = null;
      this.clientAdvancementManager.setListener(this);
      if (this.selectedTab == null && !this.tabs.isEmpty()) {
         this.clientAdvancementManager.setSelectedTab(this.tabs.values().iterator().next().getAdvancement(), true);
      } else {
         this.clientAdvancementManager.setSelectedTab(this.selectedTab == null ? null : this.selectedTab.getAdvancement(), true);
      }
      if (this.tabs.size() > AdvancementTabType.MAX_TABS) {
          int guiLeft = (this.width - 252) / 2;
          int guiTop = (this.height - 140) / 2;
          addButton(new net.minecraft.client.gui.widget.button.Button(guiLeft,            guiTop - 50, 20, 20, "<", b -> tabPage = Math.max(tabPage - 1, 0       )));
          addButton(new net.minecraft.client.gui.widget.button.Button(guiLeft + 252 - 20, guiTop - 50, 20, 20, ">", b -> tabPage = Math.min(tabPage + 1, maxPages)));
          maxPages = this.tabs.size() / AdvancementTabType.MAX_TABS;
      }
   }

   public void removed() {
      this.clientAdvancementManager.setListener((ClientAdvancementManager.IListener)null);
      ClientPlayNetHandler clientplaynethandler = this.minecraft.getConnection();
      if (clientplaynethandler != null) {
         clientplaynethandler.sendPacket(CSeenAdvancementsPacket.closedScreen());
      }

   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (p_mouseClicked_5_ == 0) {
         int i = (this.width - 252) / 2;
         int j = (this.height - 140) / 2;

         for(AdvancementTabGui advancementtabgui : this.tabs.values()) {
            if (advancementtabgui.getPage() == tabPage && advancementtabgui.func_195627_a(i, j, p_mouseClicked_1_, p_mouseClicked_3_)) {
               this.clientAdvancementManager.setSelectedTab(advancementtabgui.getAdvancement(), true);
               break;
            }
         }
      }

      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (this.minecraft.gameSettings.keyBindAdvancements.matchesKey(p_keyPressed_1_, p_keyPressed_2_)) {
         this.minecraft.displayGuiScreen((Screen)null);
         this.minecraft.mouseHelper.grabMouse();
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      int i = (this.width - 252) / 2;
      int j = (this.height - 140) / 2;
      this.renderBackground();
      this.renderInside(p_render_1_, p_render_2_, i, j);
      if (maxPages != 0) {
         String page = String.format("%d / %d", tabPage + 1, maxPages + 1);
         int width = this.font.getStringWidth(page);
         RenderSystem.disableLighting();
         this.font.drawStringWithShadow(page, i + (252 / 2) - (width / 2), j - 44, -1);
      }
      this.renderWindow(i, j);
      this.renderToolTips(p_render_1_, p_render_2_, i, j);
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      if (p_mouseDragged_5_ != 0) {
         this.isScrolling = false;
         return false;
      } else {
         if (!this.isScrolling) {
            this.isScrolling = true;
         } else if (this.selectedTab != null) {
            this.selectedTab.func_195626_a(p_mouseDragged_6_, p_mouseDragged_8_);
         }

         return true;
      }
   }

   private void renderInside(int p_191936_1_, int p_191936_2_, int p_191936_3_, int p_191936_4_) {
      AdvancementTabGui advancementtabgui = this.selectedTab;
      if (advancementtabgui == null) {
         fill(p_191936_3_ + 9, p_191936_4_ + 18, p_191936_3_ + 9 + 234, p_191936_4_ + 18 + 113, -16777216);
         String s = I18n.format("advancements.empty");
         int i = this.font.getStringWidth(s);
         this.font.drawString(s, (float)(p_191936_3_ + 9 + 117 - i / 2), (float)(p_191936_4_ + 18 + 56 - 9 / 2), -1);
         this.font.drawString(":(", (float)(p_191936_3_ + 9 + 117 - this.font.getStringWidth(":(") / 2), (float)(p_191936_4_ + 18 + 113 - 9), -1);
      } else {
         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)(p_191936_3_ + 9), (float)(p_191936_4_ + 18), 0.0F);
         advancementtabgui.drawContents();
         RenderSystem.popMatrix();
         RenderSystem.depthFunc(515);
         RenderSystem.disableDepthTest();
      }
   }

   public void renderWindow(int p_191934_1_, int p_191934_2_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableBlend();
      this.minecraft.getTextureManager().bindTexture(WINDOW);
      this.blit(p_191934_1_, p_191934_2_, 0, 0, 252, 140);
      if (this.tabs.size() > 1) {
         this.minecraft.getTextureManager().bindTexture(TABS);

         for(AdvancementTabGui advancementtabgui : this.tabs.values()) {
            if (advancementtabgui.getPage() == tabPage)
            advancementtabgui.drawTab(p_191934_1_, p_191934_2_, advancementtabgui == this.selectedTab);
         }

         RenderSystem.enableRescaleNormal();
         RenderSystem.defaultBlendFunc();

         for(AdvancementTabGui advancementtabgui1 : this.tabs.values()) {
            if (advancementtabgui1.getPage() == tabPage)
            advancementtabgui1.drawIcon(p_191934_1_, p_191934_2_, this.itemRenderer);
         }

         RenderSystem.disableBlend();
      }

      this.font.drawString(I18n.format("gui.advancements"), (float)(p_191934_1_ + 8), (float)(p_191934_2_ + 6), 4210752);
   }

   private void renderToolTips(int p_191937_1_, int p_191937_2_, int p_191937_3_, int p_191937_4_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.selectedTab != null) {
         RenderSystem.pushMatrix();
         RenderSystem.enableDepthTest();
         RenderSystem.translatef((float)(p_191937_3_ + 9), (float)(p_191937_4_ + 18), 400.0F);
         this.selectedTab.drawToolTips(p_191937_1_ - p_191937_3_ - 9, p_191937_2_ - p_191937_4_ - 18, p_191937_3_, p_191937_4_);
         RenderSystem.disableDepthTest();
         RenderSystem.popMatrix();
      }

      if (this.tabs.size() > 1) {
         for(AdvancementTabGui advancementtabgui : this.tabs.values()) {
            if (advancementtabgui.getPage() == tabPage && advancementtabgui.func_195627_a(p_191937_3_, p_191937_4_, (double)p_191937_1_, (double)p_191937_2_)) {
               this.renderTooltip(advancementtabgui.getTitle(), p_191937_1_, p_191937_2_);
            }
         }
      }

   }

   public void rootAdvancementAdded(Advancement advancementIn) {
      AdvancementTabGui advancementtabgui = AdvancementTabGui.create(this.minecraft, this, this.tabs.size(), advancementIn);
      if (advancementtabgui != null) {
         this.tabs.put(advancementIn, advancementtabgui);
      }
   }

   public void rootAdvancementRemoved(Advancement advancementIn) {
   }

   public void nonRootAdvancementAdded(Advancement advancementIn) {
      AdvancementTabGui advancementtabgui = this.getTab(advancementIn);
      if (advancementtabgui != null) {
         advancementtabgui.addAdvancement(advancementIn);
      }

   }

   public void nonRootAdvancementRemoved(Advancement advancementIn) {
   }

   public void onUpdateAdvancementProgress(Advancement advancementIn, AdvancementProgress progress) {
      AdvancementEntryGui advancemententrygui = this.getAdvancementGui(advancementIn);
      if (advancemententrygui != null) {
         advancemententrygui.setAdvancementProgress(progress);
      }

   }

   public void setSelectedTab(@Nullable Advancement advancementIn) {
      this.selectedTab = this.tabs.get(advancementIn);
   }

   public void advancementsCleared() {
      this.tabs.clear();
      this.selectedTab = null;
   }

   @Nullable
   public AdvancementEntryGui getAdvancementGui(Advancement p_191938_1_) {
      AdvancementTabGui advancementtabgui = this.getTab(p_191938_1_);
      return advancementtabgui == null ? null : advancementtabgui.getAdvancementGui(p_191938_1_);
   }

   @Nullable
   private AdvancementTabGui getTab(Advancement p_191935_1_) {
      while(p_191935_1_.getParent() != null) {
         p_191935_1_ = p_191935_1_.getParent();
      }

      return this.tabs.get(p_191935_1_);
   }
}