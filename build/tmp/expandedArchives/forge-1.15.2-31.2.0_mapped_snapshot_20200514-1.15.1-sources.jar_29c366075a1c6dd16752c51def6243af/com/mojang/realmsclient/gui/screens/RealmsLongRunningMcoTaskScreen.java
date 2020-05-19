package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsLongRunningMcoTaskScreen extends RealmsScreen {
   private static final Logger field_224238_b = LogManager.getLogger();
   private final int field_224239_c = 666;
   private final int field_224240_d = 667;
   private final RealmsScreen field_224241_e;
   private final LongRunningTask field_224242_f;
   private volatile String field_224243_g = "";
   private volatile boolean field_224244_h;
   private volatile String field_224245_i;
   private volatile boolean field_224246_j;
   private int field_224247_k;
   private final LongRunningTask field_224248_l;
   private final int field_224249_m = 212;
   public static final String[] field_224237_a = new String[]{"\u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583", "_ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584", "_ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585", "_ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586", "_ _ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587", "_ _ _ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588", "_ _ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587", "_ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586", "_ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585", "_ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584", "\u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583", "\u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _", "\u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _", "\u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _", "\u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _ _", "\u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _ _ _", "\u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _ _", "\u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _", "\u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _", "\u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _"};

   public RealmsLongRunningMcoTaskScreen(RealmsScreen p_i51764_1_, LongRunningTask p_i51764_2_) {
      this.field_224241_e = p_i51764_1_;
      this.field_224248_l = p_i51764_2_;
      p_i51764_2_.func_224987_a(this);
      this.field_224242_f = p_i51764_2_;
   }

   public void func_224233_a() {
      Thread thread = new Thread(this.field_224242_f, "Realms-long-running-task");
      thread.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(field_224238_b));
      thread.start();
   }

   public void tick() {
      super.tick();
      Realms.narrateRepeatedly(this.field_224243_g);
      ++this.field_224247_k;
      this.field_224248_l.func_224990_b();
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         this.func_224236_c();
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public void init() {
      this.field_224248_l.func_224991_c();
      this.buttonsAdd(new RealmsButton(666, this.width() / 2 - 106, RealmsConstants.func_225109_a(12), 212, 20, getLocalizedString("gui.cancel")) {
         public void onPress() {
            RealmsLongRunningMcoTaskScreen.this.func_224236_c();
         }
      });
   }

   private void func_224236_c() {
      this.field_224246_j = true;
      this.field_224248_l.func_224992_d();
      Realms.setScreen(this.field_224241_e);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.field_224243_g, this.width() / 2, RealmsConstants.func_225109_a(3), 16777215);
      if (!this.field_224244_h) {
         this.drawCenteredString(field_224237_a[this.field_224247_k % field_224237_a.length], this.width() / 2, RealmsConstants.func_225109_a(8), 8421504);
      }

      if (this.field_224244_h) {
         this.drawCenteredString(this.field_224245_i, this.width() / 2, RealmsConstants.func_225109_a(8), 16711680);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public void func_224231_a(String p_224231_1_) {
      this.field_224244_h = true;
      this.field_224245_i = p_224231_1_;
      Realms.narrateNow(p_224231_1_);
      this.buttonsClear();
      this.buttonsAdd(new RealmsButton(667, this.width() / 2 - 106, this.height() / 4 + 120 + 12, getLocalizedString("gui.back")) {
         public void onPress() {
            RealmsLongRunningMcoTaskScreen.this.func_224236_c();
         }
      });
   }

   public void func_224234_b(String p_224234_1_) {
      this.field_224243_g = p_224234_1_;
   }

   public boolean func_224235_b() {
      return this.field_224246_j;
   }
}