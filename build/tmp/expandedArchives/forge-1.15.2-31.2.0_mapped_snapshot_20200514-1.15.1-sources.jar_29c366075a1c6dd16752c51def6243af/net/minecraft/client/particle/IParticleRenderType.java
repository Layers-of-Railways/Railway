package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IParticleRenderType {
   IParticleRenderType TERRAIN_SHEET = new IParticleRenderType() {
      public void beginRender(BufferBuilder p_217600_1_, TextureManager p_217600_2_) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.depthMask(true);
         p_217600_2_.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
         p_217600_1_.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
      }

      public void finishRender(Tessellator p_217599_1_) {
         p_217599_1_.draw();
      }

      public String toString() {
         return "TERRAIN_SHEET";
      }
   };
   IParticleRenderType PARTICLE_SHEET_OPAQUE = new IParticleRenderType() {
      public void beginRender(BufferBuilder p_217600_1_, TextureManager p_217600_2_) {
         RenderSystem.disableBlend();
         RenderSystem.depthMask(true);
         p_217600_2_.bindTexture(AtlasTexture.LOCATION_PARTICLES_TEXTURE);
         p_217600_1_.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
      }

      public void finishRender(Tessellator p_217599_1_) {
         p_217599_1_.draw();
      }

      public String toString() {
         return "PARTICLE_SHEET_OPAQUE";
      }
   };
   IParticleRenderType PARTICLE_SHEET_TRANSLUCENT = new IParticleRenderType() {
      public void beginRender(BufferBuilder p_217600_1_, TextureManager p_217600_2_) {
         RenderSystem.depthMask(true);
         p_217600_2_.bindTexture(AtlasTexture.LOCATION_PARTICLES_TEXTURE);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
         RenderSystem.alphaFunc(516, 0.003921569F);
         p_217600_1_.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
      }

      public void finishRender(Tessellator p_217599_1_) {
         p_217599_1_.draw();
      }

      public String toString() {
         return "PARTICLE_SHEET_TRANSLUCENT";
      }
   };
   IParticleRenderType PARTICLE_SHEET_LIT = new IParticleRenderType() {
      public void beginRender(BufferBuilder p_217600_1_, TextureManager p_217600_2_) {
         RenderSystem.disableBlend();
         RenderSystem.depthMask(true);
         p_217600_2_.bindTexture(AtlasTexture.LOCATION_PARTICLES_TEXTURE);
         p_217600_1_.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
      }

      public void finishRender(Tessellator p_217599_1_) {
         p_217599_1_.draw();
      }

      public String toString() {
         return "PARTICLE_SHEET_LIT";
      }
   };
   IParticleRenderType CUSTOM = new IParticleRenderType() {
      public void beginRender(BufferBuilder p_217600_1_, TextureManager p_217600_2_) {
         RenderSystem.depthMask(true);
         RenderSystem.disableBlend();
      }

      public void finishRender(Tessellator p_217599_1_) {
      }

      public String toString() {
         return "CUSTOM";
      }
   };
   IParticleRenderType NO_RENDER = new IParticleRenderType() {
      public void beginRender(BufferBuilder p_217600_1_, TextureManager p_217600_2_) {
      }

      public void finishRender(Tessellator p_217599_1_) {
      }

      public String toString() {
         return "NO_RENDER";
      }
   };

   void beginRender(BufferBuilder p_217600_1_, TextureManager p_217600_2_);

   void finishRender(Tessellator p_217599_1_);
}