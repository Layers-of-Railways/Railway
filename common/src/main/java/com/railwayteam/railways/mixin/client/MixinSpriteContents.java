package com.railwayteam.railways.mixin.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.railwayteam.railways.content.custom_tracks.phantom.PhantomSpriteManager;
import com.railwayteam.railways.mixin_ducks.AnimatedTextureDuck;
import com.railwayteam.railways.mixin_interfaces.IPotentiallyInvisibleSpriteContents;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpriteContents.class)
public abstract class MixinSpriteContents implements IPotentiallyInvisibleSpriteContents {

    @Shadow @Final @Nullable private SpriteContents.AnimatedTexture animatedTexture;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void railways$onInit(ResourceLocation name, FrameSize frameSize, NativeImage originalImage, AnimationMetadataSection metadata, CallbackInfo ci) {
        if (PhantomSpriteManager.register((SpriteContents) (Object) this))
            snr$shouldDoInvisibility = true;
    }

    @Unique
    private boolean snr$visible = true;
    @Unique
    private boolean snr$shouldDoInvisibility = false;

    @Override
    public void snr$uploadFrame(boolean visible) {
        this.snr$visible = visible;
        this.snr$shouldDoInvisibility = true;
        if (this.animatedTexture != null)
            ((AnimatedTextureDuck) this.animatedTexture).snr$uploadWithVisibility();
    }

    public boolean snr$shouldDoInvisibility() {
        return snr$shouldDoInvisibility;
    }

    public boolean snr$isVisible() {
        return snr$visible || !snr$shouldDoInvisibility;
    }

    @Mixin(SpriteContents.AnimatedTexture.class)
    public abstract static class MixinAnimatedTexture implements AnimatedTextureDuck {

        @Shadow(aliases = {"this$0", "field_28469", "f_uqrdoixj"})
        @Final
        private SpriteContents field_28469;

        @Shadow abstract void uploadFrame(int x, int y, int frameIndex);

        @Unique
        private int snr$uploadX = 0;
        @Unique
        private int snr$uploadY = 0;

        @Inject(method = "uploadFirstFrame", at = @At("HEAD"))
        private void railways$onUploadFirstFrame(int x, int y, CallbackInfo ci) {
            snr$uploadX = x;
            snr$uploadY = y;
        }

        @ModifyVariable(method = "uploadFrame", argsOnly = true, ordinal = 2, at = @At("LOAD"))
        private int railways$modifyFrameIndex(int frameIndex) {
            if (!((IPotentiallyInvisibleSpriteContents) field_28469).snr$shouldDoInvisibility()) return frameIndex;
            return ((IPotentiallyInvisibleSpriteContents) field_28469).snr$isVisible() ? 0 : 1;
        }

        @Override
        public void snr$uploadWithVisibility() {
            if (!((IPotentiallyInvisibleSpriteContents) field_28469).snr$shouldDoInvisibility()) return;
            uploadFrame(snr$uploadX, snr$uploadY, ((IPotentiallyInvisibleSpriteContents) field_28469).snr$isVisible() ? 0 : 1);
        }
    }
}
