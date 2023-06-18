package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.schedule.WaypointDestinationInstruction;
import com.railwayteam.railways.content.switches.TrackSwitch;
import com.railwayteam.railways.mixin_interfaces.IGenerallySearchableNavigation;
import com.railwayteam.railways.mixin_interfaces.ILimitedGlobalStation;
import com.railwayteam.railways.mixin_interfaces.IWaypointableNavigation;
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
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.mutable.MutableObject;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.*;

@Mixin(value = Navigation.class, remap = false)
public abstract class MixinNavigation implements IWaypointableNavigation, IGenerallySearchableNavigation {

    @Shadow
    public Train train;

    @Shadow public int ticksWaitingForSignal;

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

    @Redirect(method = "tick", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lcom/simibubi/create/content/trains/entity/Navigation;distanceToDestination:D"))
    private double fixWaypointDistanceInTick(Navigation instance) {
        if (((IWaypointableNavigation) instance).isWaypointMode())
            return 1000;
        return instance.distanceToDestination;
    }

    @Redirect(method = "lambda$tick$0", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/station/GlobalStation;canApproachFrom(Lcom/simibubi/create/content/trains/graph/TrackNode;)Z"))
    private boolean keepScoutingAtWaypoints(GlobalStation instance, TrackNode side) {
        return instance.canApproachFrom(side) && !isWaypointMode();
    }

    @Redirect(method = "tick", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lcom/simibubi/create/content/trains/entity/Navigation;waitingForSignal:Lcom/simibubi/create/foundation/utility/Pair;"),
    slice = @Slice(
        from = @At(value = "CONSTANT", args = {"doubleValue=0.25d"}),
        to = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/Train;leaveStation()V")
    ))
    private Pair<UUID, Boolean> brakeProperlyAtWaypoints(Navigation instance) {
        return isWaypointMode() ? null : instance.waitingForSignal;
    }

    @Redirect(method = "currentSignalResolved", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lcom/simibubi/create/content/trains/entity/Navigation;distanceToDestination:D"), slice =
    @Slice(to = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/graph/TrackGraph;getPoint(Lcom/simibubi/create/content/trains/graph/EdgePointType;Ljava/util/UUID;)Lcom/simibubi/create/content/trains/signal/TrackEdgePoint;")))
    private double preventSignalClearWithWaypoint(Navigation instance) {
        if (((IWaypointableNavigation) instance).isWaypointMode())
            return 10;
        return instance.distanceToDestination;
    }

    @Redirect(method = "search(DDZLcom/simibubi/create/content/trains/entity/Navigation$StationTest;)V", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/station/GlobalStation;getPresentTrain()Lcom/simibubi/create/content/trains/entity/Train;"))
    private Train replacePresentTrain(GlobalStation instance) {
        return ((ILimitedGlobalStation) instance).orDisablingTrain(instance.getPresentTrain(), train);
    }

    // this is probably unnecessary since we already mixin into TrackEdge#canTravelTo
/*    private TrackEdge edge;
    @SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference", "InvalidInjectorMethodSignature"})
    @Redirect(method = "search(DDZLcom/simibubi/create/content/trains/entity/Navigation$StationTest;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/Map$Entry;getValue()Ljava/lang/Object;", ordinal = 1))
    private Object snr$captureEdge(Map.Entry<TrackNode, TrackEdge> instance) {
        edge = instance.getValue();
        return instance.getValue();
    }

    @Redirect(method = "search(DDZLcom/simibubi/create/content/trains/entity/Navigation$StationTest;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"))
    private boolean snr$blockSwitches(Set<TrackMaterial.TrackType> instance, Object o) {
        if (edge != null && !((ISwitchDisabledEdge) edge.getEdgeData()).isEnabled()) {
            edge = null;
            return false;
        }
        edge = null;
        TrackMaterial.TrackType type = (TrackMaterial.TrackType) o;
        return instance.contains(type);
    }*/

    public void searchGeneral(double maxDistance, boolean forward, PointTest pointTest) {
        searchGeneral(maxDistance, -1, forward, pointTest);
    }

    public void searchGeneral(double maxDistance, double maxCost, boolean forward, PointTest pointTest) {
        TrackGraph graph = train.graph;
        if (graph == null)
            return;

        // Cache the list of track types that the train can travel on
        Set<TrackMaterial.TrackType> validTypes = new HashSet<>();
        for (int i = 0; i < train.carriages.size(); i++) {
            Carriage carriage = train.carriages.get(i);
            AbstractBogeyBlock<?> leadingType = ((AccessorCarriageBogey) carriage.leadingBogey()).getType();
            AbstractBogeyBlock<?> trailingType = ((AccessorCarriageBogey) carriage.trailingBogey()).getType();
            if (i == 0) {
                validTypes.addAll(leadingType.getValidPathfindingTypes(carriage.leadingBogey().getStyle()));
            } else {
                validTypes.retainAll(leadingType.getValidPathfindingTypes(carriage.leadingBogey().getStyle()));
            }
            if (carriage.isOnTwoBogeys())
                validTypes.retainAll(trailingType.getValidPathfindingTypes(carriage.trailingBogey().getStyle()));
        }
        if (validTypes.isEmpty()) // if there are no valid track types, a route can't be found
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
                if (!validTypes.contains(target.getValue().getTrackMaterial().trackType))
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

    public Pair<TrackSwitch, Boolean> findNearestApproachableSwitch(boolean forward) {
        TrackGraph graph = train.graph;
        if (graph == null)
            return null;

        MutableObject<TrackSwitch> result = new MutableObject<>(null);
        MutableObject<Boolean> headOn = new MutableObject<>(false);
        double acceleration = train.acceleration();
        double minDistance = 0;//.75f * (train.speed * train.speed) / (2 * acceleration);
        double maxDistance = Math.max(32, 1.5f * (train.speed * train.speed) / (2 * acceleration));

        searchGeneral(maxDistance, forward, (distance, cost, reachedVia, currentEntry, trackPoint) -> {
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
                return true;
            } else {
                return false;
            }
        });

        return Pair.of(result.getValue(), headOn.getValue());
    }
}
