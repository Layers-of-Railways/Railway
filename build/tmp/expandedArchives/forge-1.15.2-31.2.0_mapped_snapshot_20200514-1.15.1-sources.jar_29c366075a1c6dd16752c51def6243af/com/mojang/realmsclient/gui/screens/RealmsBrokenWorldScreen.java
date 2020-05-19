package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsTasks;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsMth;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsBrokenWorldScreen extends RealmsScreen {
   private static final Logger field_224071_a = LogManager.getLogger();
   private final RealmsScreen field_224072_b;
   private final RealmsMainScreen field_224073_c;
   private RealmsServer field_224074_d;
   private final long field_224075_e;
   private String field_224076_f = getLocalizedString("mco.brokenworld.title");
   private final String field_224077_g = getLocalizedString("mco.brokenworld.message.line1") + "\\n" + getLocalizedString("mco.brokenworld.message.line2");
   private int field_224078_h;
   private int field_224079_i;
   private final int field_224080_j = 80;
   private final int field_224081_k = 5;
   private static final List<Integer> field_224082_l = Arrays.asList(1, 2, 3);
   private static final List<Integer> field_224083_m = Arrays.asList(4, 5, 6);
   private static final List<Integer> field_224084_n = Arrays.asList(7, 8, 9);
   private static final List<Integer> field_224085_o = Arrays.asList(10, 11, 12);
   private final List<Integer> field_224086_p = Lists.newArrayList();
   private int field_224087_q;

   public RealmsBrokenWorldScreen(RealmsScreen p_i51776_1_, RealmsMainScreen p_i51776_2_, long p_i51776_3_) {
      this.field_224072_b = p_i51776_1_;
      this.field_224073_c = p_i51776_2_;
      this.field_224075_e = p_i51776_3_;
   }

   public void func_224052_a(String p_224052_1_) {
      this.field_224076_f = p_224052_1_;
   }

   public void init() {
      this.field_224078_h = this.width() / 2 - 150;
      this.field_224079_i = this.width() / 2 + 190;
      this.buttonsAdd(new RealmsButton(0, this.field_224079_i - 80 + 8, RealmsConstants.func_225109_a(13) - 5, 70, 20, getLocalizedString("gui.back")) {
         public void onPress() {
            RealmsBrokenWorldScreen.this.func_224060_e();
         }
      });
      if (this.field_224074_d == null) {
         this.func_224068_a(this.field_224075_e);
      } else {
         this.func_224058_a();
      }

      this.setKeyboardHandlerSendRepeatsToGui(true);
   }

   public void func_224058_a() {
      for(Entry<Integer, RealmsWorldOptions> entry : this.field_224074_d.slots.entrySet()) {
         RealmsWorldOptions realmsworldoptions = entry.getValue();
         boolean flag = entry.getKey() != this.field_224074_d.activeSlot || this.field_224074_d.worldType.equals(RealmsServer.ServerType.MINIGAME);
         RealmsButton realmsbutton;
         if (flag) {
            realmsbutton = new RealmsBrokenWorldScreen.PlayButton(field_224082_l.get(entry.getKey() - 1), this.func_224065_a(entry.getKey()), getLocalizedString("mco.brokenworld.play"));
         } else {
            realmsbutton = new RealmsBrokenWorldScreen.DownloadButton(field_224084_n.get(entry.getKey() - 1), this.func_224065_a(entry.getKey()), getLocalizedString("mco.brokenworld.download"));
         }

         if (this.field_224086_p.contains(entry.getKey())) {
            realmsbutton.active(false);
            realmsbutton.setMessage(getLocalizedString("mco.brokenworld.downloaded"));
         }

         this.buttonsAdd(realmsbutton);
         this.buttonsAdd(new RealmsButton(field_224083_m.get(entry.getKey() - 1), this.func_224065_a(entry.getKey()), RealmsConstants.func_225109_a(10), 80, 20, getLocalizedString("mco.brokenworld.reset")) {
            public void onPress() {
               int i = RealmsBrokenWorldScreen.field_224083_m.indexOf(this.id()) + 1;
               RealmsResetWorldScreen realmsresetworldscreen = new RealmsResetWorldScreen(RealmsBrokenWorldScreen.this, RealmsBrokenWorldScreen.this.field_224074_d, RealmsBrokenWorldScreen.this);
               if (i != RealmsBrokenWorldScreen.this.field_224074_d.activeSlot || RealmsBrokenWorldScreen.this.field_224074_d.worldType.equals(RealmsServer.ServerType.MINIGAME)) {
                  realmsresetworldscreen.func_224445_b(i);
               }

               realmsresetworldscreen.func_224444_a(14);
               Realms.setScreen(realmsresetworldscreen);
            }
         });
      }

   }

   public void tick() {
      ++this.field_224087_q;
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      super.render(p_render_1_, p_render_2_, p_render_3_);
      this.drawCenteredString(this.field_224076_f, this.width() / 2, 17, 16777215);
      String[] astring = this.field_224077_g.split("\\\\n");

      for(int i = 0; i < astring.length; ++i) {
         this.drawCenteredString(astring[i], this.width() / 2, RealmsConstants.func_225109_a(-1) + 3 + i * 12, 10526880);
      }

      if (this.field_224074_d != null) {
         for(Entry<Integer, RealmsWorldOptions> entry : this.field_224074_d.slots.entrySet()) {
            if ((entry.getValue()).templateImage != null && (entry.getValue()).templateId != -1L) {
               this.func_224053_a(this.func_224065_a(entry.getKey()), RealmsConstants.func_225109_a(1) + 5, p_render_1_, p_render_2_, this.field_224074_d.activeSlot == entry.getKey() && !this.func_224069_f(), entry.getValue().getSlotName(entry.getKey()), entry.getKey(), (entry.getValue()).templateId, (entry.getValue()).templateImage, (entry.getValue()).empty);
            } else {
               this.func_224053_a(this.func_224065_a(entry.getKey()), RealmsConstants.func_225109_a(1) + 5, p_render_1_, p_render_2_, this.field_224074_d.activeSlot == entry.getKey() && !this.func_224069_f(), entry.getValue().getSlotName(entry.getKey()), entry.getKey(), -1L, (String)null, (entry.getValue()).empty);
            }
         }

      }
   }

   private int func_224065_a(int p_224065_1_) {
      return this.field_224078_h + (p_224065_1_ - 1) * 110;
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         this.func_224060_e();
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   private void func_224060_e() {
      Realms.setScreen(this.field_224072_b);
   }

   private void func_224068_a(long p_224068_1_) {
      (new Thread(() -> {
         RealmsClient realmsclient = RealmsClient.func_224911_a();

         try {
            this.field_224074_d = realmsclient.func_224935_a(p_224068_1_);
            this.func_224058_a();
         } catch (RealmsServiceException realmsserviceexception) {
            field_224071_a.error("Couldn't get own world");
            Realms.setScreen(new RealmsGenericErrorScreen(realmsserviceexception.getMessage(), this.field_224072_b));
         } catch (IOException var6) {
            field_224071_a.error("Couldn't parse response getting own world");
         }

      })).start();
   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      if (!p_confirmResult_1_) {
         Realms.setScreen(this);
      } else {
         if (p_confirmResult_2_ != 13 && p_confirmResult_2_ != 14) {
            if (field_224084_n.contains(p_confirmResult_2_)) {
               this.func_224066_b(field_224084_n.indexOf(p_confirmResult_2_) + 1);
            } else if (field_224085_o.contains(p_confirmResult_2_)) {
               this.field_224086_p.add(field_224085_o.indexOf(p_confirmResult_2_) + 1);
               this.childrenClear();
               this.func_224058_a();
            }
         } else {
            (new Thread(() -> {
               RealmsClient realmsclient = RealmsClient.func_224911_a();
               if (this.field_224074_d.state.equals(RealmsServer.Status.CLOSED)) {
                  RealmsTasks.OpenServerTask realmstasks$openservertask = new RealmsTasks.OpenServerTask(this.field_224074_d, this, this.field_224072_b, true);
                  RealmsLongRunningMcoTaskScreen realmslongrunningmcotaskscreen = new RealmsLongRunningMcoTaskScreen(this, realmstasks$openservertask);
                  realmslongrunningmcotaskscreen.func_224233_a();
                  Realms.setScreen(realmslongrunningmcotaskscreen);
               } else {
                  try {
                     this.field_224073_c.func_223942_f().func_223911_a(realmsclient.func_224935_a(this.field_224075_e), this);
                  } catch (RealmsServiceException var4) {
                     field_224071_a.error("Couldn't get own world");
                     Realms.setScreen(this.field_224072_b);
                  } catch (IOException var5) {
                     field_224071_a.error("Couldn't parse response getting own world");
                     Realms.setScreen(this.field_224072_b);
                  }
               }

            })).start();
         }

      }
   }

   private void func_224066_b(int p_224066_1_) {
      RealmsClient realmsclient = RealmsClient.func_224911_a();

      try {
         WorldDownload worlddownload = realmsclient.func_224917_b(this.field_224074_d.id, p_224066_1_);
         RealmsDownloadLatestWorldScreen realmsdownloadlatestworldscreen = new RealmsDownloadLatestWorldScreen(this, worlddownload, this.field_224074_d.name + " (" + this.field_224074_d.slots.get(p_224066_1_).getSlotName(p_224066_1_) + ")");
         realmsdownloadlatestworldscreen.func_224167_a(field_224085_o.get(p_224066_1_ - 1));
         Realms.setScreen(realmsdownloadlatestworldscreen);
      } catch (RealmsServiceException realmsserviceexception) {
         field_224071_a.error("Couldn't download world data");
         Realms.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, this));
      }

   }

   private boolean func_224069_f() {
      return this.field_224074_d != null && this.field_224074_d.worldType.equals(RealmsServer.ServerType.MINIGAME);
   }

   private void func_224053_a(int p_224053_1_, int p_224053_2_, int p_224053_3_, int p_224053_4_, boolean p_224053_5_, String p_224053_6_, int p_224053_7_, long p_224053_8_, String p_224053_10_, boolean p_224053_11_) {
      if (p_224053_11_) {
         bind("realms:textures/gui/realms/empty_frame.png");
      } else if (p_224053_10_ != null && p_224053_8_ != -1L) {
         RealmsTextureManager.func_225202_a(String.valueOf(p_224053_8_), p_224053_10_);
      } else if (p_224053_7_ == 1) {
         bind("textures/gui/title/background/panorama_0.png");
      } else if (p_224053_7_ == 2) {
         bind("textures/gui/title/background/panorama_2.png");
      } else if (p_224053_7_ == 3) {
         bind("textures/gui/title/background/panorama_3.png");
      } else {
         RealmsTextureManager.func_225202_a(String.valueOf(this.field_224074_d.minigameId), this.field_224074_d.minigameImage);
      }

      if (!p_224053_5_) {
         RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
      } else if (p_224053_5_) {
         float f = 0.9F + 0.1F * RealmsMth.cos((float)this.field_224087_q * 0.2F);
         RenderSystem.color4f(f, f, f, 1.0F);
      }

      RealmsScreen.blit(p_224053_1_ + 3, p_224053_2_ + 3, 0.0F, 0.0F, 74, 74, 74, 74);
      bind("realms:textures/gui/realms/slot_frame.png");
      if (p_224053_5_) {
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      } else {
         RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
      }

      RealmsScreen.blit(p_224053_1_, p_224053_2_, 0.0F, 0.0F, 80, 80, 80, 80);
      this.drawCenteredString(p_224053_6_, p_224053_1_ + 40, p_224053_2_ + 66, 16777215);
   }

   private void func_224056_c(int p_224056_1_) {
      RealmsTasks.SwitchSlotTask realmstasks$switchslottask = new RealmsTasks.SwitchSlotTask(this.field_224074_d.id, p_224056_1_, this, 13);
      RealmsLongRunningMcoTaskScreen realmslongrunningmcotaskscreen = new RealmsLongRunningMcoTaskScreen(this.field_224072_b, realmstasks$switchslottask);
      realmslongrunningmcotaskscreen.func_224233_a();
      Realms.setScreen(realmslongrunningmcotaskscreen);
   }

   @OnlyIn(Dist.CLIENT)
   class DownloadButton extends RealmsButton {
      public DownloadButton(int p_i51634_2_, int p_i51634_3_, String p_i51634_4_) {
         super(p_i51634_2_, p_i51634_3_, RealmsConstants.func_225109_a(8), 80, 20, p_i51634_4_);
      }

      public void onPress() {
         String s = RealmsScreen.getLocalizedString("mco.configure.world.restore.download.question.line1");
         String s1 = RealmsScreen.getLocalizedString("mco.configure.world.restore.download.question.line2");
         Realms.setScreen(new RealmsLongConfirmationScreen(RealmsBrokenWorldScreen.this, RealmsLongConfirmationScreen.Type.Info, s, s1, true, this.id()));
      }
   }

   @OnlyIn(Dist.CLIENT)
   class PlayButton extends RealmsButton {
      public PlayButton(int p_i51633_2_, int p_i51633_3_, String p_i51633_4_) {
         super(p_i51633_2_, p_i51633_3_, RealmsConstants.func_225109_a(8), 80, 20, p_i51633_4_);
      }

      public void onPress() {
         int i = RealmsBrokenWorldScreen.field_224082_l.indexOf(this.id()) + 1;
         if ((RealmsBrokenWorldScreen.this.field_224074_d.slots.get(i)).empty) {
            RealmsResetWorldScreen realmsresetworldscreen = new RealmsResetWorldScreen(RealmsBrokenWorldScreen.this, RealmsBrokenWorldScreen.this.field_224074_d, RealmsBrokenWorldScreen.this, RealmsScreen.getLocalizedString("mco.configure.world.switch.slot"), RealmsScreen.getLocalizedString("mco.configure.world.switch.slot.subtitle"), 10526880, RealmsScreen.getLocalizedString("gui.cancel"));
            realmsresetworldscreen.func_224445_b(i);
            realmsresetworldscreen.func_224432_a(RealmsScreen.getLocalizedString("mco.create.world.reset.title"));
            realmsresetworldscreen.func_224444_a(14);
            Realms.setScreen(realmsresetworldscreen);
         } else {
            RealmsBrokenWorldScreen.this.func_224056_c(i);
         }

      }
   }
}