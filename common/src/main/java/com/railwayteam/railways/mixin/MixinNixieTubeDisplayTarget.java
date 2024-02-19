package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.distant_signals.IOverridableSignal;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.NixieTubeDisplayTarget;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

import static com.railwayteam.railways.content.distant_signals.SignalDisplaySource.getSignalState;
import static com.railwayteam.railways.content.distant_signals.SignalDisplaySource.hasSignalSource;

@Mixin(value = NixieTubeDisplayTarget.class, remap = false)
public class MixinNixieTubeDisplayTarget {
    @Inject(method = "acceptLine", at = @At("HEAD"), cancellable = true)
    private void railways$handleSignalInput(MutableComponent text, DisplayLinkContext context, CallbackInfo ci) {
        if (hasSignalSource(context)) {
            ci.cancel();

            Pair<SignalBlockEntity.SignalState, Optional<SignalBlockEntity>> state = getSignalState(context, text);
            if (context.getTargetBlockEntity() instanceof IOverridableSignal overridableSignal) {
                overridableSignal.railways$refresh(
                    state.getSecond().orElse(null),
                    state.getFirst(),
                    context.getSourceBlockEntity() instanceof SignalBlockEntity ? 43 : 103
                );
            }
        }
    }

    @Inject(method = "getWidth", at = @At("HEAD"), cancellable = true)
    private void railways$overwriteWidth(DisplayLinkContext context, CallbackInfoReturnable<Integer> cir) {
        if (hasSignalSource(context))
            cir.setReturnValue(2);
    }
}
