package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.util.RealmsTasks;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsResourcePackScreen extends RealmsScreen {
   private static final Logger field_224479_a = LogManager.getLogger();
   private final RealmsScreen field_224480_b;
   private final RealmsServerAddress field_224481_c;
   private final ReentrantLock field_224482_d;

   public RealmsResourcePackScreen(RealmsScreen p_i51755_1_, RealmsServerAddress p_i51755_2_, ReentrantLock p_i51755_3_) {
      this.field_224480_b = p_i51755_1_;
      this.field_224481_c = p_i51755_2_;
      this.field_224482_d = p_i51755_3_;
   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      try {
         if (!p_confirmResult_1_) {
            Realms.setScreen(this.field_224480_b);
         } else {
            try {
               Realms.downloadResourcePack(this.field_224481_c.resourcePackUrl, this.field_224481_c.resourcePackHash).thenRun(() -> {
                  RealmsLongRunningMcoTaskScreen realmslongrunningmcotaskscreen = new RealmsLongRunningMcoTaskScreen(this.field_224480_b, new RealmsTasks.RealmsConnectTask(this.field_224480_b, this.field_224481_c));
                  realmslongrunningmcotaskscreen.func_224233_a();
                  Realms.setScreen(realmslongrunningmcotaskscreen);
               }).exceptionally((p_224477_1_) -> {
                  Realms.clearResourcePack();
                  field_224479_a.error(p_224477_1_);
                  Realms.setScreen(new RealmsGenericErrorScreen("Failed to download resource pack!", this.field_224480_b));
                  return null;
               });
            } catch (Exception exception) {
               Realms.clearResourcePack();
               field_224479_a.error(exception);
               Realms.setScreen(new RealmsGenericErrorScreen("Failed to download resource pack!", this.field_224480_b));
            }
         }
      } finally {
         if (this.field_224482_d != null && this.field_224482_d.isHeldByCurrentThread()) {
            this.field_224482_d.unlock();
         }

      }

   }
}