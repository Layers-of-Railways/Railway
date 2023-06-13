package com.railwayteam.railways.content.distant_signals;

import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface IOverridableSignal {
    default void refresh(@Nullable SignalBlockEntity signalBE, SignalBlockEntity.SignalState state, int ticks) {
        refresh(signalBE, state, ticks, false);
    }

    void refresh(@Nullable SignalBlockEntity signalBE, SignalBlockEntity.SignalState state, int ticks, boolean distantSignal);

    Optional<SignalBlockEntity.SignalState> getOverriddenState();
}
