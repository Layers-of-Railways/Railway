package net.minecraft.client.renderer;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Vector4f {
   private float x;
   private float y;
   private float z;
   private float w;

   public Vector4f() {
   }

   public Vector4f(float x, float y, float z, float w) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.w = w;
   }

   public Vector4f(Vector3f vectorIn) {
      this(vectorIn.getX(), vectorIn.getY(), vectorIn.getZ(), 1.0F);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         Vector4f vector4f = (Vector4f)p_equals_1_;
         if (Float.compare(vector4f.x, this.x) != 0) {
            return false;
         } else if (Float.compare(vector4f.y, this.y) != 0) {
            return false;
         } else if (Float.compare(vector4f.z, this.z) != 0) {
            return false;
         } else {
            return Float.compare(vector4f.w, this.w) == 0;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int i = Float.floatToIntBits(this.x);
      i = 31 * i + Float.floatToIntBits(this.y);
      i = 31 * i + Float.floatToIntBits(this.z);
      i = 31 * i + Float.floatToIntBits(this.w);
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

   public float getW() {
      return this.w;
   }

   public void scale(Vector3f vec) {
      this.x *= vec.getX();
      this.y *= vec.getY();
      this.z *= vec.getZ();
   }

   public void set(float x, float y, float z, float w) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.w = w;
   }

   public float dot(Vector4f vectorIn) {
      return this.x * vectorIn.x + this.y * vectorIn.y + this.z * vectorIn.z + this.w * vectorIn.w;
   }

   public boolean normalize() {
      float f = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
      if ((double)f < 1.0E-5D) {
         return false;
      } else {
         float f1 = MathHelper.fastInvSqrt(f);
         this.x *= f1;
         this.y *= f1;
         this.z *= f1;
         this.w *= f1;
         return true;
      }
   }

   public void transform(Matrix4f matrixIn) {
      float f = this.x;
      float f1 = this.y;
      float f2 = this.z;
      float f3 = this.w;
      this.x = matrixIn.m00 * f + matrixIn.m01 * f1 + matrixIn.m02 * f2 + matrixIn.m03 * f3;
      this.y = matrixIn.m10 * f + matrixIn.m11 * f1 + matrixIn.m12 * f2 + matrixIn.m13 * f3;
      this.z = matrixIn.m20 * f + matrixIn.m21 * f1 + matrixIn.m22 * f2 + matrixIn.m23 * f3;
      this.w = matrixIn.m30 * f + matrixIn.m31 * f1 + matrixIn.m32 * f2 + matrixIn.m33 * f3;
   }

   public void transform(Quaternion quaternionIn) {
      Quaternion quaternion = new Quaternion(quaternionIn);
      quaternion.multiply(new Quaternion(this.getX(), this.getY(), this.getZ(), 0.0F));
      Quaternion quaternion1 = new Quaternion(quaternionIn);
      quaternion1.conjugate();
      quaternion.multiply(quaternion1);
      this.set(quaternion.getX(), quaternion.getY(), quaternion.getZ(), this.getW());
   }

   public void perspectiveDivide() {
      this.x /= this.w;
      this.y /= this.w;
      this.z /= this.w;
      this.w = 1.0F;
   }

   public String toString() {
      return "[" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + "]";
   }

   // Forge start
   public void set(float[] values) {
      this.x = values[0];
      this.y = values[1];
      this.z = values[2];
      this.w = values[3];
   }
   public void setX(float x) { this.x = x; }
   public void setY(float y) { this.y = y; }
   public void setZ(float z) { this.z = z; }
   public void setW(float z) { this.w = z; }
}