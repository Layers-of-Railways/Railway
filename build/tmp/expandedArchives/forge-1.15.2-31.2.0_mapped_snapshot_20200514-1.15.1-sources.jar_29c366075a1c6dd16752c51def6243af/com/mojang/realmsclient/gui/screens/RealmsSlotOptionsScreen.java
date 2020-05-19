package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsSliderButton;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsSlotOptionsScreen extends RealmsScreen {
   private RealmsEditBox field_224642_e;
   protected final RealmsConfigureWorldScreen field_224638_a;
   private int field_224643_f;
   private int field_224644_g;
   private int field_224645_h;
   private final RealmsWorldOptions field_224646_i;
   private final RealmsServer.ServerType field_224647_j;
   private final int field_224648_k;
   private int field_224649_l;
   private int field_224650_m;
   private Boolean field_224651_n;
   private Boolean field_224652_o;
   private Boolean field_224653_p;
   private Boolean field_224654_q;
   private Integer field_224655_r;
   private Boolean field_224656_s;
   private Boolean field_224657_t;
   private RealmsButton field_224658_u;
   private RealmsButton field_224659_v;
   private RealmsButton field_224660_w;
   private RealmsButton field_224661_x;
   private RealmsSliderButton field_224662_y;
   private RealmsButton field_224663_z;
   private RealmsButton field_224635_A;
   String[] field_224639_b;
   String[] field_224640_c;
   String[][] field_224641_d;
   private RealmsLabel field_224636_B;
   private RealmsLabel field_224637_C;

   public RealmsSlotOptionsScreen(RealmsConfigureWorldScreen p_i51750_1_, RealmsWorldOptions p_i51750_2_, RealmsServer.ServerType p_i51750_3_, int p_i51750_4_) {
      this.field_224638_a = p_i51750_1_;
      this.field_224646_i = p_i51750_2_;
      this.field_224647_j = p_i51750_3_;
      this.field_224648_k = p_i51750_4_;
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   public void tick() {
      this.field_224642_e.tick();
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      switch(p_keyPressed_1_) {
      case 256:
         Realms.setScreen(this.field_224638_a);
         return true;
      default:
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public void init() {
      this.field_224644_g = 170;
      this.field_224643_f = this.width() / 2 - this.field_224644_g * 2 / 2;
      this.field_224645_h = this.width() / 2 + 10;
      this.func_224609_a();
      this.field_224649_l = this.field_224646_i.difficulty;
      this.field_224650_m = this.field_224646_i.gameMode;
      if (this.field_224647_j.equals(RealmsServer.ServerType.NORMAL)) {
         this.field_224651_n = this.field_224646_i.pvp;
         this.field_224655_r = this.field_224646_i.spawnProtection;
         this.field_224657_t = this.field_224646_i.forceGameMode;
         this.field_224653_p = this.field_224646_i.spawnAnimals;
         this.field_224654_q = this.field_224646_i.spawnMonsters;
         this.field_224652_o = this.field_224646_i.spawnNPCs;
         this.field_224656_s = this.field_224646_i.commandBlocks;
      } else {
         String s;
         if (this.field_224647_j.equals(RealmsServer.ServerType.ADVENTUREMAP)) {
            s = getLocalizedString("mco.configure.world.edit.subscreen.adventuremap");
         } else if (this.field_224647_j.equals(RealmsServer.ServerType.INSPIRATION)) {
            s = getLocalizedString("mco.configure.world.edit.subscreen.inspiration");
         } else {
            s = getLocalizedString("mco.configure.world.edit.subscreen.experience");
         }

         this.field_224637_C = new RealmsLabel(s, this.width() / 2, 26, 16711680);
         this.field_224651_n = true;
         this.field_224655_r = 0;
         this.field_224657_t = false;
         this.field_224653_p = true;
         this.field_224654_q = true;
         this.field_224652_o = true;
         this.field_224656_s = true;
      }

      this.field_224642_e = this.newEditBox(11, this.field_224643_f + 2, RealmsConstants.func_225109_a(1), this.field_224644_g - 4, 20, getLocalizedString("mco.configure.world.edit.slot.name"));
      this.field_224642_e.setMaxLength(10);
      this.field_224642_e.setValue(this.field_224646_i.getSlotName(this.field_224648_k));
      this.focusOn(this.field_224642_e);
      this.buttonsAdd(this.field_224658_u = new RealmsButton(4, this.field_224645_h, RealmsConstants.func_225109_a(1), this.field_224644_g, 20, this.func_224618_d()) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.field_224651_n = !RealmsSlotOptionsScreen.this.field_224651_n;
            this.setMessage(RealmsSlotOptionsScreen.this.func_224618_d());
         }
      });
      this.buttonsAdd(new RealmsButton(3, this.field_224643_f, RealmsConstants.func_225109_a(3), this.field_224644_g, 20, this.func_224610_c()) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.field_224650_m = (RealmsSlotOptionsScreen.this.field_224650_m + 1) % RealmsSlotOptionsScreen.this.field_224640_c.length;
            this.setMessage(RealmsSlotOptionsScreen.this.func_224610_c());
         }
      });
      this.buttonsAdd(this.field_224659_v = new RealmsButton(5, this.field_224645_h, RealmsConstants.func_225109_a(3), this.field_224644_g, 20, this.func_224606_e()) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.field_224653_p = !RealmsSlotOptionsScreen.this.field_224653_p;
            this.setMessage(RealmsSlotOptionsScreen.this.func_224606_e());
         }
      });
      this.buttonsAdd(new RealmsButton(2, this.field_224643_f, RealmsConstants.func_225109_a(5), this.field_224644_g, 20, this.func_224625_b()) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.field_224649_l = (RealmsSlotOptionsScreen.this.field_224649_l + 1) % RealmsSlotOptionsScreen.this.field_224639_b.length;
            this.setMessage(RealmsSlotOptionsScreen.this.func_224625_b());
            if (RealmsSlotOptionsScreen.this.field_224647_j.equals(RealmsServer.ServerType.NORMAL)) {
               RealmsSlotOptionsScreen.this.field_224660_w.active(RealmsSlotOptionsScreen.this.field_224649_l != 0);
               RealmsSlotOptionsScreen.this.field_224660_w.setMessage(RealmsSlotOptionsScreen.this.func_224626_f());
            }

         }
      });
      this.buttonsAdd(this.field_224660_w = new RealmsButton(6, this.field_224645_h, RealmsConstants.func_225109_a(5), this.field_224644_g, 20, this.func_224626_f()) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.field_224654_q = !RealmsSlotOptionsScreen.this.field_224654_q;
            this.setMessage(RealmsSlotOptionsScreen.this.func_224626_f());
         }
      });
      this.buttonsAdd(this.field_224662_y = new RealmsSlotOptionsScreen.SettingsSlider(8, this.field_224643_f, RealmsConstants.func_225109_a(7), this.field_224644_g, this.field_224655_r, 0.0F, 16.0F));
      this.buttonsAdd(this.field_224661_x = new RealmsButton(7, this.field_224645_h, RealmsConstants.func_225109_a(7), this.field_224644_g, 20, this.func_224621_g()) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.field_224652_o = !RealmsSlotOptionsScreen.this.field_224652_o;
            this.setMessage(RealmsSlotOptionsScreen.this.func_224621_g());
         }
      });
      this.buttonsAdd(this.field_224635_A = new RealmsButton(10, this.field_224643_f, RealmsConstants.func_225109_a(9), this.field_224644_g, 20, this.func_224634_i()) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.field_224657_t = !RealmsSlotOptionsScreen.this.field_224657_t;
            this.setMessage(RealmsSlotOptionsScreen.this.func_224634_i());
         }
      });
      this.buttonsAdd(this.field_224663_z = new RealmsButton(9, this.field_224645_h, RealmsConstants.func_225109_a(9), this.field_224644_g, 20, this.func_224594_h()) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.field_224656_s = !RealmsSlotOptionsScreen.this.field_224656_s;
            this.setMessage(RealmsSlotOptionsScreen.this.func_224594_h());
         }
      });
      if (!this.field_224647_j.equals(RealmsServer.ServerType.NORMAL)) {
         this.field_224658_u.active(false);
         this.field_224659_v.active(false);
         this.field_224661_x.active(false);
         this.field_224660_w.active(false);
         this.field_224662_y.active(false);
         this.field_224663_z.active(false);
         this.field_224662_y.active(false);
         this.field_224635_A.active(false);
      }

      if (this.field_224649_l == 0) {
         this.field_224660_w.active(false);
      }

      this.buttonsAdd(new RealmsButton(1, this.field_224643_f, RealmsConstants.func_225109_a(13), this.field_224644_g, 20, getLocalizedString("mco.configure.world.buttons.done")) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.func_224613_k();
         }
      });
      this.buttonsAdd(new RealmsButton(0, this.field_224645_h, RealmsConstants.func_225109_a(13), this.field_224644_g, 20, getLocalizedString("gui.cancel")) {
         public void onPress() {
            Realms.setScreen(RealmsSlotOptionsScreen.this.field_224638_a);
         }
      });
      this.addWidget(this.field_224642_e);
      this.addWidget(this.field_224636_B = new RealmsLabel(getLocalizedString("mco.configure.world.buttons.options"), this.width() / 2, 17, 16777215));
      if (this.field_224637_C != null) {
         this.addWidget(this.field_224637_C);
      }

      this.narrateLabels();
   }

   private void func_224609_a() {
      this.field_224639_b = new String[]{getLocalizedString("options.difficulty.peaceful"), getLocalizedString("options.difficulty.easy"), getLocalizedString("options.difficulty.normal"), getLocalizedString("options.difficulty.hard")};
      this.field_224640_c = new String[]{getLocalizedString("selectWorld.gameMode.survival"), getLocalizedString("selectWorld.gameMode.creative"), getLocalizedString("selectWorld.gameMode.adventure")};
      this.field_224641_d = new String[][]{{getLocalizedString("selectWorld.gameMode.survival.line1"), getLocalizedString("selectWorld.gameMode.survival.line2")}, {getLocalizedString("selectWorld.gameMode.creative.line1"), getLocalizedString("selectWorld.gameMode.creative.line2")}, {getLocalizedString("selectWorld.gameMode.adventure.line1"), getLocalizedString("selectWorld.gameMode.adventure.line2")}};
   }

   private String func_224625_b() {
      String s = getLocalizedString("options.difficulty");
      return s + ": " + this.field_224639_b[this.field_224649_l];
   }

   private String func_224610_c() {
      String s = getLocalizedString("selectWorld.gameMode");
      return s + ": " + this.field_224640_c[this.field_224650_m];
   }

   private String func_224618_d() {
      return getLocalizedString("mco.configure.world.pvp") + ": " + getLocalizedString(this.field_224651_n ? "mco.configure.world.on" : "mco.configure.world.off");
   }

   private String func_224606_e() {
      return getLocalizedString("mco.configure.world.spawnAnimals") + ": " + getLocalizedString(this.field_224653_p ? "mco.configure.world.on" : "mco.configure.world.off");
   }

   private String func_224626_f() {
      return this.field_224649_l == 0 ? getLocalizedString("mco.configure.world.spawnMonsters") + ": " + getLocalizedString("mco.configure.world.off") : getLocalizedString("mco.configure.world.spawnMonsters") + ": " + getLocalizedString(this.field_224654_q ? "mco.configure.world.on" : "mco.configure.world.off");
   }

   private String func_224621_g() {
      return getLocalizedString("mco.configure.world.spawnNPCs") + ": " + getLocalizedString(this.field_224652_o ? "mco.configure.world.on" : "mco.configure.world.off");
   }

   private String func_224594_h() {
      return getLocalizedString("mco.configure.world.commandBlocks") + ": " + getLocalizedString(this.field_224656_s ? "mco.configure.world.on" : "mco.configure.world.off");
   }

   private String func_224634_i() {
      return getLocalizedString("mco.configure.world.forceGameMode") + ": " + getLocalizedString(this.field_224657_t ? "mco.configure.world.on" : "mco.configure.world.off");
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      String s = getLocalizedString("mco.configure.world.edit.slot.name");
      this.drawString(s, this.field_224643_f + this.field_224644_g / 2 - this.fontWidth(s) / 2, RealmsConstants.func_225109_a(0) - 5, 16777215);
      this.field_224636_B.render(this);
      if (this.field_224637_C != null) {
         this.field_224637_C.render(this);
      }

      this.field_224642_e.render(p_render_1_, p_render_2_, p_render_3_);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   private String func_224604_j() {
      return this.field_224642_e.getValue().equals(this.field_224646_i.getDefaultSlotName(this.field_224648_k)) ? "" : this.field_224642_e.getValue();
   }

   private void func_224613_k() {
      if (!this.field_224647_j.equals(RealmsServer.ServerType.ADVENTUREMAP) && !this.field_224647_j.equals(RealmsServer.ServerType.EXPERIENCE) && !this.field_224647_j.equals(RealmsServer.ServerType.INSPIRATION)) {
         this.field_224638_a.func_224386_a(new RealmsWorldOptions(this.field_224651_n, this.field_224653_p, this.field_224654_q, this.field_224652_o, this.field_224655_r, this.field_224656_s, this.field_224649_l, this.field_224650_m, this.field_224657_t, this.func_224604_j()));
      } else {
         this.field_224638_a.func_224386_a(new RealmsWorldOptions(this.field_224646_i.pvp, this.field_224646_i.spawnAnimals, this.field_224646_i.spawnMonsters, this.field_224646_i.spawnNPCs, this.field_224646_i.spawnProtection, this.field_224646_i.commandBlocks, this.field_224649_l, this.field_224650_m, this.field_224646_i.forceGameMode, this.func_224604_j()));
      }

   }

   @OnlyIn(Dist.CLIENT)
   class SettingsSlider extends RealmsSliderButton {
      public SettingsSlider(int p_i51603_2_, int p_i51603_3_, int p_i51603_4_, int p_i51603_5_, int p_i51603_6_, float p_i51603_7_, float p_i51603_8_) {
         super(p_i51603_2_, p_i51603_3_, p_i51603_4_, p_i51603_5_, p_i51603_6_, (double)p_i51603_7_, (double)p_i51603_8_);
      }

      public void applyValue() {
         if (RealmsSlotOptionsScreen.this.field_224662_y.active()) {
            RealmsSlotOptionsScreen.this.field_224655_r = (int)this.toValue(this.getValue());
         }
      }

      public String getMessage() {
         return RealmsScreen.getLocalizedString("mco.configure.world.spawnProtection") + ": " + (RealmsSlotOptionsScreen.this.field_224655_r == 0 ? RealmsScreen.getLocalizedString("mco.configure.world.off") : RealmsSlotOptionsScreen.this.field_224655_r);
      }
   }
}