package net.minecraft.util.math;

import java.util.Random;
import java.util.UUID;
import java.util.function.IntPredicate;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.math.NumberUtils;

public class MathHelper {
   public static final float SQRT_2 = sqrt(2.0F);
   private static final float[] SIN_TABLE = Util.make(new float[65536], (p_203445_0_) -> {
      for(int i = 0; i < p_203445_0_.length; ++i) {
         p_203445_0_[i] = (float)Math.sin((double)i * Math.PI * 2.0D / 65536.0D);
      }

   });
   private static final Random RANDOM = new Random();
   /**
    * Though it looks like an array, this is really more like a mapping. Key (index of this array) is the upper 5 bits
    * of the result of multiplying a 32-bit unsigned integer by the B(2, 5) De Bruijn sequence 0x077CB531. Value (value
    * stored in the array) is the unique index (from the right) of the leftmo
    */
   private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
   private static final double FRAC_BIAS = Double.longBitsToDouble(4805340802404319232L);
   private static final double[] ASINE_TAB = new double[257];
   private static final double[] COS_TAB = new double[257];

   /**
    * sin looked up in a table
    */
   public static float sin(float value) {
      return SIN_TABLE[(int)(value * 10430.378F) & '\uffff'];
   }

   /**
    * cos looked up in the sin table with the appropriate offset
    */
   public static float cos(float value) {
      return SIN_TABLE[(int)(value * 10430.378F + 16384.0F) & '\uffff'];
   }

   public static float sqrt(float value) {
      return (float)Math.sqrt((double)value);
   }

   public static float sqrt(double value) {
      return (float)Math.sqrt(value);
   }

   /**
    * Returns the greatest integer less than or equal to the float argument
    */
   public static int floor(float value) {
      int i = (int)value;
      return value < (float)i ? i - 1 : i;
   }

   /**
    * returns par0 cast as an int, and no greater than Integer.MAX_VALUE-1024
    */
   @OnlyIn(Dist.CLIENT)
   public static int fastFloor(double value) {
      return (int)(value + 1024.0D) - 1024;
   }

   /**
    * Returns the greatest integer less than or equal to the double argument
    */
   public static int floor(double value) {
      int i = (int)value;
      return value < (double)i ? i - 1 : i;
   }

   /**
    * Long version of floor()
    */
   public static long lfloor(double value) {
      long i = (long)value;
      return value < (double)i ? i - 1L : i;
   }

   @OnlyIn(Dist.CLIENT)
   public static int absFloor(double value) {
      return (int)(value >= 0.0D ? value : -value + 1.0D);
   }

   public static float abs(float value) {
      return Math.abs(value);
   }

   /**
    * Returns the unsigned value of an int.
    */
   public static int abs(int value) {
      return Math.abs(value);
   }

   public static int ceil(float value) {
      int i = (int)value;
      return value > (float)i ? i + 1 : i;
   }

   public static int ceil(double value) {
      int i = (int)value;
      return value > (double)i ? i + 1 : i;
   }

