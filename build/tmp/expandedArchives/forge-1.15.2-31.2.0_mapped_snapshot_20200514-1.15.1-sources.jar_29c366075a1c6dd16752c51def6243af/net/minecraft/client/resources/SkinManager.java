package net.minecraft.client.resources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DownloadingTexture;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkinManager {
   private final TextureManager textureManager;
   private final File skinCacheDir;
   private final MinecraftSessionService sessionService;
   private final LoadingCache<GameProfile, Map<Type, MinecraftProfileTexture>> skinCacheLoader;

   public SkinManager(TextureManager textureManagerInstance, File skinCacheDirectory, MinecraftSessionService sessionService) {
      this.textureManager = textureManagerInstance;
      this.skinCacheDir = skinCacheDirectory;
      this.sessionService = sessionService;
      this.skinCacheLoader = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build(new CacheLoader<GameProfile, Map<Type, MinecraftProfileTexture>>() {
         public Map<Type, MinecraftProfileTexture> load(GameProfile p_load_1_) throws Exception {
            try {
               return Minecraft.getInstance().getSessionService().getTextures(p_load_1_, false);
            } catch (Throwable var3) {
               return Maps.newHashMap();
            }
         }
      });
   }

   /**
    * Used in the Skull renderer to fetch a skin. May download the skin if it's not in the cache
    */
   public ResourceLocation loadSkin(MinecraftProfileTexture profileTexture, Type textureType) {
      return this.loadSkin(profileTexture, textureType, (SkinManager.ISkinAvailableCallback)null);
   }

   /**
    * May download the skin if its not in the cache, can be passed a SkinManager#SkinAvailableCallback for handling
    */
   public ResourceLocation loadSkin(MinecraftProfileTexture profileTexture, Type textureType, @Nullable SkinManager.ISkinAvailableCallback skinAvailableCallback) {
      String s = Hashing.sha1().hashUnencodedChars(profileTexture.getHash()).toString();
      ResourceLocation resourcelocation = new ResourceLocation("skins/" + s);
      Texture texture = this.textureManager.getTexture(resourcelocation);
      if (texture != null) {
         if (skinAvailableCallback != null) {
            skinAvailableCallback.onSkinTextureAvailable(textureType, resourcelocation, profileTexture);
         }
      } else {
         File file1 = new File(this.skinCacheDir, s.length() > 2 ? s.substring(0, 2) : "xx");
         File file2 = new File(file1, s);
         DownloadingTexture downloadingtexture = new DownloadingTexture(file2, profileTexture.getUrl(), DefaultPlayerSkin.getDefaultSkinLegacy(), textureType == Type.SKIN, () -> {
            if (skinAvailableCallback != null) {
               skinAvailableCallback.onSkinTextureAvailable(textureType, resourcelocation, profileTexture);
            }

         });
         this.textureManager.loadTexture(resourcelocation, downloadingtexture);
      }

      return resourcelocation;
   }

   public void loadProfileTextures(GameProfile profile, SkinManager.ISkinAvailableCallback skinAvailableCallback, boolean requireSecure) {
      Runnable runnable = () -> {
         Map<Type, MinecraftProfileTexture> map = Maps.newHashMap();

         try {
            map.putAll(this.sessionService.getTextures(profile, requireSecure));
         } catch (InsecureTextureException var7) {
            ;
         }

         if (map.isEmpty()) {
            profile.getProperties().clear();
            if (profile.getId().equals(Minecraft.getInstance().getSession().getProfile().getId())) {
               profile.getProperties().putAll(Minecraft.getInstance().getProfileProperties());
               map.putAll(this.sessionService.getTextures(profile, false));
            } else {
               this.sessionService.fillProfileProperties(profile, requireSecure);

               try {
                  map.putAll(this.sessionService.getTextures(profile, requireSecure));
               } catch (InsecureTextureException var6) {
                  ;
               }
            }
         }

         Minecraft.getInstance().execute(() -> {
            RenderSystem.recordRenderCall(() -> {
               ImmutableList.of(Type.SKIN, Type.CAPE).forEach((p_229296_3_) -> {
                  if (map.containsKey(p_229296_3_)) {
                     this.loadSkin(map.get(p_229296_3_), p_229296_3_, skinAvailableCallback);
                  }

               });
            });
         });
      };
      Util.getServerExecutor().execute(runnable);
   }

   public Map<Type, MinecraftProfileTexture> loadSkinFromCache(GameProfile profile) {
      return this.skinCacheLoader.getUnchecked(profile);
   }

   @OnlyIn(Dist.CLIENT)
   public interface ISkinAvailableCallback {
      void onSkinTextureAvailable(Type p_onSkinTextureAvailable_1_, ResourceLocation p_onSkinTextureAvailable_2_, MinecraftProfileTexture p_onSkinTextureAvailable_3_);
   }
}