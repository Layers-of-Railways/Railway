package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsClientOutdatedScreen extends RealmsScreen {
   private final RealmsScreen field_224129_a;
   private final boolean field_224130_b;

   public RealmsClientOutdatedScreen(RealmsScreen p_i51775_1_, boolean p_i51775_2_) {
      this.field_224129_a = p_i51775_1_;
      this.field_224130_b = p_i51775_2_;
   }

   public void init() {
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 100, RealmsConstants.func_225109_a(12), getLocalizedString("gui.back")) {
         public void onPress() {
            Realms.setScreen(RealmsClientOutdatedScreen.this.field_224129_a);
         }
      });
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      String s = getLocalizedString(this.field_224130_b ? "mco.client.outdated.title" : "mco.client.incompatible.title");
      this.drawCenteredString(s, this.width() / 2, RealmsConstants.func_225109_a(3), 16711680);
      int i = this.field_224130_b ? 2 : 3;

      for(int j = 0; j < i; ++j) {
         String s1 = getLocalizedString((this.field_224130_b ? "mco.client.outdated.msg.line" : "mco.client.incompatible.msg.line") + (j + 1));
         this.drawCenteredString(s1, this.width() / 2, RealmsConstants.func_225109_a(5) + j * 12, 16777215);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335 && p_keyPressed_1_ != 256) {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      } else {
         Realms.setScreen(this.field_224129_a);
         return true;
      }
   }
}