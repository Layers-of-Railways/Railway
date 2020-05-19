package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsSettingsScreen extends RealmsScreen {
   private final RealmsConfigureWorldScreen field_224565_a;
   private final RealmsServer field_224566_b;
   private final int field_224567_c = 212;
   private RealmsButton field_224568_d;
   private RealmsEditBox field_224569_e;
   private RealmsEditBox field_224570_f;
   private RealmsLabel field_224571_g;

   public RealmsSettingsScreen(RealmsConfigureWorldScreen p_i51751_1_, RealmsServer p_i51751_2_) {
      this.field_224565_a = p_i51751_1_;
      this.field_224566_b = p_i51751_2_;
   }

   public void tick() {
      this.field_224570_f.tick();
      this.field_224569_e.tick();
      this.field_224568_d.active(this.field_224570_f.getValue() != null && !this.field_224570_f.getValue().trim().isEmpty());
   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      int i = this.width() / 2 - 106;
      this.buttonsAdd(this.field_224568_d = new RealmsButton(1, i - 2, RealmsConstants.func_225109_a(12), 106, 20, getLocalizedString("mco.configure.world.buttons.done")) {
         public void onPress() {
            RealmsSettingsScreen.this.func_224563_a();
         }
      });
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 + 2, RealmsConstants.func_225109_a(12), 106, 20, getLocalizedString("gui.cancel")) {
         public void onPress() {
            Realms.setScreen(RealmsSettingsScreen.this.field_224565_a);
         }
      });
      this.buttonsAdd(new RealmsButton(5, this.width() / 2 - 53, RealmsConstants.func_225109_a(0), 106, 20, getLocalizedString(this.field_224566_b.state.equals(RealmsServer.Status.OPEN) ? "mco.configure.world.buttons.close" : "mco.configure.world.buttons.open")) {
         public void onPress() {
            if (RealmsSettingsScreen.this.field_224566_b.state.equals(RealmsServer.Status.OPEN)) {
               String s = RealmsScreen.getLocalizedString("mco.configure.world.close.question.line1");
               String s1 = RealmsScreen.getLocalizedString("mco.configure.world.close.question.line2");
               Realms.setScreen(new RealmsLongConfirmationScreen(RealmsSettingsScreen.this, RealmsLongConfirmationScreen.Type.Info, s, s1, true, 5));
            } else {
               RealmsSettingsScreen.this.field_224565_a.func_224383_a(false, RealmsSettingsScreen.this);
            }

         }
      });
      this.field_224570_f = this.newEditBox(2, i, RealmsConstants.func_225109_a(4), 212, 20, getLocalizedString("mco.configure.world.name"));
      this.field_224570_f.setMaxLength(32);
      if (this.field_224566_b.getName() != null) {
         this.field_224570_f.setValue(this.field_224566_b.getName());
      }

      this.addWidget(this.field_224570_f);
      this.focusOn(this.field_224570_f);
      this.field_224569_e = this.newEditBox(3, i, RealmsConstants.func_225109_a(8), 212, 20, getLocalizedString("mco.configure.world.description"));
      this.field_224569_e.setMaxLength(32);
      if (this.field_224566_b.getDescription() != null) {
         this.field_224569_e.setValue(this.field_224566_b.getDescription());
      }

      this.addWidget(this.field_224569_e);
      this.addWidget(this.field_224571_g = new RealmsLabel(getLocalizedString("mco.configure.world.settings.title"), this.width() / 2, 17, 16777215));
      this.narrateLabels();
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      switch(p_confirmResult_2_) {
      case 5:
         if (p_confirmResult_1_) {
            this.field_224565_a.func_224405_a(this);
         } else {
            Realms.setScreen(this);
         }
      default:
      }
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      switch(p_keyPressed_1_) {
      case 256:
         Realms.setScreen(this.field_224565_a);
         return true;
      default:
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.field_224571_g.render(this);
      this.drawString(getLocalizedString("mco.configure.world.name"), this.width() / 2 - 106, RealmsConstants.func_225109_a(3), 10526880);
      this.drawString(getLocalizedString("mco.configure.world.description"), this.width() / 2 - 106, RealmsConstants.func_225109_a(7), 10526880);
      this.field_224570_f.render(p_render_1_, p_render_2_, p_render_3_);
      this.field_224569_e.render(p_render_1_, p_render_2_, p_render_3_);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public void func_224563_a() {
      this.field_224565_a.func_224410_a(this.field_224570_f.getValue(), this.field_224569_e.getValue());
   }
}