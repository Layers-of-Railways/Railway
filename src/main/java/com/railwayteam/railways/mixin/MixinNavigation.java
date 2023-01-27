package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.schedule.WaypointDestinationInstruction;
import com.railwayteam.railways.mixin_interfaces.IWaypointableNavigation;
import com.simibubi.create.content.logistics.trains.TrackNode;
import com.simibubi.create.content.logistics.trains.entity.Navigation;
import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.GlobalStation;
import com.simibubi.create.foundation.utility.Pair;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.UUID;

@Mixin(value = Navigation.class, remap = false)
public abstract class MixinNavigation implements IWaypointableNavigation {

    @Shadow
    public Train train;

    @Override
    public boolean isWaypointMode() {
        try {
            return !train.manualTick && !train.runtime.paused && !train.runtime.completed && train.runtime.getSchedule() != null && train.runtime.currentEntry < train.runtime.getSchedule().entries.size() &&
                train.runtime.getSchedule().entries.get(train.runtime.currentEntry).instruction instanceof WaypointDestinationInstruction;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Redirect(method = "tick", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lcom/simibubi/create/content/logistics/trains/entity/Navigation;distanceToDestination:D"))
    private double fixWaypointDistanceInTick(Navigation instance) {
        if (((IWaypointableNavigation) instance).isWaypointMode())
            return 1000;
        return instance.distanceToDestination;
    }

    @Redirect(method = "lambda$tick$0", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/trains/management/edgePoint/station/GlobalStation;canApproachFrom(Lcom/simibubi/create/content/logistics/trains/TrackNode;)Z"))
    private boolean keepScoutingAtWaypoints(GlobalStation instance, TrackNode side) {
        return instance.canApproachFrom(side) && !isWaypointMode();
    }

    @Redirect(method = "tick", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lcom/simibubi/create/content/logistics/trains/entity/Navigation;waitingForSignal:Lcom/simibubi/create/foundation/utility/Pair;"),
    slice = @Slice(
        from = @At(value = "CONSTANT", args = {"doubleValue=0.25d"}),
        to = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/trains/entity/Train;leaveStation()V")
    ))
    private Pair<UUID, Boolean> brakeProperlyAtWaypoints(Navigation instance) {
        return isWaypointMode() ? null : instance.waitingForSignal;
    }

    @Redirect(method = "currentSignalResolved", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lcom/simibubi/create/content/logistics/trains/entity/Navigation;distanceToDestination:D"), slice =
    @Slice(to = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/trains/TrackGraph;getPoint(Lcom/simibubi/create/content/logistics/trains/management/edgePoint/EdgePointType;Ljava/util/UUID;)Lcom/simibubi/create/content/logistics/trains/management/edgePoint/signal/TrackEdgePoint;")))
    private double preventSignalClearWithWaypoint(Navigation instance) {
        if (((IWaypointableNavigation) instance).isWaypointMode())
            return 10;
        return instance.distanceToDestination;
    }
}
