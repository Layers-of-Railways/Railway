package com.mojang.realmsclient.util;

import com.google.common.collect.Maps;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.util.UUIDTypeAdapter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsTextureManager {
   private static final Map<String, RealmsTextureManager.RealmsTexture> field_225209_a = Maps.newHashMap();
   private static final Map<String, Boolean> field_225210_b = Maps.newHashMap();
   private static final Map<String, String> field_225211_c = Maps.newHashMap();
   private static final Logger field_225212_d = LogManager.getLogger();

   public static void func_225202_a(String p_225202_0_, String p_225202_1_) {
      if (p_225202_1_ == null) {
         RealmsScreen.bind("textures/gui/presets/isles.png");
      } else {
         int i = func_225203_b(p_225202_0_, p_225202_1_);
         RenderSystem.bindTexture(i);
      }
   }

   public static void func_225205_a(String p_225205_0_, Runnable p_225205_1_) {
      RenderSystem.pushTextureAttributes();

      try {
         func_225200_a(p_225205_0_);
         p_225205_1_.run();
      } finally {
         RenderSystem.popAttributes();
      }

   }

   private static void func_225204_a(UUID p_225204_0_) {
      RealmsScreen.bind((p_225204_0_.hashCode() & 1) == 1 ? "minecraft:textures/entity/alex.png" : "minecraft:textures/entity/steve.png");
   }

   private static void func_225200_a(final String p_225200_0_) {
      UUID uuid = UUIDTypeAdapter.fromString(p_225200_0_);
      if (field_225209_a.containsKey(p_225200_0_)) {
         RenderSystem.bindTexture((field_225209_a.get(p_225200_0_)).field_225198_b);
      } else if (field_225210_b.containsKey(p_225200_0_)) {
         if (!field_225210_b.get(p_225200_0_)) {
            func_225204_a(uuid);
         } else if (field_225211_c.containsKey(p_225200_0_)) {
            int i = func_225203_b(p_225200_0_, field_225211_c.get(p_225200_0_));
            RenderSystem.bindTexture(i);
         } else {
            func_225204_a(uuid);
         }

      } else {
         field_225210_b.put(p_225200_0_, false);
         func_225204_a(uuid);
         Thread thread = new Thread("Realms Texture Downloader") {
            public void run() {
               Map<Type, MinecraftProfileTexture> map = RealmsUtil.func_225191_b(p_225200_0_);
               if (map.containsKey(Type.SKIN)) {
                  MinecraftProfileTexture minecraftprofiletexture = map.get(Type.SKIN);
                  String s = minecraftprofiletexture.getUrl();
                  HttpURLConnection httpurlconnection = null;
                  RealmsTextureManager.field_225212_d.debug("Downloading http texture from {}", (Object)s);

                  try {
                     httpurlconnection = (HttpURLConnection)(new URL(s)).openConnection(Realms.getProxy());
                     httpurlconnection.setDoInput(true);
                     httpurlconnection.setDoOutput(false);
                     httpurlconnection.connect();
                     if (httpurlconnection.getResponseCode() / 100 == 2) {
                        BufferedImage bufferedimage;
                        try {
                           bufferedimage = ImageIO.read(httpurlconnection.getInputStream());
                        } catch (Exception var17) {
                           RealmsTextureManager.field_225210_b.remove(p_225200_0_);
                           return;
                        } finally {
                           IOUtils.closeQuietly(httpurlconnection.getInputStream());
                        }

                        bufferedimage = (new SkinProcessor()).func_225228_a(bufferedimage);
                        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                        ImageIO.write(bufferedimage, "png", bytearrayoutputstream);
                        RealmsTextureManager.field_225211_c.put(p_225200_0_, (new Base64()).encodeToString(bytearrayoutputstream.toByteArray()));
                        RealmsTextureManager.field_225210_b.put(p_225200_0_, true);
                        return;
                     }

                     RealmsTextureManager.field_225210_b.remove(p_225200_0_);
                  } catch (Exception exception) {
                     RealmsTextureManager.field_225212_d.error("Couldn't download http texture", (Throwable)exception);
                     RealmsTextureManager.field_225210_b.remove(p_225200_0_);
                     return;
                  } finally {
                     if (httpurlconnection != null) {
                        httpurlconnection.disconnect();
                     }

                  }

               } else {
                  RealmsTextureManager.field_225210_b.put(p_225200_0_, true);
               }
            }
         };
         thread.setDaemon(true);
         thread.start();
      }
   }

   private static int func_225203_b(String p_225203_0_, String p_225203_1_) {
      int i;
      if (field_225209_a.containsKey(p_225203_0_)) {
         RealmsTextureManager.RealmsTexture realmstexturemanager$realmstexture = field_225209_a.get(p_225203_0_);
         if (realmstexturemanager$realmstexture.field_225197_a.equals(p_225203_1_)) {
            return realmstexturemanager$realmstexture.field_225198_b;
         }

         RenderSystem.deleteTexture(realmstexturemanager$realmstexture.field_225198_b);
         i = realmstexturemanager$realmstexture.field_225198_b;
      } else {
         i = GlStateManager.genTexture();
      }

      IntBuffer intbuffer = null;
      int j = 0;
      int k = 0;

      try {
         InputStream inputstream = new ByteArrayInputStream((new Base64()).decode(p_225203_1_));

         BufferedImage bufferedimage;
         try {
            bufferedimage = ImageIO.read(inputstream);
         } finally {
            IOUtils.closeQuietly(inputstream);
         }

         j = bufferedimage.getWidth();
         k = bufferedimage.getHeight();
         int[] lvt_8_1_ = new int[j * k];
         bufferedimage.getRGB(0, 0, j, k, lvt_8_1_, 0, j);
         intbuffer = ByteBuffer.allocateDirect(4 * j * k).order(ByteOrder.nativeOrder()).asIntBuffer();
         intbuffer.put(lvt_8_1_);
         ((Buffer)intbuffer).flip();
      } catch (IOException ioexception) {
         ioexception.printStackTrace();
      }

      RenderSystem.activeTexture(33984);
      RenderSystem.bindTexture(i);
      TextureUtil.initTexture(intbuffer, j, k);
      field_225209_a.put(p_225203_0_, new RealmsTextureManager.RealmsTexture(p_225203_1_, i));
      return i;
   }

   @OnlyIn(Dist.CLIENT)
   public static class RealmsTexture {
      String field_225197_a;
      int field_225198_b;

      public RealmsTexture(String p_i51693_1_, int p_i51693_2_) {
         this.field_225197_a = p_i51693_1_;
         this.field_225198_b = p_i51693_2_;
      }
   }
}