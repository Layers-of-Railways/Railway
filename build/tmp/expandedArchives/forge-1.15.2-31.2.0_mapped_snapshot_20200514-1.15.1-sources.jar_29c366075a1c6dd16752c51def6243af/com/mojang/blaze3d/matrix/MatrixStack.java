package com.mojang.blaze3d.matrix;

import com.google.common.collect.Queues;
import java.util.Deque;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MatrixStack {
   private final Deque<MatrixStack.Entry> stack = Util.make(Queues.newArrayDeque(), (p_227864_0_) -> {
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.setIdentity();
      Matrix3f matrix3f = new Matrix3f();
      matrix3f.setIdentity();
      p_227864_0_.add(new MatrixStack.Entry(matrix4f, matrix3f));
   });

   public void translate(double p_227861_1_, double p_227861_3_, double p_227861_5_) {
      MatrixStack.Entry matrixstack$entry = this.stack.getLast();
      matrixstack$entry.matrix.mul(Matrix4f.makeTranslate((float)p_227861_1_, (float)p_227861_3_, (float)p_227861_5_));
   }

   public void scale(float p_227862_1_, float p_227862_2_, float p_227862_3_) {
      MatrixStack.Entry matrixstack$entry = this.stack.getLast();
      matrixstack$entry.matrix.mul(Matrix4f.makeScale(p_227862_1_, p_227862_2_, p_227862_3_));
      if (p_227862_1_ == p_227862_2_ && p_227862_2_ == p_227862_3_) {
         if (p_227862_1_ > 0.0F) {
            return;
         }

         matrixstack$entry.normal.mul(-1.0F);
      }

      float f = 1.0F / p_227862_1_;
      float f1 = 1.0F / p_227862_2_;
      float f2 = 1.0F / p_227862_3_;
      float f3 = MathHelper.fastInvCubeRoot(f * f1 * f2);
      matrixstack$entry.normal.mul(Matrix3f.makeScaleMatrix(f3 * f, f3 * f1, f3 * f2));
   }

   public void rotate(Quaternion p_227863_1_) {
      MatrixStack.Entry matrixstack$entry = this.stack.getLast();
      matrixstack$entry.matrix.mul(p_227863_1_);
      matrixstack$entry.normal.mul(p_227863_1_);
   }

   public void push() {
      MatrixStack.Entry matrixstack$entry = this.stack.getLast();
      this.stack.addLast(new MatrixStack.Entry(matrixstack$entry.matrix.copy(), matrixstack$entry.normal.copy()));
   }

   public void pop() {
      this.stack.removeLast();
   }

   public MatrixStack.Entry getLast() {
      return this.stack.getLast();
   }

   public boolean clear() {
      return this.stack.size() == 1;
   }

   @OnlyIn(Dist.CLIENT)
   public static final class Entry {
      private final Matrix4f matrix;
      private final Matrix3f normal;

      private Entry(Matrix4f p_i225909_1_, Matrix3f p_i225909_2_) {
         this.matrix = p_i225909_1_;
         this.normal = p_i225909_2_;
      }

      public Matrix4f getMatrix() {
         return this.matrix;
      }

      public Matrix3f getNormal() {
         return this.normal;
      }
   }
}