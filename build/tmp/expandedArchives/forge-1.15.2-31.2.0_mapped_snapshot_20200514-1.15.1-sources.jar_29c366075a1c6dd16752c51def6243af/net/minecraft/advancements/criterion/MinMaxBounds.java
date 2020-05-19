package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class MinMaxBounds<T extends Number> {
   public static final SimpleCommandExceptionType ERROR_EMPTY = new SimpleCommandExceptionType(new TranslationTextComponent("argument.range.empty"));
   public static final SimpleCommandExceptionType ERROR_SWAPPED = new SimpleCommandExceptionType(new TranslationTextComponent("argument.range.swapped"));
   protected final T min;
   protected final T max;

   protected MinMaxBounds(@Nullable T p_i49720_1_, @Nullable T p_i49720_2_) {
      this.min = p_i49720_1_;
      this.max = p_i49720_2_;
   }

   @Nullable
   public T getMin() {
      return this.min;
   }

   @Nullable
   public T getMax() {
      return this.max;
   }

   public boolean isUnbounded() {
      return this.min == null && this.max == null;
   }

   public JsonElement serialize() {
      if (this.isUnbounded()) {
         return JsonNull.INSTANCE;
      } else if (this.min != null && this.min.equals(this.max)) {
         return new JsonPrimitive(this.min);
      } else {
         JsonObject jsonobject = new JsonObject();
         if (this.min != null) {
            jsonobject.addProperty("min", this.min);
         }

         if (this.max != null) {
            jsonobject.addProperty("max", this.max);
         }

         return jsonobject;
      }
   }

   protected static <T extends Number, R extends MinMaxBounds<T>> R fromJson(@Nullable JsonElement p_211331_0_, R p_211331_1_, BiFunction<JsonElement, String, T> p_211331_2_, MinMaxBounds.IBoundFactory<T, R> p_211331_3_) {
      if (p_211331_0_ != null && !p_211331_0_.isJsonNull()) {
         if (JSONUtils.isNumber(p_211331_0_)) {
            T t2 = p_211331_2_.apply(p_211331_0_, "value");
            return p_211331_3_.create(t2, t2);
         } else {
            JsonObject jsonobject = JSONUtils.getJsonObject(p_211331_0_, "value");
            T t = jsonobject.has("min") ? p_211331_2_.apply(jsonobject.get("min"), "min") : null;
            T t1 = jsonobject.has("max") ? p_211331_2_.apply(jsonobject.get("max"), "max") : null;
            return p_211331_3_.create(t, t1);
         }
      } else {
         return p_211331_1_;
      }
   }

   protected static <T extends Number, R extends MinMaxBounds<T>> R fromReader(StringReader p_211337_0_, MinMaxBounds.IBoundReader<T, R> p_211337_1_, Function<String, T> p_211337_2_, Supplier<DynamicCommandExceptionType> p_211337_3_, Function<T, T> p_211337_4_) throws CommandSyntaxException {
      if (!p_211337_0_.canRead()) {
         throw ERROR_EMPTY.createWithContext(p_211337_0_);
      } else {
         int i = p_211337_0_.getCursor();

         try {
            T t = optionallyFormat(readNumber(p_211337_0_, p_211337_2_, p_211337_3_), p_211337_4_);
            T t1;
            if (p_211337_0_.canRead(2) && p_211337_0_.peek() == '.' && p_211337_0_.peek(1) == '.') {
               p_211337_0_.skip();
               p_211337_0_.skip();
               t1 = optionallyFormat(readNumber(p_211337_0_, p_211337_2_, p_211337_3_), p_211337_4_);
               if (t == null && t1 == null) {
                  throw ERROR_EMPTY.createWithContext(p_211337_0_);
               }
            } else {
               t1 = t;
            }

            if (t == null && t1 == null) {
               throw ERROR_EMPTY.createWithContext(p_211337_0_);
            } else {
               return p_211337_1_.create(p_211337_0_, t, t1);
            }
         } catch (CommandSyntaxException commandsyntaxexception) {
            p_211337_0_.setCursor(i);
            throw new CommandSyntaxException(commandsyntaxexception.getType(), commandsyntaxexception.getRawMessage(), commandsyntaxexception.getInput(), i);
         }
      }
   }

   @Nullable
   private static <T extends Number> T readNumber(StringReader p_196975_0_, Function<String, T> p_196975_1_, Supplier<DynamicCommandExceptionType> p_196975_2_) throws CommandSyntaxException {
      int i = p_196975_0_.getCursor();

      while(p_196975_0_.canRead() && isAllowedInputChat(p_196975_0_)) {
         p_196975_0_.skip();
      }

      String s = p_196975_0_.getString().substring(i, p_196975_0_.getCursor());
      if (s.isEmpty()) {
         return (T)null;
      } else {
         try {
            return (T)(p_196975_1_.apply(s));
         } catch (NumberFormatException var6) {
            throw p_196975_2_.get().createWithContext(p_196975_0_, s);
         }
      }
   }

   private static boolean isAllowedInputChat(StringReader p_196970_0_) {
      char c0 = p_196970_0_.peek();
      if ((c0 < '0' || c0 > '9') && c0 != '-') {
         if (c0 != '.') {
            return false;
         } else {
            return !p_196970_0_.canRead(2) || p_196970_0_.peek(1) != '.';
         }
      } else {
         return true;
      }
   }

   @Nullable
   private static <T> T optionallyFormat(@Nullable T p_196972_0_, Function<T, T> p_196972_1_) {
      return (T)(p_196972_0_ == null ? null : p_196972_1_.apply(p_196972_0_));
   }

   public static class FloatBound extends MinMaxBounds<Float> {
      public static final MinMaxBounds.FloatBound UNBOUNDED = new MinMaxBounds.FloatBound((Float)null, (Float)null);
      private final Double minSquared;
      private final Double maxSquared;

      private static MinMaxBounds.FloatBound create(StringReader p_211352_0_, @Nullable Float p_211352_1_, @Nullable Float p_211352_2_) throws CommandSyntaxException {
         if (p_211352_1_ != null && p_211352_2_ != null && p_211352_1_ > p_211352_2_) {
            throw ERROR_SWAPPED.createWithContext(p_211352_0_);
         } else {
            return new MinMaxBounds.FloatBound(p_211352_1_, p_211352_2_);
         }
      }

      @Nullable
      private static Double square(@Nullable Float p_211350_0_) {
         return p_211350_0_ == null ? null : p_211350_0_.doubleValue() * p_211350_0_.doubleValue();
      }

      private FloatBound(@Nullable Float p_i49717_1_, @Nullable Float p_i49717_2_) {
         super(p_i49717_1_, p_i49717_2_);
         this.minSquared = square(p_i49717_1_);
         this.maxSquared = square(p_i49717_2_);
      }

      public static MinMaxBounds.FloatBound atLeast(float p_211355_0_) {
         return new MinMaxBounds.FloatBound(p_211355_0_, (Float)null);
      }

      public boolean test(float p_211354_1_) {
         if (this.min != null && this.min > p_211354_1_) {
            return false;
         } else {
            return this.max == null || !(this.max < p_211354_1_);
         }
      }

      public boolean testSquared(double p_211351_1_) {
         if (this.minSquared != null && this.minSquared > p_211351_1_) {
            return false;
         } else {
            return this.maxSquared == null || !(this.maxSquared < p_211351_1_);
         }
      }

      public static MinMaxBounds.FloatBound fromJson(@Nullable JsonElement p_211356_0_) {
         return fromJson(p_211356_0_, UNBOUNDED, JSONUtils::getFloat, MinMaxBounds.FloatBound::new);
      }

      public static MinMaxBounds.FloatBound fromReader(StringReader p_211357_0_) throws CommandSyntaxException {
         return fromReader(p_211357_0_, (p_211358_0_) -> {
            return p_211358_0_;
         });
      }

      public static MinMaxBounds.FloatBound fromReader(StringReader p_211353_0_, Function<Float, Float> p_211353_1_) throws CommandSyntaxException {
         return fromReader(p_211353_0_, MinMaxBounds.FloatBound::create, Float::parseFloat, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidFloat, p_211353_1_);
      }
   }

   @FunctionalInterface
   public interface IBoundFactory<T extends Number, R extends MinMaxBounds<T>> {
      R create(@Nullable T p_create_1_, @Nullable T p_create_2_);
   }

   @FunctionalInterface
   public interface IBoundReader<T extends Number, R extends MinMaxBounds<T>> {
      R create(StringReader p_create_1_, @Nullable T p_create_2_, @Nullable T p_create_3_) throws CommandSyntaxException;
   }

   public static class IntBound extends MinMaxBounds<Integer> {
      public static final MinMaxBounds.IntBound UNBOUNDED = new MinMaxBounds.IntBound((Integer)null, (Integer)null);
      private final Long minSquared;
      private final Long maxSquared;

      private static MinMaxBounds.IntBound create(StringReader p_211338_0_, @Nullable Integer p_211338_1_, @Nullable Integer p_211338_2_) throws CommandSyntaxException {
         if (p_211338_1_ != null && p_211338_2_ != null && p_211338_1_ > p_211338_2_) {
            throw ERROR_SWAPPED.createWithContext(p_211338_0_);
         } else {
            return new MinMaxBounds.IntBound(p_211338_1_, p_211338_2_);
         }
      }

      @Nullable
      private static Long square(@Nullable Integer p_211343_0_) {
         return p_211343_0_ == null ? null : p_211343_0_.longValue() * p_211343_0_.longValue();
      }

      private IntBound(@Nullable Integer p_i49716_1_, @Nullable Integer p_i49716_2_) {
         super(p_i49716_1_, p_i49716_2_);
         this.minSquared = square(p_i49716_1_);
         this.maxSquared = square(p_i49716_2_);
      }

      public static MinMaxBounds.IntBound exactly(int p_211345_0_) {
         return new MinMaxBounds.IntBound(p_211345_0_, p_211345_0_);
      }

      public static MinMaxBounds.IntBound atLeast(int p_211340_0_) {
         return new MinMaxBounds.IntBound(p_211340_0_, (Integer)null);
      }

      public boolean test(int p_211339_1_) {
         if (this.min != null && this.min > p_211339_1_) {
            return false;
         } else {
            return this.max == null || this.max >= p_211339_1_;
         }
      }

      public static MinMaxBounds.IntBound fromJson(@Nullable JsonElement p_211344_0_) {
         return fromJson(p_211344_0_, UNBOUNDED, JSONUtils::getInt, MinMaxBounds.IntBound::new);
      }

      public static MinMaxBounds.IntBound fromReader(StringReader p_211342_0_) throws CommandSyntaxException {
         return fromReader(p_211342_0_, (p_211346_0_) -> {
            return p_211346_0_;
         });
      }

      public static MinMaxBounds.IntBound fromReader(StringReader p_211341_0_, Function<Integer, Integer> p_211341_1_) throws CommandSyntaxException {
         return fromReader(p_211341_0_, MinMaxBounds.IntBound::create, Integer::parseInt, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidInt, p_211341_1_);
      }
   }
}