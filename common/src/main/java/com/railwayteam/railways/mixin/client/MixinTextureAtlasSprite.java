package com.railwayteam.railways.mixin.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.railwayteam.railways.content.custom_tracks.phantom.PhantomSpriteManager;
import com.railwayteam.railways.mixin_interfaces.IPotentiallyInvisibleTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextureAtlasSprite.class)
public abstract class MixinTextureAtlasSprite implements IPotentiallyInvisibleTextureAtlasSprite {
    @Shadow @Final private float u1;

    @Shadow @Final private float u0;

    @Shadow @Final private ResourceLocation name;

    @Shadow public abstract void uploadFirstFrame();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void railways$onInit(TextureAtlas atlas, TextureAtlasSprite.Info spriteInfo, int mipLevel, int storageX, int storageY, int x, int y, NativeImage image, CallbackInfo ci) {
        PhantomSpriteManager.register((TextureAtlasSprite) (Object) this);
    }

    private boolean visible = true;
    private boolean shouldDoInvisibility = false;

    @Override
    public void uploadFrame(boolean visible) {
        this.visible = visible;
        this.shouldDoInvisibility = true;
        this.uploadFirstFrame();
    }

    @ModifyArg(method = "upload", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/NativeImage;upload(IIIIIIIZZ)V"), index = 5) //fixme this doesn't actually work
    private int railways$applyInvisibility(int value) {
        if (!shouldDoInvisibility)
            return value;
        return visible ? value : 0;
    }
}
