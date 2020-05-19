package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LightTexture implements AutoCloseable {
   private final DynamicTexture dynamicTexture;
   private final NativeImage nativeImage;
   private final ResourceLocation resourceLocation;
   private boolean needsUpdate;
   private float torchFlicker;
   private final GameRenderer entityRenderer;
   private final Minecraft client;

   public LightTexture(GameRenderer entityRendererIn, Minecraft mcIn) {
      this.entityRenderer = entityRendererIn;
      this.client = mcIn;
      this.dynamicTexture = new DynamicTexture(16, 16, false);
      this.resourceLocation = this.client.getTextureManager().getDynamicTextureLocation("light_map", this.dynamicTexture);
      this.nativeImage = this.dynamicTexture.getTextureData();

      for(int i = 0; i < 16; ++i) {
         for(int j = 0; j < 16; ++j) {
            this.nativeImage.setPixelRGBA(j, i, -1);
         }
      }

      this.dynamicTexture.updateDynamicTexture();
   }

   public void close() {
      this.dynamicTexture.close();
   }

   public void tick() {
      this.torchFlicker = (float)((double)this.torchFlicker + (Math.random() - Math.random()) * Math.random() * Math.random() * 0.1D);
      this.torchFlicker = (float)((double)this.torchFlicker * 0.9D);
      this.needsUpdate = true;
   }

   public void disableLightmap() {
      RenderSystem.activeTexture(33986);
      RenderSystem.disableTexture();
      RenderSystem.activeTexture(33984);
   }

   public void enableLightmap() {
      RenderSystem.activeTexture(33986);
      RenderSystem.matrixMode(5890);
      RenderSystem.loadIdentity();
      float f = 0.00390625F;
      RenderSystem.scalef(0.00390625F, 0.00390625F, 0.00390625F);
      RenderSystem.translatef(8.0F, 8.0F, 8.0F);
      RenderSystem.matrixMode(5888);
      this.client.getTextureManager().bindTexture(this.resourceLocation);
      RenderSystem.texParameter(3553, 10241, 9729);
      RenderSystem.texParameter(3553, 10240, 9729);
      RenderSystem.texParameter(3553, 10242, 10496);
      RenderSystem.texParameter(3553, 10243, 10496);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableTexture();
      RenderSystem.activeTexture(33984);
   }

   public void updateLightmap(float partialTicks) {
      if (this.needsUpdate) {
         this.needsUpdate = false;
         this.client.getProfiler().startSection("lightTex");
         ClientWorld clientworld = this.client.world;
         if (clientworld != null) {
            float f = clientworld.getSunBrightness(1.0F);
            float f1;
            if (clientworld.getTimeLightningFlash() > 0) {
               f1 = 1.0F;
            } else {
               f1 = f * 0.95F + 0.05F;
            }

            float f3 = this.client.player.getWaterBrightness();
            float f2;
            if (this.client.player.isPotionActive(Effects.NIGHT_VISION)) {
               f2 = GameRenderer.getNightVisionBrightness(this.client.player, partialTicks);
            } else if (f3 > 0.0F && this.client.player.isPotionActive(Effects.CONDUIT_POWER)) {
               f2 = f3;
            } else {
               f2 = 0.0F;
            }

            Vector3f vector3f = new Vector3f(f, f, 1.0F);
            vector3f.lerp(new Vector3f(1.0F, 1.0F, 1.0F), 0.35F);
            float f4 = this.torchFlicker + 1.5F;
            Vector3f vector3f1 = new Vector3f();

            for(int i = 0; i < 16; ++i) {
               for(int j = 0; j < 16; ++j) {
                  float f5 = this.getLightBrightness(clientworld, i) * f1;
                  float f6 = this.getLightBrightness(clientworld, j) * f4;
                  float f7 = f6 * ((f6 * 0.6F + 0.4F) * 0.6F + 0.4F);
                  float f8 = f6 * (f6 * f6 * 0.6F + 0.4F);
                  vector3f1.set(f6, f7, f8);
                  if (clientworld.dimension.getType() == DimensionType.THE_END) {
                     vector3f1.lerp(new Vector3f(0.99F, 1.12F, 1.0F), 0.25F);
                  } else {
                     Vector3f vector3f2 = vector3f.copy();
                     vector3f2.mul(f5);
                     vector3f1.add(vector3f2);
                     vector3f1.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
                     if (this.entityRenderer.getBossColorModifier(partialTicks) > 0.0F) {
                        float f9 = this.entityRenderer.getBossColorModifier(partialTicks);
                        Vector3f vector3f3 = vector3f1.copy();
                        vector3f3.mul(0.7F, 0.6F, 0.6F);
                        vector3f1.lerp(vector3f3, f9);
                     }
                  }

                  clientworld.getDimension().getLightmapColors(partialTicks, f, f4, f5, vector3f1);

                  vector3f1.clamp(0.0F, 1.0F);
                  if (f2 > 0.0F) {
                     float f10 = Math.max(vector3f1.getX(), Math.max(vector3f1.getY(), vector3f1.getZ()));
                     if (f10 < 1.0F) {
                        float f12 = 1.0F / f10;
                        Vector3f vector3f5 = vector3f1.copy();
                        vector3f5.mul(f12);
                        vector3f1.lerp(vector3f5, f2);
                     }
                  }

                  float f11 = (float)this.client.gameSettings.gamma;
                  Vector3f vector3f4 = vector3f1.copy();
                  vector3f4.apply(this::invGamma);
                  vector3f1.lerp(vector3f4, f11);
                  vector3f1.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
                  vector3f1.clamp(0.0F, 1.0F);
                  vector3f1.mul(255.0F);
                  int j1 = 255;
                  int k = (int)vector3f1.getX();
                  int l = (int)vector3f1.getY();
                  int i1 = (int)vector3f1.getZ();
                  this.nativeImage.setPixelRGBA(j, i, -16777216 | i1 << 16 | l << 8 | k);
               }
            }

            this.dynamicTexture.updateDynamicTexture();
            this.client.getProfiler().endSection();
         }
      }
   }

   private float invGamma(float valueIn) {
      float f = 1.0F - valueIn;
      return 1.0F - f * f * f * f;
   }

   private float getLightBrightness(World worldIn, int lightLevelIn) {
      return worldIn.dimension.getLightBrightness(lightLevelIn);
   }

   public static int packLight(int blockLightIn, int skyLightIn) {
      return blockLightIn << 4 | skyLightIn << 20;
   }

   public static int getLightBlock(int packedLightIn) {
      return (packedLightIn & 0xFFFF) >> 4; // Forge: Fix fullbright quads showing dark artifacts. Reported as MC-169806
   }

   public static int getLightSky(int packedLightIn) {
      return packedLightIn >> 20 & '\uffff';
   }
}