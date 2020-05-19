package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.util.text.TranslationTextComponent;

public class LocationPart {
   public static final SimpleCommandExceptionType EXPECTED_DOUBLE = new SimpleCommandExceptionType(new TranslationTextComponent("argument.pos.missing.double"));
   public static final SimpleCommandExceptionType EXPECTED_INT = new SimpleCommandExceptionType(new TranslationTextComponent("argument.pos.missing.int"));
   private final boolean relative;
   private final double value;

   public LocationPart(boolean relativeIn, double valueIn) {
      this.relative = relativeIn;
      this.value = valueIn;
   }

   public double get(double coord) {
      return this.relative ? this.value + coord : this.value;
   }

   public static LocationPart parseDouble(StringReader reader, boolean centerIntegers) throws CommandSyntaxException {
      if (reader.canRead() && reader.peek() == '^') {
         throw Vec3Argument.POS_MIXED_TYPES.createWithContext(reader);
      } else if (!reader.canRead()) {
         throw EXPECTED_DOUBLE.createWithContext(reader);
      } else {
         boolean flag = isRelative(reader);
         int i = reader.getCursor();
         double d0 = reader.canRead() && reader.peek() != ' ' ? reader.readDouble() : 0.0D;
         String s = reader.getString().substring(i, reader.getCursor());
         if (flag && s.isEmpty()) {
            return new LocationPart(true, 0.0D);
         } else {
            if (!s.contains(".") && !flag && centerIntegers) {
               d0 += 0.5D;
            }

            return new LocationPart(flag, d0);
         }
      }
   }

   public static LocationPart parseInt(StringReader reader) throws CommandSyntaxException {
      if (reader.canRead() && reader.peek() == '^') {
         throw Vec3Argument.POS_MIXED_TYPES.createWithContext(reader);
      } else if (!reader.canRead()) {
         throw EXPECTED_INT.createWithContext(reader);
      } else {
         boolean flag = isRelative(reader);
         double d0;
         if (reader.canRead() && reader.peek() != ' ') {
            d0 = flag ? reader.readDouble() : (double)reader.readInt();
         } else {
            d0 = 0.0D;
         }

         return new LocationPart(flag, d0);
      }
   }

   private static boolean isRelative(StringReader reader) {
      boolean flag;
      if (reader.peek() == '~') {
         flag = true;
         reader.skip();
      } else {
         flag = false;
      }

      return flag;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof LocationPart)) {
         return false;
      } else {
         LocationPart locationpart = (LocationPart)p_equals_1_;
         if (this.relative != locationpart.relative) {
            return false;
         } else {
            return Double.compare(locationpart.value, this.value) == 0;
         }
      }
   }

   public int hashCode() {
      int i = this.relative ? 1 : 0;
      long j = Double.doubleToLongBits(this.value);
      i = 31 * i + (int)(j ^ j >>> 32);
      return i;
   }

   public boolean isRelative() {
      return this.relative;
   }
}