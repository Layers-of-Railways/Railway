package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpriteAwareVertexBuilder implements IVertexBuilder {
   private final IVertexBuilder vertexBuilder;
   private final TextureAtlasSprite atlasSprite;

   public SpriteAwareVertexBuilder(IVertexBuilder bufferIn, TextureAtlasSprite spriteIn) {
      this.vertexBuilder = bufferIn;
      this.atlasSprite = spriteIn;
   }

   public IVertexBuilder pos(double x, double y, double z) {
      return this.vertexBuilder.pos(x, y, z);
   }

   public IVertexBuilder color(int red, int green, int blue, int alpha) {
      return this.vertexBuilder.color(red, green, blue, alpha);
   }

   public IVertexBuilder tex(float u, float v) {
      return this.vertexBuilder.tex(this.atlasSprite.getInterpolatedU((double)(u * 16.0F)), this.atlasSprite.getInterpolatedV((double)(v * 16.0F)));
   }

   public IVertexBuilder overlay(int u, int v) {
      return this.vertexBuilder.overlay(u, v);
   }

   public IVertexBuilder lightmap(int u, int v) {
      return this.vertexBuilder.lightmap(u, v);
   }

   public IVertexBuilder normal(float x, float y, float z) {
      return this.vertexBuilder.normal(x, y, z);
   }

   public void endVertex() {
      this.vertexBuilder.endVertex();
   }

   public void addVertex(float x, float y, float z, float red, float green, float blue, float alpha, float texU, float texV, int overlayUV, int lightmapUV, float normalX, float normalY, float normalZ) {
      this.vertexBuilder.addVertex(x, y, z, red, green, blue, alpha, this.atlasSprite.getInterpolatedU((double)(texU * 16.0F)), this.atlasSprite.getInterpolatedV((double)(texV * 16.0F)), overlayUV, lightmapUV, normalX, normalY, normalZ);
   }
}