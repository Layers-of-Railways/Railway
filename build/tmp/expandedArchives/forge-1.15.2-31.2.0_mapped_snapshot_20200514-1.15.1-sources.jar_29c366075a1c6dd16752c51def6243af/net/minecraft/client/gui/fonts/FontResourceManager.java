package net.minecraft.client.gui.fonts;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.providers.DefaultGlyphProvider;
import net.minecraft.client.gui.fonts.providers.GlyphProviderTypes;
import net.minecraft.client.gui.fonts.providers.IGlyphProvider;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.EmptyProfiler;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class FontResourceManager implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<ResourceLocation, FontRenderer> fontRenderers = Maps.newHashMap();
   private final TextureManager textureManager;
   private boolean forceUnicodeFont;
   private final IFutureReloadListener reloadListener = new ReloadListener<Map<ResourceLocation, List<IGlyphProvider>>>() {
      /**
       * Performs any reloading that can be done off-thread, such as file IO
       */
      protected Map<ResourceLocation, List<IGlyphProvider>> prepare(IResourceManager resourceManagerIn, IProfiler profilerIn) {
         profilerIn.startTick();
         Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
         Map<ResourceLocation, List<IGlyphProvider>> map = Maps.newHashMap();

         for(ResourceLocation resourcelocation : resourceManagerIn.getAllResourceLocations("font", (p_215274_0_) -> {
            return p_215274_0_.endsWith(".json");
         })) {
            String s = resourcelocation.getPath();
            ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring("font/".length(), s.length() - ".json".length()));
            List<IGlyphProvider> list = map.computeIfAbsent(resourcelocation1, (p_215272_0_) -> {
               return Lists.newArrayList(new DefaultGlyphProvider());
            });
            profilerIn.startSection(resourcelocation1::toString);

            try {
               for(IResource iresource : resourceManagerIn.getAllResources(resourcelocation)) {
                  profilerIn.startSection(iresource::getPackName);

                  try (
                     InputStream inputstream = iresource.getInputStream();
                     Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
                  ) {
                     profilerIn.startSection("reading");
                     JsonArray jsonarray = JSONUtils.getJsonArray(JSONUtils.fromJson(gson, reader, JsonObject.class), "providers");
                     profilerIn.endStartSection("parsing");

                     for(int i = jsonarray.size() - 1; i >= 0; --i) {
                        JsonObject jsonobject = JSONUtils.getJsonObject(jsonarray.get(i), "providers[" + i + "]");

                        try {
                           String s1 = JSONUtils.getString(jsonobject, "type");
                           GlyphProviderTypes glyphprovidertypes = GlyphProviderTypes.byName(s1);
                           if (!FontResourceManager.this.forceUnicodeFont || glyphprovidertypes == GlyphProviderTypes.LEGACY_UNICODE || !resourcelocation1.equals(Minecraft.DEFAULT_FONT_RENDERER_NAME)) {
                              profilerIn.startSection(s1);
                              list.add(glyphprovidertypes.getFactory(jsonobject).create(resourceManagerIn));
                              profilerIn.endSection();
                           }
                        } catch (RuntimeException runtimeexception) {
                           FontResourceManager.LOGGER.warn("Unable to read definition '{}' in fonts.json in resourcepack: '{}': {}", resourcelocation1, iresource.getPackName(), runtimeexception.getMessage());
                        }
                     }

                     profilerIn.endSection();
                  } catch (RuntimeException runtimeexception1) {
                     FontResourceManager.LOGGER.warn("Unable to load font '{}' in fonts.json in resourcepack: '{}': {}", resourcelocation1, iresource.getPackName(), runtimeexception1.getMessage());
                  }

                  profilerIn.endSection();
               }
            } catch (IOException ioexception) {
               FontResourceManager.LOGGER.warn("Unable to load font '{}' in fonts.json: {}", resourcelocation1, ioexception.getMessage());
            }

            profilerIn.startSection("caching");

            for(char c0 = 0; c0 < '\uffff'; ++c0) {
               if (c0 != ' ') {
                  for(IGlyphProvider iglyphprovider : Lists.reverse(list)) {
                     if (iglyphprovider.getGlyphInfo(c0) != null) {
                        break;
                     }
                  }
               }
            }

            profilerIn.endSection();
            profilerIn.endSection();
         }

         profilerIn.endTick();
         return map;
      }

      protected void apply(Map<ResourceLocation, List<IGlyphProvider>> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
         profilerIn.startTick();
         profilerIn.startSection("reloading");
         Stream.concat(FontResourceManager.this.fontRenderers.keySet().stream(), objectIn.keySet().stream()).distinct().forEach((p_215271_2_) -> {
            List<IGlyphProvider> list = objectIn.getOrDefault(p_215271_2_, Collections.emptyList());
            Collections.reverse(list);
            FontResourceManager.this.fontRenderers.computeIfAbsent(p_215271_2_, (p_215273_1_) -> {
               return new FontRenderer(FontResourceManager.this.textureManager, new Font(FontResourceManager.this.textureManager, p_215273_1_));
            }).setGlyphProviders(list);
         });
         profilerIn.endSection();
         profilerIn.endTick();
      }

      public String func_225594_i_() {
         return "FontManager";
      }
   };

   public FontResourceManager(TextureManager textureManagerIn, boolean forceUnicodeFontIn) {
      this.textureManager = textureManagerIn;
      this.forceUnicodeFont = forceUnicodeFontIn;
   }

   @Nullable
   public FontRenderer getFontRenderer(ResourceLocation id) {
      return this.fontRenderers.computeIfAbsent(id, (p_212318_1_) -> {
         FontRenderer fontrenderer = new FontRenderer(this.textureManager, new Font(this.textureManager, p_212318_1_));
         fontrenderer.setGlyphProviders(Lists.newArrayList(new DefaultGlyphProvider()));
         return fontrenderer;
      });
   }

   public void setForceUnicodeFont(boolean p_216883_1_, Executor p_216883_2_, Executor p_216883_3_) {
      if (p_216883_1_ != this.forceUnicodeFont) {
         this.forceUnicodeFont = p_216883_1_;
         IResourceManager iresourcemanager = Minecraft.getInstance().getResourceManager();
         IFutureReloadListener.IStage ifuturereloadlistener$istage = new IFutureReloadListener.IStage() {
            public <T> CompletableFuture<T> markCompleteAwaitingOthers(T backgroundResult) {
               return CompletableFuture.completedFuture(backgroundResult);
            }
         };
         this.reloadListener.reload(ifuturereloadlistener$istage, iresourcemanager, EmptyProfiler.INSTANCE, EmptyProfiler.INSTANCE, p_216883_2_, p_216883_3_);
      }
   }

   public IFutureReloadListener getReloadListener() {
      return this.reloadListener;
   }

   public void close() {
      this.fontRenderers.values().forEach(FontRenderer::close);
   }
}