package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsResetNormalWorldScreen extends RealmsScreen {
   private final RealmsResetWorldScreen field_224354_b;
   private RealmsLabel field_224355_c;
   private RealmsEditBox field_224356_d;
   private Boolean field_224357_e = true;
   private Integer field_224358_f = 0;
   String[] field_224353_a;
   private final int field_224359_g = 0;
   private final int field_224360_h = 1;
   private final int field_224361_i = 4;
   private RealmsButton field_224362_j;
   private RealmsButton field_224363_k;
   private RealmsButton field_224364_l;
   private String field_224365_m = getLocalizedString("mco.backup.button.reset");

   public RealmsResetNormalWorldScreen(RealmsResetWorldScreen p_i51758_1_) {
      this.field_224354_b = p_i51758_1_;
   }

   public RealmsResetNormalWorldScreen(RealmsResetWorldScreen p_i51759_1_, String p_i51759_2_) {
      this(p_i51759_1_);
      this.field_224365_m = p_i51759_2_;
   }

   public void tick() {
      this.field_224356_d.tick();
      super.tick();
   }

   public void init() {
      this.field_224353_a = new String[]{getLocalizedString("generator.default"), getLocalizedString("generator.flat"), getLocalizedString("generator.largeBiomes"), getLocalizedString("generator.amplified")};
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 + 8, RealmsConstants.func_225109_a(12), 97, 20, getLocalizedString("gui.back")) {
         public void onPress() {
            Realms.setScreen(RealmsResetNormalWorldScreen.this.field_224354_b);
         }
      });
      this.buttonsAdd(this.field_224362_j = new RealmsButton(1, this.width() / 2 - 102, RealmsConstants.func_225109_a(12), 97, 20, this.field_224365_m) {
         public void onPress() {
            RealmsResetNormalWorldScreen.this.func_224350_a();
         }
      });
      this.field_224356_d = this.newEditBox(4, this.width() / 2 - 100, RealmsConstants.func_225109_a(2), 200, 20, getLocalizedString("mco.reset.world.seed"));
      this.field_224356_d.setMaxLength(32);
      this.field_224356_d.setValue("");
      this.addWidget(this.field_224356_d);
      this.focusOn(this.field_224356_d);
      this.buttonsAdd(this.field_224363_k = new RealmsButton(2, this.width() / 2 - 102, RealmsConstants.func_225109_a(4), 205, 20, this.func_224347_b()) {
         public void onPress() {
            RealmsResetNormalWorldScreen.this.field_224358_f = (RealmsResetNormalWorldScreen.this.field_224358_f + 1) % RealmsResetNormalWorldScreen.this.field_224353_a.length;
            this.setMessage(RealmsResetNormalWorldScreen.this.func_224347_b());
         }
      });
      this.buttonsAdd(this.field_224364_l = new RealmsButton(3, this.width() / 2 - 102, RealmsConstants.func_225109_a(6) - 2, 205, 20, this.func_224351_c()) {
         public void onPress() {
            RealmsResetNormalWorldScreen.this.field_224357_e = !RealmsResetNormalWorldScreen.this.field_224357_e;
            this.setMessage(RealmsResetNormalWorldScreen.this.func_224351_c());
         }
      });
      this.field_224355_c = new RealmsLabel(getLocalizedString("mco.reset.world.generate"), this.width() / 2, 17, 16777215);
      this.addWidget(this.field_224355_c);
      this.narrateLabels();
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         Realms.setScreen(this.field_224354_b);
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   private void func_224350_a() {
      this.field_224354_b.func_224438_a(new RealmsResetWorldScreen.ResetWorldInfo(this.field_224356_d.getValue(), this.field_224358_f, this.field_224357_e));
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.field_224355_c.render(this);
      this.drawString(getLocalizedString("mco.reset.world.seed"), this.width() / 2 - 100, RealmsConstants.func_225109_a(1), 10526880);
      this.field_224356_d.render(p_render_1_, p_render_2_, p_render_3_);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   private String func_224347_b() {
      String s = getLocalizedString("selectWorld.mapType");
      return s + " " + this.field_224353_a[this.field_224358_f];
   }

   private String func_224351_c() {
      return getLocalizedString("selectWorld.mapFeatures") + " " + getLocalizedString(this.field_224357_e ? "mco.configure.world.on" : "mco.configure.world.off");
   }
}