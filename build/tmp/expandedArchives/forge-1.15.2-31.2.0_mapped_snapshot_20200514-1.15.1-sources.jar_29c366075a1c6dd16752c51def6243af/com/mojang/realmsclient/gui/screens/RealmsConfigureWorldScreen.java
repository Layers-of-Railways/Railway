package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.gui.RealmsServerSlotButton;
import com.mojang.realmsclient.util.RealmsTasks;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.annotation.Nullable;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsConfigureWorldScreen extends RealmsScreenWithCallback<WorldTemplate> implements RealmsServerSlotButton.IHandler {
   private static final Logger field_224413_a = LogManager.getLogger();
   private String field_224414_b;
   private final RealmsMainScreen field_224415_c;
   @Nullable
   private RealmsServer field_224416_d;
   private final long field_224417_e;
   private int field_224418_f;
   private int field_224419_g;
   private final int field_224420_h = 80;
   private final int field_224421_i = 5;
   private RealmsButton field_224422_j;
   private RealmsButton field_224423_k;
   private RealmsButton field_224424_l;
   private RealmsButton field_224425_m;
   private RealmsButton field_224426_n;
   private RealmsButton field_224427_o;
   private RealmsButton field_224428_p;
   private boolean field_224429_q;
   private int field_224430_r;
   private int field_224431_s;

   public RealmsConfigureWorldScreen(RealmsMainScreen p_i51774_1_, long p_i51774_2_) {
      this.field_224415_c = p_i51774_1_;
      this.field_224417_e = p_i51774_2_;
   }

   public void init() {
      if (this.field_224416_d == null) {
         this.func_224387_a(this.field_224417_e);
      }

      this.field_224418_f = this.width() / 2 - 187;
      this.field_224419_g = this.width() / 2 + 190;
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.buttonsAdd(this.field_224422_j = new RealmsButton(2, this.func_224374_a(0, 3), RealmsConstants.func_225109_a(0), 100, 20, getLocalizedString("mco.configure.world.buttons.players")) {
         public void onPress() {
            Realms.setScreen(new RealmsPlayerScreen(RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.field_224416_d));
         }
      });
      this.buttonsAdd(this.field_224423_k = new RealmsButton(3, this.func_224374_a(1, 3), RealmsConstants.func_225109_a(0), 100, 20, getLocalizedString("mco.configure.world.buttons.settings")) {
         public void onPress() {
            Realms.setScreen(new RealmsSettingsScreen(RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.field_224416_d.clone()));
         }
      });
      this.buttonsAdd(this.field_224424_l = new RealmsButton(4, this.func_224374_a(2, 3), RealmsConstants.func_225109_a(0), 100, 20, getLocalizedString("mco.configure.world.buttons.subscription")) {
         public void onPress() {
            Realms.setScreen(new RealmsSubscriptionInfoScreen(RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.field_224416_d.clone(), RealmsConfigureWorldScreen.this.field_224415_c));
         }
      });

      for(int i = 1; i < 5; ++i) {
         this.func_224402_a(i);
      }

      this.buttonsAdd(this.field_224428_p = new RealmsButton(8, this.func_224411_b(0), RealmsConstants.func_225109_a(13) - 5, 100, 20, getLocalizedString("mco.configure.world.buttons.switchminigame")) {
         public void onPress() {
            RealmsSelectWorldTemplateScreen realmsselectworldtemplatescreen = new RealmsSelectWorldTemplateScreen(RealmsConfigureWorldScreen.this, RealmsServer.ServerType.MINIGAME);
            realmsselectworldtemplatescreen.func_224483_a(RealmsScreen.getLocalizedString("mco.template.title.minigame"));
            Realms.setScreen(realmsselectworldtemplatescreen);
         }
      });
      this.buttonsAdd(this.field_224425_m = new RealmsButton(5, this.func_224411_b(0), RealmsConstants.func_225109_a(13) - 5, 90, 20, getLocalizedString("mco.configure.world.buttons.options")) {
         public void onPress() {
            Realms.setScreen(new RealmsSlotOptionsScreen(RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.field_224416_d.slots.get(RealmsConfigureWorldScreen.this.field_224416_d.activeSlot).clone(), RealmsConfigureWorldScreen.this.field_224416_d.worldType, RealmsConfigureWorldScreen.this.field_224416_d.activeSlot));
         }
      });
      this.buttonsAdd(this.field_224426_n = new RealmsButton(6, this.func_224411_b(1), RealmsConstants.func_225109_a(13) - 5, 90, 20, getLocalizedString("mco.configure.world.backup")) {
         public void onPress() {
            Realms.setScreen(new RealmsBackupScreen(RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.field_224416_d.clone(), RealmsConfigureWorldScreen.this.field_224416_d.activeSlot));
         }
      });
      this.buttonsAdd(this.field_224427_o = new RealmsButton(7, this.func_224411_b(2), RealmsConstants.func_225109_a(13) - 5, 90, 20, getLocalizedString("mco.configure.world.buttons.resetworld")) {
         public void onPress() {
            Realms.setScreen(new RealmsResetWorldScreen(RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.field_224416_d.clone(), RealmsConfigureWorldScreen.this.func_224407_b()));
         }
      });
      this.buttonsAdd(new RealmsButton(0, this.field_224419_g - 80 + 8, RealmsConstants.func_225109_a(13) - 5, 70, 20, getLocalizedString("gui.back")) {
         public void onPress() {
            RealmsConfigureWorldScreen.this.func_224390_d();
         }
      });
      this.field_224426_n.active(true);
      if (this.field_224416_d == null) {
         this.func_224412_j();
         this.func_224377_h();
         this.field_224422_j.active(false);
         this.field_224423_k.active(false);
         this.field_224424_l.active(false);
      } else {
         this.func_224400_e();
         if (this.func_224376_g()) {
            this.func_224377_h();
         } else {
            this.func_224412_j();
         }
      }

   }

   private void func_224402_a(int p_224402_1_) {
      int i = this.func_224368_c(p_224402_1_);
      int j = RealmsConstants.func_225109_a(5) + 5;
      int k = 100 + p_224402_1_;
      RealmsServerSlotButton realmsserverslotbutton = new RealmsServerSlotButton(i, j, 80, 80, () -> {
         return this.field_224416_d;
      }, (p_224391_1_) -> {
         this.field_224414_b = p_224391_1_;
      }, k, p_224402_1_, this);
      this.getProxy().buttonsAdd(realmsserverslotbutton);
   }

   private int func_224411_b(int p_224411_1_) {
      return this.field_224418_f + p_224411_1_ * 95;
   }

   private int func_224374_a(int p_224374_1_, int p_224374_2_) {
      return this.width() / 2 - (p_224374_2_ * 105 - 5) / 2 + p_224374_1_ * 105;
   }

   public void tick() {
      this.tickButtons();
      ++this.field_224430_r;
      --this.field_224431_s;
      if (this.field_224431_s < 0) {
         this.field_224431_s = 0;
      }

   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.field_224414_b = null;
      this.renderBackground();
      this.drawCenteredString(getLocalizedString("mco.configure.worlds.title"), this.width() / 2, RealmsConstants.func_225109_a(4), 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
      if (this.field_224416_d == null) {
         this.drawCenteredString(getLocalizedString("mco.configure.world.title"), this.width() / 2, 17, 16777215);
      } else {
         String s = this.field_224416_d.getName();
         int i = this.fontWidth(s);
         int j = this.field_224416_d.state == RealmsServer.Status.CLOSED ? 10526880 : 8388479;
         int k = this.fontWidth(getLocalizedString("mco.configure.world.title"));
         this.drawCenteredString(getLocalizedString("mco.configure.world.title"), this.width() / 2, 12, 16777215);
         this.drawCenteredString(s, this.width() / 2, 24, j);
         int l = Math.min(this.func_224374_a(2, 3) + 80 - 11, this.width() / 2 + i / 2 + k / 2 + 10);
         this.func_224379_a(l, 7, p_render_1_, p_render_2_);
         if (this.func_224376_g()) {
            this.drawString(getLocalizedString("mco.configure.current.minigame") + ": " + this.field_224416_d.getMinigameName(), this.field_224418_f + 80 + 20 + 10, RealmsConstants.func_225109_a(13), 16777215);
         }

         if (this.field_224414_b != null) {
            this.func_224394_a(this.field_224414_b, p_render_1_, p_render_2_);
         }

      }
   }

   private int func_224368_c(int p_224368_1_) {
      return this.field_224418_f + (p_224368_1_ - 1) * 98;
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         this.func_224390_d();
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   private void func_224390_d() {
      if (this.field_224429_q) {
         this.field_224415_c.func_223978_e();
      }

      Realms.setScreen(this.field_224415_c);
   }

   private void func_224387_a(long p_224387_1_) {
      (new Thread(() -> {
         RealmsClient realmsclient = RealmsClient.func_224911_a();

         try {
            this.field_224416_d = realmsclient.func_224935_a(p_224387_1_);
            this.func_224400_e();
            if (this.func_224376_g()) {
               this.func_224375_k();
            } else {
               this.func_224399_i();
            }
         } catch (RealmsServiceException realmsserviceexception) {
            field_224413_a.error("Couldn't get own world");
            Realms.setScreen(new RealmsGenericErrorScreen(realmsserviceexception.getMessage(), this.field_224415_c));
         } catch (IOException var6) {
            field_224413_a.error("Couldn't parse response getting own world");
         }

      })).start();
   }

   private void func_224400_e() {
      this.field_224422_j.active(!this.field_224416_d.expired);
      this.field_224423_k.active(!this.field_224416_d.expired);
      this.field_224424_l.active(true);
      this.field_224428_p.active(!this.field_224416_d.expired);
      this.field_224425_m.active(!this.field_224416_d.expired);
      this.field_224427_o.active(!this.field_224416_d.expired);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   private void func_224385_a(RealmsServer p_224385_1_) {
      if (this.field_224416_d.state == RealmsServer.Status.OPEN) {
         this.field_224415_c.func_223911_a(p_224385_1_, new RealmsConfigureWorldScreen(this.field_224415_c.func_223942_f(), this.field_224417_e));
      } else {
         this.func_224383_a(true, new RealmsConfigureWorldScreen(this.field_224415_c.func_223942_f(), this.field_224417_e));
      }

   }

   public void func_224366_a(int p_224366_1_, RealmsServerSlotButton.Action p_224366_2_, boolean p_224366_3_, boolean p_224366_4_) {
      switch(p_224366_2_) {
      case NOTHING:
         break;
      case JOIN:
         this.func_224385_a(this.field_224416_d);
         break;
      case SWITCH_SLOT:
         if (p_224366_3_) {
            this.func_224401_f();
         } else if (p_224366_4_) {
            this.func_224388_b(p_224366_1_, this.field_224416_d);
         } else {
            this.func_224403_a(p_224366_1_, this.field_224416_d);
         }
         break;
      default:
         throw new IllegalStateException("Unknown action " + p_224366_2_);
      }

   }

   private void func_224401_f() {
      RealmsSelectWorldTemplateScreen realmsselectworldtemplatescreen = new RealmsSelectWorldTemplateScreen(this, RealmsServer.ServerType.MINIGAME);
      realmsselectworldtemplatescreen.func_224483_a(getLocalizedString("mco.template.title.minigame"));
      realmsselectworldtemplatescreen.func_224492_b(getLocalizedString("mco.minigame.world.info.line1") + "\\n" + getLocalizedString("mco.minigame.world.info.line2"));
      Realms.setScreen(realmsselectworldtemplatescreen);
   }

   private void func_224403_a(int p_224403_1_, RealmsServer p_224403_2_) {
      String s = getLocalizedString("mco.configure.world.slot.switch.question.line1");
      String s1 = getLocalizedString("mco.configure.world.slot.switch.question.line2");
      Realms.setScreen(new RealmsLongConfirmationScreen((p_227973_3_, p_227973_4_) -> {
         if (p_227973_3_) {
            this.func_224406_a(p_224403_2_.id, p_224403_1_);
         } else {
            Realms.setScreen(this);
         }

      }, RealmsLongConfirmationScreen.Type.Info, s, s1, true, 9));
   }

   private void func_224388_b(int p_224388_1_, RealmsServer p_224388_2_) {
      String s = getLocalizedString("mco.configure.world.slot.switch.question.line1");
      String s1 = getLocalizedString("mco.configure.world.slot.switch.question.line2");
      Realms.setScreen(new RealmsLongConfirmationScreen((p_227970_3_, p_227970_4_) -> {
         if (p_227970_3_) {
            RealmsResetWorldScreen realmsresetworldscreen = new RealmsResetWorldScreen(this, p_224388_2_, this.func_224407_b(), getLocalizedString("mco.configure.world.switch.slot"), getLocalizedString("mco.configure.world.switch.slot.subtitle"), 10526880, getLocalizedString("gui.cancel"));
            realmsresetworldscreen.func_224445_b(p_224388_1_);
            realmsresetworldscreen.func_224432_a(getLocalizedString("mco.create.world.reset.title"));
            Realms.setScreen(realmsresetworldscreen);
         } else {
            Realms.setScreen(this);
         }

      }, RealmsLongConfirmationScreen.Type.Info, s, s1, true, 10));
   }

   protected void func_224394_a(String p_224394_1_, int p_224394_2_, int p_224394_3_) {
      if (p_224394_1_ != null) {
         int i = p_224394_2_ + 12;
         int j = p_224394_3_ - 12;
         int k = this.fontWidth(p_224394_1_);
         if (i + k + 3 > this.field_224419_g) {
            i = i - k - 20;
         }

         this.fillGradient(i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
         this.fontDrawShadow(p_224394_1_, i, j, 16777215);
      }
   }

   private void func_224379_a(int p_224379_1_, int p_224379_2_, int p_224379_3_, int p_224379_4_) {
      if (this.field_224416_d.expired) {
         this.func_224408_b(p_224379_1_, p_224379_2_, p_224379_3_, p_224379_4_);
      } else if (this.field_224416_d.state == RealmsServer.Status.CLOSED) {
         this.func_224409_d(p_224379_1_, p_224379_2_, p_224379_3_, p_224379_4_);
      } else if (this.field_224416_d.state == RealmsServer.Status.OPEN) {
         if (this.field_224416_d.daysLeft < 7) {
            this.func_224381_a(p_224379_1_, p_224379_2_, p_224379_3_, p_224379_4_, this.field_224416_d.daysLeft);
         } else {
            this.func_224382_c(p_224379_1_, p_224379_2_, p_224379_3_, p_224379_4_);
         }
      }

   }

   private void func_224408_b(int p_224408_1_, int p_224408_2_, int p_224408_3_, int p_224408_4_) {
      RealmsScreen.bind("realms:textures/gui/realms/expired_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_224408_1_, p_224408_2_, 0.0F, 0.0F, 10, 28, 10, 28);
      RenderSystem.popMatrix();
      if (p_224408_3_ >= p_224408_1_ && p_224408_3_ <= p_224408_1_ + 9 && p_224408_4_ >= p_224408_2_ && p_224408_4_ <= p_224408_2_ + 27) {
         this.field_224414_b = getLocalizedString("mco.selectServer.expired");
      }

   }

   private void func_224381_a(int p_224381_1_, int p_224381_2_, int p_224381_3_, int p_224381_4_, int p_224381_5_) {
      RealmsScreen.bind("realms:textures/gui/realms/expires_soon_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      if (this.field_224430_r % 20 < 10) {
         RealmsScreen.blit(p_224381_1_, p_224381_2_, 0.0F, 0.0F, 10, 28, 20, 28);
      } else {
         RealmsScreen.blit(p_224381_1_, p_224381_2_, 10.0F, 0.0F, 10, 28, 20, 28);
      }

      RenderSystem.popMatrix();
      if (p_224381_3_ >= p_224381_1_ && p_224381_3_ <= p_224381_1_ + 9 && p_224381_4_ >= p_224381_2_ && p_224381_4_ <= p_224381_2_ + 27) {
         if (p_224381_5_ <= 0) {
            this.field_224414_b = getLocalizedString("mco.selectServer.expires.soon");
         } else if (p_224381_5_ == 1) {
            this.field_224414_b = getLocalizedString("mco.selectServer.expires.day");
         } else {
            this.field_224414_b = getLocalizedString("mco.selectServer.expires.days", new Object[]{p_224381_5_});
         }
      }

   }

   private void func_224382_c(int p_224382_1_, int p_224382_2_, int p_224382_3_, int p_224382_4_) {
      RealmsScreen.bind("realms:textures/gui/realms/on_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_224382_1_, p_224382_2_, 0.0F, 0.0F, 10, 28, 10, 28);
      RenderSystem.popMatrix();
      if (p_224382_3_ >= p_224382_1_ && p_224382_3_ <= p_224382_1_ + 9 && p_224382_4_ >= p_224382_2_ && p_224382_4_ <= p_224382_2_ + 27) {
         this.field_224414_b = getLocalizedString("mco.selectServer.open");
      }

   }

   private void func_224409_d(int p_224409_1_, int p_224409_2_, int p_224409_3_, int p_224409_4_) {
      RealmsScreen.bind("realms:textures/gui/realms/off_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_224409_1_, p_224409_2_, 0.0F, 0.0F, 10, 28, 10, 28);
      RenderSystem.popMatrix();
      if (p_224409_3_ >= p_224409_1_ && p_224409_3_ <= p_224409_1_ + 9 && p_224409_4_ >= p_224409_2_ && p_224409_4_ <= p_224409_2_ + 27) {
         this.field_224414_b = getLocalizedString("mco.selectServer.closed");
      }

   }

   private boolean func_224376_g() {
      return this.field_224416_d != null && this.field_224416_d.worldType.equals(RealmsServer.ServerType.MINIGAME);
   }

   private void func_224377_h() {
      this.func_224378_a(this.field_224425_m);
      this.func_224378_a(this.field_224426_n);
      this.func_224378_a(this.field_224427_o);
   }

   private void func_224378_a(RealmsButton p_224378_1_) {
      p_224378_1_.setVisible(false);
      this.removeButton(p_224378_1_);
   }

   private void func_224399_i() {
      this.func_224404_b(this.field_224425_m);
      this.func_224404_b(this.field_224426_n);
      this.func_224404_b(this.field_224427_o);
   }

   private void func_224404_b(RealmsButton p_224404_1_) {
      p_224404_1_.setVisible(true);
      this.buttonsAdd(p_224404_1_);
   }

   private void func_224412_j() {
      this.func_224378_a(this.field_224428_p);
   }

   private void func_224375_k() {
      this.func_224404_b(this.field_224428_p);
   }

   public void func_224386_a(RealmsWorldOptions p_224386_1_) {
      RealmsWorldOptions realmsworldoptions = this.field_224416_d.slots.get(this.field_224416_d.activeSlot);
      p_224386_1_.templateId = realmsworldoptions.templateId;
      p_224386_1_.templateImage = realmsworldoptions.templateImage;
      RealmsClient realmsclient = RealmsClient.func_224911_a();

      try {
         realmsclient.func_224925_a(this.field_224416_d.id, this.field_224416_d.activeSlot, p_224386_1_);
         this.field_224416_d.slots.put(this.field_224416_d.activeSlot, p_224386_1_);
      } catch (RealmsServiceException realmsserviceexception) {
         field_224413_a.error("Couldn't save slot settings");
         Realms.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, this));
         return;
      } catch (UnsupportedEncodingException var6) {
         field_224413_a.error("Couldn't save slot settings");
      }

      Realms.setScreen(this);
   }

   public void func_224410_a(String p_224410_1_, String p_224410_2_) {
      String s = p_224410_2_ != null && !p_224410_2_.trim().isEmpty() ? p_224410_2_ : null;
      RealmsClient realmsclient = RealmsClient.func_224911_a();

      try {
         realmsclient.func_224922_b(this.field_224416_d.id, p_224410_1_, s);
         this.field_224416_d.setName(p_224410_1_);
         this.field_224416_d.setDescription(s);
      } catch (RealmsServiceException realmsserviceexception) {
         field_224413_a.error("Couldn't save settings");
         Realms.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, this));
         return;
      } catch (UnsupportedEncodingException var7) {
         field_224413_a.error("Couldn't save settings");
      }

      Realms.setScreen(this);
   }

   public void func_224383_a(boolean p_224383_1_, RealmsScreen p_224383_2_) {
      RealmsTasks.OpenServerTask realmstasks$openservertask = new RealmsTasks.OpenServerTask(this.field_224416_d, this, this.field_224415_c, p_224383_1_);
      RealmsLongRunningMcoTaskScreen realmslongrunningmcotaskscreen = new RealmsLongRunningMcoTaskScreen(p_224383_2_, realmstasks$openservertask);
      realmslongrunningmcotaskscreen.func_224233_a();
      Realms.setScreen(realmslongrunningmcotaskscreen);
   }

   public void func_224405_a(RealmsScreen p_224405_1_) {
      RealmsTasks.CloseServerTask realmstasks$closeservertask = new RealmsTasks.CloseServerTask(this.field_224416_d, this);
      RealmsLongRunningMcoTaskScreen realmslongrunningmcotaskscreen = new RealmsLongRunningMcoTaskScreen(p_224405_1_, realmstasks$closeservertask);
      realmslongrunningmcotaskscreen.func_224233_a();
      Realms.setScreen(realmslongrunningmcotaskscreen);
   }

   public void func_224398_a() {
      this.field_224429_q = true;
   }

   void func_223627_a_(WorldTemplate p_223627_1_) {
      if (p_223627_1_ != null) {
         if (WorldTemplate.Type.MINIGAME.equals(p_223627_1_.type)) {
            this.func_224393_b(p_223627_1_);
         }

      }
   }

   private void func_224406_a(long p_224406_1_, int p_224406_3_) {
      RealmsConfigureWorldScreen realmsconfigureworldscreen = this.func_224407_b();
      RealmsTasks.SwitchSlotTask realmstasks$switchslottask = new RealmsTasks.SwitchSlotTask(p_224406_1_, p_224406_3_, (p_227971_1_, p_227971_2_) -> {
         Realms.setScreen(realmsconfigureworldscreen);
      }, 11);
      RealmsLongRunningMcoTaskScreen realmslongrunningmcotaskscreen = new RealmsLongRunningMcoTaskScreen(this.field_224415_c, realmstasks$switchslottask);
      realmslongrunningmcotaskscreen.func_224233_a();
      Realms.setScreen(realmslongrunningmcotaskscreen);
   }

   private void func_224393_b(WorldTemplate p_224393_1_) {
      RealmsTasks.SwitchMinigameTask realmstasks$switchminigametask = new RealmsTasks.SwitchMinigameTask(this.field_224416_d.id, p_224393_1_, this.func_224407_b());
      RealmsLongRunningMcoTaskScreen realmslongrunningmcotaskscreen = new RealmsLongRunningMcoTaskScreen(this.field_224415_c, realmstasks$switchminigametask);
      realmslongrunningmcotaskscreen.func_224233_a();
      Realms.setScreen(realmslongrunningmcotaskscreen);
   }

   public RealmsConfigureWorldScreen func_224407_b() {
      return new RealmsConfigureWorldScreen(this.field_224415_c, this.field_224417_e);
   }
}