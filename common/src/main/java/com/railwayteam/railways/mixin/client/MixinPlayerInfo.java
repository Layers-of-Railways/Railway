package com.railwayteam.railways.mixin.client;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.util.DevCapeUtils;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(PlayerInfo.class)
public class MixinPlayerInfo {
    @Shadow
    @Final
    private GameProfile profile;
    @Shadow @Final
    private Map<MinecraftProfileTexture.Type, ResourceLocation> textureLocations;
    @Unique
    private boolean railway$texturesLoaded;

    @Inject(at = @At("HEAD"), method = "getCapeLocation")
    protected void registerTextures(CallbackInfoReturnable<ResourceLocation> cir) {
        if (!railway$texturesLoaded && DevCapeUtils.INSTANCE.isDev(profile.getId())) {
            railway$texturesLoaded = true;
            this.textureLocations.put(MinecraftProfileTexture.Type.CAPE, Railways.asResource("textures/misc/dev_cape.png"));
        }
    }
}
