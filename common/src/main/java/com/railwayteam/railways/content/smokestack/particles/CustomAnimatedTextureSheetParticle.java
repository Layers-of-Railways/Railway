package com.railwayteam.railways.content.smokestack.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;

@Environment(EnvType.CLIENT)
public abstract class CustomAnimatedTextureSheetParticle extends TextureSheetParticle {
    protected CustomAnimatedTextureSheetParticle(ClientLevel clientLevel, double d, double e, double f) {
        super(clientLevel, d, e, f);
    }

    protected CustomAnimatedTextureSheetParticle(ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
        super(clientLevel, d, e, f, g, h, i);
    }

    protected abstract double getAnimationProgress();

    protected int frameWidthFactor() {
        return 1;
    }

    protected int frameHeightFactor() {
        return 1;
    }

    @Override
    protected float getV0() {
        int frames = (this.sprite.contents().height() * frameWidthFactor()) / (this.sprite.contents().width() * frameHeightFactor());
        int frameNumber = (int) (frames * getAnimationProgress());
        return this.sprite.getV(16. * ((double) frameNumber / frames));
    }

    @Override
    protected float getV1() {
        int frames = (this.sprite.contents().height() * frameWidthFactor()) / (this.sprite.contents().width() * frameHeightFactor());
        int frameNumber = (int) (frames * getAnimationProgress());
        return this.sprite.getV(16. * ((double) frameNumber + 1) / frames);
    }
}
