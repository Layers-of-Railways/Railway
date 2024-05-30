/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.coupling;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.mixin.AccessorAbstractContraptionEntity;
import com.railwayteam.railways.mixin.AccessorOrientedContraptionEntity;
import com.railwayteam.railways.mixin.AccessorScheduleRuntime;
import com.railwayteam.railways.mixin.AccessorTrain;
import com.railwayteam.railways.mixin_interfaces.IHandcarTrain;
import com.railwayteam.railways.mixin_interfaces.IIndexedSchedule;
import com.railwayteam.railways.mixin_interfaces.IStrictSignalTrain;
import com.railwayteam.railways.multiloader.PlayerSelection;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.util.packet.AddTrainEndPacket;
import com.railwayteam.railways.util.packet.CarriageContraptionEntityUpdatePacket;
import com.railwayteam.railways.util.packet.ChopTrainEndPacket;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.ContraptionDisassemblyPacket;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.trains.entity.*;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TrainUtils {
    /**
     * Splits the train into two trains, with the last carriage of the original train being the first carriage of the new train.
     * @param train The train to split.
     * @param numberOffEnd The number of carriages to move to the new train.
     * @return The new train.
     */
    public static Train splitTrain(Train train, int numberOffEnd) {
        if (((IHandcarTrain) train).railways$isHandcar()) return train;
        if (numberOffEnd == 0)
            return train;
        if (train.carriages.size() <= numberOffEnd)
            return train;
        if (!allCarriagesLoaded(train))
            return train;

        Integer frontSpacingBackup  = null;
        Carriage[] lastCarriages = new Carriage[numberOffEnd];
        Integer[] lastCarriageSpacings = new Integer[numberOffEnd - 1];

        for (int i = numberOffEnd-1; i >= 0; i--) {
            lastCarriages[i] = train.carriages.remove(train.carriages.size() - 1);
            if (i > 0) {
                lastCarriageSpacings[i-1] = train.carriageSpacing.remove(train.carriageSpacing.size() - 1);
            } else { //discard front spacing - there is no spacing between the front carriage and nothing (or the back carriage and nothing)
                frontSpacingBackup = train.carriageSpacing.remove(train.carriageSpacing.size() - 1);
            }
        }

//        Carriage lastCarriage = train.carriages.remove(train.carriages.size() - 1);
        double[] originalStress = ((AccessorTrain) train).railways$getStress();
        double[] newStress = new double[originalStress.length - numberOffEnd];
        System.arraycopy(originalStress, 0, newStress, 0, newStress.length);
        ((AccessorTrain) train).railways$setStress(newStress);
//        train.carriageSpacing.remove(train.carriageSpacing.size() - 1);

        Train newTrain;
        try {
            newTrain = new Train(UUID.randomUUID(), train.owner, train.graph, new ArrayList<>(List.of(lastCarriages)), new ArrayList<>(List.of(lastCarriageSpacings)), Arrays.stream(lastCarriages).anyMatch(carriage -> carriage.anyAvailableEntity().getContraption() instanceof CarriageContraption carriageContraption && carriageContraption.hasBackwardControls()));
        } catch (NullPointerException e) {
            train.carriages.addAll(List.of(lastCarriages));
            if (frontSpacingBackup != null)
                train.carriageSpacing.add(frontSpacingBackup);
            train.carriageSpacing.addAll(List.of(lastCarriageSpacings));
            ((AccessorTrain) train).railways$setStress(originalStress);
            return train;
        }
        train.doubleEnded = train.carriages.stream().anyMatch(carriage -> carriage.anyAvailableEntity().getContraption() instanceof CarriageContraption carriageContraption && carriageContraption.hasBackwardControls());
        if(!train.name.getString().contains("Split off from: ")){
            newTrain.name = Components.literal("Split off from: "+train.name.getString());
        }
        else newTrain.name = Components.literal(train.name.getString());



//        lastCarriage.setTrain(newTrain);
//        lastCarriage.storage = null; //since storage is per-carriage, not per-train, this should be fine
        for (int i = 0; i < lastCarriages.length; i++) {
            Carriage lastCarriage = lastCarriages[i];
            int finalI = i;
            lastCarriage.forEachPresentEntity(cce -> {
                cce.carriageIndex = finalI;
                cce.trainId = newTrain.id;
                cce.setCarriage(lastCarriage);
//            CarriageContraption cc = (CarriageContraption) cce.getContraption();
                cce.syncCarriage();
            });
        }

        // move new train back and forth a little bit to prevent signal overruns
        {
            final double bufferDist = 0.1;
            Carriage leadingCarriage = newTrain.carriages.get(0);
            TravellingPoint returnPoint = copy(leadingCarriage.getLeadingPoint());
            leadingCarriage.travel(null, newTrain.graph, -bufferDist, null, null, 0);
            newTrain.collectInitiallyOccupiedSignalBlocks();
            ((IStrictSignalTrain) newTrain).railways$setStrictSignals(true);
            leadingCarriage.travel(null, newTrain.graph, bufferDist, returnPoint, null, 0);
            ((IStrictSignalTrain) newTrain).railways$setStrictSignals(false);
            newTrain.collectInitiallyOccupiedSignalBlocks();
        }
        train.updateSignalBlocks = true;

        Create.RAILWAYS.addTrain(newTrain);
        CRPackets.PACKETS.sendTo(PlayerSelection.all(), new TrainPacket(newTrain, true));
//        CRPackets.PACKETS.sendTo(PlayerSelection.all(), new TrainPacket(train, true));

        Arrays.stream(lastCarriages).forEach(c -> c.forEachPresentEntity(CarriageContraptionEntity::syncCarriage));
//        lastCarriage.forEachPresentEntity(CarriageContraptionEntity::syncCarriage);
        train.carriages.forEach(carriage -> carriage.forEachPresentEntity(CarriageContraptionEntity::syncCarriage));
        newTrain.carriages.forEach(carriage -> carriage.forEachPresentEntity(CarriageContraptionEntity::syncCarriage));

        //DONE clientside carriages need to update carriage.train and cce.trainId
        // if we update cce.trainId and set cce.carriage to null and call cce.bindCarriage() and then
        // set cce.carriage.train to the correct train, we should be good (try skipping this last line to test some stuff)
        PlayerSelection allPlayers = PlayerSelection.all();
        Arrays.stream(lastCarriages).forEach(
            c -> c.forEachPresentEntity(
                cce -> CRPackets.PACKETS.sendTo(allPlayers, new CarriageContraptionEntityUpdatePacket(cce, newTrain))
            )
        );
        CRPackets.PACKETS.sendTo(allPlayers, new ChopTrainEndPacket(train, numberOffEnd, train.doubleEnded));

        if (train.runtime.getSchedule() != null && ((IIndexedSchedule) train).railways$getIndex() >= train.carriages.size()) {
            int newIndex = ((IIndexedSchedule) train).railways$getIndex() - train.carriages.size();
            ((IIndexedSchedule) newTrain).railways$setIndex(newIndex);

            newTrain.runtime.read(train.runtime.write());
            if (train.runtime.state == ScheduleRuntime.State.IN_TRANSIT) {
                newTrain.runtime.state = ScheduleRuntime.State.PRE_TRANSIT;
                ((AccessorScheduleRuntime) newTrain.runtime).setCooldown(0);
            }
            train.runtime.discardSchedule();
            Railways.LOGGER.info("[DISCARD_SCHEDULE] on train {} called in TrainUtils.splitTrain because it was transferred to a decoupled rear train because the train's schedule index {} was at least the carriage count {}", train.name.getString(), ((IIndexedSchedule) train).railways$getIndex(), train.carriages.size());
        }

        if (train.carriages.isEmpty()) {
            Create.RAILWAYS.removeTrain(train.id);
        }

        // park at nearby stations
        tryToParkNearby(newTrain, 0.75);
        newTrain.collectInitiallyOccupiedSignalBlocks();

        return newTrain;
    }

    public static void tryToParkNearby(Train train, double maxDistance) {
        {
            final double offsetDist = 0.05;
            Carriage leadingCarriage = train.carriages.get(0);
            leadingCarriage.travel(null, train.graph, -offsetDist, null, null, 0);
            TravellingPoint discoveryPoint = copy(leadingCarriage.getLeadingPoint());
            MutableObject<GlobalStation> targetStation = new MutableObject<>(null);
            double distance = discoveryPoint.travel(train.graph, maxDistance+offsetDist, discoveryPoint.steer(TravellingPoint.SteerDirection.NONE, new Vec3(0, 1, 0)), (Double a, Pair<TrackEdgePoint, Couple<TrackNode>> couple) -> {
                if (couple.getFirst() instanceof GlobalStation station && station.canApproachFrom(couple.getSecond().getSecond())
                    && (station.getNearestTrain() == null || station.getNearestTrain() == train) && station.getPresentTrain() == null) {
                    targetStation.setValue(station);
                    return true;
                }
                return false;
            });

            if (targetStation.getValue() != null) {
                Navigation oldNavigation = train.navigation;
                ScheduleRuntime oldRuntime = train.runtime;
                train.navigation = new Navigation(train);
                train.runtime = new ScheduleRuntime(train);
                train.navigation.destination = targetStation.getValue();
                leadingCarriage.travel(null, train.graph, Math.max(0.01, distance)+offsetDist, discoveryPoint, null, 0);
                targetStation.getValue().reserveFor(train);
                train.navigation.train = null; // prevent reference cycle
                train.runtime = oldRuntime;
                train.navigation = oldNavigation;
            } else {
                ((IStrictSignalTrain) train).railways$setStrictSignals(true);
                leadingCarriage.travel(null, train.graph, offsetDist, null, null, 0);
                ((IStrictSignalTrain) train).railways$setStrictSignals(false);
            }
        }
    }

    private static TravellingPoint copy(TravellingPoint original) {
        TravellingPoint copy = new TravellingPoint(original.node1, original.node2, original.edge, original.position, original.upsideDown);
        copy.blocked = original.blocked;
        return copy;
    }

    public static Train combineTrains(Train frontTrain, Train backTrain, BlockPos itemDropPos, Level itemDropLevel, int carriageSpacing) {
        return combineTrains(frontTrain, backTrain, Vec3.atBottomCenterOf(itemDropPos), itemDropLevel, carriageSpacing);
    }

    /**
     * Adds the carriages of backTrain onto the end of frontTrain.
     */
    public static Train combineTrains(Train frontTrain, Train backTrain, Vec3 itemDropPos, Level itemDropLevel, int carriageSpacing) {
        if (((IHandcarTrain) frontTrain).railways$isHandcar() || ((IHandcarTrain) backTrain).railways$isHandcar()) {
            return frontTrain;
        }
        if (frontTrain.derailed || backTrain.derailed) {
            return frontTrain;
        }
        if (!allCarriagesLoaded(frontTrain) || !allCarriagesLoaded(backTrain)) {
            return frontTrain;
        }
        int frontTrainSize = frontTrain.carriages.size();
        frontTrain.carriages.addAll(backTrain.carriages);
        backTrain.carriages.clear();

        frontTrain.carriageSpacing.add(carriageSpacing);
        frontTrain.carriageSpacing.addAll(backTrain.carriageSpacing);
        backTrain.carriageSpacing.clear();
        double[] newStress = new double[((AccessorTrain) frontTrain).railways$getStress().length + ((AccessorTrain) backTrain).railways$getStress().length + 1];
        System.arraycopy(((AccessorTrain) frontTrain).railways$getStress(), 0, newStress, 0, ((AccessorTrain) frontTrain).railways$getStress().length);
        newStress[((AccessorTrain) frontTrain).railways$getStress().length] = 0;
        System.arraycopy(((AccessorTrain) backTrain).railways$getStress(), 0, newStress, ((AccessorTrain) frontTrain).railways$getStress().length + 1, ((AccessorTrain) backTrain).railways$getStress().length);
        ((AccessorTrain) frontTrain).railways$setStress(newStress);

        frontTrain.doubleEnded = frontTrain.carriages.stream().anyMatch(carriage -> carriage.anyAvailableEntity().getContraption() instanceof CarriageContraption carriageContraption && carriageContraption.hasBackwardControls());
        for (int i = 0; i < frontTrain.carriages.size(); i++) {
            int finalI = i;
            Carriage lastCarriage = frontTrain.carriages.get(i);
            lastCarriage.setTrain(frontTrain);
            frontTrain.carriages.get(i).forEachPresentEntity(cce -> {
                cce.carriageIndex = finalI;
                cce.trainId = frontTrain.id;
                cce.setCarriage(lastCarriage);
//                cce.syncCarriage();
            });
        }
        if (backTrain.getCurrentStation() != null) {
            backTrain.getCurrentStation().cancelReservation(backTrain);
        }
        frontTrain.collectInitiallyOccupiedSignalBlocks();
        Create.RAILWAYS.removeTrain(backTrain.id);
        PlayerSelection allPlayers = PlayerSelection.all();
        CRPackets.PACKETS.sendTo(allPlayers, new AddTrainEndPacket(frontTrain, backTrain, carriageSpacing, backTrain.doubleEnded));
        frontTrain.carriages.forEach(carriage -> carriage.forEachPresentEntity(cce ->
                CRPackets.PACKETS.sendTo(allPlayers, new CarriageContraptionEntityUpdatePacket(cce, frontTrain))
        ));
//        frontTrain.carriages.forEach(carriage -> carriage.forEachPresentEntity(CarriageContraptionEntity::syncCarriage));
        if (frontTrain.runtime.getSchedule() == null && backTrain.runtime.getSchedule() != null) {
            ((IIndexedSchedule) frontTrain).railways$setIndex(((IIndexedSchedule) backTrain).railways$getIndex() + frontTrainSize);
            frontTrain.runtime.read(backTrain.runtime.write());
            if (backTrain.runtime.state == ScheduleRuntime.State.IN_TRANSIT) {
                frontTrain.runtime.state = ScheduleRuntime.State.PRE_TRANSIT;
                ((AccessorScheduleRuntime) frontTrain.runtime).setCooldown(0);
            }
        } else if (backTrain.runtime.getSchedule() != null) {
            if (frontTrain.runtime.completed) {
                ItemStack stack = frontTrain.runtime.returnSchedule();
                Containers.dropItemStack(itemDropLevel, itemDropPos.x, itemDropPos.y, itemDropPos.z, stack);
                ((IIndexedSchedule) frontTrain).railways$setIndex(((IIndexedSchedule) backTrain).railways$getIndex() + frontTrainSize);
                frontTrain.runtime.read(backTrain.runtime.write());
                if (backTrain.runtime.state == ScheduleRuntime.State.IN_TRANSIT) {
                    frontTrain.runtime.state = ScheduleRuntime.State.PRE_TRANSIT;
                    ((AccessorScheduleRuntime) frontTrain.runtime).setCooldown(0);
                }
            } else {
                ItemStack stack = backTrain.runtime.returnSchedule();
                Containers.dropItemStack(itemDropLevel, itemDropPos.x, itemDropPos.y, itemDropPos.z, stack);
            }
        }
        return frontTrain;
    }

    public static boolean allCarriagesLoaded(Train train) {
        for (Carriage carriage : train.carriages) {
            if (carriage.anyAvailableEntity() == null) {
                return false;
            }
        }
        return true;
    }

    public static void discardTrain(Train train) {
        for (Carriage carriage : train.carriages) {
            CarriageContraptionEntity entity = carriage.anyAvailableEntity();
            if (entity == null) continue;

            StructureTransform transform = ((AccessorOrientedContraptionEntity) entity).railways$makeStructureTransform();

            CRPackets.PACKETS.sendTo(PlayerSelection.tracking(entity), new ContraptionDisassemblyPacket(entity.getId(), transform));
            entity.getContraption().addPassengersToWorld(entity.level, transform, entity.getPassengers());
            ((AccessorAbstractContraptionEntity) entity).railways$setSkipActorStop(true);
            entity.discard();
            entity.ejectPassengers();
            ((AccessorAbstractContraptionEntity) entity).railways$moveCollidedEntitiesOnDisassembly(transform);
        }
        train.invalid = true; // don't remove yet, otherwise concurrent modification exceptions happen
        /*Create.RAILWAYS.removeTrain(train.id);
        CRPackets.PACKETS.sendTo(PlayerSelection.all(), new TrainPacket(train, false));*/
    }
}
