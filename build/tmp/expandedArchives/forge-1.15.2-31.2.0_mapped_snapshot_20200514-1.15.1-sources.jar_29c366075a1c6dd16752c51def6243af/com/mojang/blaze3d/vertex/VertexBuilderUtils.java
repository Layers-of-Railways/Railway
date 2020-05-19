package com.mojang.blaze3d.vertex;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VertexBuilderUtils {
   public static IVertexBuilder newDelegate(IVertexBuilder p_227915_0_, IVertexBuilder p_227915_1_) {
      return new VertexBuilderUtils.DelegatingVertexBuilder(p_227915_0_, p_227915_1_);
   }

   @OnlyIn(Dist.CLIENT)
   static class DelegatingVertexBuilder implements IVertexBuilder {
      private final IVertexBuilder field_227916_a_;
      private final IVertexBuilder field_227917_b_;

      public DelegatingVertexBuilder(IVertexBuilder p_i225913_1_, IVertexBuilder p_i225913_2_) {
         if (p_i225913_1_ == p_i225913_2_) {
            throw new IllegalArgumentException("Duplicate delegates");
         } else {
            this.field_227916_a_ = p_i225913_1_;
            this.field_227917_b_ = p_i225913_2_;
         }
      }

      public IVertexBuilder pos(double x, double y, double z) {
         this.field_227916_a_.pos(x, y, z);
         this.field_227917_b_.pos(x, y, z);
         return this;
      }

      public IVertexBuilder color(int red, int green, int blue, int alpha) {
         this.field_227916_a_.color(red, green, blue, alpha);
         this.field_227917_b_.color(red, green, blue, alpha);
         return this;
      }

      public IVertexBuilder tex(float u, float v) {
         this.field_227916_a_.tex(u, v);
         this.field_227917_b_.tex(u, v);
         return this;
      }

      public IVertexBuilder overlay(int u, int v) {
         this.field_227916_a_.overlay(u, v);
         this.field_227917_b_.overlay(u, v);
         return this;
      }

      public IVertexBuilder lightmap(int u, int v) {
         this.field_227916_a_.lightmap(u, v);
         this.field_227917_b_.lightmap(u, v);
         return this;
      }

      public IVertexBuilder normal(float x, float y, float z) {
         this.field_227916_a_.normal(x, y, z);
         this.field_227917_b_.normal(x, y, z);
         return this;
      }

      public void addVertex(float x, float y, float z, float red, float green, float blue, float alpha, float texU, float texV, int overlayUV, int lightmapUV, float normalX, float normalY, float normalZ) {
         this.field_227916_a_.addVertex(x, y, z, red, green, blue, alpha, texU, texV, overlayUV, lightmapUV, normalX, normalY, normalZ);
         this.field_227917_b_.addVertex(x, y, z, red, green, blue, alpha, texU, texV, overlayUV, lightmapUV, normalX, normalY, normalZ);
      }

      public void endVertex() {
         this.field_227916_a_.endVertex();
         this.field_227917_b_.endVertex();
      }
   }
}