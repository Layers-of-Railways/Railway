package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.List;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsParentalConsentScreen extends RealmsScreen {
   private final RealmsScreen field_224260_a;

   public RealmsParentalConsentScreen(RealmsScreen p_i51762_1_) {
      this.field_224260_a = p_i51762_1_;
   }

   public void init() {
      Realms.narrateNow(getLocalizedString("mco.account.privacyinfo"));
      String s = getLocalizedString("mco.account.update");
      String s1 = getLocalizedString("gui.back");
      int i = Math.max(this.fontWidth(s), this.fontWidth(s1)) + 30;
      String s2 = getLocalizedString("mco.account.privacy.info");
      int j = (int)((double)this.fontWidth(s2) * 1.2D);
      this.buttonsAdd(new RealmsButton(1, this.width() / 2 - j / 2, RealmsConstants.func_225109_a(11), j, 20, s2) {
         public void onPress() {
            RealmsUtil.func_225190_c("https://minecraft.net/privacy/gdpr/");
         }
      });
      this.buttonsAdd(new RealmsButton(1, this.width() / 2 - (i + 5), RealmsConstants.func_225109_a(13), i, 20, s) {
         public void onPress() {
            RealmsUtil.func_225190_c("https://minecraft.net/update-account");
         }
      });
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 + 5, RealmsConstants.func_225109_a(13), i, 20, s1) {
         public void onPress() {
            Realms.setScreen(RealmsParentalConsentScreen.this.field_224260_a);
         }
      });
   }

   public void tick() {
      super.tick();
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      List<String> list = this.getLocalizedStringWithLineWidth("mco.account.privacyinfo", (int)Math.round((double)this.width() * 0.9D));
      int i = 15;

      for(String s : list) {
         this.drawCenteredString(s, this.width() / 2, i, 16777215);
         i += 15;
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}