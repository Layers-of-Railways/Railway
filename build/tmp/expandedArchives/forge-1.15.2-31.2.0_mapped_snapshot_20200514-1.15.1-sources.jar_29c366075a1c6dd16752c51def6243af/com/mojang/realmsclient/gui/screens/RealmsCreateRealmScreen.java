package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.util.RealmsTasks;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsCreateRealmScreen extends RealmsScreen {
   private final RealmsServer field_224135_a;
   private final RealmsMainScreen field_224136_b;
   private RealmsEditBox field_224137_c;
   private RealmsEditBox field_224138_d;
   private RealmsButton field_224139_e;
   private RealmsLabel field_224140_f;

   public RealmsCreateRealmScreen(RealmsServer p_i51772_1_, RealmsMainScreen p_i51772_2_) {
      this.field_224135_a = p_i51772_1_;
      this.field_224136_b = p_i51772_2_;
   }

   public void tick() {
      if (this.field_224137_c != null) {
         this.field_224137_c.tick();
      }

      if (this.field_224138_d != null) {
         this.field_224138_d.tick();
      }

   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.buttonsAdd(this.field_224139_e = new RealmsButton(0, this.width() / 2 - 100, this.height() / 4 + 120 + 17, 97, 20, getLocalizedString("mco.create.world")) {
         public void onPress() {
            RealmsCreateRealmScreen.this.func_224132_a();
         }
      });
      this.buttonsAdd(new RealmsButton(1, this.width() / 2 + 5, this.height() / 4 + 120 + 17, 95, 20, getLocalizedString("gui.cancel")) {
         public void onPress() {
            Realms.setScreen(RealmsCreateRealmScreen.this.field_224136_b);
         }
      });
      this.field_224139_e.active(false);
      this.field_224137_c = this.newEditBox(3, this.width() / 2 - 100, 65, 200, 20, getLocalizedString("mco.configure.world.name"));
      this.addWidget(this.field_224137_c);
      this.focusOn(this.field_224137_c);
      this.field_224138_d = this.newEditBox(4, this.width() / 2 - 100, 115, 200, 20, getLocalizedString("mco.configure.world.description"));
      this.addWidget(this.field_224138_d);
      this.field_224140_f = new RealmsLabel(getLocalizedString("mco.selectServer.create"), this.width() / 2, 11, 16777215);
      this.addWidget(this.field_224140_f);
      this.narrateLabels();
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      this.field_224139_e.active(this.func_224133_b());
      return false;
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      switch(p_keyPressed_1_) {
      case 256:
         Realms.setScreen(this.field_224136_b);
         return true;
      default:
         this.field_224139_e.active(this.func_224133_b());
         return false;
      }
   }

   private void func_224132_a() {
      if (this.func_224133_b()) {
         RealmsResetWorldScreen realmsresetworldscreen = new RealmsResetWorldScreen(this.field_224136_b, this.field_224135_a, this.field_224136_b.func_223942_f(), getLocalizedString("mco.selectServer.create"), getLocalizedString("mco.create.world.subtitle"), 10526880, getLocalizedString("mco.create.world.skip"));
         realmsresetworldscreen.func_224432_a(getLocalizedString("mco.create.world.reset.title"));
         RealmsTasks.WorldCreationTask realmstasks$worldcreationtask = new RealmsTasks.WorldCreationTask(this.field_224135_a.id, this.field_224137_c.getValue(), this.field_224138_d.getValue(), realmsresetworldscreen);
         RealmsLongRunningMcoTaskScreen realmslongrunningmcotaskscreen = new RealmsLongRunningMcoTaskScreen(this.field_224136_b, realmstasks$worldcreationtask);
         realmslongrunningmcotaskscreen.func_224233_a();
         Realms.setScreen(realmslongrunningmcotaskscreen);
      }

   }

   private boolean func_224133_b() {
      return this.field_224137_c.getValue() != null && !this.field_224137_c.getValue().trim().isEmpty();
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.field_224140_f.render(this);
      this.drawString(getLocalizedString("mco.configure.world.name"), this.width() / 2 - 100, 52, 10526880);
      this.drawString(getLocalizedString("mco.configure.world.description"), this.width() / 2 - 100, 102, 10526880);
      if (this.field_224137_c != null) {
         this.field_224137_c.render(p_render_1_, p_render_2_, p_render_3_);
      }

      if (this.field_224138_d != null) {
         this.field_224138_d.render(p_render_1_, p_render_2_, p_render_3_);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}