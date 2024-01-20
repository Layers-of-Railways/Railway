package com.railwayteam.railways.content.distant_signals;

import com.railwayteam.railways.content.semaphore.SemaphoreBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.source.NixieTubeDisplaySource;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.content.trains.signal.SignalBlockEntity.SignalState;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.network.chat.MutableComponent;

import java.util.Locale;
import java.util.Optional;

public class SignalDisplaySource extends SingleLineDisplaySource {
    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        SignalState state = SignalState.INVALID;
        if (context.getSourceBlockEntity() instanceof SignalBlockEntity signalBE) {
            state = signalBE.getState();
        }
        if (isSignalTarget(context)) {
            return Components.literal(state.name());
        }
        return Components.translatable("railways.display_source.signal." + state.name().toLowerCase(Locale.ROOT));
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return !isSignalTarget(context);
    }

    public static boolean isSignalTarget(DisplayLinkContext context) {
        return context.getTargetBlockEntity() instanceof NixieTubeBlockEntity
            || context.getTargetBlockEntity() instanceof SemaphoreBlockEntity;
    }

    public void updateState(DisplayLinkBlockEntity be) {
        be.updateGatheredData();
    }

    @Override
    public int getPassiveRefreshTicks() {
        return 40;
    }

    /**
     * Should only be called by a target
     */
    public static boolean hasSignalSource(DisplayLinkContext context) {
        if (context.getSourceBlockEntity() instanceof SignalBlockEntity) {
            return true;
        } else if (context.getSourceBlockEntity() instanceof NixieTubeBlockEntity nixie) {
            DisplaySource source = context.blockEntity().activeSource;
            if (source instanceof SignalDisplaySource)
                return true;

            if (source instanceof NixieTubeDisplaySource && context.getSourceBlockEntity() instanceof NixieTubeBlockEntity nixieSource) {
                if (((IOverridableSignal) nixieSource).snr$getOverriddenState().isPresent())
                    return true;
                if (getState(nixie.getFullText()).isPresent()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Pair<SignalState, Optional<SignalBlockEntity>> getSignalState(DisplayLinkContext context, MutableComponent text) {
        if (context.getSourceBlockEntity() instanceof SignalBlockEntity signalBE) {
            return Pair.of(signalBE.getState(), Optional.of(signalBE));
        }
        String name = text.getString();
        return Pair.of(getState(text).orElse(SignalState.INVALID), Optional.empty());
    }

    public static Optional<SignalState> getState(MutableComponent text) {
        String name = text.getString();
        try {
            return Optional.of(SignalState.valueOf(name));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
