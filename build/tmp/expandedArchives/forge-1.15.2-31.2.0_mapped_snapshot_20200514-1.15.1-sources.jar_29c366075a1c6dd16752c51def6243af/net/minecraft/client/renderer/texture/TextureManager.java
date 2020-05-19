package net.minecraft.client.renderer.texture;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class TextureManager implements ITickable, AutoCloseable, IFutureReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ResourceLocation RESOURCE_LOCATION_EMPTY = new ResourceLocation("");
   private final Map<ResourceLocation, Texture> mapTextureObjects = Maps.newHashMap();
   private final Set<ITickable> listTickables = Sets.newHashSet();
   private final Map<String, Integer> mapTextureCounters = Maps.newHashMap();
   private final IResourceManager resourceManager;

   public TextureManager(IResourceManager resourceManager) {
      this.resourceManager = resourceManager;
   }

   public void bindTexture(ResourceLocation resource) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            this.bindTextureRaw(resource);
         });
      } else {
         this.bindTextureRaw(resource);
      }

   }

   private void bindTextureRaw(ResourceLocation resource) {
      Texture texture = this.mapTextureObjects.get(resource);
      if (texture == null) {
         texture = new SimpleTexture(resource);
         this.loadTexture(resource, texture);
      }

      texture.bindTexture();
   }

   public void loadTexture(ResourceLocation textureLocation, Texture textureObj) {
      textureObj = this.func_230183_b_(textureLocation, textureObj);
      Texture texture = this.mapTextureObjects.put(textureLocation, textureObj);
      if (texture != textureObj) {
         if (texture != null && texture != MissingTextureSprite.getDynamicTexture()) {
            texture.deleteGlTexture();
            this.listTickables.remove(texture);
         }

         if (textureObj instanceof ITickable) {
            this.listTickables.add((ITickable)textureObj);
         }
      }

   }

   private Texture func_230183_b_(ResourceLocation p_230183_1_, Texture p_230183_2_) {
      try {
         p_230183_2_.loadTexture(this.resourceManager);
         return p_230183_2_;
      } catch (IOException ioexception) {
         if (p_230183_1_ != RESOURCE_LOCATION_EMPTY) {
            LOGGER.warn("Failed to load texture: {}", p_230183_1_, ioexception);
         }

         return MissingTextureSprite.getDynamicTexture();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Registering texture");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Resource location being registered");
         crashreportcategory.addDetail("Resource location", p_230183_1_);
         crashreportcategory.addDetail("Texture object class", () -> {
            return p_230183_2_.getClass().getName();
         });
         throw new ReportedException(crashreport);
      }
   }

   @Nullable
   public Texture getTexture(ResourceLocation textureLocation) {
      return this.mapTextureObjects.get(textureLocation);
   }

   public ResourceLocation getDynamicTextureLocation(String name, DynamicTexture texture) {
      Integer integer = this.mapTextureCounters.get(name);
      if (integer == null) {
         integer = 1;
      } else {
         integer = integer + 1;
      }

      this.mapTextureCounters.put(name, integer);
      ResourceLocation resourcelocation = new ResourceLocation(String.format("dynamic/%s_%d", name, integer));
      this.loadTexture(resourcelocation, texture);
      return resourcelocation;
   }

   public CompletableFuture<Void> loadAsync(ResourceLocation textureLocation, Executor executor) {
      if (!this.mapTextureObjects.containsKey(textureLocation)) {
         PreloadedTexture preloadedtexture = new PreloadedTexture(this.resourceManager, textureLocation, executor);
         this.mapTextureObjects.put(textureLocation, preloadedtexture);
         return preloadedtexture.getCompletableFuture().thenRunAsync(() -> {
            this.loadTexture(textureLocation, preloadedtexture);
         }, TextureManager::execute);
      } else {
         return CompletableFuture.completedFuture((Void)null);
      }
   }

   private static void execute(Runnable runnableIn) {
      Minecraft.getInstance().execute(() -> {
         RenderSystem.recordRenderCall(runnableIn::run);
      });
   }

   public void tick() {
      for(ITickable itickable : this.listTickables) {
         itickable.tick();
      }

   }

   public void deleteTexture(ResourceLocation textureLocation) {
      Texture texture = this.getTexture(textureLocation);
      if (texture != null) {
         this.mapTextureObjects.remove(textureLocation); // Forge: fix MC-98707
         TextureUtil.releaseTextureId(texture.getGlTextureId());
      }

   }

   public void close() {
      this.mapTextureObjects.values().forEach(Texture::deleteGlTexture);
      this.mapTextureObjects.clear();
      this.listTickables.clear();
      this.mapTextureCounters.clear();
   }

   public CompletableFuture<Void> reload(IFutureReloadListener.IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
      return CompletableFuture.allOf(MainMenuScreen.loadAsync(this, backgroundExecutor), this.loadAsync(Widget.WIDGETS_LOCATION, backgroundExecutor)).<Void>thenCompose(stage::markCompleteAwaitingOthers).thenAcceptAsync((p_229265_3_) -> {
         MissingTextureSprite.getDynamicTexture();
         RealmsMainScreen.func_227932_a_(this.resourceManager);
         Iterator<Entry<ResourceLocation, Texture>> iterator = this.mapTextureObjects.entrySet().iterator();

         while(iterator.hasNext()) {
            Entry<ResourceLocation, Texture> entry = iterator.next();
            ResourceLocation resourcelocation = entry.getKey();
            Texture texture = entry.getValue();
            if (texture == MissingTextureSprite.getDynamicTexture() && !resourcelocation.equals(MissingTextureSprite.getLocation())) {
               iterator.remove();
            } else {
               texture.loadTexture(this, resourceManager, resourcelocation, gameExecutor);
            }
         }

      }, (p_229266_0_) -> {
         RenderSystem.recordRenderCall(p_229266_0_::run);
      });
   }
}