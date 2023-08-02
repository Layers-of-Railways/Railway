package com.railwayteam.railways.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.railwayteam.railways.Railways;
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
import java.util.Set;

@Mixin(PlayerInfo.class)
public class MixinPlayerInfo {
    @Shadow
    @Final
    private GameProfile profile;
    @Shadow @Final
    private Map<MinecraftProfileTexture.Type, ResourceLocation> textureLocations;
    @Unique
    private boolean railway$texturesLoaded;

    @Inject(at = @At("HEAD"), method = "getCapeLocation()Lnet/minecraft/resources/ResourceLocation;")
    protected void registerTextures(CallbackInfoReturnable<ResourceLocation> cir) {
        // TODO: switch to making a request for this instead of hardcoded
        final Set<String> DEV_UUID = Set.of(
                "e67eb09a-b5af-4822-b756-9065cdc49913"  // IThundxr
        );

        if (!railway$texturesLoaded && DEV_UUID.contains(profile.getId().toString())) {
            railway$texturesLoaded = true;
            this.textureLocations.put(MinecraftProfileTexture.Type.CAPE, Railways.asResource("textures/misc/dev_cape.png"));
        }
    }
}
