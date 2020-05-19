package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.AbstractRealmsButton;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsConfirmScreen extends RealmsScreen {
   protected RealmsScreen field_224141_a;
   protected String field_224142_b;
   private final String field_224146_f;
   protected String field_224143_c;
   protected String field_224144_d;
   protected int field_224145_e;
   private int field_224147_g;

   public RealmsConfirmScreen(RealmsScreen p_i51773_1_, String p_i51773_2_, String p_i51773_3_, int p_i51773_4_) {
      this.field_224141_a = p_i51773_1_;
      this.field_224142_b = p_i51773_2_;
      this.field_224146_f = p_i51773_3_;
      this.field_224145_e = p_i51773_4_;
      this.field_224143_c = getLocalizedString("gui.yes");
      this.field_224144_d = getLocalizedString("gui.no");
   }

   public void init() {
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 105, RealmsConstants.func_225109_a(9), 100, 20, this.field_224143_c) {
         public void onPress() {
            RealmsConfirmScreen.this.field_224141_a.confirmResult(true, RealmsConfirmScreen.this.field_224145_e);
         }
      });
      this.buttonsAdd(new RealmsButton(1, this.width() / 2 + 5, RealmsConstants.func_225109_a(9), 100, 20, this.field_224144_d) {
         public void onPress() {
            RealmsConfirmScreen.this.field_224141_a.confirmResult(false, RealmsConfirmScreen.this.field_224145_e);
         }
      });
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.field_224142_b, this.width() / 2, RealmsConstants.func_225109_a(3), 16777215);
      this.drawCenteredString(this.field_224146_f, this.width() / 2, RealmsConstants.func_225109_a(5), 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public void tick() {
      super.tick();
      if (--this.field_224147_g == 0) {
         for(AbstractRealmsButton<?> abstractrealmsbutton : this.buttons()) {
            abstractrealmsbutton.active(true);
         }
      }

   }
}