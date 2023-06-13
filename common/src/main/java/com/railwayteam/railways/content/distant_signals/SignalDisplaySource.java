package com.railwayteam.railways.content.distant_signals;

import com.railwayteam.railways.content.semaphore.SemaphoreBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.network.chat.MutableComponent;

import java.util.Optional;

public class SignalDisplaySource extends SingleLineDisplaySource {
    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        SignalBlockEntity.SignalState state = SignalBlockEntity.SignalState.INVALID;
        if (context.getSourceBlockEntity() instanceof SignalBlockEntity signalBE) {
            state = signalBE.getState();
        }
        if (isSignalTarget(context)) {
            return Components.literal(state.name());
        }
        return Components.translatable("railways.display_source.signal." + state.name().toLowerCase());
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

    public static boolean shouldActAsSignal(DisplayLinkContext context) {
        return context.getSourceBlockEntity() instanceof SignalBlockEntity
                || context.getSourceBlockEntity() instanceof NixieTubeBlockEntity;
    }

    public static Pair<SignalBlockEntity.SignalState, Optional<SignalBlockEntity>> getSignalState(DisplayLinkContext context, MutableComponent text) {
        if (context.getSourceBlockEntity() instanceof SignalBlockEntity signalBE) {
            return Pair.of(signalBE.getState(), Optional.of(signalBE));
        }
        String name = text.getString();
        try {
            return Pair.of(SignalBlockEntity.SignalState.valueOf(name), Optional.empty());
        } catch (IllegalArgumentException e) {
            return Pair.of(SignalBlockEntity.SignalState.INVALID, Optional.empty());
        }
    }
}
