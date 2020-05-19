package net.minecraft.client.util;

import java.util.Arrays;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeyCombo {
   private final char[] field_224801_a;
   private int field_224802_b;
   private final Runnable field_224803_c;

   public KeyCombo(char[] p_i51793_1_, Runnable p_i51793_2_) {
      this.field_224803_c = p_i51793_2_;
      if (p_i51793_1_.length < 1) {
         throw new IllegalArgumentException("Must have at least one char");
      } else {
         this.field_224801_a = p_i51793_1_;
         this.field_224802_b = 0;
      }
   }

   public boolean func_224799_a(char p_224799_1_) {
      if (p_224799_1_ == this.field_224801_a[this.field_224802_b]) {
         ++this.field_224802_b;
         if (this.field_224802_b == this.field_224801_a.length) {
            this.func_224800_a();
            this.field_224803_c.run();
            return true;
         } else {
            return false;
         }
      } else {
         this.func_224800_a();
         return false;
      }
   }

   public void func_224800_a() {
      this.field_224802_b = 0;
   }

   public String toString() {
      return "KeyCombo{chars=" + Arrays.toString(this.field_224801_a) + ", matchIndex=" + this.field_224802_b + '}';
   }
}