package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.schedule.RedstoneLinkInstruction;
import com.railwayteam.railways.content.schedule.WaypointDestinationInstruction;
import com.railwayteam.railways.mixin_interfaces.IWaypointableNavigation;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.trains.DimensionPalette;
import com.simibubi.create.content.logistics.trains.TrackGraph;
import com.simibubi.create.content.logistics.trains.TrackNode;
import com.simibubi.create.content.logistics.trains.entity.Navigation;
import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.content.logistics.trains.entity.TravellingPoint;
import com.simibubi.create.content.logistics.trains.management.edgePoint.EdgePointType;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SignalBlock;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SignalBoundary;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SignalEdgeGroup;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.TrackEdgePoint;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.GlobalStation;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableObject;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.UUID;

@Mixin(value = Navigation.class, remap = false)
public abstract class MixinNavigation implements IWaypointableNavigation {
    @Shadow
    public GlobalStation destination;

    @Shadow
    public Train train;

    @Shadow
    public boolean destinationBehindTrain;

    @Shadow
    public abstract void cancelNavigation();

    @Shadow
    public Pair<UUID, Boolean> waitingForSignal;

    @Shadow
    protected abstract boolean currentSignalResolved();

    @Shadow
    private Map<UUID, Pair<SignalBoundary, Boolean>> waitingForChainedGroups;

    @Shadow
    public double distanceToSignal;

    @Shadow
    public int ticksWaitingForSignal;

    @Shadow
    private TravellingPoint signalScout;

    @Shadow
    public double distanceToDestination;

    @Shadow
    public abstract TravellingPoint.ITrackSelector controlSignalScout();

    @Shadow
    protected abstract void reserveChain();

