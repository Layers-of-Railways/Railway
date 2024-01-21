package com.railwayteam.railways.content.distant_signals;

import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface IOverridableSignal {
    default void railways$refresh(@Nullable SignalBlockEntity signalBE, SignalBlockEntity.SignalState state, int ticks) {
        railways$refresh(signalBE, state, ticks, false);
    }

    void railways$refresh(@Nullable SignalBlockEntity signalBE, SignalBlockEntity.SignalState state, int ticks, boolean distantSignal);

    Optional<SignalBlockEntity.SignalState> railways$getOverriddenState();
}
