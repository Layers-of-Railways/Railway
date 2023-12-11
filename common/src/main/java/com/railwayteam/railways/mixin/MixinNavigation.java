package com.railwayteam.railways.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.buffer.TrackBuffer;
import com.railwayteam.railways.content.schedule.WaypointDestinationInstruction;
import com.railwayteam.railways.content.switches.TrackSwitch;
import com.railwayteam.railways.content.switches.TrackSwitchBlock.SwitchState;
import com.railwayteam.railways.mixin_interfaces.*;
import com.railwayteam.railways.registry.CRTrackMaterials.CRTrackType;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Navigation;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.graph.EdgeData;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.signal.SignalEdgeGroup;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableObject;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(value = Navigation.class, remap = false)
public abstract class MixinNavigation implements IWaypointableNavigation, IGenerallySearchableNavigation {

    @Shadow
    public Train train;

    @Shadow public int ticksWaitingForSignal;

    @Shadow public GlobalStation destination;

    @Shadow private TravellingPoint signalScout;

    @Shadow public double distanceToDestination;

    @Shadow public abstract TravellingPoint.ITrackSelector controlSignalScout();

    @Override
    public boolean snr$isWaypointMode() {
        try {
            return !train.manualTick && !train.runtime.paused && !train.runtime.completed && train.runtime.getSchedule() != null && train.runtime.currentEntry < train.runtime.getSchedule().entries.size() &&
                train.runtime.getSchedule().entries.get(train.runtime.currentEntry).instruction instanceof WaypointDestinationInstruction;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Redirect(method = "tick", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lcom/simibubi/create/content/trains/entity/Navigation;distanceToDestination:D"))
    private double fixWaypointDistanceInTick(Navigation instance) {
        if (((IWaypointableNavigation) instance).snr$isWaypointMode())
            return 1000;
        return instance.distanceToDestination;
    }

    @Redirect(method = "lambda$tick$0", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/station/GlobalStation;canApproachFrom(Lcom/simibubi/create/content/trains/graph/TrackNode;)Z"))
    private boolean keepScoutingAtWaypoints(GlobalStation instance, TrackNode side) {
        return instance.canApproachFrom(side) && !snr$isWaypointMode();
    }

    @Redirect(method = "tick", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lcom/simibubi/create/content/trains/entity/Navigation;waitingForSignal:Lcom/simibubi/create/foundation/utility/Pair;"),
    slice = @Slice(
        from = @At(value = "CONSTANT", args = {"doubleValue=0.25d"}),
        to = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/Train;leaveStation()V")
    ))
    private Pair<UUID, Boolean> brakeProperlyAtWaypoints(Navigation instance) {
        return snr$isWaypointMode() ? null : instance.waitingForSignal;
    }

    @Redirect(method = "currentSignalResolved", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lcom/simibubi/create/content/trains/entity/Navigation;distanceToDestination:D"), slice =
    @Slice(to = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/graph/TrackGraph;getPoint(Lcom/simibubi/create/content/trains/graph/EdgePointType;Ljava/util/UUID;)Lcom/simibubi/create/content/trains/signal/TrackEdgePoint;")))
    private double preventSignalClearWithWaypoint(Navigation instance) {
        if (((IWaypointableNavigation) instance).snr$isWaypointMode())
            return 10;
        return instance.distanceToDestination;
    }

    @Redirect(method = "search(DDZLcom/simibubi/create/content/trains/entity/Navigation$StationTest;)V", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/station/GlobalStation;getPresentTrain()Lcom/simibubi/create/content/trains/entity/Train;"))
    private Train replacePresentTrain(GlobalStation instance) {
        return ((ILimitedGlobalStation) instance).orDisablingTrain(instance.getPresentTrain(), train);
    }

    @SuppressWarnings("unused")
    @ModifyExpressionValue(method = "search(DDZLcom/simibubi/create/content/trains/entity/Navigation$StationTest;)V",
        at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"))
    private boolean isNavigationIncompatible(boolean original, @Local Map.Entry<TrackNode, TrackEdge> target) {
        if (target.getValue().getTrackMaterial().trackType == CRTrackType.UNIVERSAL)
            return true;
        return original;
    }

    @Override
    public void snr$searchGeneral(double maxDistance, boolean forward, PointTest pointTest) {
        snr$searchGeneral(maxDistance, -1, forward, pointTest);
    }

    @Override
    public void snr$searchGeneral(double maxDistance, double maxCost, boolean forward, PointTest pointTest) {
        TrackGraph graph = train.graph;
        if (graph == null)
            return;

        /*
        Valid types work as follows:
        if the FIRST bogey is universal, set skipValidCheck to true
        if any bogey is not universal, set skipValidCheck to false

        if addAllDone not set yet, do addAll
        else: if universal, do nothing, else retainAll
         */

        // Cache the list of track types that the train can travel on
        boolean skipValidCheck = false;
        Set<TrackType> validTypes = new HashSet<>();
        for (int i = 0; i < train.carriages.size(); i++) {
            Carriage carriage = train.carriages.get(i);
            AbstractBogeyBlock<?> leadingType = ((AccessorCarriageBogey) carriage.leadingBogey()).getType();
            AbstractBogeyBlock<?> trailingType = ((AccessorCarriageBogey) carriage.trailingBogey()).getType();
            if (leadingType.getTrackType(carriage.leadingBogey().getStyle()) == CRTrackType.UNIVERSAL) { // todo PR this into Create
                if (i == 0) {
                    skipValidCheck = true;
                }
            } else {
                if (i == 0 || skipValidCheck) {
                    validTypes.addAll(leadingType.getValidPathfindingTypes(carriage.leadingBogey().getStyle()));
                } else {
                    validTypes.retainAll(leadingType.getValidPathfindingTypes(carriage.leadingBogey().getStyle()));
                }
                skipValidCheck = false;
            }
            if (carriage.isOnTwoBogeys()) {
                if (trailingType.getTrackType(carriage.trailingBogey().getStyle()) != CRTrackType.UNIVERSAL) {
                    if (skipValidCheck) {
                        validTypes.addAll(trailingType.getValidPathfindingTypes(carriage.trailingBogey().getStyle()));
                    } else {
                        validTypes.retainAll(trailingType.getValidPathfindingTypes(carriage.trailingBogey().getStyle()));
                    }
                    skipValidCheck = false;
                }
            }
        }
        if (validTypes.isEmpty() && !skipValidCheck) // if there are no valid track types, a route can't be found
            return;

        Map<TrackEdge, Integer> penalties = new IdentityHashMap<>();
        boolean costRelevant = maxCost >= 0;
        if (costRelevant) {
            for (Train otherTrain : Create.RAILWAYS.trains.values()) {
                if (otherTrain.graph != graph)
                    continue;
                if (otherTrain == train)
                    continue;
                int navigationPenalty = otherTrain.getNavigationPenalty();
                otherTrain.getEndpointEdges()
                        .forEach(nodes -> {
                            if (nodes.either(Objects::isNull))
                                return;
                            for (boolean flip : Iterate.trueAndFalse) {
                                TrackEdge e = graph.getConnection(flip ? nodes.swap() : nodes);
                                if (e == null)
                                    continue;
                                int existing = penalties.getOrDefault(e, 0);
                                penalties.put(e, existing + navigationPenalty / 2);
                            }
                        });
            }
        }

        TravellingPoint startingPoint = forward ? train.carriages.get(0)
                .getLeadingPoint()
                : train.carriages.get(train.carriages.size() - 1)
                .getTrailingPoint();

        Set<TrackEdge> visited = new HashSet<>();
        Map<TrackEdge, Pair<Boolean, Couple<TrackNode>>> reachedVia = new IdentityHashMap<>();
        PriorityQueue<FrontierEntry> frontier = new PriorityQueue<>();

        TrackNode initialNode1 = forward ? startingPoint.node1 : startingPoint.node2;
        TrackNode initialNode2 = forward ? startingPoint.node2 : startingPoint.node1;
        TrackEdge initialEdge = graph.getConnectionsFrom(initialNode1)
                .get(initialNode2);
        if (initialEdge == null)
            return;

        double distanceToNode2 = forward ? initialEdge.getLength() - startingPoint.position : startingPoint.position;

        frontier.add(new FrontierEntry(distanceToNode2, 0, initialNode1, initialNode2, initialEdge));
        int signalWeight = Mth.clamp(ticksWaitingForSignal * 2, AccessorTrain.AccessorPenalties.getRED_SIGNAL(), 200);

        Search: while (!frontier.isEmpty()) {
            FrontierEntry entry = frontier.poll();
            if (!visited.add(entry.edge))
                continue;

            double distance = entry.distance;
            int penalty = entry.penalty;

            if (distance > maxDistance)
                continue;

            TrackEdge edge = entry.edge;
            TrackNode node1 = entry.node1;
            TrackNode node2 = entry.node2;

            if (costRelevant)
                penalty += penalties.getOrDefault(edge, 0);

            EdgeData signalData = edge.getEdgeData();
            if (signalData.hasPoints()) {
                for (TrackEdgePoint point : signalData.getPoints()) {
                    if (node1 == initialNode1 && point.getLocationOn(edge) < edge.getLength() - distanceToNode2)
                        continue;
                    if (costRelevant && distance + penalty > maxCost)
                        continue Search;
                    if (!point.canNavigateVia(node2))
                        continue Search;
                    if (point instanceof SignalBoundary signal) {
                        if (signal.isForcedRed(node2)) {
                            penalty += AccessorTrain.AccessorPenalties.getREDSTONE_RED_SIGNAL();
                            continue;
                        }
                        UUID group = signal.getGroup(node2);
                        if (group == null)
                            continue;
                        SignalEdgeGroup signalEdgeGroup = Create.RAILWAYS.signalEdgeGroups.get(group);
                        if (signalEdgeGroup == null)
                            continue;
                        if (signalEdgeGroup.isOccupiedUnless(signal)) {
                            penalty += signalWeight;
                            signalWeight /= 2;
                        }
                    }
                    if (point instanceof GlobalStation station) {
                        Train presentTrain = station.getPresentTrain();
                        boolean isOwnStation = presentTrain == train;
                        if (presentTrain != null && !isOwnStation)
                            penalty += AccessorTrain.AccessorPenalties.getSTATION_WITH_TRAIN();
                        if (station.canApproachFrom(node2) && pointTest.test(distance, distance + penalty, reachedVia,
                                Pair.of(Couple.create(node1, node2), edge), station))
                            return;
                        if (!isOwnStation)
                            penalty += AccessorTrain.AccessorPenalties.getSTATION();
                    }
                    if (pointTest.test(distance, distance + penalty, reachedVia,
                            Pair.of(Couple.create(node1, node2), edge), point))
                        return;
                }
            }

            if (costRelevant && distance + penalty > maxCost)
                continue;

            List<Map.Entry<TrackNode, TrackEdge>> validTargets = new ArrayList<>();
            Map<TrackNode, TrackEdge> connectionsFrom = graph.getConnectionsFrom(node2);
            for (Map.Entry<TrackNode, TrackEdge> connection : connectionsFrom.entrySet()) {
                TrackNode newNode = connection.getKey();
                if (newNode == node1)
                    continue;
                if (edge.canTravelTo(connection.getValue()))
                    validTargets.add(connection);
            }

            if (validTargets.isEmpty())
                continue;

            for (Map.Entry<TrackNode, TrackEdge> target : validTargets) {
                if (!(skipValidCheck || validTypes.contains(target.getValue().getTrackMaterial().trackType) || target.getValue().getTrackMaterial().trackType == CRTrackType.UNIVERSAL))
                    continue;
                TrackNode newNode = target.getKey();
                TrackEdge newEdge = target.getValue();
                double newDistance = newEdge.getLength() + distance;
                int newPenalty = penalty;
                reachedVia.putIfAbsent(newEdge, Pair.of(validTargets.size() > 1, Couple.create(node1, node2)));
                frontier.add(new FrontierEntry(newDistance, newPenalty, node2, newNode, newEdge));
            }
        }
    }

    @Override
    public Pair<TrackSwitch, Pair<Boolean, Optional<SwitchState>>> snr$findNearestApproachableSwitch(boolean forward) {
        TrackGraph graph = train.graph;
        if (graph == null)
            return null;

        MutableObject<TrackSwitch> result = new MutableObject<>(null);
        MutableObject<Boolean> headOn = new MutableObject<>(false);
        MutableObject<SwitchState> targetState = new MutableObject<>(null);
        double acceleration = train.acceleration();
        double minDistance = 0;//.75f * (train.speed * train.speed) / (2 * acceleration);
        double maxDistance = Math.max(32, 1.5f * (train.speed * train.speed) / (2 * acceleration));

        snr$searchGeneral(maxDistance, forward, (distance, cost, reachedVia, currentEntry, trackPoint) -> {
            if (distance < minDistance)
                return false;

            TrackEdge edge = currentEntry.getSecond();
            double position = edge.getLength() - trackPoint.getLocationOn(edge);
            if (distance - position < minDistance)
                return false;
            if (trackPoint instanceof TrackSwitch sw) {
                TrackNode node = currentEntry.getFirst().getSecond();
                headOn.setValue(sw.isPrimary(node));
                result.setValue(sw);
                if (!headOn.getValue()) {
                    // find the targeted switch direction
                    for (TrackEdge reachedEdge : reachedVia.keySet()) {
                        SwitchState state = sw.getTargetState(reachedEdge.node1.getLocation());
                        if (state == null)
                            state = sw.getTargetState(reachedEdge.node2.getLocation());
                        targetState.setValue(state);
                        if (state != null)
                            break;
                    }
                }
                return true;
            } else {
                return false;
            }
        });

        return Pair.of(result.getValue(), Pair.of(headOn.getValue(), Optional.ofNullable(targetState.getValue())));
    }

    @Inject(method = "search(DDZLcom/simibubi/create/content/trains/entity/Navigation$StationTest;)V", at = @At("HEAD"))
    private void recordSearch(double maxDistance, double maxCost, boolean forward, Navigation.StationTest stationTest, CallbackInfo ci) {
        Railways.navigationCallDepth += 1;
    }

    @Inject(method = "search(DDZLcom/simibubi/create/content/trains/entity/Navigation$StationTest;)V", at = @At("RETURN"))
    private void recordSearchReturn(double maxDistance, double maxCost, boolean forward, Navigation.StationTest stationTest, CallbackInfo ci) {
        if (Railways.navigationCallDepth > 0)
            Railways.navigationCallDepth -= 1;
    }

    @Inject(method = "findNearestApproachable", at = @At("HEAD"), cancellable = true)
    private void handcarsCannotApproachStations(boolean forward, CallbackInfoReturnable<GlobalStation> cir) {
        if (((IHandcarTrain) this.train).snr$isHandcar())
            cir.setReturnValue(null);
    }

    // can't use @Share across methods (lambda$tick$0 and tick count as separate methods)
    @Unique
    private final ThreadLocal<Double> snr$bufferDistance = ThreadLocal.withInitial(() -> Double.MAX_VALUE);

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void resetBufferDistance(Level level, CallbackInfo ci) {
        snr$bufferDistance.set(Double.MAX_VALUE);
    }

    @Inject(method = "lambda$tick$0", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/Pair;getFirst()Ljava/lang/Object;"))
    private void storeBufferSlowdown(MutableObject<Pair<UUID, Boolean>> trackingCrossSignal, double scanDistance,
                                     MutableDouble crossSignalDistanceTracker, double brakingDistanceNoFlicker,
                                     Double distance, Pair<TrackEdgePoint, Couple<TrackNode>> couple,
                                     CallbackInfoReturnable<Boolean> cir) {
        if (couple.getFirst() instanceof TrackBuffer trackBuffer) {
            // don't stop *right* on the buffer block, stop a little bit before
            double bufferedDistance = Math.max(0, distance - TrackBuffer.getBufferRoom(this.train));
            snr$bufferDistance.set(Math.min(snr$bufferDistance.get(), bufferedDistance));
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/Train;burnFuel()V"))
    private void applyBufferSlowdown(Level level, CallbackInfo ci,
                                     @Local(name = "targetDistance") LocalDoubleRef targetDistance) {
        if (snr$bufferDistance.get() < targetDistance.get())
            targetDistance.set(snr$bufferDistance.get());
        // reset buffer distance for next time
        snr$bufferDistance.set(Double.MAX_VALUE);
    }

    @ModifyVariable(method = "tick", at = @At(value = "NEW", target = "(D)Lorg/apache/commons/lang3/mutable/MutableDouble;", ordinal = 0), name = "brakingDistance")
    private double ensureSufficientBufferDistance(double brakingDistance) {
        return brakingDistance + TrackBuffer.getBufferRoom(train);
    }

    @ModifyVariable(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(DDD)D"), name = "brakingDistance")
    private double resetSufficientBufferDistance(double brakingDistance) {
        return brakingDistance - TrackBuffer.getBufferRoom(train);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void respectBuffersWithoutSchedule(Level level, CallbackInfo ci) {
        ((IBufferBlockedTrain) train).snr$setControlBlocked(false);
        if (destination == null) {
            double acceleration = train.acceleration();
            double brakingDistance = (train.speed * train.speed) / (2 * acceleration);
            boolean currentlyBackwards = train.speed < 0;
            double speedMod = currentlyBackwards ? -1 : 1;
            double preDepartureLookAhead = train.getCurrentStation() != null ? 4.5 : 0;
            double distanceToNextCurve = -1;

            if (train.graph == null) return;

            TravellingPoint leadingPoint = !currentlyBackwards ? train.carriages.get(0).getLeadingPoint()
                : train.carriages.get(train.carriages.size() - 1).getTrailingPoint();

            signalScout.node1 = leadingPoint.node1;
            signalScout.node2 = leadingPoint.node2;
            signalScout.edge = leadingPoint.edge;
            signalScout.position = leadingPoint.position;

            double brakingDistance2 = brakingDistance + TrackBuffer.getBufferRoom(train);
            double brakingDistanceNoFlicker = brakingDistance2 + 3 - (brakingDistance2 % 3);

            double scanDistance = Mth.clamp(brakingDistanceNoFlicker, preDepartureLookAhead, 500);

            MutableDouble bufferDistance = new MutableDouble(Double.MAX_VALUE);

            signalScout.travel(train.graph, (scanDistance + 50) * speedMod, controlSignalScout(),
                (distance, couple) -> {
                    if (distance > scanDistance) return true;

                    if (couple.getFirst() instanceof TrackBuffer trackBuffer) {
                        // don't stop *right* on the buffer block, stop a little bit before
                        double bufferedDistance = Math.max(0, distance - TrackBuffer.getBufferRoom(this.train, currentlyBackwards));
                        bufferDistance.setValue(Math.min(bufferDistance.getValue(), bufferedDistance));
                        return true;
                    }

                    return false;
                }, (distance, edge) -> {});

            if (bufferDistance.getValue() >= Double.MAX_VALUE)
                return;

            double targetDistance = bufferDistance.getValue();
            targetDistance += 0.25;

            /*if (targetDistance - Math.abs(train.speed) < 1 / 32f) {
                train.speed = Math.max(targetDistance, 1 / 32f) * speedMod;
                return;
            }*/

            if (targetDistance < 3)
                ((IBufferBlockedTrain) train).snr$setControlBlocked(true);

            if (targetDistance < 10) {
                double target = train.maxSpeed() * ((targetDistance) / 10);
                if (target < Math.abs(train.speed)) {
                    train.speed += (target - Math.abs(train.speed)) * .5f * speedMod;
                    return;
                }
            }

            if (targetDistance <= brakingDistance) {
                train.targetSpeed = 0;
                train.approachTargetSpeed(1);
            }
        }
    }
}
