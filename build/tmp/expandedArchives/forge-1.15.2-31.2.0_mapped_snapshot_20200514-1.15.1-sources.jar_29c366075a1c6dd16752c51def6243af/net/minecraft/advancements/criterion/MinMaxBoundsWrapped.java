package net.minecraft.advancements.criterion;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.text.TranslationTextComponent;

public class MinMaxBoundsWrapped {
   public static final MinMaxBoundsWrapped UNBOUNDED = new MinMaxBoundsWrapped((Float)null, (Float)null);
   public static final SimpleCommandExceptionType ERROR_INTS_ONLY = new SimpleCommandExceptionType(new TranslationTextComponent("argument.range.ints"));
   private final Float min;
   private final Float max;

   public MinMaxBoundsWrapped(@Nullable Float p_i49328_1_, @Nullable Float p_i49328_2_) {
      this.min = p_i49328_1_;
      this.max = p_i49328_2_;
   }

   @Nullable
   public Float getMin() {
      return this.min;
   }

   @Nullable
   public Float getMax() {
      return this.max;
   }

   public static MinMaxBoundsWrapped func_207921_a(StringReader p_207921_0_, boolean p_207921_1_, Function<Float, Float> p_207921_2_) throws CommandSyntaxException {
      if (!p_207921_0_.canRead()) {
         throw MinMaxBounds.ERROR_EMPTY.createWithContext(p_207921_0_);
      } else {
         int i = p_207921_0_.getCursor();
         Float f = map(func_207924_b(p_207921_0_, p_207921_1_), p_207921_2_);
         Float f1;
         if (p_207921_0_.canRead(2) && p_207921_0_.peek() == '.' && p_207921_0_.peek(1) == '.') {
            p_207921_0_.skip();
            p_207921_0_.skip();
            f1 = map(func_207924_b(p_207921_0_, p_207921_1_), p_207921_2_);
            if (f == null && f1 == null) {
               p_207921_0_.setCursor(i);
               throw MinMaxBounds.ERROR_EMPTY.createWithContext(p_207921_0_);
            }
         } else {
            if (!p_207921_1_ && p_207921_0_.canRead() && p_207921_0_.peek() == '.') {
               p_207921_0_.setCursor(i);
               throw ERROR_INTS_ONLY.createWithContext(p_207921_0_);
            }

            f1 = f;
         }

         if (f == null && f1 == null) {
            p_207921_0_.setCursor(i);
            throw MinMaxBounds.ERROR_EMPTY.createWithContext(p_207921_0_);
         } else {
            return new MinMaxBoundsWrapped(f, f1);
         }
      }
   }

   @Nullable
   private static Float func_207924_b(StringReader p_207924_0_, boolean p_207924_1_) throws CommandSyntaxException {
      int i = p_207924_0_.getCursor();

      while(p_207924_0_.canRead() && func_207920_c(p_207924_0_, p_207924_1_)) {
         p_207924_0_.skip();
      }

      String s = p_207924_0_.getString().substring(i, p_207924_0_.getCursor());
      if (s.isEmpty()) {
         return null;
      } else {
         try {
            return Float.parseFloat(s);
         } catch (NumberFormatException var5) {
            if (p_207924_1_) {
               throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().createWithContext(p_207924_0_, s);
            } else {
               throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext(p_207924_0_, s);
            }
         }
      }
   }

   private static boolean func_207920_c(StringReader p_207920_0_, boolean p_207920_1_) {
      char c0 = p_207920_0_.peek();
      if ((c0 < '0' || c0 > '9') && c0 != '-') {
         if (p_207920_1_ && c0 == '.') {
            return !p_207920_0_.canRead(2) || p_207920_0_.peek(1) != '.';
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   @Nullable
   private static Float map(@Nullable Float p_207922_0_, Function<Float, Float> p_207922_1_) {
      return p_207922_0_ == null ? null : p_207922_1_.apply(p_207922_0_);
   }
}