   /**
    * Returns the value of the first parameter, clamped to be within the lower and upper limits given by the second and
    * third parameters.
    */
   public static int clamp(int num, int min, int max) {
      if (num < min) {
         return min;
      } else {
         return num > max ? max : num;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static long clamp(long num, long min, long max) {
      if (num < min) {
         return min;
      } else {
         return num > max ? max : num;
      }
   }

   /**
    * Returns the value of the first parameter, clamped to be within the lower and upper limits given by the second and
    * third parameters
    */
   public static float clamp(float num, float min, float max) {
      if (num < min) {
         return min;
      } else {
         return num > max ? max : num;
      }
   }

   public static double clamp(double num, double min, double max) {
      if (num < min) {
         return min;
      } else {
         return num > max ? max : num;
      }
   }

   public static double clampedLerp(double lowerBnd, double upperBnd, double slide) {
      if (slide < 0.0D) {
         return lowerBnd;
      } else {
         return slide > 1.0D ? upperBnd : lerp(slide, lowerBnd, upperBnd);
      }
   }

   /**
    * Maximum of the absolute value of two numbers.
    */
   public static double absMax(double x, double y) {
      if (x < 0.0D) {
         x = -x;
      }

      if (y < 0.0D) {
         y = -y;
      }

      return x > y ? x : y;
   }

   /**
    * Buckets an integer with specifed bucket sizes.
    */
   public static int intFloorDiv(int x, int y) {
      return Math.floorDiv(x, y);
   }

   public static int nextInt(Random random, int minimum, int maximum) {
      return minimum >= maximum ? minimum : random.nextInt(maximum - minimum + 1) + minimum;
   }

   public static float nextFloat(Random random, float minimum, float maximum) {
      return minimum >= maximum ? minimum : random.nextFloat() * (maximum - minimum) + minimum;
   }

   public static double nextDouble(Random random, double minimum, double maximum) {
      return minimum >= maximum ? minimum : random.nextDouble() * (maximum - minimum) + minimum;
   }

   public static double average(long[] values) {
      long i = 0L;

      for(long j : values) {
         i += j;
      }

      return (double)i / (double)values.length;
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean epsilonEquals(float x, float y) {
      return Math.abs(y - x) < 1.0E-5F;
   }

   public static boolean epsilonEquals(double x, double y) {
      return Math.abs(y - x) < (double)1.0E-5F;
   }

   public static int normalizeAngle(int x, int y) {
      return Math.floorMod(x, y);
   }

   @OnlyIn(Dist.CLIENT)
   public static float positiveModulo(float numerator, float denominator) {
      return (numerator % denominator + denominator) % denominator;
   }

   @OnlyIn(Dist.CLIENT)
   public static double positiveModulo(double numerator, double denominator) {
      return (numerator % denominator + denominator) % denominator;
   }

   /**
    * Adjust the angle so that his value is in range [-180;180[
    */
   @OnlyIn(Dist.CLIENT)
   public static int wrapDegrees(int angle) {
      int i = angle % 360;
      if (i >= 180) {
         i -= 360;
      }

      if (i < -180) {
         i += 360;
      }

      return i;
   }

   /**
    * the angle is reduced to an angle between -180 and +180 by mod, and a 360 check
    */
   public static float wrapDegrees(float value) {
      float f = value % 360.0F;
      if (f >= 180.0F) {
         f -= 360.0F;
      }

      if (f < -180.0F) {
         f += 360.0F;
      }

      return f;
   }

   /**
    * the angle is reduced to an angle between -180 and +180 by mod, and a 360 check
    */
   public static double wrapDegrees(double value) {
      double d0 = value % 360.0D;
      if (d0 >= 180.0D) {
         d0 -= 360.0D;
      }

      if (d0 < -180.0D) {
         d0 += 360.0D;
      }

      return d0;
   }

   public static float wrapSubtractDegrees(float p_203302_0_, float p_203302_1_) {
      return wrapDegrees(p_203302_1_ - p_203302_0_);
   }

   public static float degreesDifferenceAbs(float p_203301_0_, float p_203301_1_) {
      return abs(wrapSubtractDegrees(p_203301_0_, p_203301_1_));
   }

   public static float func_219800_b(float p_219800_0_, float p_219800_1_, float p_219800_2_) {
      float f = wrapSubtractDegrees(p_219800_0_, p_219800_1_);
      float f1 = clamp(f, -p_219800_2_, p_219800_2_);
      return p_219800_1_ - f1;
   }

   public static float approach(float p_203300_0_, float p_203300_1_, float p_203300_2_) {
      p_203300_2_ = abs(p_203300_2_);
      return p_203300_0_ < p_203300_1_ ? clamp(p_203300_0_ + p_203300_2_, p_203300_0_, p_203300_1_) : clamp(p_203300_0_ - p_203300_2_, p_203300_1_, p_203300_0_);
   }

   public static float approachDegrees(float p_203303_0_, float p_203303_1_, float p_203303_2_) {
      float f = wrapSubtractDegrees(p_203303_0_, p_203303_1_);
      return approach(p_203303_0_, p_203303_0_ + f, p_203303_2_);
   }

   /**
    * parses the string as integer or returns the second parameter if it fails
    */
   @OnlyIn(Dist.CLIENT)
   public static int getInt(String value, int defaultValue) {
      return NumberUtils.toInt(value, defaultValue);
   }

   /**
    * parses the string as integer or returns the second parameter if it fails. this value is capped to par2
    */
   @OnlyIn(Dist.CLIENT)
   public static int getInt(String value, int defaultValue, int max) {
      return Math.max(max, getInt(value, defaultValue));
   }

   @OnlyIn(Dist.CLIENT)
   public static double getDouble(String value, double defaultValue) {
      try {
         return Double.parseDouble(value);
      } catch (Throwable var4) {
         return defaultValue;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static double getDouble(String value, double defaultValue, double max) {
      return Math.max(max, getDouble(value, defaultValue));
   }

   /**
    * Returns the input value rounded up to the next highest power of two.
    */
   public static int smallestEncompassingPowerOfTwo(int value) {
      int i = value - 1;
      i = i | i >> 1;
      i = i | i >> 2;
      i = i | i >> 4;
      i = i | i >> 8;
      i = i | i >> 16;
      return i + 1;
   }

   /**
    * Is the given value a power of two?  (1, 2, 4, 8, 16, ...)
    */
   private static boolean isPowerOfTwo(int value) {
      return value != 0 && (value & value - 1) == 0;
   }

   /**
    * Uses a B(2, 5) De Bruijn sequence and a lookup table to efficiently calculate the log-base-two of the given value.
    * Optimized for cases where the input value is a power-of-two. If the input value is not a power-of-two, then
    * subtract 1 from the return value.
    */
   public static int log2DeBruijn(int value) {
      value = isPowerOfTwo(value) ? value : smallestEncompassingPowerOfTwo(value);
      return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int)((long)value * 125613361L >> 27) & 31];
   }

   /**
    * Efficiently calculates the floor of the base-2 log of an integer value.  This is effectively the index of the
    * highest bit that is set.  For example, if the number in binary is 0...100101, this will return 5.
    */
   public static int log2(int value) {
      return log2DeBruijn(value) - (isPowerOfTwo(value) ? 0 : 1);
   }

   /**
    * Rounds the first parameter up to the next interval of the second parameter.
    *  
    * For instance, {@code roundUp(1, 4)} returns 4; {@code roundUp(0, 4)} returns 0; and {@code roundUp(4, 4)} returns
    * 4.
    */
   public static int roundUp(int number, int interval) {
      if (interval == 0) {
         return 0;
      } else if (number == 0) {
         return interval;
      } else {
         if (number < 0) {
            interval *= -1;
         }

         int i = number % interval;
         return i == 0 ? number : number + interval - i;
      }
   }

   /**
    * Makes an integer color from the given red, green, and blue float values
    */
   @OnlyIn(Dist.CLIENT)
   public static int rgb(float rIn, float gIn, float bIn) {
      return rgb(floor(rIn * 255.0F), floor(gIn * 255.0F), floor(bIn * 255.0F));
   }

   /**
    * Makes a single int color with the given red, green, and blue values.
    */
   @OnlyIn(Dist.CLIENT)
   public static int rgb(int rIn, int gIn, int bIn) {
      int lvt_3_1_ = (rIn << 8) + gIn;
      lvt_3_1_ = (lvt_3_1_ << 8) + bIn;
      return lvt_3_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public static float frac(float number) {
      return number - (float)floor(number);
   }

   /**
    * Gets the decimal portion of the given double. For instance, {@code frac(5.5)} returns {@code .5}.
    */
   public static double frac(double number) {
      return number - (double)lfloor(number);
   }

   public static long getPositionRandom(Vec3i pos) {
      return getCoordinateRandom(pos.getX(), pos.getY(), pos.getZ());
   }

   public static long getCoordinateRandom(int x, int y, int z) {
      long i = (long)(x * 3129871) ^ (long)z * 116129781L ^ (long)y;
      i = i * i * 42317861L + i * 11L;
      return i >> 16;
   }

   public static UUID getRandomUUID(Random rand) {
      long i = rand.nextLong() & -61441L | 16384L;
      long j = rand.nextLong() & 4611686018427387903L | Long.MIN_VALUE;
      return new UUID(i, j);
   }

   /**
    * Generates a random UUID using the shared random
    */
   public static UUID getRandomUUID() {
      return getRandomUUID(RANDOM);
   }

   public static double pct(double p_181160_0_, double p_181160_2_, double p_181160_4_) {
      return (p_181160_0_ - p_181160_2_) / (p_181160_4_ - p_181160_2_);
   }

   public static double atan2(double p_181159_0_, double p_181159_2_) {
      double d0 = p_181159_2_ * p_181159_2_ + p_181159_0_ * p_181159_0_;
      if (Double.isNaN(d0)) {
         return Double.NaN;
      } else {
         boolean flag = p_181159_0_ < 0.0D;
         if (flag) {
            p_181159_0_ = -p_181159_0_;
         }

         boolean flag1 = p_181159_2_ < 0.0D;
         if (flag1) {
            p_181159_2_ = -p_181159_2_;
         }

         boolean flag2 = p_181159_0_ > p_181159_2_;
         if (flag2) {
            double d1 = p_181159_2_;
            p_181159_2_ = p_181159_0_;
            p_181159_0_ = d1;
         }

         double d9 = fastInvSqrt(d0);
         p_181159_2_ = p_181159_2_ * d9;
         p_181159_0_ = p_181159_0_ * d9;
         double d2 = FRAC_BIAS + p_181159_0_;
         int i = (int)Double.doubleToRawLongBits(d2);
         double d3 = ASINE_TAB[i];
         double d4 = COS_TAB[i];
         double d5 = d2 - FRAC_BIAS;
         double d6 = p_181159_0_ * d4 - p_181159_2_ * d5;
         double d7 = (6.0D + d6 * d6) * d6 * 0.16666666666666666D;
         double d8 = d3 + d7;
         if (flag2) {
            d8 = (Math.PI / 2D) - d8;
         }

         if (flag1) {
            d8 = Math.PI - d8;
         }

         if (flag) {
            d8 = -d8;
         }

         return d8;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static float fastInvSqrt(float number) {
      float f = 0.5F * number;
      int i = Float.floatToIntBits(number);
      i = 1597463007 - (i >> 1);
      number = Float.intBitsToFloat(i);
      number = number * (1.5F - f * number * number);
      return number;
   }

   /**
    * Computes 1/sqrt(n) using <a href="https://en.wikipedia.org/wiki/Fast_inverse_square_root">the fast inverse square
    * root</a> with a constant of 0x5FE6EB50C7B537AA.
    */
   public static double fastInvSqrt(double number) {
      double d0 = 0.5D * number;
      long i = Double.doubleToRawLongBits(number);
      i = 6910469410427058090L - (i >> 1);
      number = Double.longBitsToDouble(i);
      number = number * (1.5D - d0 * number * number);
      return number;
   }

   @OnlyIn(Dist.CLIENT)
   public static float fastInvCubeRoot(float number) {
      int i = Float.floatToIntBits(number);
      i = 1419967116 - i / 3;
      float f = Float.intBitsToFloat(i);
      f = 0.6666667F * f + 1.0F / (3.0F * f * f * number);
      f = 0.6666667F * f + 1.0F / (3.0F * f * f * number);
      return f;
   }

   public static int hsvToRGB(float hue, float saturation, float value) {
      int i = (int)(hue * 6.0F) % 6;
      float f = hue * 6.0F - (float)i;
      float f1 = value * (1.0F - saturation);
      float f2 = value * (1.0F - f * saturation);
      float f3 = value * (1.0F - (1.0F - f) * saturation);
      float f4;
      float f5;
      float f6;
      switch(i) {
      case 0:
         f4 = value;
         f5 = f3;
         f6 = f1;
         break;
      case 1:
         f4 = f2;
         f5 = value;
         f6 = f1;
         break;
      case 2:
         f4 = f1;
         f5 = value;
         f6 = f3;
         break;
      case 3:
         f4 = f1;
         f5 = f2;
         f6 = value;
         break;
      case 4:
         f4 = f3;
         f5 = f1;
         f6 = value;
         break;
      case 5:
         f4 = value;
         f5 = f1;
         f6 = f2;
         break;
      default:
         throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
      }

      int j = clamp((int)(f4 * 255.0F), 0, 255);
      int k = clamp((int)(f5 * 255.0F), 0, 255);
      int l = clamp((int)(f6 * 255.0F), 0, 255);
      return j << 16 | k << 8 | l;
   }

   public static int hash(int p_188208_0_) {
      p_188208_0_ = p_188208_0_ ^ p_188208_0_ >>> 16;
      p_188208_0_ = p_188208_0_ * -2048144789;
      p_188208_0_ = p_188208_0_ ^ p_188208_0_ >>> 13;
      p_188208_0_ = p_188208_0_ * -1028477387;
      p_188208_0_ = p_188208_0_ ^ p_188208_0_ >>> 16;
      return p_188208_0_;
   }

   public static int binarySearch(int min, int max, IntPredicate isTargetBeforeOrAt) {
      int i = max - min;

      while(i > 0) {
         int j = i / 2;
         int k = min + j;
         if (isTargetBeforeOrAt.test(k)) {
            i = j;
         } else {
            min = k + 1;
            i -= j + 1;
         }
      }

      return min;
   }

   public static float lerp(float pct, float start, float end) {
      return start + pct * (end - start);
   }

   public static double lerp(double pct, double start, double end) {
      return start + pct * (end - start);
   }

   public static double lerp2(double p_219804_0_, double p_219804_2_, double p_219804_4_, double p_219804_6_, double p_219804_8_, double p_219804_10_) {
      return lerp(p_219804_2_, lerp(p_219804_0_, p_219804_4_, p_219804_6_), lerp(p_219804_0_, p_219804_8_, p_219804_10_));
   }

   public static double lerp3(double p_219807_0_, double p_219807_2_, double p_219807_4_, double p_219807_6_, double p_219807_8_, double p_219807_10_, double p_219807_12_, double p_219807_14_, double p_219807_16_, double p_219807_18_, double p_219807_20_) {
      return lerp(p_219807_4_, lerp2(p_219807_0_, p_219807_2_, p_219807_6_, p_219807_8_, p_219807_10_, p_219807_12_), lerp2(p_219807_0_, p_219807_2_, p_219807_14_, p_219807_16_, p_219807_18_, p_219807_20_));
   }

   public static double perlinFade(double p_219801_0_) {
      return p_219801_0_ * p_219801_0_ * p_219801_0_ * (p_219801_0_ * (p_219801_0_ * 6.0D - 15.0D) + 10.0D);
   }

   public static int signum(double x) {
      if (x == 0.0D) {
         return 0;
      } else {
         return x > 0.0D ? 1 : -1;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static float interpolateAngle(float p_219805_0_, float p_219805_1_, float p_219805_2_) {
      return p_219805_1_ + p_219805_0_ * wrapDegrees(p_219805_2_ - p_219805_1_);
   }

   @Deprecated
   public static float rotLerp(float p_226167_0_, float p_226167_1_, float p_226167_2_) {
      float f;
      for(f = p_226167_1_ - p_226167_0_; f < -180.0F; f += 360.0F) {
         ;
      }

      while(f >= 180.0F) {
         f -= 360.0F;
      }

      return p_226167_0_ + p_226167_2_ * f;
   }

   @Deprecated
   @OnlyIn(Dist.CLIENT)
   public static float rotWrap(double p_226168_0_) {
      while(p_226168_0_ >= 180.0D) {
         p_226168_0_ -= 360.0D;
      }

      while(p_226168_0_ < -180.0D) {
         p_226168_0_ += 360.0D;
      }

      return (float)p_226168_0_;
   }

   static {
      for(int i = 0; i < 257; ++i) {
         double d0 = (double)i / 256.0D;
         double d1 = Math.asin(d0);
         COS_TAB[i] = Math.cos(d1);
         ASINE_TAB[i] = d1;
      }

   }
}