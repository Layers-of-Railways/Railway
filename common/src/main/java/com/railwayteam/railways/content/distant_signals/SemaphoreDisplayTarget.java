package com.railwayteam.railways.content.distant_signals;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;
import java.util.Optional;

import static com.railwayteam.railways.content.distant_signals.SignalDisplaySource.getSignalState;

public class SemaphoreDisplayTarget extends DisplayTarget {
    @Override
    public void acceptText(int line, List<MutableComponent> text, DisplayLinkContext context) {
        Pair<SignalBlockEntity.SignalState, Optional<SignalBlockEntity>> state = getSignalState(context, text.get(0));
        if (context.getTargetBlockEntity() instanceof IOverridableSignal overridableSignal) {
            overridableSignal.snr$refresh(
                state.getSecond().orElse(null),
                state.getFirst(),
                context.getSourceBlockEntity() instanceof SignalBlockEntity ? 43 : 103,
                line != 0
            );
        }
    }

    @Override
    public DisplayTargetStats provideStats(DisplayLinkContext context) {
        return new DisplayTargetStats(2, 2, this);
    }

    @Override
    public Component getLineOptionText(int line) {
        return Components.translatable("railways.display_target.semaphore."+(line != 0 ? "distant" : "normal"));
    }
}
