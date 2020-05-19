package net.minecraft.client.gui.fonts;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TexturedGlyph {
   private final RenderType normalType;
   private final RenderType seeThroughType;
   private final float u0;
   private final float u1;
   private final float v0;
   private final float v1;
   private final float minX;
   private final float maxX;
   private final float minY;
   private final float maxY;

   public TexturedGlyph(RenderType p_i225922_1_, RenderType p_i225922_2_, float p_i225922_3_, float p_i225922_4_, float p_i225922_5_, float p_i225922_6_, float p_i225922_7_, float p_i225922_8_, float p_i225922_9_, float p_i225922_10_) {
      this.normalType = p_i225922_1_;
      this.seeThroughType = p_i225922_2_;
      this.u0 = p_i225922_3_;
      this.u1 = p_i225922_4_;
      this.v0 = p_i225922_5_;
      this.v1 = p_i225922_6_;
      this.minX = p_i225922_7_;
      this.maxX = p_i225922_8_;
      this.minY = p_i225922_9_;
      this.maxY = p_i225922_10_;
   }

   public void render(boolean italicIn, float xIn, float yIn, Matrix4f matrixIn, IVertexBuilder bufferIn, float redIn, float greenIn, float blueIn, float alphaIn, int packedLight) {
      int i = 3;
      float f = xIn + this.minX;
      float f1 = xIn + this.maxX;
      float f2 = this.minY - 3.0F;
      float f3 = this.maxY - 3.0F;
      float f4 = yIn + f2;
      float f5 = yIn + f3;
      float f6 = italicIn ? 1.0F - 0.25F * f2 : 0.0F;
      float f7 = italicIn ? 1.0F - 0.25F * f3 : 0.0F;
      bufferIn.pos(matrixIn, f + f6, f4, 0.0F).color(redIn, greenIn, blueIn, alphaIn).tex(this.u0, this.v0).lightmap(packedLight).endVertex();
      bufferIn.pos(matrixIn, f + f7, f5, 0.0F).color(redIn, greenIn, blueIn, alphaIn).tex(this.u0, this.v1).lightmap(packedLight).endVertex();
      bufferIn.pos(matrixIn, f1 + f7, f5, 0.0F).color(redIn, greenIn, blueIn, alphaIn).tex(this.u1, this.v1).lightmap(packedLight).endVertex();
      bufferIn.pos(matrixIn, f1 + f6, f4, 0.0F).color(redIn, greenIn, blueIn, alphaIn).tex(this.u1, this.v0).lightmap(packedLight).endVertex();
   }

   public void renderEffect(TexturedGlyph.Effect effectIn, Matrix4f matrixIn, IVertexBuilder bufferIn, int packedLightIn) {
      bufferIn.pos(matrixIn, effectIn.x0, effectIn.y0, effectIn.depth).color(effectIn.r, effectIn.g, effectIn.b, effectIn.a).tex(this.u0, this.v0).lightmap(packedLightIn).endVertex();
      bufferIn.pos(matrixIn, effectIn.x1, effectIn.y0, effectIn.depth).color(effectIn.r, effectIn.g, effectIn.b, effectIn.a).tex(this.u0, this.v1).lightmap(packedLightIn).endVertex();
      bufferIn.pos(matrixIn, effectIn.x1, effectIn.y1, effectIn.depth).color(effectIn.r, effectIn.g, effectIn.b, effectIn.a).tex(this.u1, this.v1).lightmap(packedLightIn).endVertex();
      bufferIn.pos(matrixIn, effectIn.x0, effectIn.y1, effectIn.depth).color(effectIn.r, effectIn.g, effectIn.b, effectIn.a).tex(this.u1, this.v0).lightmap(packedLightIn).endVertex();
   }

   public RenderType getRenderType(boolean seeThroughIn) {
      return seeThroughIn ? this.seeThroughType : this.normalType;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Effect {
      protected final float x0;
      protected final float y0;
      protected final float x1;
      protected final float y1;
      protected final float depth;
      protected final float r;
      protected final float g;
      protected final float b;
      protected final float a;

      public Effect(float p_i225923_1_, float p_i225923_2_, float p_i225923_3_, float p_i225923_4_, float p_i225923_5_, float p_i225923_6_, float p_i225923_7_, float p_i225923_8_, float p_i225923_9_) {
         this.x0 = p_i225923_1_;
         this.y0 = p_i225923_2_;
         this.x1 = p_i225923_3_;
         this.y1 = p_i225923_4_;
         this.depth = p_i225923_5_;
         this.r = p_i225923_6_;
         this.g = p_i225923_7_;
         this.b = p_i225923_8_;
         this.a = p_i225923_9_;
      }
   }
}