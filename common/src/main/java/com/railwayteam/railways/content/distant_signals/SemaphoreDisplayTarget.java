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

package com.railwayteam.railways.content.distant_signals;

import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import net.createmod.catnip.data.Pair;
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
            overridableSignal.railways$refresh(
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
        return Component.translatable("railways.display_target.semaphore."+(line != 0 ? "distant" : "normal"));
    }
}
