package com.railwayteam.railways.mixin.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.railwayteam.railways.content.custom_tracks.phantom.PhantomSpriteManager;
import com.railwayteam.railways.mixin_interfaces.IPotentiallyInvisibleSpriteContents;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
            shouldDoInvisibility = true;
    }

    private boolean visible = true;
    private boolean shouldDoInvisibility = false;

    @Override
    public void uploadFrame(boolean visible) {
        this.visible = visible;
        this.shouldDoInvisibility = true;
        if (this.animatedTexture != null)
            // fixme change 1, 1 to whatever it needs to be
            this.animatedTexture.uploadFirstFrame(1,1);
    }

    public boolean shouldDoInvisibility() {
        return shouldDoInvisibility;
    }

    public boolean isVisible() {
        return visible || !shouldDoInvisibility;
    }

    @Mixin(SpriteContents.AnimatedTexture.class)
    public abstract static class MixinAnimatedTexture {

        @Shadow(aliases = {"this$0", "field_28469", "f_uqrdoixj"})
        @Final
        private SpriteContents field_28469;

        @ModifyVariable(method = "uploadFrame", argsOnly = true, ordinal = 0, at = @At("LOAD"))
        private int railways$modifyFrameIndex(int frameIndex) {
            if (!((IPotentiallyInvisibleSpriteContents) field_28469).shouldDoInvisibility()) return frameIndex;
            return ((IPotentiallyInvisibleSpriteContents) field_28469).isVisible() ? 0 : 1;
        }
    }
}
