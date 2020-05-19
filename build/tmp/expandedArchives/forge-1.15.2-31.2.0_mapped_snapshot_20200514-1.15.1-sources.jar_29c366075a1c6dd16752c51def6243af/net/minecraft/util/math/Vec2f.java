package net.minecraft.util.math;

public class Vec2f {
   public static final Vec2f ZERO = new Vec2f(0.0F, 0.0F);
   public static final Vec2f ONE = new Vec2f(1.0F, 1.0F);
   public static final Vec2f UNIT_X = new Vec2f(1.0F, 0.0F);
   public static final Vec2f NEGATIVE_UNIT_X = new Vec2f(-1.0F, 0.0F);
   public static final Vec2f UNIT_Y = new Vec2f(0.0F, 1.0F);
   public static final Vec2f NEGATIVE_UNIT_Y = new Vec2f(0.0F, -1.0F);
   public static final Vec2f MAX = new Vec2f(Float.MAX_VALUE, Float.MAX_VALUE);
   public static final Vec2f MIN = new Vec2f(Float.MIN_VALUE, Float.MIN_VALUE);
   public final float x;
   public final float y;

   public Vec2f(float xIn, float yIn) {
      this.x = xIn;
      this.y = yIn;
   }

   public boolean equals(Vec2f other) {
      return this.x == other.x && this.y == other.y;
   }
}