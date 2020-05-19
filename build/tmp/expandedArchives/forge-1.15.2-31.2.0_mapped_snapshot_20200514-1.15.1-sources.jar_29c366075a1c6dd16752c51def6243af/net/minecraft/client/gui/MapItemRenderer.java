package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MapItemRenderer implements AutoCloseable {
   private static final ResourceLocation TEXTURE_MAP_ICONS = new ResourceLocation("textures/map/map_icons.png");
   private static final RenderType field_228085_d_ = RenderType.getText(TEXTURE_MAP_ICONS);
   private final TextureManager textureManager;
   private final Map<String, MapItemRenderer.Instance> loadedMaps = Maps.newHashMap();

   public MapItemRenderer(TextureManager textureManagerIn) {
      this.textureManager = textureManagerIn;
   }

   /**
    * Updates a map texture
    */
   public void updateMapTexture(MapData mapdataIn) {
      this.getMapRendererInstance(mapdataIn).updateMapTexture();
   }

   public void renderMap(MatrixStack p_228086_1_, IRenderTypeBuffer p_228086_2_, MapData p_228086_3_, boolean p_228086_4_, int p_228086_5_) {
      this.getMapRendererInstance(p_228086_3_).func_228089_a_(p_228086_1_, p_228086_2_, p_228086_4_, p_228086_5_);
   }

   /**
    * Returns {@link net.minecraft.client.gui.MapItemRenderer.Instance MapItemRenderer.Instance} with given map data
    */
   private MapItemRenderer.Instance getMapRendererInstance(MapData mapdataIn) {
      MapItemRenderer.Instance mapitemrenderer$instance = this.loadedMaps.get(mapdataIn.getName());
      if (mapitemrenderer$instance == null) {
         mapitemrenderer$instance = new MapItemRenderer.Instance(mapdataIn);
         this.loadedMaps.put(mapdataIn.getName(), mapitemrenderer$instance);
      }

      return mapitemrenderer$instance;
   }

   @Nullable
   public MapItemRenderer.Instance getMapInstanceIfExists(String p_191205_1_) {
      return this.loadedMaps.get(p_191205_1_);
   }

   /**
    * Clears the currently loaded maps and removes their corresponding textures
    */
   public void clearLoadedMaps() {
      for(MapItemRenderer.Instance mapitemrenderer$instance : this.loadedMaps.values()) {
         mapitemrenderer$instance.close();
      }

      this.loadedMaps.clear();
   }

   @Nullable
   public MapData getData(@Nullable MapItemRenderer.Instance p_191207_1_) {
      return p_191207_1_ != null ? p_191207_1_.mapData : null;
   }

   public void close() {
      this.clearLoadedMaps();
   }

   @OnlyIn(Dist.CLIENT)
   class Instance implements AutoCloseable {
      private final MapData mapData;
      private final DynamicTexture mapTexture;
      private final RenderType field_228088_d_;

      private Instance(MapData mapdataIn) {
         this.mapData = mapdataIn;
         this.mapTexture = new DynamicTexture(128, 128, true);
         ResourceLocation resourcelocation = MapItemRenderer.this.textureManager.getDynamicTextureLocation("map/" + mapdataIn.getName(), this.mapTexture);
         this.field_228088_d_ = RenderType.getText(resourcelocation);
      }

      /**
       * Updates a map {@link net.minecraft.client.gui.MapItemRenderer.Instance#mapTexture texture}
       */
      private void updateMapTexture() {
         for(int i = 0; i < 128; ++i) {
            for(int j = 0; j < 128; ++j) {
               int k = j + i * 128;
               int l = this.mapData.colors[k] & 255;
               if (l / 4 == 0) {
                  this.mapTexture.getTextureData().setPixelRGBA(j, i, 0);
               } else {
                  this.mapTexture.getTextureData().setPixelRGBA(j, i, MaterialColor.COLORS[l / 4].getMapColor(l & 3));
               }
            }
         }

         this.mapTexture.updateDynamicTexture();
      }

      private void func_228089_a_(MatrixStack p_228089_1_, IRenderTypeBuffer p_228089_2_, boolean p_228089_3_, int p_228089_4_) {
         int i = 0;
         int j = 0;
         float f = 0.0F;
         Matrix4f matrix4f = p_228089_1_.getLast().getMatrix();
         IVertexBuilder ivertexbuilder = p_228089_2_.getBuffer(this.field_228088_d_);
         ivertexbuilder.pos(matrix4f, 0.0F, 128.0F, -0.01F).color(255, 255, 255, 255).tex(0.0F, 1.0F).lightmap(p_228089_4_).endVertex();
         ivertexbuilder.pos(matrix4f, 128.0F, 128.0F, -0.01F).color(255, 255, 255, 255).tex(1.0F, 1.0F).lightmap(p_228089_4_).endVertex();
         ivertexbuilder.pos(matrix4f, 128.0F, 0.0F, -0.01F).color(255, 255, 255, 255).tex(1.0F, 0.0F).lightmap(p_228089_4_).endVertex();
         ivertexbuilder.pos(matrix4f, 0.0F, 0.0F, -0.01F).color(255, 255, 255, 255).tex(0.0F, 0.0F).lightmap(p_228089_4_).endVertex();
         int k = 0;

         for(MapDecoration mapdecoration : this.mapData.mapDecorations.values()) {
            if (!p_228089_3_ || mapdecoration.renderOnFrame()) {
               if (mapdecoration.render(k)) { k++; continue; }
               p_228089_1_.push();
               p_228089_1_.translate((double)(0.0F + (float)mapdecoration.getX() / 2.0F + 64.0F), (double)(0.0F + (float)mapdecoration.getY() / 2.0F + 64.0F), (double)-0.02F);
               p_228089_1_.rotate(Vector3f.ZP.rotationDegrees((float)(mapdecoration.getRotation() * 360) / 16.0F));
               p_228089_1_.scale(4.0F, 4.0F, 3.0F);
               p_228089_1_.translate(-0.125D, 0.125D, 0.0D);
               byte b0 = mapdecoration.getImage();
               float f1 = (float)(b0 % 16 + 0) / 16.0F;
               float f2 = (float)(b0 / 16 + 0) / 16.0F;
               float f3 = (float)(b0 % 16 + 1) / 16.0F;
               float f4 = (float)(b0 / 16 + 1) / 16.0F;
               Matrix4f matrix4f1 = p_228089_1_.getLast().getMatrix();
               float f5 = -0.001F;
               IVertexBuilder ivertexbuilder1 = p_228089_2_.getBuffer(MapItemRenderer.field_228085_d_);
               ivertexbuilder1.pos(matrix4f1, -1.0F, 1.0F, (float)k * -0.001F).color(255, 255, 255, 255).tex(f1, f2).lightmap(p_228089_4_).endVertex();
               ivertexbuilder1.pos(matrix4f1, 1.0F, 1.0F, (float)k * -0.001F).color(255, 255, 255, 255).tex(f3, f2).lightmap(p_228089_4_).endVertex();
               ivertexbuilder1.pos(matrix4f1, 1.0F, -1.0F, (float)k * -0.001F).color(255, 255, 255, 255).tex(f3, f4).lightmap(p_228089_4_).endVertex();
               ivertexbuilder1.pos(matrix4f1, -1.0F, -1.0F, (float)k * -0.001F).color(255, 255, 255, 255).tex(f1, f4).lightmap(p_228089_4_).endVertex();
               p_228089_1_.pop();
               if (mapdecoration.getCustomName() != null) {
                  FontRenderer fontrenderer = Minecraft.getInstance().fontRenderer;
                  String s = mapdecoration.getCustomName().getFormattedText();
                  float f6 = (float)fontrenderer.getStringWidth(s);
                  float f7 = MathHelper.clamp(25.0F / f6, 0.0F, 6.0F / 9.0F);
                  p_228089_1_.push();
                  p_228089_1_.translate((double)(0.0F + (float)mapdecoration.getX() / 2.0F + 64.0F - f6 * f7 / 2.0F), (double)(0.0F + (float)mapdecoration.getY() / 2.0F + 64.0F + 4.0F), (double)-0.025F);
                  p_228089_1_.scale(f7, f7, 1.0F);
                  p_228089_1_.translate(0.0D, 0.0D, (double)-0.1F);
                  fontrenderer.renderString(s, 0.0F, 0.0F, -1, false, p_228089_1_.getLast().getMatrix(), p_228089_2_, false, Integer.MIN_VALUE, p_228089_4_);
                  p_228089_1_.pop();
               }

               ++k;
            }
         }

      }

      public void close() {
         this.mapTexture.close();
      }
   }
}