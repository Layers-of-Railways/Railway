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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.railwayteam.railways.content.switches.TrackSwitch;
import com.railwayteam.railways.content.switches.TrackSwitchDebugVisualizer;
import com.railwayteam.railways.content.train_debug.TravellingPointVisualizer;
import com.railwayteam.railways.mixin_interfaces.IShadowTrain;
import com.railwayteam.railways.mixin_interfaces.RailwaySavedDataDuck;
import com.railwayteam.railways.registry.CREdgePointTypes;
import com.railwayteam.railways.util.Utils;
import com.simibubi.create.content.kinetics.KineticDebugger;
import com.simibubi.create.content.trains.GlobalRailwayManager;
import com.simibubi.create.content.trains.RailwaySavedData;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackGraph;
import org.objectweb.asm.Opcodes;
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

    @Shadow private RailwaySavedData savedData;

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

    @WrapOperation(method = "tickTrains", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/trains/entity/Train;invalid:Z", opcode = Opcodes.GETFIELD))
    private boolean removeShadowTrains(Train instance, Operation<Boolean> original) {
        if (instance instanceof IShadowTrain shadowTrain && shadowTrain.railways$isShadow()) {
            // write all carriages to store their entities
            DimensionPalette dimensions = new DimensionPalette();
            for (Carriage carriage : instance.carriages) {
                carriage.write(dimensions);
            }
            ((RailwaySavedDataDuck) savedData).railway$getShadowTrains().put(instance.id, instance);
            ((RailwaySavedDataDuck) savedData).railways$getShadowKeys().put(shadowTrain.railways$getShadowKey(), instance.id);
            savedData.setDirty();
            return true;
        }
        return original.call(instance);
    }
}
