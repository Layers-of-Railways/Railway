package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsTasks;
import com.mojang.realmsclient.util.RealmsUtil;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsBackupScreen extends RealmsScreen {
   private static final Logger field_224114_a = LogManager.getLogger();
   private static int field_224115_b = -1;
   private final RealmsConfigureWorldScreen field_224116_c;
   private List<Backup> field_224117_d = Collections.emptyList();
   private String field_224118_e;
   private RealmsBackupScreen.BackupObjectSelectionList field_224119_f;
   private int field_224120_g = -1;
   private final int field_224121_h;
   private RealmsButton field_224122_i;
   private RealmsButton field_224123_j;
   private RealmsButton field_224124_k;
   private Boolean field_224125_l = false;
   private final RealmsServer field_224126_m;
   private RealmsLabel field_224127_n;

   public RealmsBackupScreen(RealmsConfigureWorldScreen p_i51777_1_, RealmsServer p_i51777_2_, int p_i51777_3_) {
      this.field_224116_c = p_i51777_1_;
      this.field_224126_m = p_i51777_2_;
      this.field_224121_h = p_i51777_3_;
   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.field_224119_f = new RealmsBackupScreen.BackupObjectSelectionList();
      if (field_224115_b != -1) {
         this.field_224119_f.scroll(field_224115_b);
      }

      (new Thread("Realms-fetch-backups") {
         public void run() {
            RealmsClient realmsclient = RealmsClient.func_224911_a();

            try {
               List<Backup> list = realmsclient.func_224923_d(RealmsBackupScreen.this.field_224126_m.id).backups;
               Realms.execute(() -> {
                  RealmsBackupScreen.this.field_224117_d = list;
                  RealmsBackupScreen.this.field_224125_l = RealmsBackupScreen.this.field_224117_d.isEmpty();
                  RealmsBackupScreen.this.field_224119_f.clear();

                  for(Backup backup : RealmsBackupScreen.this.field_224117_d) {
                     RealmsBackupScreen.this.field_224119_f.func_223867_a(backup);
                  }

                  RealmsBackupScreen.this.func_224112_b();
               });
            } catch (RealmsServiceException realmsserviceexception) {
               RealmsBackupScreen.field_224114_a.error("Couldn't request backups", (Throwable)realmsserviceexception);
            }

         }
      }).start();
      this.func_224098_c();
   }

   private void func_224112_b() {
      if (this.field_224117_d.size() > 1) {
         for(int i = 0; i < this.field_224117_d.size() - 1; ++i) {
            Backup backup = this.field_224117_d.get(i);
            Backup backup1 = this.field_224117_d.get(i + 1);
            if (!backup.metadata.isEmpty() && !backup1.metadata.isEmpty()) {
               for(String s : backup.metadata.keySet()) {
                  if (!s.contains("Uploaded") && backup1.metadata.containsKey(s)) {
                     if (!backup.metadata.get(s).equals(backup1.metadata.get(s))) {
                        this.func_224103_a(backup, s);
                     }
                  } else {
                     this.func_224103_a(backup, s);
                  }
               }
            }
         }

      }
   }

   private void func_224103_a(Backup p_224103_1_, String p_224103_2_) {
      if (p_224103_2_.contains("Uploaded")) {
         String s = DateFormat.getDateTimeInstance(3, 3).format(p_224103_1_.lastModifiedDate);
         p_224103_1_.changeList.put(p_224103_2_, s);
         p_224103_1_.setUploadedVersion(true);
      } else {
         p_224103_1_.changeList.put(p_224103_2_, p_224103_1_.metadata.get(p_224103_2_));
      }

   }

   private void func_224098_c() {
      this.buttonsAdd(this.field_224122_i = new RealmsButton(2, this.width() - 135, RealmsConstants.func_225109_a(1), 120, 20, getLocalizedString("mco.backup.button.download")) {
         public void onPress() {
            RealmsBackupScreen.this.func_224088_g();
         }
      });
      this.buttonsAdd(this.field_224123_j = new RealmsButton(3, this.width() - 135, RealmsConstants.func_225109_a(3), 120, 20, getLocalizedString("mco.backup.button.restore")) {
         public void onPress() {
            RealmsBackupScreen.this.func_224104_b(RealmsBackupScreen.this.field_224120_g);
         }
      });
      this.buttonsAdd(this.field_224124_k = new RealmsButton(4, this.width() - 135, RealmsConstants.func_225109_a(5), 120, 20, getLocalizedString("mco.backup.changes.tooltip")) {
         public void onPress() {
            Realms.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, RealmsBackupScreen.this.field_224117_d.get(RealmsBackupScreen.this.field_224120_g)));
            RealmsBackupScreen.this.field_224120_g = -1;
         }
      });
      this.buttonsAdd(new RealmsButton(0, this.width() - 100, this.height() - 35, 85, 20, getLocalizedString("gui.back")) {
         public void onPress() {
            Realms.setScreen(RealmsBackupScreen.this.field_224116_c);
         }
      });
      this.addWidget(this.field_224119_f);
      this.addWidget(this.field_224127_n = new RealmsLabel(getLocalizedString("mco.configure.world.backup"), this.width() / 2, 12, 16777215));
      this.focusOn(this.field_224119_f);
      this.func_224113_d();
      this.narrateLabels();
   }

   private void func_224113_d() {
      this.field_224123_j.setVisible(this.func_224111_f());
      this.field_224124_k.setVisible(this.func_224096_e());
   }

   private boolean func_224096_e() {
      if (this.field_224120_g == -1) {
         return false;
      } else {
         return !(this.field_224117_d.get(this.field_224120_g)).changeList.isEmpty();
      }
   }

   private boolean func_224111_f() {
      if (this.field_224120_g == -1) {
         return false;
      } else {
         return !this.field_224126_m.expired;
      }
   }

   public void tick() {
      super.tick();
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         Realms.setScreen(this.field_224116_c);
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   private void func_224104_b(int p_224104_1_) {
      if (p_224104_1_ >= 0 && p_224104_1_ < this.field_224117_d.size() && !this.field_224126_m.expired) {
         this.field_224120_g = p_224104_1_;
         Date date = (this.field_224117_d.get(p_224104_1_)).lastModifiedDate;
         String s = DateFormat.getDateTimeInstance(3, 3).format(date);
         String s1 = RealmsUtil.func_225192_a(System.currentTimeMillis() - date.getTime());
         String s2 = getLocalizedString("mco.configure.world.restore.question.line1", new Object[]{s, s1});
         String s3 = getLocalizedString("mco.configure.world.restore.question.line2");
         Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Warning, s2, s3, true, 1));
      }

   }

   private void func_224088_g() {
      String s = getLocalizedString("mco.configure.world.restore.download.question.line1");
      String s1 = getLocalizedString("mco.configure.world.restore.download.question.line2");
      Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Info, s, s1, true, 2));
   }

   private void func_224100_h() {
      RealmsTasks.DownloadTask realmstasks$downloadtask = new RealmsTasks.DownloadTask(this.field_224126_m.id, this.field_224121_h, this.field_224126_m.name + " (" + this.field_224126_m.slots.get(this.field_224126_m.activeSlot).getSlotName(this.field_224126_m.activeSlot) + ")", this);
      RealmsLongRunningMcoTaskScreen realmslongrunningmcotaskscreen = new RealmsLongRunningMcoTaskScreen(this.field_224116_c.func_224407_b(), realmstasks$downloadtask);
      realmslongrunningmcotaskscreen.func_224233_a();
      Realms.setScreen(realmslongrunningmcotaskscreen);
   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      if (p_confirmResult_1_ && p_confirmResult_2_ == 1) {
         this.func_224097_i();
      } else if (p_confirmResult_2_ == 1) {
         this.field_224120_g = -1;
         Realms.setScreen(this);
      } else if (p_confirmResult_1_ && p_confirmResult_2_ == 2) {
         this.func_224100_h();
      } else {
         Realms.setScreen(this);
      }

   }

   private void func_224097_i() {
      Backup backup = this.field_224117_d.get(this.field_224120_g);
      this.field_224120_g = -1;
      RealmsTasks.RestoreTask realmstasks$restoretask = new RealmsTasks.RestoreTask(backup, this.field_224126_m.id, this.field_224116_c);
      RealmsLongRunningMcoTaskScreen realmslongrunningmcotaskscreen = new RealmsLongRunningMcoTaskScreen(this.field_224116_c.func_224407_b(), realmstasks$restoretask);
      realmslongrunningmcotaskscreen.func_224233_a();
      Realms.setScreen(realmslongrunningmcotaskscreen);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.field_224118_e = null;
      this.renderBackground();
      this.field_224119_f.render(p_render_1_, p_render_2_, p_render_3_);
      this.field_224127_n.render(this);
      this.drawString(getLocalizedString("mco.configure.world.backup"), (this.width() - 150) / 2 - 90, 20, 10526880);
      if (this.field_224125_l) {
         this.drawString(getLocalizedString("mco.backup.nobackups"), 20, this.height() / 2 - 10, 16777215);
      }

      this.field_224122_i.active(!this.field_224125_l);
      super.render(p_render_1_, p_render_2_, p_render_3_);
      if (this.field_224118_e != null) {
         this.func_224090_a(this.field_224118_e, p_render_1_, p_render_2_);
      }

   }

   protected void func_224090_a(String p_224090_1_, int p_224090_2_, int p_224090_3_) {
      if (p_224090_1_ != null) {
         int i = p_224090_2_ + 12;
         int j = p_224090_3_ - 12;
         int k = this.fontWidth(p_224090_1_);
         this.fillGradient(i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
         this.fontDrawShadow(p_224090_1_, i, j, 16777215);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class BackupObjectSelectionList extends RealmsObjectSelectionList {
      public BackupObjectSelectionList() {
         super(RealmsBackupScreen.this.width() - 150, RealmsBackupScreen.this.height(), 32, RealmsBackupScreen.this.height() - 15, 36);
      }

      public void func_223867_a(Backup p_223867_1_) {
         this.addEntry(RealmsBackupScreen.this.new BackupObjectSelectionListEntry(p_223867_1_));
      }

      public int getRowWidth() {
         return (int)((double)this.width() * 0.93D);
      }

      public boolean isFocused() {
         return RealmsBackupScreen.this.isFocused(this);
      }

      public int getItemCount() {
         return RealmsBackupScreen.this.field_224117_d.size();
      }

      public int getMaxPosition() {
         return this.getItemCount() * 36;
      }

      public void renderBackground() {
         RealmsBackupScreen.this.renderBackground();
      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         if (p_mouseClicked_5_ != 0) {
            return false;
         } else if (p_mouseClicked_1_ < (double)this.getScrollbarPosition() && p_mouseClicked_3_ >= (double)this.y0() && p_mouseClicked_3_ <= (double)this.y1()) {
            int i = this.width() / 2 - 92;
            int j = this.width();
            int k = (int)Math.floor(p_mouseClicked_3_ - (double)this.y0()) - this.headerHeight() + this.getScroll();
            int l = k / this.itemHeight();
            if (p_mouseClicked_1_ >= (double)i && p_mouseClicked_1_ <= (double)j && l >= 0 && k >= 0 && l < this.getItemCount()) {
               this.selectItem(l);
               this.itemClicked(k, l, p_mouseClicked_1_, p_mouseClicked_3_, this.width());
            }

            return true;
         } else {
            return false;
         }
      }

      public int getScrollbarPosition() {
         return this.width() - 5;
      }

      public void itemClicked(int p_itemClicked_1_, int p_itemClicked_2_, double p_itemClicked_3_, double p_itemClicked_5_, int p_itemClicked_7_) {
         int i = this.width() - 35;
         int j = p_itemClicked_2_ * this.itemHeight() + 36 - this.getScroll();
         int k = i + 10;
         int l = j - 3;
         if (p_itemClicked_3_ >= (double)i && p_itemClicked_3_ <= (double)(i + 9) && p_itemClicked_5_ >= (double)j && p_itemClicked_5_ <= (double)(j + 9)) {
            if (!(RealmsBackupScreen.this.field_224117_d.get(p_itemClicked_2_)).changeList.isEmpty()) {
               RealmsBackupScreen.this.field_224120_g = -1;
               RealmsBackupScreen.field_224115_b = this.getScroll();
               Realms.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, RealmsBackupScreen.this.field_224117_d.get(p_itemClicked_2_)));
            }
         } else if (p_itemClicked_3_ >= (double)k && p_itemClicked_3_ < (double)(k + 13) && p_itemClicked_5_ >= (double)l && p_itemClicked_5_ < (double)(l + 15)) {
            RealmsBackupScreen.field_224115_b = this.getScroll();
            RealmsBackupScreen.this.func_224104_b(p_itemClicked_2_);
         }

      }

      public void selectItem(int p_selectItem_1_) {
         this.setSelected(p_selectItem_1_);
         if (p_selectItem_1_ != -1) {
            Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", (RealmsBackupScreen.this.field_224117_d.get(p_selectItem_1_)).lastModifiedDate.toString()));
         }

         this.func_223866_a(p_selectItem_1_);
      }

      public void func_223866_a(int p_223866_1_) {
         RealmsBackupScreen.this.field_224120_g = p_223866_1_;
         RealmsBackupScreen.this.func_224113_d();
      }
   }

   @OnlyIn(Dist.CLIENT)
   class BackupObjectSelectionListEntry extends RealmListEntry {
      final Backup field_223742_a;

      public BackupObjectSelectionListEntry(Backup p_i51657_2_) {
         this.field_223742_a = p_i51657_2_;
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         this.func_223740_a(this.field_223742_a, p_render_3_ - 40, p_render_2_, p_render_6_, p_render_7_);
      }

      private void func_223740_a(Backup p_223740_1_, int p_223740_2_, int p_223740_3_, int p_223740_4_, int p_223740_5_) {
         int i = p_223740_1_.isUploadedVersion() ? -8388737 : 16777215;
         RealmsBackupScreen.this.drawString("Backup (" + RealmsUtil.func_225192_a(System.currentTimeMillis() - p_223740_1_.lastModifiedDate.getTime()) + ")", p_223740_2_ + 40, p_223740_3_ + 1, i);
         RealmsBackupScreen.this.drawString(this.func_223738_a(p_223740_1_.lastModifiedDate), p_223740_2_ + 40, p_223740_3_ + 12, 8421504);
         int j = RealmsBackupScreen.this.width() - 175;
         int k = -3;
         int l = j - 10;
         int i1 = 0;
         if (!RealmsBackupScreen.this.field_224126_m.expired) {
            this.func_223739_a(j, p_223740_3_ + -3, p_223740_4_, p_223740_5_);
         }

         if (!p_223740_1_.changeList.isEmpty()) {
            this.func_223741_b(l, p_223740_3_ + 0, p_223740_4_, p_223740_5_);
         }

      }

      private String func_223738_a(Date p_223738_1_) {
         return DateFormat.getDateTimeInstance(3, 3).format(p_223738_1_);
      }

      private void func_223739_a(int p_223739_1_, int p_223739_2_, int p_223739_3_, int p_223739_4_) {
         boolean flag = p_223739_3_ >= p_223739_1_ && p_223739_3_ <= p_223739_1_ + 12 && p_223739_4_ >= p_223739_2_ && p_223739_4_ <= p_223739_2_ + 14 && p_223739_4_ < RealmsBackupScreen.this.height() - 15 && p_223739_4_ > 32;
         RealmsScreen.bind("realms:textures/gui/realms/restore_icon.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RenderSystem.scalef(0.5F, 0.5F, 0.5F);
         RealmsScreen.blit(p_223739_1_ * 2, p_223739_2_ * 2, 0.0F, flag ? 28.0F : 0.0F, 23, 28, 23, 56);
         RenderSystem.popMatrix();
         if (flag) {
            RealmsBackupScreen.this.field_224118_e = RealmsScreen.getLocalizedString("mco.backup.button.restore");
         }

      }

      private void func_223741_b(int p_223741_1_, int p_223741_2_, int p_223741_3_, int p_223741_4_) {
         boolean flag = p_223741_3_ >= p_223741_1_ && p_223741_3_ <= p_223741_1_ + 8 && p_223741_4_ >= p_223741_2_ && p_223741_4_ <= p_223741_2_ + 8 && p_223741_4_ < RealmsBackupScreen.this.height() - 15 && p_223741_4_ > 32;
         RealmsScreen.bind("realms:textures/gui/realms/plus_icon.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RenderSystem.scalef(0.5F, 0.5F, 0.5F);
         RealmsScreen.blit(p_223741_1_ * 2, p_223741_2_ * 2, 0.0F, flag ? 15.0F : 0.0F, 15, 15, 15, 30);
         RenderSystem.popMatrix();
         if (flag) {
            RealmsBackupScreen.this.field_224118_e = RealmsScreen.getLocalizedString("mco.backup.changes.tooltip");
         }

      }
   }
}