package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsInviteScreen extends RealmsScreen {
   private static final Logger field_224213_a = LogManager.getLogger();
   private RealmsEditBox field_224214_b;
   private final RealmsServer field_224215_c;
   private final RealmsConfigureWorldScreen field_224216_d;
   private final RealmsScreen field_224217_e;
   private final int field_224218_f = 0;
   private final int field_224219_g = 1;
   private RealmsButton field_224220_h;
   private final int field_224221_i = 2;
   private String field_224222_j;
   private boolean field_224223_k;

   public RealmsInviteScreen(RealmsConfigureWorldScreen p_i51766_1_, RealmsScreen p_i51766_2_, RealmsServer p_i51766_3_) {
      this.field_224216_d = p_i51766_1_;
      this.field_224217_e = p_i51766_2_;
      this.field_224215_c = p_i51766_3_;
   }

   public void tick() {
      this.field_224214_b.tick();
   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.buttonsAdd(this.field_224220_h = new RealmsButton(0, this.width() / 2 - 100, RealmsConstants.func_225109_a(10), getLocalizedString("mco.configure.world.buttons.invite")) {
         public void onPress() {
            RealmsInviteScreen.this.func_224211_a();
         }
      });
      this.buttonsAdd(new RealmsButton(1, this.width() / 2 - 100, RealmsConstants.func_225109_a(12), getLocalizedString("gui.cancel")) {
         public void onPress() {
            Realms.setScreen(RealmsInviteScreen.this.field_224217_e);
         }
      });
      this.field_224214_b = this.newEditBox(2, this.width() / 2 - 100, RealmsConstants.func_225109_a(2), 200, 20, getLocalizedString("mco.configure.world.invite.profile.name"));
      this.focusOn(this.field_224214_b);
      this.addWidget(this.field_224214_b);
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   private void func_224211_a() {
      RealmsClient realmsclient = RealmsClient.func_224911_a();
      if (this.field_224214_b.getValue() != null && !this.field_224214_b.getValue().isEmpty()) {
         try {
            RealmsServer realmsserver = realmsclient.func_224910_b(this.field_224215_c.id, this.field_224214_b.getValue().trim());
            if (realmsserver != null) {
               this.field_224215_c.players = realmsserver.players;
               Realms.setScreen(new RealmsPlayerScreen(this.field_224216_d, this.field_224215_c));
            } else {
               this.func_224209_a(getLocalizedString("mco.configure.world.players.error"));
            }
         } catch (Exception var3) {
            field_224213_a.error("Couldn't invite user");
            this.func_224209_a(getLocalizedString("mco.configure.world.players.error"));
         }

      } else {
         this.func_224209_a(getLocalizedString("mco.configure.world.players.error"));
      }
   }

   private void func_224209_a(String p_224209_1_) {
      this.field_224223_k = true;
      this.field_224222_j = p_224209_1_;
      Realms.narrateNow(p_224209_1_);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         Realms.setScreen(this.field_224217_e);
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawString(getLocalizedString("mco.configure.world.invite.profile.name"), this.width() / 2 - 100, RealmsConstants.func_225109_a(1), 10526880);
      if (this.field_224223_k) {
         this.drawCenteredString(this.field_224222_j, this.width() / 2, RealmsConstants.func_225109_a(5), 16711680);
      }

      this.field_224214_b.render(p_render_1_, p_render_2_, p_render_3_);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}