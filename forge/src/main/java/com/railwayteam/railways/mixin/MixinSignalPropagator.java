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

package com.railwayteam.railways.mixin;

import com.railwayteam.railways.util.MixinVariables;
import com.simibubi.create.content.trains.graph.EdgeData;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.signal.SignalPropagator;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Predicate;

@Mixin(SignalPropagator.class)
public class MixinSignalPropagator {
    @Inject(method = "walkSignals(Lcom/simibubi/create/content/trains/graph/TrackGraph;Ljava/util/List;Ljava/util/function/Predicate;Ljava/util/function/Predicate;Z)V", at = @At("HEAD"), remap = false)
    private static void recordWalkSignals(TrackGraph graph, List<Couple<TrackNode>> frontier, Predicate<Pair<TrackNode, SignalBoundary>> boundaryCallback, Predicate<EdgeData> nonBoundaryCallback, boolean forCollection, CallbackInfo ci) {
        MixinVariables.signalPropagatorCallDepth += 1;
    }

    @Inject(method = "walkSignals(Lcom/simibubi/create/content/trains/graph/TrackGraph;Ljava/util/List;Ljava/util/function/Predicate;Ljava/util/function/Predicate;Z)V", at = @At("RETURN"), remap = false)
    private static void recordWalkSignalsReturn(TrackGraph graph, List<Couple<TrackNode>> frontier, Predicate<Pair<TrackNode, SignalBoundary>> boundaryCallback, Predicate<EdgeData> nonBoundaryCallback, boolean forCollection, CallbackInfo ci) {
        if (MixinVariables.signalPropagatorCallDepth > 0)
            MixinVariables.signalPropagatorCallDepth -= 1;
    }
}
