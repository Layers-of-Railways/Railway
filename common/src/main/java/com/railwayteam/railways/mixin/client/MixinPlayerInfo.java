package com.railwayteam.railways.mixin.client;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.util.DevCapeUtils;
import com.railwayteam.railways.util.Utils;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Objects;

@Mixin(PlayerInfo.class)
public class MixinPlayerInfo {
    @Shadow @Final private GameProfile profile;
    @Shadow @Final private Map<MinecraftProfileTexture.Type, ResourceLocation> textureLocations;
    @Shadow private @Nullable String skinModel;

    @Unique private boolean railway$texturesLoaded;
    @Unique private static final ResourceLocation DEV_CAPE = Railways.asResource("textures/misc/dev_cape.png");

    // Replaces skin inside the dev env with the conductor skin
    @Inject(method = "getSkinLocation", at = @At("HEAD"))
    private void registerSkinTextures(CallbackInfoReturnable<ResourceLocation> cir) {
        if (Utils.isDevEnv()) {
            this.textureLocations.put(
                    MinecraftProfileTexture.Type.SKIN,
                    Railways.asResource("textures/misc/devenv_skin.png")
            );
        }
    }

    @Inject(method = "getCapeLocation", at = @At("HEAD"))
    private void registerCapeTextures(CallbackInfoReturnable<ResourceLocation> cir) {
        if (!railway$texturesLoaded && DevCapeUtils.INSTANCE.useDevCape(profile.getId())) {
            railway$texturesLoaded = true;
            this.textureLocations.put(MinecraftProfileTexture.Type.CAPE, DEV_CAPE);
        }
    }

    @Inject(method = "getCapeLocation", at = @At("RETURN"), cancellable = true)
    private void skipCapeIfNeeded(CallbackInfoReturnable<ResourceLocation> cir) {
        if (Objects.equals(DEV_CAPE, cir.getReturnValue()) && !DevCapeUtils.INSTANCE.useDevCape(profile.getId())) {
            cir.setReturnValue(null);
        }
    }

    // Replaces skin model inside the dev env with the default "steve" skin model
    @Inject(method = "registerTextures", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/resources/SkinManager;registerSkins(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/client/resources/SkinManager$SkinTextureCallback;Z)V"
    ))
    private void snr$setModelToLarge(CallbackInfo ci) {
        if (Utils.isDevEnv()) {
            skinModel = "default";
        }
    }
}
