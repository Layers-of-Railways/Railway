package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class AtlasTexture extends Texture implements ITickable {
   private static final Logger LOGGER = LogManager.getLogger();
   @Deprecated
   public static final ResourceLocation LOCATION_BLOCKS_TEXTURE = PlayerContainer.LOCATION_BLOCKS_TEXTURE;
   @Deprecated
   public static final ResourceLocation LOCATION_PARTICLES_TEXTURE = new ResourceLocation("textures/atlas/particles.png");
   private final List<TextureAtlasSprite> listAnimatedSprites = Lists.newArrayList();
   private final Set<ResourceLocation> sprites = Sets.newHashSet();
   private final Map<ResourceLocation, TextureAtlasSprite> mapUploadedSprites = Maps.newHashMap();
   private final ResourceLocation textureLocation;
   private final int maximumTextureSize;

   public AtlasTexture(ResourceLocation textureLocationIn) {
      this.textureLocation = textureLocationIn;
      this.maximumTextureSize = RenderSystem.maxSupportedTextureSize();
   }

   public void loadTexture(IResourceManager manager) throws IOException {
   }

   public void upload(AtlasTexture.SheetData sheetDataIn) {
      this.sprites.clear();
      this.sprites.addAll(sheetDataIn.spriteLocations);
      LOGGER.info("Created: {}x{}x{} {}-atlas", sheetDataIn.width, sheetDataIn.height, sheetDataIn.mipmapLevel, this.textureLocation);
      TextureUtil.prepareImage(this.getGlTextureId(), sheetDataIn.mipmapLevel, sheetDataIn.width, sheetDataIn.height);
      this.clear();

      for(TextureAtlasSprite textureatlassprite : sheetDataIn.sprites) {
         this.mapUploadedSprites.put(textureatlassprite.getName(), textureatlassprite);

         try {
            textureatlassprite.uploadMipmaps();
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Stitching texture atlas");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Texture being stitched together");
            crashreportcategory.addDetail("Atlas path", this.textureLocation);
            crashreportcategory.addDetail("Sprite", textureatlassprite);
            throw new ReportedException(crashreport);
         }

         if (textureatlassprite.hasAnimationMetadata()) {
            this.listAnimatedSprites.add(textureatlassprite);
         }
      }

      net.minecraftforge.client.ForgeHooksClient.onTextureStitchedPost(this);
   }

   public AtlasTexture.SheetData stitch(IResourceManager resourceManagerIn, Stream<ResourceLocation> resourceLocationsIn, IProfiler profilerIn, int maxMipmapLevelIn) {
      profilerIn.startSection("preparing");
      Set<ResourceLocation> set = resourceLocationsIn.peek((p_229222_0_) -> {
         if (p_229222_0_ == null) {
            throw new IllegalArgumentException("Location cannot be null!");
         }
      }).collect(Collectors.toSet());
      int i = this.maximumTextureSize;
      Stitcher stitcher = new Stitcher(i, i, maxMipmapLevelIn);
      int j = Integer.MAX_VALUE;
      int k = 1 << maxMipmapLevelIn;
      profilerIn.endStartSection("extracting_frames");
      net.minecraftforge.client.ForgeHooksClient.onTextureStitchedPre(this, set);

      for(TextureAtlasSprite.Info textureatlassprite$info : this.makeSprites(resourceManagerIn, set)) {
         j = Math.min(j, Math.min(textureatlassprite$info.getSpriteWidth(), textureatlassprite$info.getSpriteHeight()));
         int l = Math.min(Integer.lowestOneBit(textureatlassprite$info.getSpriteWidth()), Integer.lowestOneBit(textureatlassprite$info.getSpriteHeight()));
         if (l < k) {
            LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", textureatlassprite$info.getSpriteLocation(), textureatlassprite$info.getSpriteWidth(), textureatlassprite$info.getSpriteHeight(), MathHelper.log2(k), MathHelper.log2(l));
            k = l;
         }

         stitcher.addSprite(textureatlassprite$info);
      }

      int i1 = Math.min(j, k);
      int j1 = MathHelper.log2(i1);
      int k1 = maxMipmapLevelIn;
      if (false) // FORGE: do not lower the mipmap level
      if (j1 < maxMipmapLevelIn) {
         LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.textureLocation, maxMipmapLevelIn, j1, i1);
         k1 = j1;
      } else {
         k1 = maxMipmapLevelIn;
      }

      profilerIn.endStartSection("register");
      stitcher.addSprite(MissingTextureSprite.getSpriteInfo());
      profilerIn.endStartSection("stitching");

      try {
         stitcher.doStitch();
      } catch (StitcherException stitcherexception) {
         CrashReport crashreport = CrashReport.makeCrashReport(stitcherexception, "Stitching");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Stitcher");
         crashreportcategory.addDetail("Sprites", stitcherexception.getSpriteInfos().stream().map((p_229216_0_) -> {
            return String.format("%s[%dx%d]", p_229216_0_.getSpriteLocation(), p_229216_0_.getSpriteWidth(), p_229216_0_.getSpriteHeight());
         }).collect(Collectors.joining(",")));
         crashreportcategory.addDetail("Max Texture Size", i);
         throw new ReportedException(crashreport);
      }

      profilerIn.endStartSection("loading");
      List<TextureAtlasSprite> list = this.getStitchedSprites(resourceManagerIn, stitcher, k1);
      profilerIn.endSection();
      return new AtlasTexture.SheetData(set, stitcher.getCurrentWidth(), stitcher.getCurrentHeight(), k1, list);
   }

   private Collection<TextureAtlasSprite.Info> makeSprites(IResourceManager resourceManagerIn, Set<ResourceLocation> spriteLocationsIn) {
      List<CompletableFuture<?>> list = Lists.newArrayList();
      ConcurrentLinkedQueue<TextureAtlasSprite.Info> concurrentlinkedqueue = new ConcurrentLinkedQueue<>();

      for(ResourceLocation resourcelocation : spriteLocationsIn) {
         if (!MissingTextureSprite.getLocation().equals(resourcelocation)) {
            list.add(CompletableFuture.runAsync(() -> {
               ResourceLocation resourcelocation1 = this.getSpritePath(resourcelocation);

               TextureAtlasSprite.Info textureatlassprite$info;
               try (IResource iresource = resourceManagerIn.getResource(resourcelocation1)) {
                  PngSizeInfo pngsizeinfo = new PngSizeInfo(iresource.toString(), iresource.getInputStream());
                  AnimationMetadataSection animationmetadatasection = iresource.getMetadata(AnimationMetadataSection.SERIALIZER);
                  if (animationmetadatasection == null) {
                     animationmetadatasection = AnimationMetadataSection.EMPTY;
                  }

                  Pair<Integer, Integer> pair = animationmetadatasection.getSpriteSize(pngsizeinfo.width, pngsizeinfo.height);
                  textureatlassprite$info = new TextureAtlasSprite.Info(resourcelocation, pair.getFirst(), pair.getSecond(), animationmetadatasection);
               } catch (RuntimeException runtimeexception) {
                  LOGGER.error("Unable to parse metadata from {} : {}", resourcelocation1, runtimeexception);
                  return;
               } catch (IOException ioexception) {
                  LOGGER.error("Using missing texture, unable to load {} : {}", resourcelocation1, ioexception);
                  return;
               }

               concurrentlinkedqueue.add(textureatlassprite$info);
            }, Util.getServerExecutor()));
         }
      }

      CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
      return concurrentlinkedqueue;
   }

   private List<TextureAtlasSprite> getStitchedSprites(IResourceManager resourceManagerIn, Stitcher stitcherIn, int mipmapLevelIn) {
      ConcurrentLinkedQueue<TextureAtlasSprite> concurrentlinkedqueue = new ConcurrentLinkedQueue<>();
      List<CompletableFuture<?>> list = Lists.newArrayList();
      stitcherIn.getStichSlots((p_229215_5_, p_229215_6_, p_229215_7_, p_229215_8_, p_229215_9_) -> {
         if (p_229215_5_ == MissingTextureSprite.getSpriteInfo()) {
            MissingTextureSprite missingtexturesprite = MissingTextureSprite.create(this, mipmapLevelIn, p_229215_6_, p_229215_7_, p_229215_8_, p_229215_9_);
            concurrentlinkedqueue.add(missingtexturesprite);
         } else {
            list.add(CompletableFuture.runAsync(() -> {
               TextureAtlasSprite textureatlassprite = this.loadSprite(resourceManagerIn, p_229215_5_, p_229215_6_, p_229215_7_, mipmapLevelIn, p_229215_8_, p_229215_9_);
               if (textureatlassprite != null) {
                  concurrentlinkedqueue.add(textureatlassprite);
               }

            }, Util.getServerExecutor()));
         }

      });
      CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
      return Lists.newArrayList(concurrentlinkedqueue);
   }

   @Nullable
   private TextureAtlasSprite loadSprite(IResourceManager resourceManagerIn, TextureAtlasSprite.Info spriteInfoIn, int widthIn, int heightIn, int mipmapLevelIn, int originX, int originY) {
      ResourceLocation resourcelocation = this.getSpritePath(spriteInfoIn.getSpriteLocation());

      try (IResource iresource = resourceManagerIn.getResource(resourcelocation)) {
         NativeImage nativeimage = NativeImage.read(iresource.getInputStream());
         TextureAtlasSprite textureatlassprite = new TextureAtlasSprite(this, spriteInfoIn, mipmapLevelIn, widthIn, heightIn, originX, originY, nativeimage);
         return textureatlassprite;
      } catch (RuntimeException runtimeexception) {
         LOGGER.error("Unable to parse metadata from {}", resourcelocation, runtimeexception);
         return null;
      } catch (IOException ioexception) {
         LOGGER.error("Using missing texture, unable to load {}", resourcelocation, ioexception);
         return null;
      }
   }

   private ResourceLocation getSpritePath(ResourceLocation location) {
      return new ResourceLocation(location.getNamespace(), String.format("textures/%s%s", location.getPath(), ".png"));
   }

   public void updateAnimations() {
      this.bindTexture();

      for(TextureAtlasSprite textureatlassprite : this.listAnimatedSprites) {
         textureatlassprite.updateAnimation();
      }

   }

   public void tick() {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(this::updateAnimations);
      } else {
         this.updateAnimations();
      }

   }

   public TextureAtlasSprite getSprite(ResourceLocation location) {
      TextureAtlasSprite textureatlassprite = this.mapUploadedSprites.get(location);
      return textureatlassprite == null ? this.mapUploadedSprites.get(MissingTextureSprite.getLocation()) : textureatlassprite;
   }

   public void clear() {
      for(TextureAtlasSprite textureatlassprite : this.mapUploadedSprites.values()) {
         textureatlassprite.close();
      }

      this.mapUploadedSprites.clear();
      this.listAnimatedSprites.clear();
   }

   public ResourceLocation getTextureLocation() {
      return this.textureLocation;
   }

   public void setBlurMipmap(AtlasTexture.SheetData sheetDataIn) {
      this.setBlurMipmapDirect(false, sheetDataIn.mipmapLevel > 0);
   }

   @OnlyIn(Dist.CLIENT)
   public static class SheetData {
      final Set<ResourceLocation> spriteLocations;
      final int width;
      final int height;
      final int mipmapLevel;
      final List<TextureAtlasSprite> sprites;

      public SheetData(Set<ResourceLocation> spriteLocationsIn, int widthIn, int heightIn, int mipmapLevelIn, List<TextureAtlasSprite> spritesIn) {
         this.spriteLocations = spriteLocationsIn;
         this.width = widthIn;
         this.height = heightIn;
         this.mipmapLevel = mipmapLevelIn;
         this.sprites = spritesIn;
      }
   }
}