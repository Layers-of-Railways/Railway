package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.Closeable;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SimpleTexture extends Texture {
   private static final Logger LOGGER = LogManager.getLogger();
   protected final ResourceLocation textureLocation;

   public SimpleTexture(ResourceLocation textureResourceLocation) {
      this.textureLocation = textureResourceLocation;
   }

   public void loadTexture(IResourceManager manager) throws IOException {
      SimpleTexture.TextureData simpletexture$texturedata = this.getTextureData(manager);
      simpletexture$texturedata.checkException();
      TextureMetadataSection texturemetadatasection = simpletexture$texturedata.getMetadata();
      boolean flag;
      boolean flag1;
      if (texturemetadatasection != null) {
         flag = texturemetadatasection.getTextureBlur();
         flag1 = texturemetadatasection.getTextureClamp();
      } else {
         flag = false;
         flag1 = false;
      }

      NativeImage nativeimage = simpletexture$texturedata.getNativeImage();
      if (!RenderSystem.isOnRenderThreadOrInit()) {
         RenderSystem.recordRenderCall(() -> {
            this.loadImage(nativeimage, flag, flag1);
         });
      } else {
         this.loadImage(nativeimage, flag, flag1);
      }

   }

   private void loadImage(NativeImage imageIn, boolean blurIn, boolean clampIn) {
      TextureUtil.prepareImage(this.getGlTextureId(), 0, imageIn.getWidth(), imageIn.getHeight());
      imageIn.uploadTextureSub(0, 0, 0, 0, 0, imageIn.getWidth(), imageIn.getHeight(), blurIn, clampIn, false, true);
   }

   protected SimpleTexture.TextureData getTextureData(IResourceManager resourceManager) {
      return SimpleTexture.TextureData.getTextureData(resourceManager, this.textureLocation);
   }

   @OnlyIn(Dist.CLIENT)
   public static class TextureData implements Closeable {
      @Nullable
      private final TextureMetadataSection metadata;
      @Nullable
      private final NativeImage nativeImage;
      @Nullable
      private final IOException exception;

      public TextureData(IOException exceptionIn) {
         this.exception = exceptionIn;
         this.metadata = null;
         this.nativeImage = null;
      }

      public TextureData(@Nullable TextureMetadataSection metadataIn, NativeImage imageIn) {
         this.exception = null;
         this.metadata = metadataIn;
         this.nativeImage = imageIn;
      }

      public static SimpleTexture.TextureData getTextureData(IResourceManager resourceManagerIn, ResourceLocation locationIn) {
         try (IResource iresource = resourceManagerIn.getResource(locationIn)) {
            NativeImage nativeimage = NativeImage.read(iresource.getInputStream());
            TextureMetadataSection texturemetadatasection = null;

            try {
               texturemetadatasection = iresource.getMetadata(TextureMetadataSection.SERIALIZER);
            } catch (RuntimeException runtimeexception) {
               SimpleTexture.LOGGER.warn("Failed reading metadata of: {}", locationIn, runtimeexception);
            }

            SimpleTexture.TextureData lvt_6_1_ = new SimpleTexture.TextureData(texturemetadatasection, nativeimage);
            return lvt_6_1_;
         } catch (IOException ioexception) {
            return new SimpleTexture.TextureData(ioexception);
         }
      }

      @Nullable
      public TextureMetadataSection getMetadata() {
         return this.metadata;
      }

      public NativeImage getNativeImage() throws IOException {
         if (this.exception != null) {
            throw this.exception;
         } else {
            return this.nativeImage;
         }
      }

      public void close() {
         if (this.nativeImage != null) {
            this.nativeImage.close();
         }

      }

      public void checkException() throws IOException {
         if (this.exception != null) {
            throw this.exception;
         }
      }
   }
}