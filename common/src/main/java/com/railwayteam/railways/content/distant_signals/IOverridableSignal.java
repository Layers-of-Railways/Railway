/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
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
