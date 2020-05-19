package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PendingInvite;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.ListButton;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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
public class RealmsPendingInvitesScreen extends RealmsScreen {
   private static final Logger field_224333_a = LogManager.getLogger();
   private final RealmsScreen field_224334_b;
   private String field_224335_c;
   private boolean field_224336_d;
   private RealmsPendingInvitesScreen.InvitationList field_224337_e;
   private RealmsLabel field_224338_f;
   private int field_224339_g = -1;
   private RealmsButton field_224340_h;
   private RealmsButton field_224341_i;

   public RealmsPendingInvitesScreen(RealmsScreen p_i51761_1_) {
      this.field_224334_b = p_i51761_1_;
   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.field_224337_e = new RealmsPendingInvitesScreen.InvitationList();
      (new Thread("Realms-pending-invitations-fetcher") {
         public void run() {
            RealmsClient realmsclient = RealmsClient.func_224911_a();

            try {
               List<PendingInvite> list = realmsclient.func_224919_k().pendingInvites;
               List<RealmsPendingInvitesScreen.InvitationEntry> list1 = list.stream().map((p_225146_1_) -> {
                  return RealmsPendingInvitesScreen.this.new InvitationEntry(p_225146_1_);
               }).collect(Collectors.toList());
               Realms.execute(() -> {
                  RealmsPendingInvitesScreen.this.field_224337_e.replaceEntries(list1);
               });
            } catch (RealmsServiceException var7) {
               RealmsPendingInvitesScreen.field_224333_a.error("Couldn't list invites");
            } finally {
               RealmsPendingInvitesScreen.this.field_224336_d = true;
            }

         }
      }).start();
      this.buttonsAdd(this.field_224340_h = new RealmsButton(1, this.width() / 2 - 174, this.height() - 32, 100, 20, getLocalizedString("mco.invites.button.accept")) {
         public void onPress() {
            RealmsPendingInvitesScreen.this.func_224329_c(RealmsPendingInvitesScreen.this.field_224339_g);
            RealmsPendingInvitesScreen.this.field_224339_g = -1;
            RealmsPendingInvitesScreen.this.func_224331_b();
         }
      });
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 50, this.height() - 32, 100, 20, getLocalizedString("gui.done")) {
         public void onPress() {
            Realms.setScreen(new RealmsMainScreen(RealmsPendingInvitesScreen.this.field_224334_b));
         }
      });
      this.buttonsAdd(this.field_224341_i = new RealmsButton(2, this.width() / 2 + 74, this.height() - 32, 100, 20, getLocalizedString("mco.invites.button.reject")) {
         public void onPress() {
            RealmsPendingInvitesScreen.this.func_224321_b(RealmsPendingInvitesScreen.this.field_224339_g);
            RealmsPendingInvitesScreen.this.field_224339_g = -1;
            RealmsPendingInvitesScreen.this.func_224331_b();
         }
      });
      this.field_224338_f = new RealmsLabel(getLocalizedString("mco.invites.title"), this.width() / 2, 12, 16777215);
      this.addWidget(this.field_224338_f);
      this.addWidget(this.field_224337_e);
      this.narrateLabels();
      this.func_224331_b();
   }

   public void tick() {
      super.tick();
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         Realms.setScreen(new RealmsMainScreen(this.field_224334_b));
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   private void func_224318_a(int p_224318_1_) {
      this.field_224337_e.func_223872_a(p_224318_1_);
   }

   private void func_224321_b(final int p_224321_1_) {
      if (p_224321_1_ < this.field_224337_e.getItemCount()) {
         (new Thread("Realms-reject-invitation") {
            public void run() {
               try {
                  RealmsClient realmsclient = RealmsClient.func_224911_a();
                  realmsclient.func_224913_b((RealmsPendingInvitesScreen.this.field_224337_e.children().get(p_224321_1_)).field_223750_a.invitationId);
                  Realms.execute(() -> {
                     RealmsPendingInvitesScreen.this.func_224318_a(p_224321_1_);
                  });
               } catch (RealmsServiceException var2) {
                  RealmsPendingInvitesScreen.field_224333_a.error("Couldn't reject invite");
               }

            }
         }).start();
      }

   }

   private void func_224329_c(final int p_224329_1_) {
      if (p_224329_1_ < this.field_224337_e.getItemCount()) {
         (new Thread("Realms-accept-invitation") {
            public void run() {
               try {
                  RealmsClient realmsclient = RealmsClient.func_224911_a();
                  realmsclient.func_224901_a((RealmsPendingInvitesScreen.this.field_224337_e.children().get(p_224329_1_)).field_223750_a.invitationId);
                  Realms.execute(() -> {
                     RealmsPendingInvitesScreen.this.func_224318_a(p_224329_1_);
                  });
               } catch (RealmsServiceException var2) {
                  RealmsPendingInvitesScreen.field_224333_a.error("Couldn't accept invite");
               }

            }
         }).start();
      }

   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.field_224335_c = null;
      this.renderBackground();
      this.field_224337_e.render(p_render_1_, p_render_2_, p_render_3_);
      this.field_224338_f.render(this);
      if (this.field_224335_c != null) {
         this.func_224322_a(this.field_224335_c, p_render_1_, p_render_2_);
      }

      if (this.field_224337_e.getItemCount() == 0 && this.field_224336_d) {
         this.drawCenteredString(getLocalizedString("mco.invites.nopending"), this.width() / 2, this.height() / 2 - 20, 16777215);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   protected void func_224322_a(String p_224322_1_, int p_224322_2_, int p_224322_3_) {
      if (p_224322_1_ != null) {
         int i = p_224322_2_ + 12;
         int j = p_224322_3_ - 12;
         int k = this.fontWidth(p_224322_1_);
         this.fillGradient(i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
         this.fontDrawShadow(p_224322_1_, i, j, 16777215);
      }
   }

   private void func_224331_b() {
      this.field_224340_h.setVisible(this.func_224316_d(this.field_224339_g));
      this.field_224341_i.setVisible(this.func_224316_d(this.field_224339_g));
   }

   private boolean func_224316_d(int p_224316_1_) {
      return p_224316_1_ != -1;
   }

   public static String func_224330_a(PendingInvite p_224330_0_) {
      return RealmsUtil.func_225192_a(System.currentTimeMillis() - p_224330_0_.date.getTime());
   }

   @OnlyIn(Dist.CLIENT)
   class InvitationEntry extends RealmListEntry {
      final PendingInvite field_223750_a;
      private final List<ListButton> field_223752_c;

      InvitationEntry(PendingInvite p_i51623_2_) {
         this.field_223750_a = p_i51623_2_;
         this.field_223752_c = Arrays.asList(new RealmsPendingInvitesScreen.InvitationEntry.AcceptButton(), new RealmsPendingInvitesScreen.InvitationEntry.RejectButton());
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         this.func_223749_a(this.field_223750_a, p_render_3_, p_render_2_, p_render_6_, p_render_7_);
      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         ListButton.func_225119_a(RealmsPendingInvitesScreen.this.field_224337_e, this, this.field_223752_c, p_mouseClicked_5_, p_mouseClicked_1_, p_mouseClicked_3_);
         return true;
      }

      private void func_223749_a(PendingInvite p_223749_1_, int p_223749_2_, int p_223749_3_, int p_223749_4_, int p_223749_5_) {
         RealmsPendingInvitesScreen.this.drawString(p_223749_1_.worldName, p_223749_2_ + 38, p_223749_3_ + 1, 16777215);
         RealmsPendingInvitesScreen.this.drawString(p_223749_1_.worldOwnerName, p_223749_2_ + 38, p_223749_3_ + 12, 8421504);
         RealmsPendingInvitesScreen.this.drawString(RealmsPendingInvitesScreen.func_224330_a(p_223749_1_), p_223749_2_ + 38, p_223749_3_ + 24, 8421504);
         ListButton.func_225124_a(this.field_223752_c, RealmsPendingInvitesScreen.this.field_224337_e, p_223749_2_, p_223749_3_, p_223749_4_, p_223749_5_);
         RealmsTextureManager.func_225205_a(p_223749_1_.worldOwnerUuid, () -> {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RealmsScreen.blit(p_223749_2_, p_223749_3_, 8.0F, 8.0F, 8, 8, 32, 32, 64, 64);
            RealmsScreen.blit(p_223749_2_, p_223749_3_, 40.0F, 8.0F, 8, 8, 32, 32, 64, 64);
         });
      }

      @OnlyIn(Dist.CLIENT)
      class AcceptButton extends ListButton {
         AcceptButton() {
            super(15, 15, 215, 5);
         }

         protected void func_225120_a(int p_225120_1_, int p_225120_2_, boolean p_225120_3_) {
            RealmsScreen.bind("realms:textures/gui/realms/accept_icon.png");
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.pushMatrix();
            RealmsScreen.blit(p_225120_1_, p_225120_2_, p_225120_3_ ? 19.0F : 0.0F, 0.0F, 18, 18, 37, 18);
            RenderSystem.popMatrix();
            if (p_225120_3_) {
               RealmsPendingInvitesScreen.this.field_224335_c = RealmsScreen.getLocalizedString("mco.invites.button.accept");
            }

         }

         public void func_225121_a(int p_225121_1_) {
            RealmsPendingInvitesScreen.this.func_224329_c(p_225121_1_);
         }
      }

      @OnlyIn(Dist.CLIENT)
      class RejectButton extends ListButton {
         RejectButton() {
            super(15, 15, 235, 5);
         }

         protected void func_225120_a(int p_225120_1_, int p_225120_2_, boolean p_225120_3_) {
            RealmsScreen.bind("realms:textures/gui/realms/reject_icon.png");
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.pushMatrix();
            RealmsScreen.blit(p_225120_1_, p_225120_2_, p_225120_3_ ? 19.0F : 0.0F, 0.0F, 18, 18, 37, 18);
            RenderSystem.popMatrix();
            if (p_225120_3_) {
               RealmsPendingInvitesScreen.this.field_224335_c = RealmsScreen.getLocalizedString("mco.invites.button.reject");
            }

         }

         public void func_225121_a(int p_225121_1_) {
            RealmsPendingInvitesScreen.this.func_224321_b(p_225121_1_);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   class InvitationList extends RealmsObjectSelectionList<RealmsPendingInvitesScreen.InvitationEntry> {
      public InvitationList() {
         super(RealmsPendingInvitesScreen.this.width(), RealmsPendingInvitesScreen.this.height(), 32, RealmsPendingInvitesScreen.this.height() - 40, 36);
      }

      public void func_223872_a(int p_223872_1_) {
         this.remove(p_223872_1_);
      }

      public int getMaxPosition() {
         return this.getItemCount() * 36;
      }

      public int getRowWidth() {
         return 260;
      }

      public boolean isFocused() {
         return RealmsPendingInvitesScreen.this.isFocused(this);
      }

      public void renderBackground() {
         RealmsPendingInvitesScreen.this.renderBackground();
      }

      public void selectItem(int p_selectItem_1_) {
         this.setSelected(p_selectItem_1_);
         if (p_selectItem_1_ != -1) {
            List<RealmsPendingInvitesScreen.InvitationEntry> list = RealmsPendingInvitesScreen.this.field_224337_e.children();
            PendingInvite pendinginvite = (list.get(p_selectItem_1_)).field_223750_a;
            String s = RealmsScreen.getLocalizedString("narrator.select.list.position", p_selectItem_1_ + 1, list.size());
            String s1 = Realms.joinNarrations(Arrays.asList(pendinginvite.worldName, pendinginvite.worldOwnerName, RealmsPendingInvitesScreen.func_224330_a(pendinginvite), s));
            Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", s1));
         }

         this.func_223873_b(p_selectItem_1_);
      }

      public void func_223873_b(int p_223873_1_) {
         RealmsPendingInvitesScreen.this.field_224339_g = p_223873_1_;
         RealmsPendingInvitesScreen.this.func_224331_b();
      }
   }
}