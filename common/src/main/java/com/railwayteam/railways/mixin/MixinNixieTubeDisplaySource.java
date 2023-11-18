package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.distant_signals.IOverridableSignal;
import com.railwayteam.railways.content.distant_signals.SemaphoreDisplayTarget;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.NixieTubeDisplaySource;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.foundation.utility.Components;
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
    private void snr$allowLabeling(DisplayLinkContext context, CallbackInfoReturnable<Boolean> cir) {
        if (context.blockEntity().activeTarget instanceof SemaphoreDisplayTarget)
            cir.setReturnValue(false);
    }

    @Inject(method = "provideLine", at = @At("HEAD"), cancellable = true)
    private void snr$provideLine(DisplayLinkContext context, DisplayTargetStats stats, CallbackInfoReturnable<MutableComponent> cir) {
        // if this is an overridden signal, provide the proper output
        SignalBlockEntity.SignalState state;
        if (context.getSourceBlockEntity() instanceof IOverridableSignal signalBE) {
            Optional<SignalBlockEntity.SignalState> optionalState = signalBE.getOverriddenState();
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
            cir.setReturnValue(Components.literal(state.name()));
            return;
        }
        cir.setReturnValue(Components.translatable("railways.display_source.signal." + state.name().toLowerCase(Locale.ROOT)));
    }
}
