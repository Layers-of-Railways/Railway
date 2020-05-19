package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsTasks;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsTermsScreen extends RealmsScreen {
   private static final Logger field_224722_a = LogManager.getLogger();
   private final RealmsScreen field_224723_b;
   private final RealmsMainScreen field_224724_c;
   /**
    * The screen to display when OK is clicked on the disconnect screen.
    *  
    * Seems to be either null (integrated server) or an instance of either {@link MultiplayerScreen} (when connecting to
    * a server) or {@link com.mojang.realmsclient.gui.screens.RealmsTermsScreen} (when connecting to MCO server)
    */
   private final RealmsServer guiScreenServer;
   private RealmsButton field_224726_e;
   private boolean field_224727_f;
   private final String field_224728_g = "https://minecraft.net/realms/terms";

   public RealmsTermsScreen(RealmsScreen p_i51748_1_, RealmsMainScreen p_i51748_2_, RealmsServer p_i51748_3_) {
      this.field_224723_b = p_i51748_1_;
      this.field_224724_c = p_i51748_2_;
      this.guiScreenServer = p_i51748_3_;
   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      int i = this.width() / 4;
      int j = this.width() / 4 - 2;
      int k = this.width() / 2 + 4;
      this.buttonsAdd(this.field_224726_e = new RealmsButton(1, i, RealmsConstants.func_225109_a(12), j, 20, getLocalizedString("mco.terms.buttons.agree")) {
         public void onPress() {
            RealmsTermsScreen.this.func_224721_a();
         }
      });
      this.buttonsAdd(new RealmsButton(2, k, RealmsConstants.func_225109_a(12), j, 20, getLocalizedString("mco.terms.buttons.disagree")) {
         public void onPress() {
            Realms.setScreen(RealmsTermsScreen.this.field_224723_b);
         }
      });
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         Realms.setScreen(this.field_224723_b);
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   private void func_224721_a() {
      RealmsClient realmsclient = RealmsClient.func_224911_a();

      try {
         realmsclient.func_224937_l();
         RealmsLongRunningMcoTaskScreen realmslongrunningmcotaskscreen = new RealmsLongRunningMcoTaskScreen(this.field_224723_b, new RealmsTasks.RealmsGetServerDetailsTask(this.field_224724_c, this.field_224723_b, this.guiScreenServer, new ReentrantLock()));
         realmslongrunningmcotaskscreen.func_224233_a();
         Realms.setScreen(realmslongrunningmcotaskscreen);
      } catch (RealmsServiceException var3) {
         field_224722_a.error("Couldn't agree to TOS");
      }

   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.field_224727_f) {
         Realms.setClipboard("https://minecraft.net/realms/terms");
         RealmsUtil.func_225190_c("https://minecraft.net/realms/terms");
         return true;
      } else {
         return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(getLocalizedString("mco.terms.title"), this.width() / 2, 17, 16777215);
      this.drawString(getLocalizedString("mco.terms.sentence.1"), this.width() / 2 - 120, RealmsConstants.func_225109_a(5), 16777215);
      int i = this.fontWidth(getLocalizedString("mco.terms.sentence.1"));
      int j = this.width() / 2 - 121 + i;
      int k = RealmsConstants.func_225109_a(5);
      int l = j + this.fontWidth("mco.terms.sentence.2") + 1;
      int i1 = k + 1 + this.fontLineHeight();
      if (j <= p_render_1_ && p_render_1_ <= l && k <= p_render_2_ && p_render_2_ <= i1) {
         this.field_224727_f = true;
         this.drawString(" " + getLocalizedString("mco.terms.sentence.2"), this.width() / 2 - 120 + i, RealmsConstants.func_225109_a(5), 7107012);
      } else {
         this.field_224727_f = false;
         this.drawString(" " + getLocalizedString("mco.terms.sentence.2"), this.width() / 2 - 120 + i, RealmsConstants.func_225109_a(5), 3368635);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}