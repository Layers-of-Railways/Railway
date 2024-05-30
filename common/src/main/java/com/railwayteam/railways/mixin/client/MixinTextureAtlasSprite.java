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

package com.railwayteam.railways.mixin.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.railwayteam.railways.content.custom_tracks.phantom.PhantomSpriteManager;
import com.railwayteam.railways.mixin_interfaces.IPotentiallyInvisibleTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextureAtlasSprite.class)
public abstract class MixinTextureAtlasSprite implements IPotentiallyInvisibleTextureAtlasSprite {

    @Shadow @Final @Nullable private TextureAtlasSprite.AnimatedTexture animatedTexture;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void railways$onInit(TextureAtlas atlas, TextureAtlasSprite.Info spriteInfo, int mipLevel, int storageX, int storageY, int x, int y, NativeImage image, CallbackInfo ci) {
        if (PhantomSpriteManager.register((TextureAtlasSprite) (Object) this))
            shouldDoInvisibility = true;
    }

    private boolean visible = true;
    private boolean shouldDoInvisibility = false;

    @Override
    public void uploadFrame(boolean visible) {
        this.visible = visible;
        this.shouldDoInvisibility = true;
        if (this.animatedTexture != null)
            this.animatedTexture.uploadFirstFrame();
    }

    public boolean shouldDoInvisibility() {
        return shouldDoInvisibility;
    }

    public boolean isVisible() {
        return visible || !shouldDoInvisibility;
    }

    @Mixin(TextureAtlasSprite.AnimatedTexture.class)
    public abstract static class MixinAnimatedTexture {
        @Shadow(aliases = {"this$0", "field_28469", "f_uqrdoixj", "sprite"})
        @Final
        private TextureAtlasSprite field_28469;

        @ModifyVariable(method = "uploadFrame", argsOnly = true, ordinal = 0, at = @At("LOAD"))
        private int railways$modifyFrameIndex(int frameIndex) {
            if (!((IPotentiallyInvisibleTextureAtlasSprite) field_28469).shouldDoInvisibility()) return frameIndex;
            return ((IPotentiallyInvisibleTextureAtlasSprite) field_28469).isVisible() ? 0 : 1;
        }
    }
}
