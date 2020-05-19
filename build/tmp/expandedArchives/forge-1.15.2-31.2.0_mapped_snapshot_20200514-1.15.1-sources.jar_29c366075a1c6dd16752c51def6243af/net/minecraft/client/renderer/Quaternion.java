package net.minecraft.client.renderer;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class Quaternion {
   public static final Quaternion ONE = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
   private float x;
   private float y;
   private float z;
   private float w;

   public Quaternion(float x, float y, float z, float w) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.w = w;
   }

   public Quaternion(Vector3f axis, float angle, boolean degrees) {
      if (degrees) {
         angle *= ((float)Math.PI / 180F);
      }

      float f = sin(angle / 2.0F);
      this.x = axis.getX() * f;
      this.y = axis.getY() * f;
      this.z = axis.getZ() * f;
      this.w = cos(angle / 2.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public Quaternion(float xAngle, float yAngle, float zAngle, boolean degrees) {
      if (degrees) {
         xAngle *= ((float)Math.PI / 180F);
         yAngle *= ((float)Math.PI / 180F);
         zAngle *= ((float)Math.PI / 180F);
      }

      float f = sin(0.5F * xAngle);
      float f1 = cos(0.5F * xAngle);
      float f2 = sin(0.5F * yAngle);
      float f3 = cos(0.5F * yAngle);
      float f4 = sin(0.5F * zAngle);
      float f5 = cos(0.5F * zAngle);
      this.x = f * f3 * f5 + f1 * f2 * f4;
      this.y = f1 * f2 * f5 - f * f3 * f4;
      this.z = f * f2 * f5 + f1 * f3 * f4;
      this.w = f1 * f3 * f5 - f * f2 * f4;
   }

   public Quaternion(Quaternion quaternionIn) {
      this.x = quaternionIn.x;
      this.y = quaternionIn.y;
      this.z = quaternionIn.z;
      this.w = quaternionIn.w;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         Quaternion quaternion = (Quaternion)p_equals_1_;
         if (Float.compare(quaternion.x, this.x) != 0) {
            return false;
         } else if (Float.compare(quaternion.y, this.y) != 0) {
            return false;
         } else if (Float.compare(quaternion.z, this.z) != 0) {
            return false;
         } else {
            return Float.compare(quaternion.w, this.w) == 0;
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

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append("Quaternion[").append(this.getW()).append(" + ");
      stringbuilder.append(this.getX()).append("i + ");
      stringbuilder.append(this.getY()).append("j + ");
      stringbuilder.append(this.getZ()).append("k]");
      return stringbuilder.toString();
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

   public void multiply(Quaternion quaternionIn) {
      float f = this.getX();
      float f1 = this.getY();
      float f2 = this.getZ();
      float f3 = this.getW();
      float f4 = quaternionIn.getX();
      float f5 = quaternionIn.getY();
      float f6 = quaternionIn.getZ();
      float f7 = quaternionIn.getW();
      this.x = f3 * f4 + f * f7 + f1 * f6 - f2 * f5;
      this.y = f3 * f5 - f * f6 + f1 * f7 + f2 * f4;
      this.z = f3 * f6 + f * f5 - f1 * f4 + f2 * f7;
      this.w = f3 * f7 - f * f4 - f1 * f5 - f2 * f6;
   }

   @OnlyIn(Dist.CLIENT)
   public void multiply(float valueIn) {
      this.x *= valueIn;
      this.y *= valueIn;
      this.z *= valueIn;
      this.w *= valueIn;
   }

   public void conjugate() {
      this.x = -this.x;
      this.y = -this.y;
      this.z = -this.z;
   }

   @OnlyIn(Dist.CLIENT)
   public void set(float p_227066_1_, float p_227066_2_, float p_227066_3_, float p_227066_4_) {
      this.x = p_227066_1_;
      this.y = p_227066_2_;
      this.z = p_227066_3_;
      this.w = p_227066_4_;
   }

   private static float cos(float p_214904_0_) {
      return (float)Math.cos((double)p_214904_0_);
   }

   private static float sin(float p_214903_0_) {
      return (float)Math.sin((double)p_214903_0_);
   }

   @OnlyIn(Dist.CLIENT)
   public void normalize() {
      float f = this.getX() * this.getX() + this.getY() * this.getY() + this.getZ() * this.getZ() + this.getW() * this.getW();
      if (f > 1.0E-6F) {
         float f1 = MathHelper.fastInvSqrt(f);
         this.x *= f1;
         this.y *= f1;
         this.z *= f1;
         this.w *= f1;
      } else {
         this.x = 0.0F;
         this.y = 0.0F;
         this.z = 0.0F;
         this.w = 0.0F;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public Quaternion copy() {
      return new Quaternion(this);
   }
}