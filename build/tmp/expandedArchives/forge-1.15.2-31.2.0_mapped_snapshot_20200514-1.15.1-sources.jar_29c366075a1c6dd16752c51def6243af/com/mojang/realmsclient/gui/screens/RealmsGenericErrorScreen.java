package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.exception.RealmsServiceException;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsGenericErrorScreen extends RealmsScreen {
   private final RealmsScreen field_224228_a;
   private String field_224229_b;
   private String field_224230_c;

   public RealmsGenericErrorScreen(RealmsServiceException p_i51767_1_, RealmsScreen p_i51767_2_) {
      this.field_224228_a = p_i51767_2_;
      this.func_224224_a(p_i51767_1_);
   }

   public RealmsGenericErrorScreen(String p_i51768_1_, RealmsScreen p_i51768_2_) {
      this.field_224228_a = p_i51768_2_;
      this.func_224225_a(p_i51768_1_);
   }

   public RealmsGenericErrorScreen(String p_i51769_1_, String p_i51769_2_, RealmsScreen p_i51769_3_) {
      this.field_224228_a = p_i51769_3_;
      this.func_224227_a(p_i51769_1_, p_i51769_2_);
   }

   private void func_224224_a(RealmsServiceException p_224224_1_) {
      if (p_224224_1_.field_224983_c == -1) {
         this.field_224229_b = "An error occurred (" + p_224224_1_.field_224981_a + "):";
         this.field_224230_c = p_224224_1_.field_224982_b;
      } else {
         this.field_224229_b = "Realms (" + p_224224_1_.field_224983_c + "):";
         String s = "mco.errorMessage." + p_224224_1_.field_224983_c;
         String s1 = getLocalizedString(s);
         this.field_224230_c = s1.equals(s) ? p_224224_1_.field_224984_d : s1;
      }

   }

   private void func_224225_a(String p_224225_1_) {
      this.field_224229_b = "An error occurred: ";
      this.field_224230_c = p_224225_1_;
   }

   private void func_224227_a(String p_224227_1_, String p_224227_2_) {
      this.field_224229_b = p_224227_1_;
      this.field_224230_c = p_224227_2_;
   }

   public void init() {
      Realms.narrateNow(this.field_224229_b + ": " + this.field_224230_c);
      this.buttonsAdd(new RealmsButton(10, this.width() / 2 - 100, this.height() - 52, 200, 20, "Ok") {
         public void onPress() {
            Realms.setScreen(RealmsGenericErrorScreen.this.field_224228_a);
         }
      });
   }

   public void tick() {
      super.tick();
   }

   @Override
   public boolean keyPressed(int key, int scanCode, int modifiers) {
      if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
         Realms.setScreen(this.field_224228_a);
         return true;
      }
      return super.keyPressed(key, scanCode, modifiers);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.field_224229_b, this.width() / 2, 80, 16777215);
      this.drawCenteredString(this.field_224230_c, this.width() / 2, 100, 16711680);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}