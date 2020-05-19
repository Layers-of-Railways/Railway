package net.minecraft.client.gui.fonts;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextInputUtil {
   private final Minecraft minecraft;
   private final FontRenderer fontRenderer;
   private final Supplier<String> textSupplier;
   private final Consumer<String> textConsumer;
   private final int textWidth;
   private int field_216905_f;
   private int field_216906_g;

   public TextInputUtil(Minecraft p_i51124_1_, Supplier<String> p_i51124_2_, Consumer<String> p_i51124_3_, int p_i51124_4_) {
      this.minecraft = p_i51124_1_;
      this.fontRenderer = p_i51124_1_.fontRenderer;
      this.textSupplier = p_i51124_2_;
      this.textConsumer = p_i51124_3_;
      this.textWidth = p_i51124_4_;
      this.func_216899_b();
   }

   public boolean func_216894_a(char p_216894_1_) {
      if (SharedConstants.isAllowedCharacter(p_216894_1_)) {
         this.func_216892_a(Character.toString(p_216894_1_));
      }

      return true;
   }

   private void func_216892_a(String p_216892_1_) {
      if (this.field_216906_g != this.field_216905_f) {
         this.func_216893_f();
      }

      String s = this.textSupplier.get();
      this.field_216905_f = MathHelper.clamp(this.field_216905_f, 0, s.length());
      String s1 = (new StringBuilder(s)).insert(this.field_216905_f, p_216892_1_).toString();
      if (this.fontRenderer.getStringWidth(s1) <= this.textWidth) {
         this.textConsumer.accept(s1);
         this.field_216906_g = this.field_216905_f = Math.min(s1.length(), this.field_216905_f + p_216892_1_.length());
      }

   }

   public boolean func_216897_a(int p_216897_1_) {
      String s = this.textSupplier.get();
      if (Screen.isSelectAll(p_216897_1_)) {
         this.field_216906_g = 0;
         this.field_216905_f = s.length();
         return true;
      } else if (Screen.isCopy(p_216897_1_)) {
         this.minecraft.keyboardListener.setClipboardString(this.func_216895_e());
         return true;
      } else if (Screen.isPaste(p_216897_1_)) {
         this.func_216892_a(SharedConstants.filterAllowedCharacters(TextFormatting.getTextWithoutFormattingCodes(this.minecraft.keyboardListener.getClipboardString().replaceAll("\\r", ""))));
         this.field_216906_g = this.field_216905_f;
         return true;
      } else if (Screen.isCut(p_216897_1_)) {
         this.minecraft.keyboardListener.setClipboardString(this.func_216895_e());
         this.func_216893_f();
         return true;
      } else if (p_216897_1_ == 259) {
         if (!s.isEmpty()) {
            if (this.field_216906_g != this.field_216905_f) {
               this.func_216893_f();
            } else if (this.field_216905_f > 0) {
               s = (new StringBuilder(s)).deleteCharAt(Math.max(0, this.field_216905_f - 1)).toString();
               this.field_216906_g = this.field_216905_f = Math.max(0, this.field_216905_f - 1);
               this.textConsumer.accept(s);
            }
         }

         return true;
      } else if (p_216897_1_ == 261) {
         if (!s.isEmpty()) {
            if (this.field_216906_g != this.field_216905_f) {
               this.func_216893_f();
            } else if (this.field_216905_f < s.length()) {
               s = (new StringBuilder(s)).deleteCharAt(Math.max(0, this.field_216905_f)).toString();
               this.textConsumer.accept(s);
            }
         }

         return true;
      } else if (p_216897_1_ == 263) {
         int j = this.fontRenderer.getBidiFlag() ? 1 : -1;
         if (Screen.hasControlDown()) {
            this.field_216905_f = this.fontRenderer.getWordPosition(s, j, this.field_216905_f, true);
         } else {
            this.field_216905_f = Math.max(0, Math.min(s.length(), this.field_216905_f + j));
         }

         if (!Screen.hasShiftDown()) {
            this.field_216906_g = this.field_216905_f;
         }

         return true;
      } else if (p_216897_1_ == 262) {
         int i = this.fontRenderer.getBidiFlag() ? -1 : 1;
         if (Screen.hasControlDown()) {
            this.field_216905_f = this.fontRenderer.getWordPosition(s, i, this.field_216905_f, true);
         } else {
            this.field_216905_f = Math.max(0, Math.min(s.length(), this.field_216905_f + i));
         }

         if (!Screen.hasShiftDown()) {
            this.field_216906_g = this.field_216905_f;
         }

         return true;
      } else if (p_216897_1_ == 268) {
         this.field_216905_f = 0;
         if (!Screen.hasShiftDown()) {
            this.field_216906_g = this.field_216905_f;
         }

         return true;
      } else if (p_216897_1_ == 269) {
         this.field_216905_f = this.textSupplier.get().length();
         if (!Screen.hasShiftDown()) {
            this.field_216906_g = this.field_216905_f;
         }

         return true;
      } else {
         return false;
      }
   }

   private String func_216895_e() {
      String s = this.textSupplier.get();
      int i = Math.min(this.field_216905_f, this.field_216906_g);
      int j = Math.max(this.field_216905_f, this.field_216906_g);
      return s.substring(i, j);
   }

   private void func_216893_f() {
      if (this.field_216906_g != this.field_216905_f) {
         String s = this.textSupplier.get();
         int i = Math.min(this.field_216905_f, this.field_216906_g);
         int j = Math.max(this.field_216905_f, this.field_216906_g);
         String s1 = s.substring(0, i) + s.substring(j);
         this.field_216905_f = i;
         this.field_216906_g = this.field_216905_f;
         this.textConsumer.accept(s1);
      }
   }

   public void func_216899_b() {
      this.field_216906_g = this.field_216905_f = this.textSupplier.get().length();
   }

   public int func_216896_c() {
      return this.field_216905_f;
   }

   public int func_216898_d() {
      return this.field_216906_g;
   }
}