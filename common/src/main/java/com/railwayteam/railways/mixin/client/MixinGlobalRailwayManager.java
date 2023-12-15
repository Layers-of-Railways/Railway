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
