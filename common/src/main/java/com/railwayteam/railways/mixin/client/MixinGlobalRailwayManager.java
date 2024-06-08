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

package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.content.switches.TrackSwitch;
import com.railwayteam.railways.content.switches.TrackSwitchDebugVisualizer;
import com.railwayteam.railways.content.train_debug.TravellingPointVisualizer;
import com.railwayteam.railways.registry.CREdgePointTypes;
import com.railwayteam.railways.util.Utils;
import com.simibubi.create.content.kinetics.KineticDebugger;
import com.simibubi.create.content.trains.GlobalRailwayManager;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.TrackGraph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(value = GlobalRailwayManager.class, remap = false)
public abstract class MixinGlobalRailwayManager {
    @Shadow public Map<UUID, Train> trains;

    @Inject(method = "clientTick", at = @At("HEAD"))
    private void showTrainDebug(CallbackInfo ci) {
        if (KineticDebugger.isActive() && Utils.isDevEnv())
            for (Train train : trains.values())
                TravellingPointVisualizer.debugTrain(train);
    }

    @Shadow public Map<UUID, TrackGraph> trackNetworks;

    @Inject(method = "clientTick", at = @At("HEAD"))
    private void showSwitchDebug(CallbackInfo ci) {
        if (KineticDebugger.isF3DebugModeActive()) {
            for (TrackGraph graph : trackNetworks.values()) {
                for (TrackSwitch sw: graph.getPoints(CREdgePointTypes.SWITCH)) {
                    TrackSwitchDebugVisualizer.visualizeSwitchExits(sw);
                }
            }
        }
    }
}
