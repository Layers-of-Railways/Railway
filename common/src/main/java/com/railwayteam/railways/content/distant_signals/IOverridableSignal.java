package com.railwayteam.railways.content.distant_signals;

import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface IOverridableSignal {
    default void snr$refresh(@Nullable SignalBlockEntity signalBE, SignalBlockEntity.SignalState state, int ticks) {
        snr$refresh(signalBE, state, ticks, false);
    }

    void snr$refresh(@Nullable SignalBlockEntity signalBE, SignalBlockEntity.SignalState state, int ticks, boolean distantSignal);

    Optional<SignalBlockEntity.SignalState> snr$getOverriddenState();
}
