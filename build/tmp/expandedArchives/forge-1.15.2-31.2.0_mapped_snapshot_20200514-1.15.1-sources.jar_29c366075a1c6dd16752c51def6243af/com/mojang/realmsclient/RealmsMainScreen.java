package com.mojang.realmsclient;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.Ping;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerPlayerList;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.dto.RegionPingResult;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.screens.RealmsClientOutdatedScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsCreateRealmScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsParentalConsentScreen;
import com.mojang.realmsclient.gui.screens.RealmsPendingInvitesScreen;
import com.mojang.realmsclient.util.RealmsPersistence;
import com.mojang.realmsclient.util.RealmsTasks;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.RealmsUtil;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.KeyCombo;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsMth;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsMainScreen extends RealmsScreen {
   private static final Logger field_224012_a = LogManager.getLogger();
   private static boolean field_224013_b;
   private final RateLimiter field_224014_c;
   private boolean field_224015_d;
   private static List<ResourceLocation> field_227918_e_ = ImmutableList.of();
   private static final RealmsDataFetcher field_224017_f = new RealmsDataFetcher();
   private static int field_224018_g = -1;
   private final RealmsScreen field_224019_h;
   private volatile RealmsMainScreen.ServerList field_224020_i;
   private long field_224021_j = -1L;
   private RealmsButton field_224022_k;
   private RealmsButton field_224023_l;
   private RealmsButton field_224024_m;
   private RealmsButton field_224025_n;
   private RealmsButton field_224026_o;
   private String field_224027_p;
   private List<RealmsServer> field_224028_q = Lists.newArrayList();
   private volatile int field_224029_r;
   private int field_224030_s;
   private static volatile boolean field_224031_t;
   private static volatile boolean field_224032_u;
   private static volatile boolean field_224033_v;
   private boolean field_224034_w;
   private boolean field_224035_x;
   private boolean field_224036_y;
   private volatile boolean field_224037_z;
   private volatile boolean field_223993_A;
   private volatile boolean field_223994_B;
   private volatile boolean field_223995_C;
   private volatile String field_223996_D;
   private int field_223997_E;
   private int field_223998_F;
   private boolean field_223999_G;
   private static RealmsScreen field_224000_H;
   private static boolean field_224001_I;
   private List<KeyCombo> field_224002_J;
   private int field_224003_K;
   private ReentrantLock field_224004_L = new ReentrantLock();
   private boolean field_224005_M;
   private RealmsMainScreen.InfoButton field_224006_N;
   private RealmsMainScreen.PendingInvitesButton field_224007_O;
   private RealmsMainScreen.NewsButton field_224008_P;
   private RealmsButton field_224009_Q;
   private RealmsButton field_224010_R;
   private RealmsButton field_224011_S;

   public RealmsMainScreen(RealmsScreen p_i51792_1_) {
      this.field_224019_h = p_i51792_1_;
      this.field_224014_c = RateLimiter.create((double)0.016666668F);
   }

   public boolean func_223928_a() {
      if (this.func_223968_l() && this.field_224034_w) {
         if (this.field_224037_z && !this.field_223993_A) {
            return true;
         } else {
            for(RealmsServer realmsserver : this.field_224028_q) {
               if (realmsserver.ownerUUID.equals(Realms.getUUID())) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public boolean func_223990_b() {
      if (this.func_223968_l() && this.field_224034_w) {
         if (this.field_224035_x) {
            return true;
         } else {
            return this.field_224037_z && !this.field_223993_A && this.field_224028_q.isEmpty() ? true : this.field_224028_q.isEmpty();
         }
      } else {
         return false;
      }
   }

   public void init() {
      this.field_224002_J = Lists.newArrayList(new KeyCombo(new char[]{'3', '2', '1', '4', '5', '6'}, () -> {
         field_224013_b = !field_224013_b;
      }), new KeyCombo(new char[]{'9', '8', '7', '1', '2', '3'}, () -> {
         if (RealmsClient.field_224944_a.equals(RealmsClient.Environment.STAGE)) {
            this.func_223973_x();
         } else {
            this.func_223884_v();
         }

      }), new KeyCombo(new char[]{'9', '8', '7', '4', '5', '6'}, () -> {
         if (RealmsClient.field_224944_a.equals(RealmsClient.Environment.LOCAL)) {
            this.func_223973_x();
         } else {
            this.func_223962_w();
         }

      }));
      if (field_224000_H != null) {
         Realms.setScreen(field_224000_H);
      } else {
         this.field_224004_L = new ReentrantLock();
         if (field_224033_v && !this.func_223968_l()) {
            this.func_223975_u();
         }

         this.func_223895_s();
         this.func_223965_t();
         if (!this.field_224015_d) {
            Realms.setConnectedToRealms(false);
         }

         this.setKeyboardHandlerSendRepeatsToGui(true);
         if (this.func_223968_l()) {
            field_224017_f.func_225087_d();
         }

         this.field_223994_B = false;
         this.func_223970_d();
      }
   }

   private boolean func_223968_l() {
      return field_224032_u && field_224031_t;
   }

   public void func_223901_c() {
      this.buttonsAdd(this.field_224025_n = new RealmsButton(1, this.width() / 2 - 190, this.height() - 32, 90, 20, getLocalizedString("mco.selectServer.configure")) {
         public void onPress() {
            RealmsMainScreen.this.func_223966_f(RealmsMainScreen.this.func_223967_a(RealmsMainScreen.this.field_224021_j));
         }
      });
      this.buttonsAdd(this.field_224022_k = new RealmsButton(3, this.width() / 2 - 93, this.height() - 32, 90, 20, getLocalizedString("mco.selectServer.play")) {
         public void onPress() {
            RealmsMainScreen.this.func_223914_p();
         }
      });
      this.buttonsAdd(this.field_224023_l = new RealmsButton(2, this.width() / 2 + 4, this.height() - 32, 90, 20, getLocalizedString("gui.back")) {
         public void onPress() {
            if (!RealmsMainScreen.this.field_224036_y) {
               Realms.setScreen(RealmsMainScreen.this.field_224019_h);
            }

         }
      });
      this.buttonsAdd(this.field_224024_m = new RealmsButton(0, this.width() / 2 + 100, this.height() - 32, 90, 20, getLocalizedString("mco.selectServer.expiredRenew")) {
         public void onPress() {
            RealmsMainScreen.this.func_223930_q();
         }
      });
      this.buttonsAdd(this.field_224026_o = new RealmsButton(7, this.width() / 2 - 202, this.height() - 32, 90, 20, getLocalizedString("mco.selectServer.leave")) {
         public void onPress() {
            RealmsMainScreen.this.func_223906_g(RealmsMainScreen.this.func_223967_a(RealmsMainScreen.this.field_224021_j));
         }
      });
      this.buttonsAdd(this.field_224007_O = new RealmsMainScreen.PendingInvitesButton());
      this.buttonsAdd(this.field_224008_P = new RealmsMainScreen.NewsButton());
      this.buttonsAdd(this.field_224006_N = new RealmsMainScreen.InfoButton());
      this.buttonsAdd(this.field_224011_S = new RealmsMainScreen.CloseButton());
      this.buttonsAdd(this.field_224009_Q = new RealmsButton(6, this.width() / 2 + 52, this.func_223932_C() + 137 - 20, 98, 20, getLocalizedString("mco.selectServer.trial")) {
         public void onPress() {
            RealmsMainScreen.this.func_223988_r();
         }
      });
      this.buttonsAdd(this.field_224010_R = new RealmsButton(5, this.width() / 2 + 52, this.func_223932_C() + 160 - 20, 98, 20, getLocalizedString("mco.selectServer.buy")) {
         public void onPress() {
            RealmsUtil.func_225190_c("https://aka.ms/BuyJavaRealms");
         }
      });
      RealmsServer realmsserver = this.func_223967_a(this.field_224021_j);
      this.func_223915_a(realmsserver);
   }

   private void func_223915_a(RealmsServer p_223915_1_) {
      this.field_224022_k.active(this.func_223897_b(p_223915_1_) && !this.func_223990_b());
      this.field_224024_m.setVisible(this.func_223920_c(p_223915_1_));
      this.field_224025_n.setVisible(this.func_223941_d(p_223915_1_));
      this.field_224026_o.setVisible(this.func_223959_e(p_223915_1_));
      boolean flag = this.func_223990_b() && this.field_224037_z && !this.field_223993_A;
      this.field_224009_Q.setVisible(flag);
      this.field_224009_Q.active(flag);
      this.field_224010_R.setVisible(this.func_223990_b());
      this.field_224011_S.setVisible(this.func_223990_b() && this.field_224035_x);
      this.field_224024_m.active(!this.func_223990_b());
      this.field_224025_n.active(!this.func_223990_b());
      this.field_224026_o.active(!this.func_223990_b());
      this.field_224008_P.active(true);
      this.field_224007_O.active(true);
      this.field_224023_l.active(true);
      this.field_224006_N.active(!this.func_223990_b());
   }

   private boolean func_223977_m() {
      return (!this.func_223990_b() || this.field_224035_x) && this.func_223968_l() && this.field_224034_w;
   }

   private boolean func_223897_b(RealmsServer p_223897_1_) {
      return p_223897_1_ != null && !p_223897_1_.expired && p_223897_1_.state == RealmsServer.Status.OPEN;
   }

   private boolean func_223920_c(RealmsServer p_223920_1_) {
      return p_223920_1_ != null && p_223920_1_.expired && this.func_223885_h(p_223920_1_);
   }

   private boolean func_223941_d(RealmsServer p_223941_1_) {
      return p_223941_1_ != null && this.func_223885_h(p_223941_1_);
   }

   private boolean func_223959_e(RealmsServer p_223959_1_) {
      return p_223959_1_ != null && !this.func_223885_h(p_223959_1_);
   }

   public void func_223970_d() {
      if (this.func_223968_l() && this.field_224034_w) {
         this.func_223901_c();
      }

      this.field_224020_i = new RealmsMainScreen.ServerList();
      if (field_224018_g != -1) {
         this.field_224020_i.scroll(field_224018_g);
      }

      this.addWidget(this.field_224020_i);
      this.focusOn(this.field_224020_i);
   }

   public void tick() {
      this.tickButtons();
      this.field_224036_y = false;
      ++this.field_224030_s;
      --this.field_224003_K;
      if (this.field_224003_K < 0) {
         this.field_224003_K = 0;
      }

      if (this.func_223968_l()) {
         field_224017_f.func_225086_b();
         if (field_224017_f.func_225083_a(RealmsDataFetcher.Task.SERVER_LIST)) {
            List<RealmsServer> list = field_224017_f.func_225078_e();
            this.field_224020_i.clear();
            boolean flag = !this.field_224034_w;
            if (flag) {
               this.field_224034_w = true;
            }

            if (list != null) {
               boolean flag1 = false;

               for(RealmsServer realmsserver : list) {
                  if (this.func_223991_i(realmsserver)) {
                     flag1 = true;
                  }
               }

               this.field_224028_q = list;
               if (this.func_223928_a()) {
                  this.field_224020_i.addEntry(new RealmsMainScreen.TrialServerEntry());
               }

               for(RealmsServer realmsserver1 : this.field_224028_q) {
                  this.field_224020_i.addEntry(new RealmsMainScreen.ServerEntry(realmsserver1));
               }

               if (!field_224001_I && flag1) {
                  field_224001_I = true;
                  this.func_223944_n();
               }
            }

            if (flag) {
               this.func_223901_c();
            }
         }

         if (field_224017_f.func_225083_a(RealmsDataFetcher.Task.PENDING_INVITE)) {
            this.field_224029_r = field_224017_f.func_225081_f();
            if (this.field_224029_r > 0 && this.field_224014_c.tryAcquire(1)) {
               Realms.narrateNow(getLocalizedString("mco.configure.world.invite.narration", new Object[]{this.field_224029_r}));
            }
         }

         if (field_224017_f.func_225083_a(RealmsDataFetcher.Task.TRIAL_AVAILABLE) && !this.field_223993_A) {
            boolean flag2 = field_224017_f.func_225071_g();
            if (flag2 != this.field_224037_z && this.func_223990_b()) {
               this.field_224037_z = flag2;
               this.field_223994_B = false;
            } else {
               this.field_224037_z = flag2;
            }
         }

         if (field_224017_f.func_225083_a(RealmsDataFetcher.Task.LIVE_STATS)) {
            RealmsServerPlayerLists realmsserverplayerlists = field_224017_f.func_225079_h();

            label87:
            for(RealmsServerPlayerList realmsserverplayerlist : realmsserverplayerlists.servers) {
               Iterator iterator = this.field_224028_q.iterator();

               RealmsServer realmsserver2;
               while(true) {
                  if (!iterator.hasNext()) {
                     continue label87;
                  }

                  realmsserver2 = (RealmsServer)iterator.next();
                  if (realmsserver2.id == realmsserverplayerlist.serverId) {
                     break;
                  }
               }

               realmsserver2.updateServerPing(realmsserverplayerlist);
            }
         }

         if (field_224017_f.func_225083_a(RealmsDataFetcher.Task.UNREAD_NEWS)) {
            this.field_223995_C = field_224017_f.func_225059_i();
            this.field_223996_D = field_224017_f.func_225063_j();
         }

         field_224017_f.func_225072_c();
         if (this.func_223990_b()) {
            ++this.field_223998_F;
         }

         if (this.field_224006_N != null) {
            this.field_224006_N.setVisible(this.func_223977_m());
         }

      }
   }

   private void func_223921_a(String p_223921_1_) {
      Realms.setClipboard(p_223921_1_);
      RealmsUtil.func_225190_c(p_223921_1_);
   }

   private void func_223944_n() {
      (new Thread(() -> {
         List<RegionPingResult> list = Ping.func_224864_a();
         RealmsClient realmsclient = RealmsClient.func_224911_a();
         PingResult pingresult = new PingResult();
         pingresult.pingResults = list;
         pingresult.worldIds = this.func_223952_o();

         try {
            realmsclient.func_224903_a(pingresult);
         } catch (Throwable throwable) {
            field_224012_a.warn("Could not send ping result to Realms: ", throwable);
         }

      })).start();
   }

   private List<Long> func_223952_o() {
      List<Long> list = Lists.newArrayList();

      for(RealmsServer realmsserver : this.field_224028_q) {
         if (this.func_223991_i(realmsserver)) {
            list.add(realmsserver.id);
         }
      }

      return list;
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
      this.func_223939_y();
   }

   private void func_223914_p() {
      RealmsServer realmsserver = this.func_223967_a(this.field_224021_j);
      if (realmsserver != null) {
         this.func_223911_a(realmsserver, this);
      }
   }

   private void func_223930_q() {
      RealmsServer realmsserver = this.func_223967_a(this.field_224021_j);
      if (realmsserver != null) {
         String s = "https://aka.ms/ExtendJavaRealms?subscriptionId=" + realmsserver.remoteSubscriptionId + "&profileId=" + Realms.getUUID() + "&ref=" + (realmsserver.expiredTrial ? "expiredTrial" : "expiredRealm");
         this.func_223921_a(s);
      }
   }

   private void func_223988_r() {
      if (this.field_224037_z && !this.field_223993_A) {
         RealmsUtil.func_225190_c("https://aka.ms/startjavarealmstrial");
         Realms.setScreen(this.field_224019_h);
      }
   }

   private void func_223895_s() {
      if (!field_224033_v) {
         field_224033_v = true;
         (new Thread("MCO Compatability Checker #1") {
            public void run() {
               RealmsClient realmsclient = RealmsClient.func_224911_a();

               try {
                  RealmsClient.CompatibleVersionResponse realmsclient$compatibleversionresponse = realmsclient.func_224939_i();
                  if (realmsclient$compatibleversionresponse.equals(RealmsClient.CompatibleVersionResponse.OUTDATED)) {
                     RealmsMainScreen.field_224000_H = new RealmsClientOutdatedScreen(RealmsMainScreen.this.field_224019_h, true);
                     Realms.setScreen(RealmsMainScreen.field_224000_H);
                  } else if (realmsclient$compatibleversionresponse.equals(RealmsClient.CompatibleVersionResponse.OTHER)) {
                     RealmsMainScreen.field_224000_H = new RealmsClientOutdatedScreen(RealmsMainScreen.this.field_224019_h, false);
                     Realms.setScreen(RealmsMainScreen.field_224000_H);
                  } else {
                     RealmsMainScreen.this.func_223975_u();
                  }
               } catch (RealmsServiceException realmsserviceexception) {
                  RealmsMainScreen.field_224033_v = false;
                  RealmsMainScreen.field_224012_a.error("Couldn't connect to realms: ", (Object)realmsserviceexception.toString());
                  if (realmsserviceexception.field_224981_a == 401) {
                     RealmsMainScreen.field_224000_H = new RealmsGenericErrorScreen(RealmsScreen.getLocalizedString("mco.error.invalid.session.title"), RealmsScreen.getLocalizedString("mco.error.invalid.session.message"), RealmsMainScreen.this.field_224019_h);
                     Realms.setScreen(RealmsMainScreen.field_224000_H);
                  } else {
                     Realms.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, RealmsMainScreen.this.field_224019_h));
                  }
               } catch (IOException ioexception) {
                  RealmsMainScreen.field_224033_v = false;
                  RealmsMainScreen.field_224012_a.error("Couldn't connect to realms: ", (Object)ioexception.getMessage());
                  Realms.setScreen(new RealmsGenericErrorScreen(ioexception.getMessage(), RealmsMainScreen.this.field_224019_h));
               }
            }
         }).start();
      }

   }

   private void func_223965_t() {
   }

   private void func_223975_u() {
      (new Thread("MCO Compatability Checker #1") {
         public void run() {
            RealmsClient realmsclient = RealmsClient.func_224911_a();

            try {
               Boolean obool = realmsclient.func_224918_g();
               if (obool) {
                  RealmsMainScreen.field_224012_a.info("Realms is available for this user");
                  RealmsMainScreen.field_224031_t = true;
               } else {
                  RealmsMainScreen.field_224012_a.info("Realms is not available for this user");
                  RealmsMainScreen.field_224031_t = false;
                  Realms.setScreen(new RealmsParentalConsentScreen(RealmsMainScreen.this.field_224019_h));
               }

               RealmsMainScreen.field_224032_u = true;
            } catch (RealmsServiceException realmsserviceexception) {
               RealmsMainScreen.field_224012_a.error("Couldn't connect to realms: ", (Object)realmsserviceexception.toString());
               Realms.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, RealmsMainScreen.this.field_224019_h));
            } catch (IOException ioexception) {
               RealmsMainScreen.field_224012_a.error("Couldn't connect to realms: ", (Object)ioexception.getMessage());
               Realms.setScreen(new RealmsGenericErrorScreen(ioexception.getMessage(), RealmsMainScreen.this.field_224019_h));
            }

         }
      }).start();
   }

   private void func_223884_v() {
      if (!RealmsClient.field_224944_a.equals(RealmsClient.Environment.STAGE)) {
         (new Thread("MCO Stage Availability Checker #1") {
            public void run() {
               RealmsClient realmsclient = RealmsClient.func_224911_a();

               try {
                  Boolean obool = realmsclient.func_224931_h();
                  if (obool) {
                     RealmsClient.func_224940_b();
                     RealmsMainScreen.field_224012_a.info("Switched to stage");
                     RealmsMainScreen.field_224017_f.func_225087_d();
                  }
               } catch (RealmsServiceException realmsserviceexception) {
                  RealmsMainScreen.field_224012_a.error("Couldn't connect to Realms: " + realmsserviceexception);
               } catch (IOException ioexception) {
                  RealmsMainScreen.field_224012_a.error("Couldn't parse response connecting to Realms: " + ioexception.getMessage());
               }

            }
         }).start();
      }

   }

   private void func_223962_w() {
      if (!RealmsClient.field_224944_a.equals(RealmsClient.Environment.LOCAL)) {
         (new Thread("MCO Local Availability Checker #1") {
            public void run() {
               RealmsClient realmsclient = RealmsClient.func_224911_a();

               try {
                  Boolean obool = realmsclient.func_224931_h();
                  if (obool) {
                     RealmsClient.func_224941_d();
                     RealmsMainScreen.field_224012_a.info("Switched to local");
                     RealmsMainScreen.field_224017_f.func_225087_d();
                  }
               } catch (RealmsServiceException realmsserviceexception) {
                  RealmsMainScreen.field_224012_a.error("Couldn't connect to Realms: " + realmsserviceexception);
               } catch (IOException ioexception) {
                  RealmsMainScreen.field_224012_a.error("Couldn't parse response connecting to Realms: " + ioexception.getMessage());
               }

            }
         }).start();
      }

   }

   private void func_223973_x() {
      RealmsClient.func_224921_c();
      field_224017_f.func_225087_d();
   }

   private void func_223939_y() {
      field_224017_f.func_225070_k();
   }

   private void func_223966_f(RealmsServer p_223966_1_) {
      if (Realms.getUUID().equals(p_223966_1_.ownerUUID) || field_224013_b) {
         this.func_223949_z();
         Minecraft minecraft = Minecraft.getInstance();
         minecraft.execute(() -> {
            minecraft.displayGuiScreen((new RealmsConfigureWorldScreen(this, p_223966_1_.id)).getProxy());
         });
      }

   }

   private void func_223906_g(@Nullable RealmsServer p_223906_1_) {
      if (p_223906_1_ != null && !Realms.getUUID().equals(p_223906_1_.ownerUUID)) {
         this.func_223949_z();
         String s = getLocalizedString("mco.configure.world.leave.question.line1");
         String s1 = getLocalizedString("mco.configure.world.leave.question.line2");
         Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Info, s, s1, true, 4));
      }

   }

   private void func_223949_z() {
      field_224018_g = this.field_224020_i.getScroll();
   }

   private RealmsServer func_223967_a(long p_223967_1_) {
      for(RealmsServer realmsserver : this.field_224028_q) {
         if (realmsserver.id == p_223967_1_) {
            return realmsserver;
         }
      }

      return null;
   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      if (p_confirmResult_2_ == 4) {
         if (p_confirmResult_1_) {
            (new Thread("Realms-leave-server") {
               public void run() {
                  try {
                     RealmsServer realmsserver = RealmsMainScreen.this.func_223967_a(RealmsMainScreen.this.field_224021_j);
                     if (realmsserver != null) {
                        RealmsClient realmsclient = RealmsClient.func_224911_a();
                        realmsclient.func_224912_c(realmsserver.id);
                        RealmsMainScreen.field_224017_f.func_225085_a(realmsserver);
                        RealmsMainScreen.this.field_224028_q.remove(realmsserver);
                        RealmsMainScreen.this.field_224020_i.children().removeIf((p_230230_1_) -> {
                           return p_230230_1_ instanceof RealmsMainScreen.ServerEntry && ((RealmsMainScreen.ServerEntry)p_230230_1_).field_223734_a.id == RealmsMainScreen.this.field_224021_j;
                        });
                        RealmsMainScreen.this.field_224020_i.setSelected(-1);
                        RealmsMainScreen.this.func_223915_a((RealmsServer)null);
                        RealmsMainScreen.this.field_224021_j = -1L;
                        RealmsMainScreen.this.field_224022_k.active(false);
                     }
                  } catch (RealmsServiceException realmsserviceexception) {
                     RealmsMainScreen.field_224012_a.error("Couldn't configure world");
                     Realms.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, RealmsMainScreen.this));
                  }

               }
            }).start();
         }

         Realms.setScreen(this);
      }

   }

   public void func_223978_e() {
      this.field_224021_j = -1L;
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      switch(p_keyPressed_1_) {
      case 256:
         this.field_224002_J.forEach(KeyCombo::func_224800_a);
         this.func_223955_A();
         return true;
      default:
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   private void func_223955_A() {
      if (this.func_223990_b() && this.field_224035_x) {
         this.field_224035_x = false;
      } else {
         Realms.setScreen(this.field_224019_h);
      }

   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      this.field_224002_J.forEach((p_227920_1_) -> {
         p_227920_1_.func_224799_a(p_charTyped_1_);
      });
      return true;
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.field_224005_M = false;
      this.field_224027_p = null;
      this.renderBackground();
      this.field_224020_i.render(p_render_1_, p_render_2_, p_render_3_);
      this.func_223883_a(this.width() / 2 - 50, 7);
      if (RealmsClient.field_224944_a.equals(RealmsClient.Environment.STAGE)) {
         this.func_223888_E();
      }

      if (RealmsClient.field_224944_a.equals(RealmsClient.Environment.LOCAL)) {
         this.func_223964_D();
      }

      if (this.func_223990_b()) {
         this.func_223980_b(p_render_1_, p_render_2_);
      } else {
         if (this.field_223994_B) {
            this.func_223915_a((RealmsServer)null);
            if (!this.hasWidget(this.field_224020_i)) {
               this.addWidget(this.field_224020_i);
            }

            RealmsServer realmsserver = this.func_223967_a(this.field_224021_j);
            this.field_224022_k.active(this.func_223897_b(realmsserver));
         }

         this.field_223994_B = false;
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
      if (this.field_224027_p != null) {
         this.func_223922_a(this.field_224027_p, p_render_1_, p_render_2_);
      }

      if (this.field_224037_z && !this.field_223993_A && this.func_223990_b()) {
         RealmsScreen.bind("realms:textures/gui/realms/trial_icon.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         int k = 8;
         int i = 8;
         int j = 0;
         if ((System.currentTimeMillis() / 800L & 1L) == 1L) {
            j = 8;
         }

         RealmsScreen.blit(this.field_224009_Q.func_214457_x() + this.field_224009_Q.getWidth() - 8 - 4, this.field_224009_Q.func_223291_y_() + this.field_224009_Q.getHeight() / 2 - 4, 0.0F, (float)j, 8, 8, 8, 16);
         RenderSystem.popMatrix();
      }

   }

   private void func_223883_a(int p_223883_1_, int p_223883_2_) {
      RealmsScreen.bind("realms:textures/gui/title/realms.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RenderSystem.scalef(0.5F, 0.5F, 0.5F);
      RealmsScreen.blit(p_223883_1_ * 2, p_223883_2_ * 2 - 5, 0.0F, 0.0F, 200, 50, 200, 50);
      RenderSystem.popMatrix();
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.func_223979_a(p_mouseClicked_1_, p_mouseClicked_3_) && this.field_224035_x) {
         this.field_224035_x = false;
         this.field_224036_y = true;
         return true;
      } else {
         return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
      }
   }

   private boolean func_223979_a(double p_223979_1_, double p_223979_3_) {
      int i = this.func_223989_B();
      int j = this.func_223932_C();
      return p_223979_1_ < (double)(i - 5) || p_223979_1_ > (double)(i + 315) || p_223979_3_ < (double)(j - 5) || p_223979_3_ > (double)(j + 171);
   }

   private void func_223980_b(int p_223980_1_, int p_223980_2_) {
      int i = this.func_223989_B();
      int j = this.func_223932_C();
      String s = getLocalizedString("mco.selectServer.popup");
      List<String> list = this.fontSplit(s, 100);
      if (!this.field_223994_B) {
         this.field_223997_E = 0;
         this.field_223998_F = 0;
         this.field_223999_G = true;
         this.func_223915_a((RealmsServer)null);
         if (this.hasWidget(this.field_224020_i)) {
            this.removeWidget(this.field_224020_i);
         }

         Realms.narrateNow(s);
      }

      if (this.field_224034_w) {
         this.field_223994_B = true;
      }

      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.7F);
      RenderSystem.enableBlend();
      RealmsScreen.bind("realms:textures/gui/realms/darken.png");
      RenderSystem.pushMatrix();
      int k = 0;
      int l = 32;
      RealmsScreen.blit(0, 32, 0.0F, 0.0F, this.width(), this.height() - 40 - 32, 310, 166);
      RenderSystem.popMatrix();
      RenderSystem.disableBlend();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RealmsScreen.bind("realms:textures/gui/realms/popup.png");
      RenderSystem.pushMatrix();
      RealmsScreen.blit(i, j, 0.0F, 0.0F, 310, 166, 310, 166);
      RenderSystem.popMatrix();
      if (!field_227918_e_.isEmpty()) {
         RealmsScreen.bind(field_227918_e_.get(this.field_223997_E).toString());
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RealmsScreen.blit(i + 7, j + 7, 0.0F, 0.0F, 195, 152, 195, 152);
         RenderSystem.popMatrix();
         if (this.field_223998_F % 95 < 5) {
            if (!this.field_223999_G) {
               this.field_223997_E = (this.field_223997_E + 1) % field_227918_e_.size();
               this.field_223999_G = true;
            }
         } else {
            this.field_223999_G = false;
         }
      }

      int i1 = 0;

      for(String s1 : list) {
         int j1 = this.width() / 2 + 52;
         ++i1;
         this.drawString(s1, j1, j + 10 * i1 - 3, 8421504, false);
      }

   }

   private int func_223989_B() {
      return (this.width() - 310) / 2;
   }

   private int func_223932_C() {
      return this.height() / 2 - 80;
   }

   private void func_223960_a(int p_223960_1_, int p_223960_2_, int p_223960_3_, int p_223960_4_, boolean p_223960_5_, boolean p_223960_6_) {
      int i = this.field_224029_r;
      boolean flag = this.func_223931_b((double)p_223960_1_, (double)p_223960_2_);
      boolean flag1 = p_223960_6_ && p_223960_5_;
      if (flag1) {
         float f = 0.25F + (1.0F + RealmsMth.sin((float)this.field_224030_s * 0.5F)) * 0.25F;
         int j = -16777216 | (int)(f * 64.0F) << 16 | (int)(f * 64.0F) << 8 | (int)(f * 64.0F) << 0;
         this.fillGradient(p_223960_3_ - 2, p_223960_4_ - 2, p_223960_3_ + 18, p_223960_4_ + 18, j, j);
         j = -16777216 | (int)(f * 255.0F) << 16 | (int)(f * 255.0F) << 8 | (int)(f * 255.0F) << 0;
         this.fillGradient(p_223960_3_ - 2, p_223960_4_ - 2, p_223960_3_ + 18, p_223960_4_ - 1, j, j);
         this.fillGradient(p_223960_3_ - 2, p_223960_4_ - 2, p_223960_3_ - 1, p_223960_4_ + 18, j, j);
         this.fillGradient(p_223960_3_ + 17, p_223960_4_ - 2, p_223960_3_ + 18, p_223960_4_ + 18, j, j);
         this.fillGradient(p_223960_3_ - 2, p_223960_4_ + 17, p_223960_3_ + 18, p_223960_4_ + 18, j, j);
      }

      RealmsScreen.bind("realms:textures/gui/realms/invite_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      boolean flag3 = p_223960_6_ && p_223960_5_;
      RealmsScreen.blit(p_223960_3_, p_223960_4_ - 6, flag3 ? 16.0F : 0.0F, 0.0F, 15, 25, 31, 25);
      RenderSystem.popMatrix();
      boolean flag4 = p_223960_6_ && i != 0;
      if (flag4) {
         int k = (Math.min(i, 6) - 1) * 8;
         int l = (int)(Math.max(0.0F, Math.max(RealmsMth.sin((float)(10 + this.field_224030_s) * 0.57F), RealmsMth.cos((float)this.field_224030_s * 0.35F))) * -6.0F);
         RealmsScreen.bind("realms:textures/gui/realms/invitation_icons.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RealmsScreen.blit(p_223960_3_ + 4, p_223960_4_ + 4 + l, (float)k, flag ? 8.0F : 0.0F, 8, 8, 48, 16);
         RenderSystem.popMatrix();
      }

      int j1 = p_223960_1_ + 12;
      boolean flag2 = p_223960_6_ && flag;
      if (flag2) {
         String s = getLocalizedString(i == 0 ? "mco.invites.nopending" : "mco.invites.pending");
         int i1 = this.fontWidth(s);
         this.fillGradient(j1 - 3, p_223960_2_ - 3, j1 + i1 + 3, p_223960_2_ + 8 + 3, -1073741824, -1073741824);
         this.fontDrawShadow(s, j1, p_223960_2_, -1);
      }

   }

   private boolean func_223931_b(double p_223931_1_, double p_223931_3_) {
      int i = this.width() / 2 + 50;
      int j = this.width() / 2 + 66;
      int k = 11;
      int l = 23;
      if (this.field_224029_r != 0) {
         i -= 3;
         j += 3;
         k -= 5;
         l += 5;
      }

      return (double)i <= p_223931_1_ && p_223931_1_ <= (double)j && (double)k <= p_223931_3_ && p_223931_3_ <= (double)l;
   }

   public void func_223911_a(RealmsServer p_223911_1_, RealmsScreen p_223911_2_) {
      if (p_223911_1_ != null) {
         try {
            if (!this.field_224004_L.tryLock(1L, TimeUnit.SECONDS)) {
               return;
            }

            if (this.field_224004_L.getHoldCount() > 1) {
               return;
            }
         } catch (InterruptedException var4) {
            return;
         }

         this.field_224015_d = true;
         this.func_223950_b(p_223911_1_, p_223911_2_);
      }

   }

   private void func_223950_b(RealmsServer p_223950_1_, RealmsScreen p_223950_2_) {
      RealmsLongRunningMcoTaskScreen realmslongrunningmcotaskscreen = new RealmsLongRunningMcoTaskScreen(p_223950_2_, new RealmsTasks.RealmsGetServerDetailsTask(this, p_223950_2_, p_223950_1_, this.field_224004_L));
      realmslongrunningmcotaskscreen.func_224233_a();
      Realms.setScreen(realmslongrunningmcotaskscreen);
   }

   private boolean func_223885_h(RealmsServer p_223885_1_) {
      return p_223885_1_.ownerUUID != null && p_223885_1_.ownerUUID.equals(Realms.getUUID());
   }

   private boolean func_223991_i(RealmsServer p_223991_1_) {
      return p_223991_1_.ownerUUID != null && p_223991_1_.ownerUUID.equals(Realms.getUUID()) && !p_223991_1_.expired;
   }

   private void func_223907_a(int p_223907_1_, int p_223907_2_, int p_223907_3_, int p_223907_4_) {
      RealmsScreen.bind("realms:textures/gui/realms/expired_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_223907_1_, p_223907_2_, 0.0F, 0.0F, 10, 28, 10, 28);
      RenderSystem.popMatrix();
      if (p_223907_3_ >= p_223907_1_ && p_223907_3_ <= p_223907_1_ + 9 && p_223907_4_ >= p_223907_2_ && p_223907_4_ <= p_223907_2_ + 27 && p_223907_4_ < this.height() - 40 && p_223907_4_ > 32 && !this.func_223990_b()) {
         this.field_224027_p = getLocalizedString("mco.selectServer.expired");
      }

   }

   private void func_223909_a(int p_223909_1_, int p_223909_2_, int p_223909_3_, int p_223909_4_, int p_223909_5_) {
      RealmsScreen.bind("realms:textures/gui/realms/expires_soon_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      if (this.field_224030_s % 20 < 10) {
         RealmsScreen.blit(p_223909_1_, p_223909_2_, 0.0F, 0.0F, 10, 28, 20, 28);
      } else {
         RealmsScreen.blit(p_223909_1_, p_223909_2_, 10.0F, 0.0F, 10, 28, 20, 28);
      }

      RenderSystem.popMatrix();
      if (p_223909_3_ >= p_223909_1_ && p_223909_3_ <= p_223909_1_ + 9 && p_223909_4_ >= p_223909_2_ && p_223909_4_ <= p_223909_2_ + 27 && p_223909_4_ < this.height() - 40 && p_223909_4_ > 32 && !this.func_223990_b()) {
         if (p_223909_5_ <= 0) {
            this.field_224027_p = getLocalizedString("mco.selectServer.expires.soon");
         } else if (p_223909_5_ == 1) {
            this.field_224027_p = getLocalizedString("mco.selectServer.expires.day");
         } else {
            this.field_224027_p = getLocalizedString("mco.selectServer.expires.days", new Object[]{p_223909_5_});
         }
      }

   }

   private void func_223987_b(int p_223987_1_, int p_223987_2_, int p_223987_3_, int p_223987_4_) {
      RealmsScreen.bind("realms:textures/gui/realms/on_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_223987_1_, p_223987_2_, 0.0F, 0.0F, 10, 28, 10, 28);
      RenderSystem.popMatrix();
      if (p_223987_3_ >= p_223987_1_ && p_223987_3_ <= p_223987_1_ + 9 && p_223987_4_ >= p_223987_2_ && p_223987_4_ <= p_223987_2_ + 27 && p_223987_4_ < this.height() - 40 && p_223987_4_ > 32 && !this.func_223990_b()) {
         this.field_224027_p = getLocalizedString("mco.selectServer.open");
      }

   }

   private void func_223912_c(int p_223912_1_, int p_223912_2_, int p_223912_3_, int p_223912_4_) {
      RealmsScreen.bind("realms:textures/gui/realms/off_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_223912_1_, p_223912_2_, 0.0F, 0.0F, 10, 28, 10, 28);
      RenderSystem.popMatrix();
      if (p_223912_3_ >= p_223912_1_ && p_223912_3_ <= p_223912_1_ + 9 && p_223912_4_ >= p_223912_2_ && p_223912_4_ <= p_223912_2_ + 27 && p_223912_4_ < this.height() - 40 && p_223912_4_ > 32 && !this.func_223990_b()) {
         this.field_224027_p = getLocalizedString("mco.selectServer.closed");
      }

   }

   private void func_223945_d(int p_223945_1_, int p_223945_2_, int p_223945_3_, int p_223945_4_) {
      boolean flag = false;
      if (p_223945_3_ >= p_223945_1_ && p_223945_3_ <= p_223945_1_ + 28 && p_223945_4_ >= p_223945_2_ && p_223945_4_ <= p_223945_2_ + 28 && p_223945_4_ < this.height() - 40 && p_223945_4_ > 32 && !this.func_223990_b()) {
         flag = true;
      }

      RealmsScreen.bind("realms:textures/gui/realms/leave_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_223945_1_, p_223945_2_, flag ? 28.0F : 0.0F, 0.0F, 28, 28, 56, 28);
      RenderSystem.popMatrix();
      if (flag) {
         this.field_224027_p = getLocalizedString("mco.selectServer.leave");
      }

   }

   private void func_223916_e(int p_223916_1_, int p_223916_2_, int p_223916_3_, int p_223916_4_) {
      boolean flag = false;
      if (p_223916_3_ >= p_223916_1_ && p_223916_3_ <= p_223916_1_ + 28 && p_223916_4_ >= p_223916_2_ && p_223916_4_ <= p_223916_2_ + 28 && p_223916_4_ < this.height() - 40 && p_223916_4_ > 32 && !this.func_223990_b()) {
         flag = true;
      }

      RealmsScreen.bind("realms:textures/gui/realms/configure_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_223916_1_, p_223916_2_, flag ? 28.0F : 0.0F, 0.0F, 28, 28, 56, 28);
      RenderSystem.popMatrix();
      if (flag) {
         this.field_224027_p = getLocalizedString("mco.selectServer.configure");
      }

   }

   protected void func_223922_a(String p_223922_1_, int p_223922_2_, int p_223922_3_) {
      if (p_223922_1_ != null) {
         int i = 0;
         int j = 0;

         for(String s : p_223922_1_.split("\n")) {
            int k = this.fontWidth(s);
            if (k > j) {
               j = k;
            }
         }

         int l = p_223922_2_ - j - 5;
         int i1 = p_223922_3_;
         if (l < 0) {
            l = p_223922_2_ + 12;
         }

         for(String s1 : p_223922_1_.split("\n")) {
            this.fillGradient(l - 3, i1 - (i == 0 ? 3 : 0) + i, l + j + 3, i1 + 8 + 3 + i, -1073741824, -1073741824);
            this.fontDrawShadow(s1, l, i1 + i, 16777215);
            i += 10;
         }

      }
   }

   private void func_223933_a(int p_223933_1_, int p_223933_2_, int p_223933_3_, int p_223933_4_, boolean p_223933_5_) {
      boolean flag = false;
      if (p_223933_1_ >= p_223933_3_ && p_223933_1_ <= p_223933_3_ + 20 && p_223933_2_ >= p_223933_4_ && p_223933_2_ <= p_223933_4_ + 20) {
         flag = true;
      }

      RealmsScreen.bind("realms:textures/gui/realms/questionmark.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_223933_3_, p_223933_4_, p_223933_5_ ? 20.0F : 0.0F, 0.0F, 20, 20, 40, 20);
      RenderSystem.popMatrix();
      if (flag) {
         this.field_224027_p = getLocalizedString("mco.selectServer.info");
      }

   }

   private void func_223982_a(int p_223982_1_, int p_223982_2_, boolean p_223982_3_, int p_223982_4_, int p_223982_5_, boolean p_223982_6_, boolean p_223982_7_) {
      boolean flag = false;
      if (p_223982_1_ >= p_223982_4_ && p_223982_1_ <= p_223982_4_ + 20 && p_223982_2_ >= p_223982_5_ && p_223982_2_ <= p_223982_5_ + 20) {
         flag = true;
      }

      RealmsScreen.bind("realms:textures/gui/realms/news_icon.png");
      if (p_223982_7_) {
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      } else {
         RenderSystem.color4f(0.5F, 0.5F, 0.5F, 1.0F);
      }

      RenderSystem.pushMatrix();
      boolean flag1 = p_223982_7_ && p_223982_6_;
      RealmsScreen.blit(p_223982_4_, p_223982_5_, flag1 ? 20.0F : 0.0F, 0.0F, 20, 20, 40, 20);
      RenderSystem.popMatrix();
      if (flag && p_223982_7_) {
         this.field_224027_p = getLocalizedString("mco.news");
      }

      if (p_223982_3_ && p_223982_7_) {
         int i = flag ? 0 : (int)(Math.max(0.0F, Math.max(RealmsMth.sin((float)(10 + this.field_224030_s) * 0.57F), RealmsMth.cos((float)this.field_224030_s * 0.35F))) * -6.0F);
         RealmsScreen.bind("realms:textures/gui/realms/invitation_icons.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RealmsScreen.blit(p_223982_4_ + 10, p_223982_5_ + 2 + i, 40.0F, 0.0F, 8, 8, 48, 16);
         RenderSystem.popMatrix();
      }

   }

   private void func_223964_D() {
      String s = "LOCAL!";
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RenderSystem.translatef((float)(this.width() / 2 - 25), 20.0F, 0.0F);
      RenderSystem.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
      RenderSystem.scalef(1.5F, 1.5F, 1.5F);
      this.drawString("LOCAL!", 0, 0, 8388479);
      RenderSystem.popMatrix();
   }

   private void func_223888_E() {
      String s = "STAGE!";
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RenderSystem.translatef((float)(this.width() / 2 - 25), 20.0F, 0.0F);
      RenderSystem.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
      RenderSystem.scalef(1.5F, 1.5F, 1.5F);
      this.drawString("STAGE!", 0, 0, -256);
      RenderSystem.popMatrix();
   }

   public RealmsMainScreen func_223942_f() {
      return new RealmsMainScreen(this.field_224019_h);
   }

   public static void func_227932_a_(IResourceManager p_227932_0_) {
      Collection<ResourceLocation> collection = p_227932_0_.getAllResourceLocations("textures/gui/images", (p_227934_0_) -> {
         return p_227934_0_.endsWith(".png");
      });
      field_227918_e_ = collection.stream().filter((p_227931_0_) -> {
         return p_227931_0_.getNamespace().equals("realms");
      }).collect(ImmutableList.toImmutableList());
   }

   @OnlyIn(Dist.CLIENT)
   class CloseButton extends RealmsButton {
      public CloseButton() {
         super(11, RealmsMainScreen.this.func_223989_B() + 4, RealmsMainScreen.this.func_223932_C() + 4, 12, 12, RealmsScreen.getLocalizedString("mco.selectServer.close"));
      }

      public void tick() {
         super.tick();
      }

      public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
         super.render(p_render_1_, p_render_2_, p_render_3_);
      }

      public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
         RealmsScreen.bind("realms:textures/gui/realms/cross_icon.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RealmsScreen.blit(this.func_214457_x(), this.func_223291_y_(), 0.0F, this.getProxy().isHovered() ? 12.0F : 0.0F, 12, 12, 12, 24);
         RenderSystem.popMatrix();
         if (this.getProxy().isMouseOver((double)p_renderButton_1_, (double)p_renderButton_2_)) {
            RealmsMainScreen.this.field_224027_p = this.getProxy().getMessage();
         }

      }

      public void onPress() {
         RealmsMainScreen.this.func_223955_A();
      }
   }

   @OnlyIn(Dist.CLIENT)
   class InfoButton extends RealmsButton {
      public InfoButton() {
         super(10, RealmsMainScreen.this.width() - 37, 6, 20, 20, RealmsScreen.getLocalizedString("mco.selectServer.info"));
      }

      public void tick() {
         super.tick();
      }

      public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
         super.render(p_render_1_, p_render_2_, p_render_3_);
      }

      public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
         RealmsMainScreen.this.func_223933_a(p_renderButton_1_, p_renderButton_2_, this.func_214457_x(), this.func_223291_y_(), this.getProxy().isHovered());
      }

      public void onPress() {
         RealmsMainScreen.this.field_224035_x = !RealmsMainScreen.this.field_224035_x;
      }
   }

   @OnlyIn(Dist.CLIENT)
   class NewsButton extends RealmsButton {
      public NewsButton() {
         super(9, RealmsMainScreen.this.width() - 62, 6, 20, 20, "");
      }

      public void tick() {
         this.setMessage(Realms.getLocalizedString("mco.news"));
      }

      public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
         super.render(p_render_1_, p_render_2_, p_render_3_);
      }

      public void onPress() {
         if (RealmsMainScreen.this.field_223996_D != null) {
            RealmsUtil.func_225190_c(RealmsMainScreen.this.field_223996_D);
            if (RealmsMainScreen.this.field_223995_C) {
               RealmsPersistence.RealmsPersistenceData realmspersistence$realmspersistencedata = RealmsPersistence.func_225188_a();
               realmspersistence$realmspersistencedata.field_225186_b = false;
               RealmsMainScreen.this.field_223995_C = false;
               RealmsPersistence.func_225187_a(realmspersistence$realmspersistencedata);
            }

         }
      }

      public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
         RealmsMainScreen.this.func_223982_a(p_renderButton_1_, p_renderButton_2_, RealmsMainScreen.this.field_223995_C, this.func_214457_x(), this.func_223291_y_(), this.getProxy().isHovered(), this.active());
      }
   }

   @OnlyIn(Dist.CLIENT)
   class PendingInvitesButton extends RealmsButton {
      public PendingInvitesButton() {
         super(8, RealmsMainScreen.this.width() / 2 + 47, 6, 22, 22, "");
      }

      public void tick() {
         this.setMessage(Realms.getLocalizedString(RealmsMainScreen.this.field_224029_r == 0 ? "mco.invites.nopending" : "mco.invites.pending"));
      }

      public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
         super.render(p_render_1_, p_render_2_, p_render_3_);
      }

      public void onPress() {
         RealmsPendingInvitesScreen realmspendinginvitesscreen = new RealmsPendingInvitesScreen(RealmsMainScreen.this.field_224019_h);
         Realms.setScreen(realmspendinginvitesscreen);
      }

      public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
         RealmsMainScreen.this.func_223960_a(p_renderButton_1_, p_renderButton_2_, this.func_214457_x(), this.func_223291_y_(), this.getProxy().isHovered(), this.active());
      }
   }

   @OnlyIn(Dist.CLIENT)
   class ServerEntry extends RealmListEntry {
      final RealmsServer field_223734_a;

      public ServerEntry(RealmsServer resourceManagerIn) {
         this.field_223734_a = resourceManagerIn;
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         this.func_223731_a(this.field_223734_a, p_render_3_, p_render_2_, p_render_6_, p_render_7_);
      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         if (this.field_223734_a.state == RealmsServer.Status.UNINITIALIZED) {
            RealmsMainScreen.this.field_224021_j = -1L;
            Realms.setScreen(new RealmsCreateRealmScreen(this.field_223734_a, RealmsMainScreen.this));
         } else {
            RealmsMainScreen.this.field_224021_j = this.field_223734_a.id;
         }

         return true;
      }

      private void func_223731_a(RealmsServer p_223731_1_, int p_223731_2_, int p_223731_3_, int p_223731_4_, int p_223731_5_) {
         this.func_223733_b(p_223731_1_, p_223731_2_ + 36, p_223731_3_, p_223731_4_, p_223731_5_);
      }

      private void func_223733_b(RealmsServer p_223733_1_, int p_223733_2_, int p_223733_3_, int p_223733_4_, int p_223733_5_) {
         if (p_223733_1_.state == RealmsServer.Status.UNINITIALIZED) {
            RealmsScreen.bind("realms:textures/gui/realms/world_icon.png");
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableAlphaTest();
            RenderSystem.pushMatrix();
            RealmsScreen.blit(p_223733_2_ + 10, p_223733_3_ + 6, 0.0F, 0.0F, 40, 20, 40, 20);
            RenderSystem.popMatrix();
            float f = 0.5F + (1.0F + RealmsMth.sin((float)RealmsMainScreen.this.field_224030_s * 0.25F)) * 0.25F;
            int k2 = -16777216 | (int)(127.0F * f) << 16 | (int)(255.0F * f) << 8 | (int)(127.0F * f);
            RealmsMainScreen.this.drawCenteredString(RealmsScreen.getLocalizedString("mco.selectServer.uninitialized"), p_223733_2_ + 10 + 40 + 75, p_223733_3_ + 12, k2);
         } else {
            int i = 225;
            int j = 2;
            if (p_223733_1_.expired) {
               RealmsMainScreen.this.func_223907_a(p_223733_2_ + 225 - 14, p_223733_3_ + 2, p_223733_4_, p_223733_5_);
            } else if (p_223733_1_.state == RealmsServer.Status.CLOSED) {
               RealmsMainScreen.this.func_223912_c(p_223733_2_ + 225 - 14, p_223733_3_ + 2, p_223733_4_, p_223733_5_);
            } else if (RealmsMainScreen.this.func_223885_h(p_223733_1_) && p_223733_1_.daysLeft < 7) {
               RealmsMainScreen.this.func_223909_a(p_223733_2_ + 225 - 14, p_223733_3_ + 2, p_223733_4_, p_223733_5_, p_223733_1_.daysLeft);
            } else if (p_223733_1_.state == RealmsServer.Status.OPEN) {
               RealmsMainScreen.this.func_223987_b(p_223733_2_ + 225 - 14, p_223733_3_ + 2, p_223733_4_, p_223733_5_);
            }

            if (!RealmsMainScreen.this.func_223885_h(p_223733_1_) && !RealmsMainScreen.field_224013_b) {
               RealmsMainScreen.this.func_223945_d(p_223733_2_ + 225, p_223733_3_ + 2, p_223733_4_, p_223733_5_);
            } else {
               RealmsMainScreen.this.func_223916_e(p_223733_2_ + 225, p_223733_3_ + 2, p_223733_4_, p_223733_5_);
            }

            if (!"0".equals(p_223733_1_.serverPing.nrOfPlayers)) {
               String s = ChatFormatting.GRAY + "" + p_223733_1_.serverPing.nrOfPlayers;
               RealmsMainScreen.this.drawString(s, p_223733_2_ + 207 - RealmsMainScreen.this.fontWidth(s), p_223733_3_ + 3, 8421504);
               if (p_223733_4_ >= p_223733_2_ + 207 - RealmsMainScreen.this.fontWidth(s) && p_223733_4_ <= p_223733_2_ + 207 && p_223733_5_ >= p_223733_3_ + 1 && p_223733_5_ <= p_223733_3_ + 10 && p_223733_5_ < RealmsMainScreen.this.height() - 40 && p_223733_5_ > 32 && !RealmsMainScreen.this.func_223990_b()) {
                  RealmsMainScreen.this.field_224027_p = p_223733_1_.serverPing.playerList;
               }
            }

            if (RealmsMainScreen.this.func_223885_h(p_223733_1_) && p_223733_1_.expired) {
               boolean flag = false;
               RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
               RenderSystem.enableBlend();
               RealmsScreen.bind("minecraft:textures/gui/widgets.png");
               RenderSystem.pushMatrix();
               RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
               String s2 = RealmsScreen.getLocalizedString("mco.selectServer.expiredList");
               String s3 = RealmsScreen.getLocalizedString("mco.selectServer.expiredRenew");
               if (p_223733_1_.expiredTrial) {
                  s2 = RealmsScreen.getLocalizedString("mco.selectServer.expiredTrial");
                  s3 = RealmsScreen.getLocalizedString("mco.selectServer.expiredSubscribe");
               }

               int l = RealmsMainScreen.this.fontWidth(s3) + 17;
               int i1 = 16;
               int j1 = p_223733_2_ + RealmsMainScreen.this.fontWidth(s2) + 8;
               int k1 = p_223733_3_ + 13;
               if (p_223733_4_ >= j1 && p_223733_4_ < j1 + l && p_223733_5_ > k1 && p_223733_5_ <= k1 + 16 & p_223733_5_ < RealmsMainScreen.this.height() - 40 && p_223733_5_ > 32 && !RealmsMainScreen.this.func_223990_b()) {
                  flag = true;
                  RealmsMainScreen.this.field_224005_M = true;
               }

               int l1 = flag ? 2 : 1;
               RealmsScreen.blit(j1, k1, 0.0F, (float)(46 + l1 * 20), l / 2, 8, 256, 256);
               RealmsScreen.blit(j1 + l / 2, k1, (float)(200 - l / 2), (float)(46 + l1 * 20), l / 2, 8, 256, 256);
               RealmsScreen.blit(j1, k1 + 8, 0.0F, (float)(46 + l1 * 20 + 12), l / 2, 8, 256, 256);
               RealmsScreen.blit(j1 + l / 2, k1 + 8, (float)(200 - l / 2), (float)(46 + l1 * 20 + 12), l / 2, 8, 256, 256);
               RenderSystem.popMatrix();
               RenderSystem.disableBlend();
               int i2 = p_223733_3_ + 11 + 5;
               int j2 = flag ? 16777120 : 16777215;
               RealmsMainScreen.this.drawString(s2, p_223733_2_ + 2, i2 + 1, 15553363);
               RealmsMainScreen.this.drawCenteredString(s3, j1 + l / 2, i2 + 1, j2);
            } else {
               if (p_223733_1_.worldType.equals(RealmsServer.ServerType.MINIGAME)) {
                  int l2 = 13413468;
                  String s1 = RealmsScreen.getLocalizedString("mco.selectServer.minigame") + " ";
                  int k = RealmsMainScreen.this.fontWidth(s1);
                  RealmsMainScreen.this.drawString(s1, p_223733_2_ + 2, p_223733_3_ + 12, 13413468);
                  RealmsMainScreen.this.drawString(p_223733_1_.getMinigameName(), p_223733_2_ + 2 + k, p_223733_3_ + 12, 8421504);
               } else {
                  RealmsMainScreen.this.drawString(p_223733_1_.getDescription(), p_223733_2_ + 2, p_223733_3_ + 12, 8421504);
               }

               if (!RealmsMainScreen.this.func_223885_h(p_223733_1_)) {
                  RealmsMainScreen.this.drawString(p_223733_1_.owner, p_223733_2_ + 2, p_223733_3_ + 12 + 11, 8421504);
               }
            }

            RealmsMainScreen.this.drawString(p_223733_1_.getName(), p_223733_2_ + 2, p_223733_3_ + 1, 16777215);
            RealmsTextureManager.func_225205_a(p_223733_1_.ownerUUID, () -> {
               RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
               RealmsScreen.blit(p_223733_2_ - 36, p_223733_3_, 8.0F, 8.0F, 8, 8, 32, 32, 64, 64);
               RealmsScreen.blit(p_223733_2_ - 36, p_223733_3_, 40.0F, 8.0F, 8, 8, 32, 32, 64, 64);
            });
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   class ServerList extends RealmsObjectSelectionList<RealmListEntry> {
      public ServerList() {
         super(RealmsMainScreen.this.width(), RealmsMainScreen.this.height(), 32, RealmsMainScreen.this.height() - 40, 36);
      }

      public boolean isFocused() {
         return RealmsMainScreen.this.isFocused(this);
      }

      public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
         if (p_keyPressed_1_ != 257 && p_keyPressed_1_ != 32 && p_keyPressed_1_ != 335) {
            return false;
         } else {
            RealmListEntry realmlistentry = this.getSelected();
            return realmlistentry == null ? super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) : realmlistentry.mouseClicked(0.0D, 0.0D, 0);
         }
      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         if (p_mouseClicked_5_ == 0 && p_mouseClicked_1_ < (double)this.getScrollbarPosition() && p_mouseClicked_3_ >= (double)this.y0() && p_mouseClicked_3_ <= (double)this.y1()) {
            int i = RealmsMainScreen.this.field_224020_i.getRowLeft();
            int j = this.getScrollbarPosition();
            int k = (int)Math.floor(p_mouseClicked_3_ - (double)this.y0()) - this.headerHeight() + this.getScroll() - 4;
            int l = k / this.itemHeight();
            if (p_mouseClicked_1_ >= (double)i && p_mouseClicked_1_ <= (double)j && l >= 0 && k >= 0 && l < this.getItemCount()) {
               this.itemClicked(k, l, p_mouseClicked_1_, p_mouseClicked_3_, this.width());
               RealmsMainScreen.this.field_224003_K = RealmsMainScreen.this.field_224003_K + 7;
               this.selectItem(l);
            }

            return true;
         } else {
            return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
         }
      }

      public void selectItem(int p_selectItem_1_) {
         this.setSelected(p_selectItem_1_);
         if (p_selectItem_1_ != -1) {
            RealmsServer realmsserver;
            if (RealmsMainScreen.this.func_223928_a()) {
               if (p_selectItem_1_ == 0) {
                  Realms.narrateNow(RealmsScreen.getLocalizedString("mco.trial.message.line1"), RealmsScreen.getLocalizedString("mco.trial.message.line2"));
                  realmsserver = null;
               } else {
                  if (p_selectItem_1_ - 1 >= RealmsMainScreen.this.field_224028_q.size()) {
                     RealmsMainScreen.this.field_224021_j = -1L;
                     return;
                  }

                  realmsserver = RealmsMainScreen.this.field_224028_q.get(p_selectItem_1_ - 1);
               }
            } else {
               if (p_selectItem_1_ >= RealmsMainScreen.this.field_224028_q.size()) {
                  RealmsMainScreen.this.field_224021_j = -1L;
                  return;
               }

               realmsserver = RealmsMainScreen.this.field_224028_q.get(p_selectItem_1_);
            }

            RealmsMainScreen.this.func_223915_a(realmsserver);
            if (realmsserver == null) {
               RealmsMainScreen.this.field_224021_j = -1L;
            } else if (realmsserver.state == RealmsServer.Status.UNINITIALIZED) {
               Realms.narrateNow(RealmsScreen.getLocalizedString("mco.selectServer.uninitialized") + RealmsScreen.getLocalizedString("mco.gui.button"));
               RealmsMainScreen.this.field_224021_j = -1L;
            } else {
               RealmsMainScreen.this.field_224021_j = realmsserver.id;
               if (RealmsMainScreen.this.field_224003_K >= 10 && RealmsMainScreen.this.field_224022_k.active()) {
                  RealmsMainScreen.this.func_223911_a(RealmsMainScreen.this.func_223967_a(RealmsMainScreen.this.field_224021_j), RealmsMainScreen.this);
               }

               Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", realmsserver.name));
            }
         }
      }

      public void itemClicked(int p_itemClicked_1_, int p_itemClicked_2_, double p_itemClicked_3_, double p_itemClicked_5_, int p_itemClicked_7_) {
         if (RealmsMainScreen.this.func_223928_a()) {
            if (p_itemClicked_2_ == 0) {
               RealmsMainScreen.this.field_224035_x = true;
               return;
            }

            --p_itemClicked_2_;
         }

         if (p_itemClicked_2_ < RealmsMainScreen.this.field_224028_q.size()) {
            RealmsServer realmsserver = RealmsMainScreen.this.field_224028_q.get(p_itemClicked_2_);
            if (realmsserver != null) {
               if (realmsserver.state == RealmsServer.Status.UNINITIALIZED) {
                  RealmsMainScreen.this.field_224021_j = -1L;
                  Realms.setScreen(new RealmsCreateRealmScreen(realmsserver, RealmsMainScreen.this));
               } else {
                  RealmsMainScreen.this.field_224021_j = realmsserver.id;
               }

               if (RealmsMainScreen.this.field_224027_p != null && RealmsMainScreen.this.field_224027_p.equals(RealmsScreen.getLocalizedString("mco.selectServer.configure"))) {
                  RealmsMainScreen.this.field_224021_j = realmsserver.id;
                  RealmsMainScreen.this.func_223966_f(realmsserver);
               } else if (RealmsMainScreen.this.field_224027_p != null && RealmsMainScreen.this.field_224027_p.equals(RealmsScreen.getLocalizedString("mco.selectServer.leave"))) {
                  RealmsMainScreen.this.field_224021_j = realmsserver.id;
                  RealmsMainScreen.this.func_223906_g(realmsserver);
               } else if (RealmsMainScreen.this.func_223885_h(realmsserver) && realmsserver.expired && RealmsMainScreen.this.field_224005_M) {
                  RealmsMainScreen.this.func_223930_q();
               }

            }
         }
      }

      public int getMaxPosition() {
         return this.getItemCount() * 36;
      }

      public int getRowWidth() {
         return 300;
      }
   }

   @OnlyIn(Dist.CLIENT)
   class TrialServerEntry extends RealmListEntry {
      public TrialServerEntry() {
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         this.func_223736_a(p_render_1_, p_render_3_, p_render_2_, p_render_6_, p_render_7_);
      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         RealmsMainScreen.this.field_224035_x = true;
         return true;
      }

      private void func_223736_a(int p_223736_1_, int p_223736_2_, int p_223736_3_, int p_223736_4_, int p_223736_5_) {
         int i = p_223736_3_ + 8;
         int j = 0;
         String s = RealmsScreen.getLocalizedString("mco.trial.message.line1") + "\\n" + RealmsScreen.getLocalizedString("mco.trial.message.line2");
         boolean flag = false;
         if (p_223736_2_ <= p_223736_4_ && p_223736_4_ <= RealmsMainScreen.this.field_224020_i.getScroll() && p_223736_3_ <= p_223736_5_ && p_223736_5_ <= p_223736_3_ + 32) {
            flag = true;
         }

         int k = 8388479;
         if (flag && !RealmsMainScreen.this.func_223990_b()) {
            k = 6077788;
         }

         for(String s1 : s.split("\\\\n")) {
            RealmsMainScreen.this.drawCenteredString(s1, RealmsMainScreen.this.width() / 2, i + j, k);
            j += 10;
         }

      }
   }
}