    @Override
    public boolean isWaypointMode() {
        try {
            return train.runtime.getSchedule() != null && train.runtime.currentEntry < train.runtime.getSchedule().entries.size() &&
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

    /**
     * @author Slimeist
     * @reason Testing
     */
    //@Overwrite
    private void nope_tick(Level level) {
        if (destination == null)
            return;

        if (!train.runtime.paused) {
            boolean frontDriver = train.hasForwardConductor();
            boolean backDriver = train.hasBackwardConductor();
            if (destinationBehindTrain && !backDriver) {
                if (frontDriver)
                    train.status.missingCorrectConductor();
                else
                    train.status.missingConductor();
                cancelNavigation();
                return;
            }

            if (!destinationBehindTrain && !frontDriver) {
                train.status.missingConductor();
                cancelNavigation();
                return;
            }

            train.status.foundConductor();
        }

        destination.reserveFor(train);

        double acceleration = train.acceleration();
        double brakingDistance = (train.speed * train.speed) / (2 * acceleration);
        double speedMod = destinationBehindTrain ? -1 : 1;
        double preDepartureLookAhead = train.getCurrentStation() != null ? 4.5 : 0;
        double distanceToNextCurve = -1;

        // Signals
        if (train.graph != null) {

            if (waitingForSignal != null && currentSignalResolved()) {
                UUID signalId = waitingForSignal.getFirst();
                SignalBoundary signal = train.graph.getPoint(EdgePointType.SIGNAL, signalId);
                if (signal != null && signal.types.get(waitingForSignal.getSecond()) == SignalBlock.SignalType.CROSS_SIGNAL)
                    waitingForChainedGroups.clear();
                waitingForSignal = null;
            }

            TravellingPoint leadingPoint = !destinationBehindTrain ? train.carriages.get(0)
                .getLeadingPoint()
                : train.carriages.get(train.carriages.size() - 1)
                .getTrailingPoint();

            if (waitingForSignal == null) {
                distanceToSignal = Double.MAX_VALUE;
                ticksWaitingForSignal = 0;
            }

            if (distanceToSignal > 1 / 16f) {
                MutableDouble curveDistanceTracker = new MutableDouble(-1);

                signalScout.node1 = leadingPoint.node1;
                signalScout.node2 = leadingPoint.node2;
                signalScout.edge = leadingPoint.edge;
                signalScout.position = leadingPoint.position;

                double brakingDistanceNoFlicker = brakingDistance + 3 - (brakingDistance % 3);
                double signalScoutDistanceToDestination = isWaypointMode() ? 1000 : distanceToDestination; //FIXME DONE
                double scanDistance = Mth.clamp(brakingDistanceNoFlicker, preDepartureLookAhead, signalScoutDistanceToDestination);

                MutableDouble crossSignalDistanceTracker = new MutableDouble(-1);
                MutableObject<Pair<UUID, Boolean>> trackingCrossSignal = new MutableObject<>(null);
                waitingForChainedGroups.clear();

                // Adding 50 to the distance due to unresolved inaccuracies in
                // TravellingPoint::travel
                signalScout.travel(train.graph, (signalScoutDistanceToDestination + 50) * speedMod, controlSignalScout(),
                    (distance, couple) -> {
                        // > scanDistance and not following down a cross signal
                        boolean crossSignalTracked = trackingCrossSignal.getValue() != null;
                        if (!crossSignalTracked && distance > scanDistance)
                            return true;

                        Couple<TrackNode> nodes = couple.getSecond();
                        TrackEdgePoint boundary = couple.getFirst();
                        if (boundary == destination && ((GlobalStation) boundary).canApproachFrom(nodes.getSecond()) && !isWaypointMode()) //FIXME DONE
                            return true;
                        if (!(boundary instanceof SignalBoundary signal))
                            return false;

                        UUID entering = signal.getGroup(nodes.getSecond());
                        SignalEdgeGroup signalEdgeGroup = Create.RAILWAYS.signalEdgeGroups.get(entering);
                        if (signalEdgeGroup == null)
                            return false;

                        boolean primary = entering.equals(signal.groups.getFirst());
                        boolean crossSignal = signal.types.get(primary) == SignalBlock.SignalType.CROSS_SIGNAL;
                        boolean occupied = !train.manualTick
                            && (signal.isForcedRed(nodes.getSecond()) || signalEdgeGroup.isOccupiedUnless(train));

                        if (!crossSignalTracked) {
                            if (crossSignal) { // Now entering cross signal path
                                trackingCrossSignal.setValue(Pair.of(boundary.id, primary));
                                crossSignalDistanceTracker.setValue(distance);
                                waitingForChainedGroups.put(entering, Pair.of(signal, primary));
                            }
                            if (occupied) { // Section is occupied
                                waitingForSignal = Pair.of(boundary.id, primary);
                                distanceToSignal = distance;
                                if (!crossSignal)
                                    return true; // Standard entry signal, do not collect any further segments
                            }
                            if (!occupied && !crossSignal && distance < distanceToSignal + .25
                                && distance < brakingDistanceNoFlicker)
                                signalEdgeGroup.reserved = signal; // Reserve group for traversal
                            return false;
                        }

                        if (crossSignalTracked) {
                            waitingForChainedGroups.put(entering, Pair.of(signal, primary)); // Add group to chain
                            if (occupied) { // Section is occupied, but wait at the cross signal that started the chain
                                waitingForSignal = trackingCrossSignal.getValue();
                                distanceToSignal = crossSignalDistanceTracker.doubleValue();
                                if (!crossSignal)
                                    return true; // Entry signals end a chain
                            }
                            if (!crossSignal) {
                                if (distance < distanceToSignal + .25) {
                                    // Collect and reset the signal chain because none were blocked
                                    trackingCrossSignal.setValue(null);
                                    reserveChain();
                                    return false;
                                } else
                                    return true; // End of a blocked signal chain
                            }
                        }

                        return false;

                    }, (distance, edge) -> {
                        float current = curveDistanceTracker.floatValue();
                        if (current == -1 || distance < current)
                            curveDistanceTracker.setValue(distance);
                    });

                if (trackingCrossSignal.getValue() != null && waitingForSignal == null)
                    reserveChain();

                distanceToNextCurve = curveDistanceTracker.floatValue();

            } else
                ticksWaitingForSignal++;
        }

        double targetDistance = waitingForSignal != null ? distanceToSignal : (isWaypointMode() ? 1000 : distanceToDestination); //FIXME DONE

        double realTargetDistance = waitingForSignal != null ? distanceToSignal : distanceToDestination; //FIXME IGNORED

        // always overshoot to ensure the travelling point crosses the target
        targetDistance += 0.25d;

        // dont leave until green light
        if (targetDistance > 1 / 32f && train.getCurrentStation() != null) {
            if (waitingForSignal != null && distanceToSignal < preDepartureLookAhead && !isWaypointMode()) { //FIXME DONE
                ticksWaitingForSignal++;
                return;
            }
            train.leaveStation();
        }

        train.currentlyBackwards = destinationBehindTrain;

        if (realTargetDistance < -10) {
            cancelNavigation();
            return;
        }

        if (targetDistance - Math.abs(train.speed) < 1 / 32f) {
            train.speed = Math.max(targetDistance, 1 / 32f) * speedMod;
            return;
        }

        train.burnFuel();

        double topSpeed = train.maxSpeed();

        if (targetDistance < 10) {
            double target = topSpeed * ((targetDistance) / 10);
            if (target < Math.abs(train.speed)) {
                train.speed += (target - Math.abs(train.speed)) * .5f * speedMod;
                return;
            }
        }

        topSpeed *= train.throttle;
        double turnTopSpeed = Math.min(topSpeed, train.maxTurnSpeed());

        double targetSpeed = targetDistance > brakingDistance ? topSpeed * speedMod : 0;

        if (distanceToNextCurve != -1) {
            double slowingDistance = brakingDistance - (turnTopSpeed * turnTopSpeed) / (2 * acceleration);
            double targetTurnSpeed =
                distanceToNextCurve > slowingDistance ? topSpeed * speedMod : turnTopSpeed * speedMod;
            if (Math.abs(targetTurnSpeed) < Math.abs(targetSpeed))
                targetSpeed = targetTurnSpeed;
        }

        train.targetSpeed = targetSpeed;
        train.approachTargetSpeed(1);
    }

    @Redirect(method = "currentSignalResolved", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lcom/simibubi/create/content/logistics/trains/entity/Navigation;distanceToDestination:D"), slice =
    @Slice(to = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/trains/TrackGraph;getPoint(Lcom/simibubi/create/content/logistics/trains/management/edgePoint/EdgePointType;Ljava/util/UUID;)Lcom/simibubi/create/content/logistics/trains/management/edgePoint/signal/TrackEdgePoint;")))
    private double preventSignalClearWithWaypoint(Navigation instance) {
        if (((IWaypointableNavigation) instance).isWaypointMode())
            return 10;
        return instance.distanceToDestination;
    }
}
