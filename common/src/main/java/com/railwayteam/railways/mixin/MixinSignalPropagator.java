package com.railwayteam.railways.mixin;

import com.railwayteam.railways.Railways;
import com.simibubi.create.content.trains.graph.EdgeData;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.signal.SignalPropagator;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Pair;
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
        Railways.signalPropagatorCallDepth += 1;
    }

    @Inject(method = "walkSignals(Lcom/simibubi/create/content/trains/graph/TrackGraph;Ljava/util/List;Ljava/util/function/Predicate;Ljava/util/function/Predicate;Z)V", at = @At("RETURN"), remap = false)
    private static void recordWalkSignalsReturn(TrackGraph graph, List<Couple<TrackNode>> frontier, Predicate<Pair<TrackNode, SignalBoundary>> boundaryCallback, Predicate<EdgeData> nonBoundaryCallback, boolean forCollection, CallbackInfo ci) {
        if (Railways.signalPropagatorCallDepth > 0)
            Railways.signalPropagatorCallDepth -= 1;
    }
}
