package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class MissingTextureSprite extends TextureAtlasSprite {
   private static final ResourceLocation LOCATION = new ResourceLocation("missingno");
   @Nullable
   private static DynamicTexture dynamicTexture;
   private static final LazyValue<NativeImage> IMAGE = new LazyValue<>(() -> {
      NativeImage nativeimage = new NativeImage(16, 16, false);
      int i = -16777216;
      int j = -524040;

      for(int k = 0; k < 16; ++k) {
         for(int l = 0; l < 16; ++l) {
            if (k < 8 ^ l < 8) {
               nativeimage.setPixelRGBA(l, k, -524040);
            } else {
               nativeimage.setPixelRGBA(l, k, -16777216);
            }
         }
      }

      nativeimage.untrack();
      return nativeimage;
   });
   private static final TextureAtlasSprite.Info spriteInfo = new TextureAtlasSprite.Info(LOCATION, 16, 16, new AnimationMetadataSection(Lists.newArrayList(new AnimationFrame(0, -1)), 16, 16, 1, false));

   private MissingTextureSprite(AtlasTexture atlasTextureIn, int mipmapLevelIn, int atlasWidthIn, int atlasHeightIn, int xIn, int yIn) {
      super(atlasTextureIn, spriteInfo, mipmapLevelIn, atlasWidthIn, atlasHeightIn, xIn, yIn, IMAGE.getValue());
   }

   public static MissingTextureSprite create(AtlasTexture atlasTextureIn, int mipmapLevelIn, int atlasWidthIn, int atlasHeightIn, int xIn, int yIn) {
      return new MissingTextureSprite(atlasTextureIn, mipmapLevelIn, atlasWidthIn, atlasHeightIn, xIn, yIn);
   }

   public static ResourceLocation getLocation() {
      return LOCATION;
   }

   public static TextureAtlasSprite.Info getSpriteInfo() {
      return spriteInfo;
   }

   public void close() {
      for(int i = 1; i < this.frames.length; ++i) {
         this.frames[i].close();
      }

   }

   public static DynamicTexture getDynamicTexture() {
      if (dynamicTexture == null) {
         dynamicTexture = new DynamicTexture(IMAGE.getValue());
         Minecraft.getInstance().getTextureManager().loadTexture(LOCATION, dynamicTexture);
      }

      return dynamicTexture;
   }
}