package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.dto.Backup;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsSimpleScrolledSelectionList;
import net.minecraft.realms.Tezzelator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsBackupInfoScreen extends RealmsScreen {
   private final RealmsScreen field_224047_c;
   private final int field_224048_d = 0;
   private final Backup field_224049_e;
   private final List<String> field_224050_f = Lists.newArrayList();
   private RealmsBackupInfoScreen.BackupInfoList field_224051_g;
   String[] field_224045_a = new String[]{getLocalizedString("options.difficulty.peaceful"), getLocalizedString("options.difficulty.easy"), getLocalizedString("options.difficulty.normal"), getLocalizedString("options.difficulty.hard")};
   String[] field_224046_b = new String[]{getLocalizedString("selectWorld.gameMode.survival"), getLocalizedString("selectWorld.gameMode.creative"), getLocalizedString("selectWorld.gameMode.adventure")};

   public RealmsBackupInfoScreen(RealmsScreen p_i51778_1_, Backup p_i51778_2_) {
      this.field_224047_c = p_i51778_1_;
      this.field_224049_e = p_i51778_2_;
      if (p_i51778_2_.changeList != null) {
         for(Entry<String, String> entry : p_i51778_2_.changeList.entrySet()) {
            this.field_224050_f.add(entry.getKey());
         }
      }

   }

   public void tick() {
   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 100, this.height() / 4 + 120 + 24, getLocalizedString("gui.back")) {
         public void onPress() {
            Realms.setScreen(RealmsBackupInfoScreen.this.field_224047_c);
         }
      });
      this.field_224051_g = new RealmsBackupInfoScreen.BackupInfoList();
      this.addWidget(this.field_224051_g);
      this.focusOn(this.field_224051_g);
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         Realms.setScreen(this.field_224047_c);
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString("Changes from last backup", this.width() / 2, 10, 16777215);
      this.field_224051_g.render(p_render_1_, p_render_2_, p_render_3_);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   private String func_224039_a(String p_224039_1_, String p_224039_2_) {
      String s = p_224039_1_.toLowerCase(Locale.ROOT);
      if (s.contains("game") && s.contains("mode")) {
         return this.func_224043_b(p_224039_2_);
      } else {
         return s.contains("game") && s.contains("difficulty") ? this.func_224042_a(p_224039_2_) : p_224039_2_;
      }
   }

   private String func_224042_a(String p_224042_1_) {
      try {
         return this.field_224045_a[Integer.parseInt(p_224042_1_)];
      } catch (Exception var3) {
         return "UNKNOWN";
      }
   }

   private String func_224043_b(String p_224043_1_) {
      try {
         return this.field_224046_b[Integer.parseInt(p_224043_1_)];
      } catch (Exception var3) {
         return "UNKNOWN";
      }
   }

   @OnlyIn(Dist.CLIENT)
   class BackupInfoList extends RealmsSimpleScrolledSelectionList {
      public BackupInfoList() {
         super(RealmsBackupInfoScreen.this.width(), RealmsBackupInfoScreen.this.height(), 32, RealmsBackupInfoScreen.this.height() - 64, 36);
      }

      public int getItemCount() {
         return RealmsBackupInfoScreen.this.field_224049_e.changeList.size();
      }

      public boolean isSelectedItem(int p_isSelectedItem_1_) {
         return false;
      }

      public int getMaxPosition() {
         return this.getItemCount() * 36;
      }

      public void renderBackground() {
      }

      public void renderItem(int p_renderItem_1_, int p_renderItem_2_, int p_renderItem_3_, int p_renderItem_4_, Tezzelator p_renderItem_5_, int p_renderItem_6_, int p_renderItem_7_) {
         String s = RealmsBackupInfoScreen.this.field_224050_f.get(p_renderItem_1_);
         RealmsBackupInfoScreen.this.drawString(s, this.width() / 2 - 40, p_renderItem_3_, 10526880);
         String s1 = RealmsBackupInfoScreen.this.field_224049_e.changeList.get(s);
         RealmsBackupInfoScreen.this.drawString(RealmsBackupInfoScreen.this.func_224039_a(s, s1), this.width() / 2 - 40, p_renderItem_3_ + 12, 16777215);
      }
   }
}