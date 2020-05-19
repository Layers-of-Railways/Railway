package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsTextureManager;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsDefaultVertexFormat;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.Tezzelator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsPlayerScreen extends RealmsScreen {
   private static final Logger field_224300_a = LogManager.getLogger();
   private String field_224301_b;
   private final RealmsConfigureWorldScreen field_224302_c;
   private final RealmsServer field_224303_d;
   private RealmsPlayerScreen.InvitedList field_224304_e;
   private int field_224305_f;
   private int field_224306_g;
   private int field_224307_h;
   private RealmsButton field_224308_i;
   private RealmsButton field_224309_j;
   private int field_224310_k = -1;
   private String field_224311_l;
   private int field_224312_m = -1;
   private boolean field_224313_n;
   private RealmsLabel field_224314_o;

   public RealmsPlayerScreen(RealmsConfigureWorldScreen p_i51760_1_, RealmsServer p_i51760_2_) {
      this.field_224302_c = p_i51760_1_;
      this.field_224303_d = p_i51760_2_;
   }

   public void tick() {
      super.tick();
   }

   public void init() {
      this.field_224305_f = this.width() / 2 - 160;
      this.field_224306_g = 150;
      this.field_224307_h = this.width() / 2 + 12;
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.buttonsAdd(new RealmsButton(1, this.field_224307_h, RealmsConstants.func_225109_a(1), this.field_224306_g + 10, 20, getLocalizedString("mco.configure.world.buttons.invite")) {
         public void onPress() {
            Realms.setScreen(new RealmsInviteScreen(RealmsPlayerScreen.this.field_224302_c, RealmsPlayerScreen.this, RealmsPlayerScreen.this.field_224303_d));
         }
      });
      this.buttonsAdd(this.field_224308_i = new RealmsButton(4, this.field_224307_h, RealmsConstants.func_225109_a(7), this.field_224306_g + 10, 20, getLocalizedString("mco.configure.world.invites.remove.tooltip")) {
         public void onPress() {
            RealmsPlayerScreen.this.func_224274_d(RealmsPlayerScreen.this.field_224312_m);
         }
      });
      this.buttonsAdd(this.field_224309_j = new RealmsButton(5, this.field_224307_h, RealmsConstants.func_225109_a(9), this.field_224306_g + 10, 20, getLocalizedString("mco.configure.world.invites.ops.tooltip")) {
         public void onPress() {
            if (RealmsPlayerScreen.this.field_224303_d.players.get(RealmsPlayerScreen.this.field_224312_m).isOperator()) {
               RealmsPlayerScreen.this.func_224279_c(RealmsPlayerScreen.this.field_224312_m);
            } else {
               RealmsPlayerScreen.this.func_224289_b(RealmsPlayerScreen.this.field_224312_m);
            }

         }
      });
      this.buttonsAdd(new RealmsButton(0, this.field_224307_h + this.field_224306_g / 2 + 2, RealmsConstants.func_225109_a(12), this.field_224306_g / 2 + 10 - 2, 20, getLocalizedString("gui.back")) {
         public void onPress() {
            RealmsPlayerScreen.this.func_224298_b();
         }
      });
      this.field_224304_e = new RealmsPlayerScreen.InvitedList();
      this.field_224304_e.setLeftPos(this.field_224305_f);
      this.addWidget(this.field_224304_e);

      for(PlayerInfo playerinfo : this.field_224303_d.players) {
         this.field_224304_e.func_223870_a(playerinfo);
      }

      this.addWidget(this.field_224314_o = new RealmsLabel(getLocalizedString("mco.configure.world.players.title"), this.width() / 2, 17, 16777215));
      this.narrateLabels();
      this.func_224280_a();
   }

   private void func_224280_a() {
      this.field_224308_i.setVisible(this.func_224296_a(this.field_224312_m));
      this.field_224309_j.setVisible(this.func_224296_a(this.field_224312_m));
   }

   private boolean func_224296_a(int p_224296_1_) {
      return p_224296_1_ != -1;
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         this.func_224298_b();
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   private void func_224298_b() {
      if (this.field_224313_n) {
         Realms.setScreen(this.field_224302_c.func_224407_b());
      } else {
         Realms.setScreen(this.field_224302_c);
      }

   }

   private void func_224289_b(int p_224289_1_) {
      this.func_224280_a();
      RealmsClient realmsclient = RealmsClient.func_224911_a();
      String s = this.field_224303_d.players.get(p_224289_1_).getUuid();

      try {
         this.func_224283_a(realmsclient.func_224906_e(this.field_224303_d.id, s));
      } catch (RealmsServiceException var5) {
         field_224300_a.error("Couldn't op the user");
      }

   }

   private void func_224279_c(int p_224279_1_) {
      this.func_224280_a();
      RealmsClient realmsclient = RealmsClient.func_224911_a();
      String s = this.field_224303_d.players.get(p_224279_1_).getUuid();

      try {
         this.func_224283_a(realmsclient.func_224929_f(this.field_224303_d.id, s));
      } catch (RealmsServiceException var5) {
         field_224300_a.error("Couldn't deop the user");
      }

   }

   private void func_224283_a(Ops p_224283_1_) {
      for(PlayerInfo playerinfo : this.field_224303_d.players) {
         playerinfo.setOperator(p_224283_1_.ops.contains(playerinfo.getName()));
      }

   }

   private void func_224274_d(int p_224274_1_) {
      this.func_224280_a();
      if (p_224274_1_ >= 0 && p_224274_1_ < this.field_224303_d.players.size()) {
         PlayerInfo playerinfo = this.field_224303_d.players.get(p_224274_1_);
         this.field_224311_l = playerinfo.getUuid();
         this.field_224310_k = p_224274_1_;
         RealmsConfirmScreen realmsconfirmscreen = new RealmsConfirmScreen(this, "Question", getLocalizedString("mco.configure.world.uninvite.question") + " '" + playerinfo.getName() + "' ?", 2);
         Realms.setScreen(realmsconfirmscreen);
      }

   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      if (p_confirmResult_2_ == 2) {
         if (p_confirmResult_1_) {
            RealmsClient realmsclient = RealmsClient.func_224911_a();

            try {
               realmsclient.func_224908_a(this.field_224303_d.id, this.field_224311_l);
            } catch (RealmsServiceException var5) {
               field_224300_a.error("Couldn't uninvite user");
            }

            this.func_224292_e(this.field_224310_k);
            this.field_224312_m = -1;
            this.func_224280_a();
         }

         this.field_224313_n = true;
         Realms.setScreen(this);
      }

   }

   private void func_224292_e(int p_224292_1_) {
      this.field_224303_d.players.remove(p_224292_1_);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.field_224301_b = null;
      this.renderBackground();
      if (this.field_224304_e != null) {
         this.field_224304_e.render(p_render_1_, p_render_2_, p_render_3_);
      }

      int i = RealmsConstants.func_225109_a(12) + 20;
      Tezzelator tezzelator = Tezzelator.instance;
      bind("textures/gui/options_background.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float f = 32.0F;
      tezzelator.begin(7, RealmsDefaultVertexFormat.POSITION_TEX_COLOR);
      tezzelator.vertex(0.0D, (double)this.height(), 0.0D).tex(0.0F, (float)(this.height() - i) / 32.0F + 0.0F).color(64, 64, 64, 255).endVertex();
      tezzelator.vertex((double)this.width(), (double)this.height(), 0.0D).tex((float)this.width() / 32.0F, (float)(this.height() - i) / 32.0F + 0.0F).color(64, 64, 64, 255).endVertex();
      tezzelator.vertex((double)this.width(), (double)i, 0.0D).tex((float)this.width() / 32.0F, 0.0F).color(64, 64, 64, 255).endVertex();
      tezzelator.vertex(0.0D, (double)i, 0.0D).tex(0.0F, 0.0F).color(64, 64, 64, 255).endVertex();
      tezzelator.end();
      this.field_224314_o.render(this);
      if (this.field_224303_d != null && this.field_224303_d.players != null) {
         this.drawString(getLocalizedString("mco.configure.world.invited") + " (" + this.field_224303_d.players.size() + ")", this.field_224305_f, RealmsConstants.func_225109_a(0), 10526880);
      } else {
         this.drawString(getLocalizedString("mco.configure.world.invited"), this.field_224305_f, RealmsConstants.func_225109_a(0), 10526880);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
      if (this.field_224303_d != null) {
         if (this.field_224301_b != null) {
            this.func_224277_a(this.field_224301_b, p_render_1_, p_render_2_);
         }

      }
   }

   protected void func_224277_a(String p_224277_1_, int p_224277_2_, int p_224277_3_) {
      if (p_224277_1_ != null) {
         int i = p_224277_2_ + 12;
         int j = p_224277_3_ - 12;
         int k = this.fontWidth(p_224277_1_);
         this.fillGradient(i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
         this.fontDrawShadow(p_224277_1_, i, j, 16777215);
      }
   }

   private void func_224291_a(int p_224291_1_, int p_224291_2_, int p_224291_3_, int p_224291_4_) {
      boolean flag = p_224291_3_ >= p_224291_1_ && p_224291_3_ <= p_224291_1_ + 9 && p_224291_4_ >= p_224291_2_ && p_224291_4_ <= p_224291_2_ + 9 && p_224291_4_ < RealmsConstants.func_225109_a(12) + 20 && p_224291_4_ > RealmsConstants.func_225109_a(1);
      bind("realms:textures/gui/realms/cross_player_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_224291_1_, p_224291_2_, 0.0F, flag ? 7.0F : 0.0F, 8, 7, 8, 14);
      RenderSystem.popMatrix();
      if (flag) {
         this.field_224301_b = getLocalizedString("mco.configure.world.invites.remove.tooltip");
      }

   }

   private void func_224295_b(int p_224295_1_, int p_224295_2_, int p_224295_3_, int p_224295_4_) {
      boolean flag = p_224295_3_ >= p_224295_1_ && p_224295_3_ <= p_224295_1_ + 9 && p_224295_4_ >= p_224295_2_ && p_224295_4_ <= p_224295_2_ + 9 && p_224295_4_ < RealmsConstants.func_225109_a(12) + 20 && p_224295_4_ > RealmsConstants.func_225109_a(1);
      bind("realms:textures/gui/realms/op_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_224295_1_, p_224295_2_, 0.0F, flag ? 8.0F : 0.0F, 8, 8, 8, 16);
      RenderSystem.popMatrix();
      if (flag) {
         this.field_224301_b = getLocalizedString("mco.configure.world.invites.ops.tooltip");
      }

   }

   private void func_224294_c(int p_224294_1_, int p_224294_2_, int p_224294_3_, int p_224294_4_) {
      boolean flag = p_224294_3_ >= p_224294_1_ && p_224294_3_ <= p_224294_1_ + 9 && p_224294_4_ >= p_224294_2_ && p_224294_4_ <= p_224294_2_ + 9 && p_224294_4_ < RealmsConstants.func_225109_a(12) + 20 && p_224294_4_ > RealmsConstants.func_225109_a(1);
      bind("realms:textures/gui/realms/user_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_224294_1_, p_224294_2_, 0.0F, flag ? 8.0F : 0.0F, 8, 8, 8, 16);
      RenderSystem.popMatrix();
      if (flag) {
         this.field_224301_b = getLocalizedString("mco.configure.world.invites.normal.tooltip");
      }

   }

   @OnlyIn(Dist.CLIENT)
   class InvitedEntry extends RealmListEntry {
      final PlayerInfo field_223746_a;

      public InvitedEntry(PlayerInfo p_i51614_2_) {
         this.field_223746_a = p_i51614_2_;
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         this.func_223745_a(this.field_223746_a, p_render_3_, p_render_2_, p_render_6_, p_render_7_);
      }

      private void func_223745_a(PlayerInfo p_223745_1_, int p_223745_2_, int p_223745_3_, int p_223745_4_, int p_223745_5_) {
         int i;
         if (!p_223745_1_.getAccepted()) {
            i = 10526880;
         } else if (p_223745_1_.getOnline()) {
            i = 8388479;
         } else {
            i = 16777215;
         }

         RealmsPlayerScreen.this.drawString(p_223745_1_.getName(), RealmsPlayerScreen.this.field_224305_f + 3 + 12, p_223745_3_ + 1, i);
         if (p_223745_1_.isOperator()) {
            RealmsPlayerScreen.this.func_224295_b(RealmsPlayerScreen.this.field_224305_f + RealmsPlayerScreen.this.field_224306_g - 10, p_223745_3_ + 1, p_223745_4_, p_223745_5_);
         } else {
            RealmsPlayerScreen.this.func_224294_c(RealmsPlayerScreen.this.field_224305_f + RealmsPlayerScreen.this.field_224306_g - 10, p_223745_3_ + 1, p_223745_4_, p_223745_5_);
         }

         RealmsPlayerScreen.this.func_224291_a(RealmsPlayerScreen.this.field_224305_f + RealmsPlayerScreen.this.field_224306_g - 22, p_223745_3_ + 2, p_223745_4_, p_223745_5_);
         RealmsPlayerScreen.this.drawString(RealmsScreen.getLocalizedString("mco.configure.world.activityfeed.disabled"), RealmsPlayerScreen.this.field_224307_h, RealmsConstants.func_225109_a(5), 10526880);
         RealmsTextureManager.func_225205_a(p_223745_1_.getUuid(), () -> {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RealmsScreen.blit(RealmsPlayerScreen.this.field_224305_f + 2 + 2, p_223745_3_ + 1, 8.0F, 8.0F, 8, 8, 8, 8, 64, 64);
            RealmsScreen.blit(RealmsPlayerScreen.this.field_224305_f + 2 + 2, p_223745_3_ + 1, 40.0F, 8.0F, 8, 8, 8, 8, 64, 64);
         });
      }
   }

   @OnlyIn(Dist.CLIENT)
   class InvitedList extends RealmsObjectSelectionList {
      public InvitedList() {
         super(RealmsPlayerScreen.this.field_224306_g + 10, RealmsConstants.func_225109_a(12) + 20, RealmsConstants.func_225109_a(1), RealmsConstants.func_225109_a(12) + 20, 13);
      }

      public void func_223870_a(PlayerInfo p_223870_1_) {
         this.addEntry(RealmsPlayerScreen.this.new InvitedEntry(p_223870_1_));
      }

      public int getRowWidth() {
         return (int)((double)this.width() * 1.0D);
      }

      public boolean isFocused() {
         return RealmsPlayerScreen.this.isFocused(this);
      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         if (p_mouseClicked_5_ == 0 && p_mouseClicked_1_ < (double)this.getScrollbarPosition() && p_mouseClicked_3_ >= (double)this.y0() && p_mouseClicked_3_ <= (double)this.y1()) {
            int i = RealmsPlayerScreen.this.field_224305_f;
            int j = RealmsPlayerScreen.this.field_224305_f + RealmsPlayerScreen.this.field_224306_g;
            int k = (int)Math.floor(p_mouseClicked_3_ - (double)this.y0()) - this.headerHeight() + this.getScroll() - 4;
            int l = k / this.itemHeight();
            if (p_mouseClicked_1_ >= (double)i && p_mouseClicked_1_ <= (double)j && l >= 0 && k >= 0 && l < this.getItemCount()) {
               this.selectItem(l);
               this.itemClicked(k, l, p_mouseClicked_1_, p_mouseClicked_3_, this.width());
            }

            return true;
         } else {
            return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
         }
      }

      public void itemClicked(int p_itemClicked_1_, int p_itemClicked_2_, double p_itemClicked_3_, double p_itemClicked_5_, int p_itemClicked_7_) {
         if (p_itemClicked_2_ >= 0 && p_itemClicked_2_ <= RealmsPlayerScreen.this.field_224303_d.players.size() && RealmsPlayerScreen.this.field_224301_b != null) {
            if (!RealmsPlayerScreen.this.field_224301_b.equals(RealmsScreen.getLocalizedString("mco.configure.world.invites.ops.tooltip")) && !RealmsPlayerScreen.this.field_224301_b.equals(RealmsScreen.getLocalizedString("mco.configure.world.invites.normal.tooltip"))) {
               if (RealmsPlayerScreen.this.field_224301_b.equals(RealmsScreen.getLocalizedString("mco.configure.world.invites.remove.tooltip"))) {
                  RealmsPlayerScreen.this.func_224274_d(p_itemClicked_2_);
               }
            } else if (RealmsPlayerScreen.this.field_224303_d.players.get(p_itemClicked_2_).isOperator()) {
               RealmsPlayerScreen.this.func_224279_c(p_itemClicked_2_);
            } else {
               RealmsPlayerScreen.this.func_224289_b(p_itemClicked_2_);
            }

         }
      }

      public void selectItem(int p_selectItem_1_) {
         this.setSelected(p_selectItem_1_);
         if (p_selectItem_1_ != -1) {
            Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", RealmsPlayerScreen.this.field_224303_d.players.get(p_selectItem_1_).getName()));
         }

         this.func_223869_a(p_selectItem_1_);
      }

      public void func_223869_a(int p_223869_1_) {
         RealmsPlayerScreen.this.field_224312_m = p_223869_1_;
         RealmsPlayerScreen.this.func_224280_a();
      }

      public void renderBackground() {
         RealmsPlayerScreen.this.renderBackground();
      }

      public int getScrollbarPosition() {
         return RealmsPlayerScreen.this.field_224305_f + this.width() - 5;
      }

      public int getItemCount() {
         return RealmsPlayerScreen.this.field_224303_d == null ? 1 : RealmsPlayerScreen.this.field_224303_d.players.size();
      }

      public int getMaxPosition() {
         return this.getItemCount() * 13;
      }
   }
}