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

package com.railwayteam.railways.mixin;

import com.railwayteam.railways.mixin_interfaces.ISwitchDisabledEdge;
import com.railwayteam.railways.util.MixinVariables;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TravellingPoint.class, remap = false)
public class MixinTravellingPoint {
    @Shadow public TrackEdge edge;

    @Inject(
            method = "travel(Lcom/simibubi/create/content/trains/graph/TrackGraph;DLcom/simibubi/create/content/trains/entity/TravellingPoint$ITrackSelector;Lcom/simibubi/create/content/trains/entity/TravellingPoint$IEdgePointListener;Lcom/simibubi/create/content/trains/entity/TravellingPoint$ITurnListener;Lcom/simibubi/create/content/trains/entity/TravellingPoint$IPortalListener;)D",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/graph/TrackGraph;getConnectionsFrom(Lcom/simibubi/create/content/trains/graph/TrackNode;)Ljava/util/Map;", ordinal = 2))
    private void railways$flipEdgeCheck(TrackGraph graph, double distance, TravellingPoint.ITrackSelector trackSelector, TravellingPoint.IEdgePointListener signalListener, TravellingPoint.ITurnListener turnListener, TravellingPoint.IPortalListener portalListener, CallbackInfoReturnable<Double> cir) {
        MixinVariables.trackEdgeTemporarilyFlipped = true;
    }

    @Inject(
            method = "travel(Lcom/simibubi/create/content/trains/graph/TrackGraph;DLcom/simibubi/create/content/trains/entity/TravellingPoint$ITrackSelector;Lcom/simibubi/create/content/trains/entity/TravellingPoint$IEdgePointListener;Lcom/simibubi/create/content/trains/entity/TravellingPoint$ITurnListener;Lcom/simibubi/create/content/trains/entity/TravellingPoint$IPortalListener;)D",
            at = @At("RETURN")
    )
    private void railways$selectEdge(TrackGraph graph, double distance, TravellingPoint.ITrackSelector trackSelector, TravellingPoint.IEdgePointListener signalListener, TravellingPoint.ITurnListener turnListener, TravellingPoint.IPortalListener portalListener, CallbackInfoReturnable<Double> cir) {
        if (MixinVariables.trackEdgeCarriageTravelling && edge != null) {
            if (ISwitchDisabledEdge.isAutomatic(edge) && ISwitchDisabledEdge.isDisabled(edge)) {
                ISwitchDisabledEdge.automaticallySelect(edge);
            }
        }
    }
}
