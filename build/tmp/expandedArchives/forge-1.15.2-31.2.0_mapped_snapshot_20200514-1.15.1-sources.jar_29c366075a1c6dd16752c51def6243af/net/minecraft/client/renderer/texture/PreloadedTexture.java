package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PreloadedTexture extends SimpleTexture {
   @Nullable
   private CompletableFuture<SimpleTexture.TextureData> textureDataFuture;

   public PreloadedTexture(IResourceManager resourceManagerIn, ResourceLocation locationIn, Executor executorIn) {
      super(locationIn);
      this.textureDataFuture = CompletableFuture.supplyAsync(() -> {
         return SimpleTexture.TextureData.getTextureData(resourceManagerIn, locationIn);
      }, executorIn);
   }

   protected SimpleTexture.TextureData getTextureData(IResourceManager resourceManager) {
      if (this.textureDataFuture != null) {
         SimpleTexture.TextureData simpletexture$texturedata = this.textureDataFuture.join();
         this.textureDataFuture = null;
         return simpletexture$texturedata;
      } else {
         return SimpleTexture.TextureData.getTextureData(resourceManager, this.textureLocation);
      }
   }

   public CompletableFuture<Void> getCompletableFuture() {
      return this.textureDataFuture == null ? CompletableFuture.completedFuture((Void)null) : this.textureDataFuture.thenApply((p_215247_0_) -> {
         return null;
      });
   }

   public void loadTexture(TextureManager textureManagerIn, IResourceManager resourceManagerIn, ResourceLocation resourceLocationIn, Executor executorIn) {
      this.textureDataFuture = CompletableFuture.supplyAsync(() -> {
         return SimpleTexture.TextureData.getTextureData(resourceManagerIn, this.textureLocation);
      }, Util.getServerExecutor());
      this.textureDataFuture.thenRunAsync(() -> {
         textureManagerIn.loadTexture(this.textureLocation, this);
      }, getExecutor(executorIn));
   }

   private static Executor getExecutor(Executor executorIn) {
      return (p_229206_1_) -> {
         executorIn.execute(() -> {
            RenderSystem.recordRenderCall(p_229206_1_::run);
         });
      };
   }
}