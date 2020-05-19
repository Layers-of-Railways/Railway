package net.minecraft.client.entity.player;

import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.renderer.texture.DownloadingTexture;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractClientPlayerEntity extends PlayerEntity {
   private NetworkPlayerInfo playerInfo;
   public float rotateElytraX;
   public float rotateElytraY;
   public float rotateElytraZ;
   public final ClientWorld worldClient;

   public AbstractClientPlayerEntity(ClientWorld p_i50991_1_, GameProfile p_i50991_2_) {
      super(p_i50991_1_, p_i50991_2_);
      this.worldClient = p_i50991_1_;
   }

   /**
    * Returns true if the player is in spectator mode.
    */
   public boolean isSpectator() {
      NetworkPlayerInfo networkplayerinfo = Minecraft.getInstance().getConnection().getPlayerInfo(this.getGameProfile().getId());
      return networkplayerinfo != null && networkplayerinfo.getGameType() == GameType.SPECTATOR;
   }

   public boolean isCreative() {
      NetworkPlayerInfo networkplayerinfo = Minecraft.getInstance().getConnection().getPlayerInfo(this.getGameProfile().getId());
      return networkplayerinfo != null && networkplayerinfo.getGameType() == GameType.CREATIVE;
   }

   /**
    * Checks if this instance of AbstractClientPlayer has any associated player data.
    */
   public boolean hasPlayerInfo() {
      return this.getPlayerInfo() != null;
   }

   @Nullable
   protected NetworkPlayerInfo getPlayerInfo() {
      if (this.playerInfo == null) {
         this.playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(this.getUniqueID());
      }

      return this.playerInfo;
   }

   /**
    * Returns true if the player has an associated skin.
    */
   public boolean hasSkin() {
      NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
      return networkplayerinfo != null && networkplayerinfo.hasLocationSkin();
   }

   /**
    * Returns the ResourceLocation associated with the player's skin
    */
   public ResourceLocation getLocationSkin() {
      NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
      return networkplayerinfo == null ? DefaultPlayerSkin.getDefaultSkin(this.getUniqueID()) : networkplayerinfo.getLocationSkin();
   }

   @Nullable
   public ResourceLocation getLocationCape() {
      NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
      return networkplayerinfo == null ? null : networkplayerinfo.getLocationCape();
   }

   public boolean isPlayerInfoSet() {
      return this.getPlayerInfo() != null;
   }

   /**
    * Gets the special Elytra texture for the player.
    */
   @Nullable
   public ResourceLocation getLocationElytra() {
      NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
      return networkplayerinfo == null ? null : networkplayerinfo.getLocationElytra();
   }

   public static DownloadingTexture getDownloadImageSkin(ResourceLocation resourceLocationIn, String username) {
      TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
      Texture texture = texturemanager.getTexture(resourceLocationIn);
      if (texture == null) {
         texture = new DownloadingTexture((File)null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringUtils.stripControlCodes(username)), DefaultPlayerSkin.getDefaultSkin(getOfflineUUID(username)), true, (Runnable)null);
         texturemanager.loadTexture(resourceLocationIn, texture);
      }

      return (DownloadingTexture)texture;
   }

   /**
    * Returns true if the username has an associated skin.
    */
   public static ResourceLocation getLocationSkin(String username) {
      return new ResourceLocation("skins/" + Hashing.sha1().hashUnencodedChars(StringUtils.stripControlCodes(username)));
   }

   public String getSkinType() {
      NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
      return networkplayerinfo == null ? DefaultPlayerSkin.getSkinType(this.getUniqueID()) : networkplayerinfo.getSkinType();
   }

   public float getFovModifier() {
      float f = 1.0F;
      if (this.abilities.isFlying) {
         f *= 1.1F;
      }

      IAttributeInstance iattributeinstance = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
      f = (float)((double)f * ((iattributeinstance.getValue() / (double)this.abilities.getWalkSpeed() + 1.0D) / 2.0D));
      if (this.abilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
         f = 1.0F;
      }

      if (this.isHandActive() && this.getActiveItemStack().getItem() instanceof net.minecraft.item.BowItem) {
         int i = this.getItemInUseMaxCount();
         float f1 = (float)i / 20.0F;
         if (f1 > 1.0F) {
            f1 = 1.0F;
         } else {
            f1 = f1 * f1;
         }

         f *= 1.0F - f1 * 0.15F;
      }

      return net.minecraftforge.client.ForgeHooksClient.getOffsetFOV(this, f);
   }
}