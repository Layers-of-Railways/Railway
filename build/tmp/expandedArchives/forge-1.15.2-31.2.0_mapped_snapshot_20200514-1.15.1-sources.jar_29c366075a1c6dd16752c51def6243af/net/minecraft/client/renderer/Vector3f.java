package net.minecraft.client.renderer;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class Vector3f {
   public static Vector3f XN = new Vector3f(-1.0F, 0.0F, 0.0F);
   public static Vector3f XP = new Vector3f(1.0F, 0.0F, 0.0F);
   public static Vector3f YN = new Vector3f(0.0F, -1.0F, 0.0F);
   public static Vector3f YP = new Vector3f(0.0F, 1.0F, 0.0F);
   public static Vector3f ZN = new Vector3f(0.0F, 0.0F, -1.0F);
   public static Vector3f ZP = new Vector3f(0.0F, 0.0F, 1.0F);
   private float x;
   private float y;
   private float z;

   public Vector3f() {
   }

   public Vector3f(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Vector3f(Vec3d vecIn) {
      this((float)vecIn.x, (float)vecIn.y, (float)vecIn.z);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         Vector3f vector3f = (Vector3f)p_equals_1_;
         if (Float.compare(vector3f.x, this.x) != 0) {
            return false;
         } else if (Float.compare(vector3f.y, this.y) != 0) {
            return false;
         } else {
            return Float.compare(vector3f.z, this.z) == 0;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int i = Float.floatToIntBits(this.x);
      i = 31 * i + Float.floatToIntBits(this.y);
      i = 31 * i + Float.floatToIntBits(this.z);
      return i;
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public float getZ() {
      return this.z;
   }

   @OnlyIn(Dist.CLIENT)
   public void mul(float multiplier) {
      this.x *= multiplier;
      this.y *= multiplier;
      this.z *= multiplier;
   }

   @OnlyIn(Dist.CLIENT)
   public void mul(float mx, float my, float mz) {
      this.x *= mx;
      this.y *= my;
      this.z *= mz;
   }

   @OnlyIn(Dist.CLIENT)
   public void clamp(float min, float max) {
      this.x = MathHelper.clamp(this.x, min, max);
      this.y = MathHelper.clamp(this.y, min, max);
      this.z = MathHelper.clamp(this.z, min, max);
   }

   public void set(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   @OnlyIn(Dist.CLIENT)
   public void add(float x, float y, float z) {
      this.x += x;
      this.y += y;
      this.z += z;
   }

   @OnlyIn(Dist.CLIENT)
   public void add(Vector3f vectorIn) {
      this.x += vectorIn.x;
      this.y += vectorIn.y;
      this.z += vectorIn.z;
   }

   @OnlyIn(Dist.CLIENT)
   public void sub(Vector3f vec) {
      this.x -= vec.x;
      this.y -= vec.y;
      this.z -= vec.z;
   }

   @OnlyIn(Dist.CLIENT)
   public float dot(Vector3f vec) {
      return this.x * vec.x + this.y * vec.y + this.z * vec.z;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean normalize() {
      float f = this.x * this.x + this.y * this.y + this.z * this.z;
      if ((double)f < 1.0E-5D) {
         return false;
      } else {
         float f1 = MathHelper.fastInvSqrt(f);
         this.x *= f1;
         this.y *= f1;
         this.z *= f1;
         return true;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void cross(Vector3f vec) {
      float f = this.x;
      float f1 = this.y;
      float f2 = this.z;
      float f3 = vec.getX();
      float f4 = vec.getY();
      float f5 = vec.getZ();
      this.x = f1 * f5 - f2 * f4;
      this.y = f2 * f3 - f * f5;
      this.z = f * f4 - f1 * f3;
   }

   @OnlyIn(Dist.CLIENT)
   public void transform(Matrix3f matrixIn) {
      float f = this.x;
      float f1 = this.y;
      float f2 = this.z;
      this.x = matrixIn.m00 * f + matrixIn.m01 * f1 + matrixIn.m02 * f2;
      this.y = matrixIn.m10 * f + matrixIn.m11 * f1 + matrixIn.m12 * f2;
      this.z = matrixIn.m20 * f + matrixIn.m21 * f1 + matrixIn.m22 * f2;
   }

   public void transform(Quaternion quaternionIn) {
      Quaternion quaternion = new Quaternion(quaternionIn);
      quaternion.multiply(new Quaternion(this.getX(), this.getY(), this.getZ(), 0.0F));
      Quaternion quaternion1 = new Quaternion(quaternionIn);
      quaternion1.conjugate();
      quaternion.multiply(quaternion1);
      this.set(quaternion.getX(), quaternion.getY(), quaternion.getZ());
   }

   @OnlyIn(Dist.CLIENT)
   public void lerp(Vector3f vectorIn, float pctIn) {
      float f = 1.0F - pctIn;
      this.x = this.x * f + vectorIn.x * pctIn;
      this.y = this.y * f + vectorIn.y * pctIn;
      this.z = this.z * f + vectorIn.z * pctIn;
   }

   @OnlyIn(Dist.CLIENT)
   public Quaternion rotation(float valueIn) {
      return new Quaternion(this, valueIn, false);
   }

   @OnlyIn(Dist.CLIENT)
   public Quaternion rotationDegrees(float valueIn) {
      return new Quaternion(this, valueIn, true);
   }

   @OnlyIn(Dist.CLIENT)
   public Vector3f copy() {
      return new Vector3f(this.x, this.y, this.z);
   }

   @OnlyIn(Dist.CLIENT)
   public void apply(Float2FloatFunction functionIn) {
      this.x = functionIn.get(this.x);
      this.y = functionIn.get(this.y);
      this.z = functionIn.get(this.z);
   }

   public String toString() {
      return "[" + this.x + ", " + this.y + ", " + this.z + "]";
   }

   // Forge start
   public Vector3f(float[] values) {
      set(values);
   }
   public void set(float[] values) {
      this.x = values[0];
      this.y = values[1];
      this.z = values[2];
   }
   public void setX(float x) { this.x = x; }
   public void setY(float y) { this.y = y; }
   public void setZ(float z) { this.z = z; }
}