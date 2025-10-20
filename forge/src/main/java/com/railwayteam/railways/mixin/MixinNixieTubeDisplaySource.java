/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.distant_signals.IOverridableSignal;
import com.railwayteam.railways.content.distant_signals.SemaphoreDisplayTarget;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.NixieTubeDisplaySource;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Locale;
import java.util.Optional;

import static com.railwayteam.railways.content.distant_signals.SignalDisplaySource.isSignalTarget;

@Mixin(value = NixieTubeDisplaySource.class, remap = false)
public abstract class MixinNixieTubeDisplaySource extends SingleLineDisplaySource {
    @Inject(method = "allowsLabeling", at = @At("RETURN"), cancellable = true)
    private void railways$allowLabeling(DisplayLinkContext context, CallbackInfoReturnable<Boolean> cir) {
        if (context.blockEntity().activeTarget instanceof SemaphoreDisplayTarget)
            cir.setReturnValue(false);
    }

    @Inject(method = "provideLine", at = @At("HEAD"), cancellable = true)
    private void railways$provideLine(DisplayLinkContext context, DisplayTargetStats stats, CallbackInfoReturnable<MutableComponent> cir) {
        // if this is an overridden signal, provide the proper output
        SignalBlockEntity.SignalState state;
        if (context.getSourceBlockEntity() instanceof IOverridableSignal signalBE) {
            Optional<SignalBlockEntity.SignalState> optionalState = signalBE.railways$getOverriddenState();
            if (optionalState.isPresent()) {
                state = optionalState.get();
            } else {
                return;
            }
        } else {
            return;
        }
        context.flapDisplayContext = null;
        if (isSignalTarget(context)) {
            cir.setReturnValue(Component.literal(state.name()));
            return;
        }
        cir.setReturnValue(Component.translatable("railways.display_source.signal." + state.name().toLowerCase(Locale.ROOT)));
    }
}
