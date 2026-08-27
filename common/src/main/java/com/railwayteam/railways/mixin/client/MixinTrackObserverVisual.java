/*
 * Steam 'n' Rails
 * Copyright (c) 2026 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.railwayteam.railways.content.custom_tracks.phantom.PhantomSpriteManager;
import com.railwayteam.railways.content.custom_tracks.phantom.PhantomTrackBlock;
import com.simibubi.create.content.trains.observer.TrackObserverVisual;
import com.simibubi.create.content.trains.track.ITrackBlock;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TrackObserverVisual.class)
public class MixinTrackObserverVisual {
    @Unique
    private @Nullable Boolean railways$previousPhantomVisible = null;

    @WrapOperation(method = "setupVisual", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;equals(Ljava/lang/Object;)Z", remap = true), remap = false)
    private boolean updateOnPhantomChange(
        BlockPos instance,
        Object other,
        Operation<Boolean> original,
        @Local(name = "trackBlock") ITrackBlock trackBlock
    ) {
        if (trackBlock instanceof PhantomTrackBlock) {
            //noinspection WrapperTypeMayBePrimitive
            Boolean visible = PhantomSpriteManager.isVisible();
            if (visible != railways$previousPhantomVisible) {
                railways$previousPhantomVisible = visible;
                return false;
            }
        }
        return original.call(instance, other);
    }
}
