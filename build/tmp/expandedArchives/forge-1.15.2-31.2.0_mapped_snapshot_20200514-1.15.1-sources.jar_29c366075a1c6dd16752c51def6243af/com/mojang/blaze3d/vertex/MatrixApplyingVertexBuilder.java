package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MatrixApplyingVertexBuilder extends DefaultColorVertexBuilder {
   private final IVertexBuilder field_227808_g_;
   private final Matrix4f field_227809_h_;
   private final Matrix3f field_227810_i_;
   private float field_227811_j_;
   private float field_227812_k_;
   private float field_227813_l_;
   private int field_227814_m_;
   private int field_227815_n_;
   private int field_227816_o_;
   private float field_227817_p_;
   private float field_227818_q_;
   private float field_227819_r_;

   public MatrixApplyingVertexBuilder(IVertexBuilder p_i225904_1_, MatrixStack.Entry p_i225904_2_) {
      this.field_227808_g_ = p_i225904_1_;
      this.field_227809_h_ = p_i225904_2_.getMatrix().copy();
      this.field_227809_h_.invert();
      this.field_227810_i_ = p_i225904_2_.getNormal().copy();
      this.field_227810_i_.invert();
      this.reset();
   }

   private void reset() {
      this.field_227811_j_ = 0.0F;
      this.field_227812_k_ = 0.0F;
      this.field_227813_l_ = 0.0F;
      this.field_227814_m_ = 0;
      this.field_227815_n_ = 10;
      this.field_227816_o_ = 15728880;
      this.field_227817_p_ = 0.0F;
      this.field_227818_q_ = 1.0F;
      this.field_227819_r_ = 0.0F;
   }

   public void endVertex() {
      Vector3f vector3f = new Vector3f(this.field_227817_p_, this.field_227818_q_, this.field_227819_r_);
      vector3f.transform(this.field_227810_i_);
      Direction direction = Direction.getFacingFromVector(vector3f.getX(), vector3f.getY(), vector3f.getZ());
      Vector4f vector4f = new Vector4f(this.field_227811_j_, this.field_227812_k_, this.field_227813_l_, 1.0F);
      vector4f.transform(this.field_227809_h_);
      vector4f.transform(Vector3f.YP.rotationDegrees(180.0F));
      vector4f.transform(Vector3f.XP.rotationDegrees(-90.0F));
      vector4f.transform(direction.getRotation());
      float f = -vector4f.getX();
      float f1 = -vector4f.getY();
      this.field_227808_g_.pos((double)this.field_227811_j_, (double)this.field_227812_k_, (double)this.field_227813_l_).color(1.0F, 1.0F, 1.0F, 1.0F).tex(f, f1).overlay(this.field_227814_m_, this.field_227815_n_).lightmap(this.field_227816_o_).normal(this.field_227817_p_, this.field_227818_q_, this.field_227819_r_).endVertex();
      this.reset();
   }

   public IVertexBuilder pos(double x, double y, double z) {
      this.field_227811_j_ = (float)x;
      this.field_227812_k_ = (float)y;
      this.field_227813_l_ = (float)z;
      return this;
   }

   public IVertexBuilder color(int red, int green, int blue, int alpha) {
      return this;
   }

   public IVertexBuilder tex(float u, float v) {
      return this;
   }

   public IVertexBuilder overlay(int u, int v) {
      this.field_227814_m_ = u;
      this.field_227815_n_ = v;
      return this;
   }

   public IVertexBuilder lightmap(int u, int v) {
      this.field_227816_o_ = u | v << 16;
      return this;
   }

   public IVertexBuilder normal(float x, float y, float z) {
      this.field_227817_p_ = x;
      this.field_227818_q_ = y;
      this.field_227819_r_ = z;
      return this;
   }
}