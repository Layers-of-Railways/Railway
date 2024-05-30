/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
        int frames = (this.sprite.getHeight() * frameWidthFactor()) / (this.sprite.getWidth() * frameHeightFactor());
        int frameNumber = (int) (frames * getAnimationProgress());
        return this.sprite.getV(16. * ((double) frameNumber / frames));
    }

    @Override
    protected float getV1() {
        int frames = (this.sprite.getHeight() * frameWidthFactor()) / (this.sprite.getWidth() * frameHeightFactor());
        int frameNumber = (int) (frames * getAnimationProgress());
        return this.sprite.getV(16. * ((double) frameNumber + 1) / frames);
    }
}
