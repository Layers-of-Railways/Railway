package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.content.switches.TrackSwitch;
import com.railwayteam.railways.content.switches.TrackSwitchDebugVisualizer;
import com.railwayteam.railways.content.train_debug.TravellingPointVisualizer;
import com.railwayteam.railways.registry.CREdgePointTypes;
import com.simibubi.create.content.contraptions.KineticDebugger;
import com.simibubi.create.content.logistics.trains.GlobalRailwayManager;
import com.simibubi.create.content.logistics.trains.TrackGraph;
import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.content.logistics.trains.management.edgePoint.EdgePointType;
import com.simibubi.create.content.trains.GlobalRailwayManager;
import com.simibubi.create.content.trains.entity.Train;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mixin(value = GlobalRailwayManager.class, remap = false)
public abstract class MixinGlobalRailwayManager {
    @Shadow public Map<UUID, Train> trains;

/*    @Inject(method = "clientTick", at = @At("HEAD"))
    private void showTrainDebug(CallbackInfo ci) {
        if (KineticDebugger.isActive())
            for (Train train : trains.values())
                TravellingPointVisualizer.debugTrain(train);
    }*/

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
