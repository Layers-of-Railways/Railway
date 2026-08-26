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
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.content.trains.signal.SignalVisual;
import com.simibubi.create.content.trains.track.ITrackBlock;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SignalVisual.class)
public class MixinSignalVisual {
    @Unique
    private @Nullable Boolean railways$previousPhantomVisible = null;

    @WrapOperation(method = "setupVisual", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/trains/signal/SignalVisual;previousOverlayState:Lcom/simibubi/create/content/trains/signal/SignalBlockEntity$OverlayState;", opcode = Opcodes.GETFIELD), remap = false)
    private SignalBlockEntity.OverlayState updateOnPhantomChange(
        SignalVisual instance,
        Operation<SignalBlockEntity.OverlayState> original,
        @Local(name = "trackBlock") ITrackBlock trackBlock
    ) {
        if (trackBlock instanceof PhantomTrackBlock) {
            //noinspection WrapperTypeMayBePrimitive
            Boolean visible = PhantomSpriteManager.isVisible();
            if (visible != railways$previousPhantomVisible) {
                railways$previousPhantomVisible = visible;
                return null;
            }
        }
        return original.call(instance);
    }
}
