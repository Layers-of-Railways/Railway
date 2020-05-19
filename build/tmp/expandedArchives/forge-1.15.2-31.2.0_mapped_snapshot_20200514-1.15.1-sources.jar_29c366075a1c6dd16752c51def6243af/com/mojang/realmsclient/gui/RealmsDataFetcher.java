package com.mojang.realmsclient.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.util.RealmsPersistence;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.realms.Realms;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsDataFetcher {
   private static final Logger field_225088_a = LogManager.getLogger();
   private final ScheduledExecutorService field_225089_b = Executors.newScheduledThreadPool(3);
   private volatile boolean field_225090_c = true;
   private final RealmsDataFetcher.ServerListUpdateTask field_225091_d = new RealmsDataFetcher.ServerListUpdateTask();
   private final RealmsDataFetcher.PendingInviteUpdateTask field_225092_e = new RealmsDataFetcher.PendingInviteUpdateTask();
   private final RealmsDataFetcher.TrialAvailabilityTask field_225093_f = new RealmsDataFetcher.TrialAvailabilityTask();
   private final RealmsDataFetcher.LiveStatsTask field_225094_g = new RealmsDataFetcher.LiveStatsTask();
   private final RealmsDataFetcher.UnreadNewsTask field_225095_h = new RealmsDataFetcher.UnreadNewsTask();
   private final Set<RealmsServer> field_225096_i = Sets.newHashSet();
   private List<RealmsServer> field_225097_j = Lists.newArrayList();
   private RealmsServerPlayerLists field_225098_k;
   private int field_225099_l;
   private boolean field_225100_m;
   private boolean field_225101_n;
   private String field_225102_o;
   private ScheduledFuture<?> field_225103_p;
   private ScheduledFuture<?> field_225104_q;
   private ScheduledFuture<?> field_225105_r;
   private ScheduledFuture<?> field_225106_s;
   private ScheduledFuture<?> field_225107_t;
   private final Map<RealmsDataFetcher.Task, Boolean> field_225108_u = new ConcurrentHashMap<>(RealmsDataFetcher.Task.values().length);

   public boolean func_225065_a() {
      return this.field_225090_c;
   }

   public synchronized void func_225086_b() {
      if (this.field_225090_c) {
         this.field_225090_c = false;
         this.func_225084_n();
         this.func_225069_m();
      }

   }

   public synchronized void func_225077_a(List<RealmsDataFetcher.Task> p_225077_1_) {
      if (this.field_225090_c) {
         this.field_225090_c = false;
         this.func_225084_n();

         for(RealmsDataFetcher.Task realmsdatafetcher$task : p_225077_1_) {
            this.field_225108_u.put(realmsdatafetcher$task, false);
            switch(realmsdatafetcher$task) {
            case SERVER_LIST:
               this.field_225103_p = this.field_225089_b.scheduleAtFixedRate(this.field_225091_d, 0L, 60L, TimeUnit.SECONDS);
               break;
            case PENDING_INVITE:
               this.field_225104_q = this.field_225089_b.scheduleAtFixedRate(this.field_225092_e, 0L, 10L, TimeUnit.SECONDS);
               break;
            case TRIAL_AVAILABLE:
               this.field_225105_r = this.field_225089_b.scheduleAtFixedRate(this.field_225093_f, 0L, 60L, TimeUnit.SECONDS);
               break;
            case LIVE_STATS:
               this.field_225106_s = this.field_225089_b.scheduleAtFixedRate(this.field_225094_g, 0L, 10L, TimeUnit.SECONDS);
               break;
            case UNREAD_NEWS:
               this.field_225107_t = this.field_225089_b.scheduleAtFixedRate(this.field_225095_h, 0L, 300L, TimeUnit.SECONDS);
            }
         }
      }

   }

   public boolean func_225083_a(RealmsDataFetcher.Task p_225083_1_) {
      Boolean obool = this.field_225108_u.get(p_225083_1_);
      return obool == null ? false : obool;
   }

   public void func_225072_c() {
      for(RealmsDataFetcher.Task realmsdatafetcher$task : this.field_225108_u.keySet()) {
         this.field_225108_u.put(realmsdatafetcher$task, false);
      }

   }

   public synchronized void func_225087_d() {
      this.func_225070_k();
      this.func_225086_b();
   }

   public synchronized List<RealmsServer> func_225078_e() {
      return Lists.newArrayList(this.field_225097_j);
   }

   public synchronized int func_225081_f() {
      return this.field_225099_l;
   }

   public synchronized boolean func_225071_g() {
      return this.field_225100_m;
   }

   public synchronized RealmsServerPlayerLists func_225079_h() {
      return this.field_225098_k;
   }

   public synchronized boolean func_225059_i() {
      return this.field_225101_n;
   }

   public synchronized String func_225063_j() {
      return this.field_225102_o;
   }

   public synchronized void func_225070_k() {
      this.field_225090_c = true;
      this.func_225084_n();
   }

   private void func_225069_m() {
      for(RealmsDataFetcher.Task realmsdatafetcher$task : RealmsDataFetcher.Task.values()) {
         this.field_225108_u.put(realmsdatafetcher$task, false);
      }

      this.field_225103_p = this.field_225089_b.scheduleAtFixedRate(this.field_225091_d, 0L, 60L, TimeUnit.SECONDS);
      this.field_225104_q = this.field_225089_b.scheduleAtFixedRate(this.field_225092_e, 0L, 10L, TimeUnit.SECONDS);
      this.field_225105_r = this.field_225089_b.scheduleAtFixedRate(this.field_225093_f, 0L, 60L, TimeUnit.SECONDS);
      this.field_225106_s = this.field_225089_b.scheduleAtFixedRate(this.field_225094_g, 0L, 10L, TimeUnit.SECONDS);
      this.field_225107_t = this.field_225089_b.scheduleAtFixedRate(this.field_225095_h, 0L, 300L, TimeUnit.SECONDS);
   }

   private void func_225084_n() {
      try {
         if (this.field_225103_p != null) {
            this.field_225103_p.cancel(false);
         }

         if (this.field_225104_q != null) {
            this.field_225104_q.cancel(false);
         }

         if (this.field_225105_r != null) {
            this.field_225105_r.cancel(false);
         }

         if (this.field_225106_s != null) {
            this.field_225106_s.cancel(false);
         }

         if (this.field_225107_t != null) {
            this.field_225107_t.cancel(false);
         }
      } catch (Exception exception) {
         field_225088_a.error("Failed to cancel Realms tasks", (Throwable)exception);
      }

   }

   private synchronized void func_225080_b(List<RealmsServer> p_225080_1_) {
      int i = 0;

      for(RealmsServer realmsserver : this.field_225096_i) {
         if (p_225080_1_.remove(realmsserver)) {
            ++i;
         }
      }

      if (i == 0) {
         this.field_225096_i.clear();
      }

      this.field_225097_j = p_225080_1_;
   }

   public synchronized void func_225085_a(RealmsServer p_225085_1_) {
      this.field_225097_j.remove(p_225085_1_);
      this.field_225096_i.add(p_225085_1_);
   }

   private void func_225082_c(List<RealmsServer> p_225082_1_) {
      p_225082_1_.sort(new RealmsServer.ServerComparator(Realms.getName()));
   }

   private boolean func_225068_o() {
      return !this.field_225090_c;
   }

   @OnlyIn(Dist.CLIENT)
   class LiveStatsTask implements Runnable {
      private LiveStatsTask() {
      }

      public void run() {
         if (RealmsDataFetcher.this.func_225068_o()) {
            this.func_225048_a();
         }

      }

      private void func_225048_a() {
         try {
            RealmsClient realmsclient = RealmsClient.func_224911_a();
            if (realmsclient != null) {
               RealmsDataFetcher.this.field_225098_k = realmsclient.func_224915_f();
               RealmsDataFetcher.this.field_225108_u.put(RealmsDataFetcher.Task.LIVE_STATS, true);
            }
         } catch (Exception exception) {
            RealmsDataFetcher.field_225088_a.error("Couldn't get live stats", (Throwable)exception);
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   class PendingInviteUpdateTask implements Runnable {
      private PendingInviteUpdateTask() {
      }

      public void run() {
         if (RealmsDataFetcher.this.func_225068_o()) {
            this.func_225051_a();
         }

      }

      private void func_225051_a() {
         try {
            RealmsClient realmsclient = RealmsClient.func_224911_a();
            if (realmsclient != null) {
               RealmsDataFetcher.this.field_225099_l = realmsclient.func_224909_j();
               RealmsDataFetcher.this.field_225108_u.put(RealmsDataFetcher.Task.PENDING_INVITE, true);
            }
         } catch (Exception exception) {
            RealmsDataFetcher.field_225088_a.error("Couldn't get pending invite count", (Throwable)exception);
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   class ServerListUpdateTask implements Runnable {
      private ServerListUpdateTask() {
      }

      public void run() {
         if (RealmsDataFetcher.this.func_225068_o()) {
            this.func_225053_a();
         }

      }

      private void func_225053_a() {
         try {
            RealmsClient realmsclient = RealmsClient.func_224911_a();
            if (realmsclient != null) {
               List<RealmsServer> list = realmsclient.func_224902_e().servers;
               if (list != null) {
                  RealmsDataFetcher.this.func_225082_c(list);
                  RealmsDataFetcher.this.func_225080_b(list);
                  RealmsDataFetcher.this.field_225108_u.put(RealmsDataFetcher.Task.SERVER_LIST, true);
               } else {
                  RealmsDataFetcher.field_225088_a.warn("Realms server list was null or empty");
               }
            }
         } catch (Exception exception) {
            RealmsDataFetcher.this.field_225108_u.put(RealmsDataFetcher.Task.SERVER_LIST, true);
            RealmsDataFetcher.field_225088_a.error("Couldn't get server list", (Throwable)exception);
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Task {
      SERVER_LIST,
      PENDING_INVITE,
      TRIAL_AVAILABLE,
      LIVE_STATS,
      UNREAD_NEWS;
   }

   @OnlyIn(Dist.CLIENT)
   class TrialAvailabilityTask implements Runnable {
      private TrialAvailabilityTask() {
      }

      public void run() {
         if (RealmsDataFetcher.this.func_225068_o()) {
            this.func_225055_a();
         }

      }

      private void func_225055_a() {
         try {
            RealmsClient realmsclient = RealmsClient.func_224911_a();
            if (realmsclient != null) {
               RealmsDataFetcher.this.field_225100_m = realmsclient.func_224914_n();
               RealmsDataFetcher.this.field_225108_u.put(RealmsDataFetcher.Task.TRIAL_AVAILABLE, true);
            }
         } catch (Exception exception) {
            RealmsDataFetcher.field_225088_a.error("Couldn't get trial availability", (Throwable)exception);
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   class UnreadNewsTask implements Runnable {
      private UnreadNewsTask() {
      }

      public void run() {
         if (RealmsDataFetcher.this.func_225068_o()) {
            this.func_225057_a();
         }

      }

      private void func_225057_a() {
         try {
            RealmsClient realmsclient = RealmsClient.func_224911_a();
            if (realmsclient != null) {
               RealmsNews realmsnews = null;

               try {
                  realmsnews = realmsclient.func_224920_m();
               } catch (Exception var5) {
                  ;
               }

               RealmsPersistence.RealmsPersistenceData realmspersistence$realmspersistencedata = RealmsPersistence.func_225188_a();
               if (realmsnews != null) {
                  String s = realmsnews.newsLink;
                  if (s != null && !s.equals(realmspersistence$realmspersistencedata.field_225185_a)) {
                     realmspersistence$realmspersistencedata.field_225186_b = true;
                     realmspersistence$realmspersistencedata.field_225185_a = s;
                     RealmsPersistence.func_225187_a(realmspersistence$realmspersistencedata);
                  }
               }

               RealmsDataFetcher.this.field_225101_n = realmspersistence$realmspersistencedata.field_225186_b;
               RealmsDataFetcher.this.field_225102_o = realmspersistence$realmspersistencedata.field_225185_a;
               RealmsDataFetcher.this.field_225108_u.put(RealmsDataFetcher.Task.UNREAD_NEWS, true);
            }
         } catch (Exception exception) {
            RealmsDataFetcher.field_225088_a.error("Couldn't get unread news", (Throwable)exception);
         }

      }
   }
}