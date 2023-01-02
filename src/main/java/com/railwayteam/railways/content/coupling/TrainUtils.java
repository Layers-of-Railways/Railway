package com.railwayteam.railways.content.coupling;

import com.railwayteam.railways.mixin.AccessorScheduleRuntime;
import com.railwayteam.railways.mixin.AccessorTrain;
import com.railwayteam.railways.mixin_interfaces.IIndexedSchedule;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.util.packet.AddTrainEndPacket;
import com.railwayteam.railways.util.packet.CarriageContraptionEntityUpdatePacket;
import com.railwayteam.railways.util.packet.ChopTrainEndPacket;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.trains.entity.*;
import com.simibubi.create.content.logistics.trains.management.schedule.ScheduleRuntime;
import com.simibubi.create.foundation.networking.AllPackets;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

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
        if (train.carriages.size() <= numberOffEnd)
            return train;

        Carriage[] lastCarriages = new Carriage[numberOffEnd];
        Integer[] lastCarriageSpacings = new Integer[numberOffEnd - 1];

        for (int i = numberOffEnd-1; i >= 0; i--) {
            lastCarriages[i] = train.carriages.remove(train.carriages.size() - 1);
            if (i > 0) {
                lastCarriageSpacings[i-1] = train.carriageSpacing.remove(train.carriageSpacing.size() - 1);
            } else { //discard front spacing - there is no spacing between the front carriage and nothing (or the back carriage and nothing)
                train.carriageSpacing.remove(train.carriageSpacing.size() - 1);
            }
        }

//        Carriage lastCarriage = train.carriages.remove(train.carriages.size() - 1);
        double[] originalStress = ((AccessorTrain) train).snr_getStress();
        double[] newStress = new double[originalStress.length - numberOffEnd];
        System.arraycopy(originalStress, 0, newStress, 0, newStress.length);
        ((AccessorTrain) train).snr_setStress(newStress);
//        train.carriageSpacing.remove(train.carriageSpacing.size() - 1);

        Train newTrain = new Train(UUID.randomUUID(), train.owner, train.graph, new ArrayList<>(List.of(lastCarriages)), new ArrayList<>(List.of(lastCarriageSpacings)), Arrays.stream(lastCarriages).anyMatch(carriage -> carriage.anyAvailableEntity().getContraption() instanceof CarriageContraption carriageContraption && carriageContraption.hasBackwardControls()));
        train.doubleEnded = train.carriages.stream().anyMatch(carriage -> carriage.anyAvailableEntity().getContraption() instanceof CarriageContraption carriageContraption && carriageContraption.hasBackwardControls());
        newTrain.name = Component.literal("Split off from: "+train.name.getString());
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
        newTrain.collectInitiallyOccupiedSignalBlocks();
        train.updateSignalBlocks = true;
        Create.RAILWAYS.addTrain(newTrain);
        AllPackets.channel.send(PacketDistributor.ALL.noArg(), new TrainPacket(newTrain, true));
//        AllPackets.channel.send(PacketDistributor.ALL.noArg(), new TrainPacket(train, true));

        Arrays.stream(lastCarriages).forEach(c -> c.forEachPresentEntity(CarriageContraptionEntity::syncCarriage));
//        lastCarriage.forEachPresentEntity(CarriageContraptionEntity::syncCarriage);
        train.carriages.forEach(carriage -> carriage.forEachPresentEntity(CarriageContraptionEntity::syncCarriage));
        newTrain.carriages.forEach(carriage -> carriage.forEachPresentEntity(CarriageContraptionEntity::syncCarriage));

        //DONE clientside carriages need to update carriage.train and cce.trainId
        // if we update cce.trainId and set cce.carriage to null and call cce.bindCarriage() and then
        // set cce.carriage.train to the correct train, we should be good (try skipping this last line to test some stuff)
        Arrays.stream(lastCarriages).forEach(
            c -> c.forEachPresentEntity(
                cce -> CRPackets.channel.send(PacketDistributor.ALL.noArg(), new CarriageContraptionEntityUpdatePacket(cce, newTrain))
            )
        );
        CRPackets.channel.send(PacketDistributor.ALL.noArg(), new ChopTrainEndPacket(train, numberOffEnd, train.doubleEnded));

        if (train.runtime.getSchedule() != null && ((IIndexedSchedule) train).getIndex() >= train.carriages.size()) {
            int newIndex = ((IIndexedSchedule) train).getIndex() - train.carriages.size();
            ((IIndexedSchedule) newTrain).setIndex(newIndex);

            newTrain.runtime.read(train.runtime.write());
            if (train.runtime.state == ScheduleRuntime.State.IN_TRANSIT) {
                newTrain.runtime.state = ScheduleRuntime.State.PRE_TRANSIT;
                ((AccessorScheduleRuntime) newTrain.runtime).setCooldown(0);
            }
            train.runtime.discardSchedule();
        }

        return newTrain;
    }

    public static Train combineTrains(Train frontTrain, Train backTrain, BlockPos itemDropPos, Level itemDropLevel, int carriageSpacing) {
        return combineTrains(frontTrain, backTrain, Vec3.atBottomCenterOf(itemDropPos), itemDropLevel, carriageSpacing);
    }

    /**
     * Adds the carriages of backTrain onto the end of frontTrain.
     */
    public static Train combineTrains(Train frontTrain, Train backTrain, Vec3 itemDropPos, Level itemDropLevel, int carriageSpacing) {
        int frontTrainSize = frontTrain.carriages.size();
        frontTrain.carriages.addAll(backTrain.carriages);
        backTrain.carriages.clear();

        frontTrain.carriageSpacing.add(carriageSpacing);
        frontTrain.carriageSpacing.addAll(backTrain.carriageSpacing);
        backTrain.carriageSpacing.clear();
        double[] newStress = new double[((AccessorTrain) frontTrain).snr_getStress().length + ((AccessorTrain) backTrain).snr_getStress().length + 1];
        System.arraycopy(((AccessorTrain) frontTrain).snr_getStress(), 0, newStress, 0, ((AccessorTrain) frontTrain).snr_getStress().length);
        newStress[((AccessorTrain) frontTrain).snr_getStress().length] = 0;
        System.arraycopy(((AccessorTrain) backTrain).snr_getStress(), 0, newStress, ((AccessorTrain) frontTrain).snr_getStress().length + 1, ((AccessorTrain) backTrain).snr_getStress().length);
        ((AccessorTrain) frontTrain).snr_setStress(newStress);

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
        CRPackets.channel.send(PacketDistributor.ALL.noArg(), new AddTrainEndPacket(frontTrain, backTrain, carriageSpacing, backTrain.doubleEnded));
        frontTrain.carriages.forEach(carriage -> carriage.forEachPresentEntity(cce -> CRPackets.channel.send(PacketDistributor.ALL.noArg(), new CarriageContraptionEntityUpdatePacket(cce, frontTrain))));
//        frontTrain.carriages.forEach(carriage -> carriage.forEachPresentEntity(CarriageContraptionEntity::syncCarriage));
        if (frontTrain.runtime.getSchedule() == null && backTrain.runtime.getSchedule() != null) {
            ((IIndexedSchedule) frontTrain).setIndex(((IIndexedSchedule) backTrain).getIndex() + frontTrainSize);
            frontTrain.runtime.read(backTrain.runtime.write());
            if (backTrain.runtime.state == ScheduleRuntime.State.IN_TRANSIT) {
                frontTrain.runtime.state = ScheduleRuntime.State.PRE_TRANSIT;
                ((AccessorScheduleRuntime) frontTrain.runtime).setCooldown(0);
            }
        } else if (backTrain.runtime.getSchedule() != null) {
            if (frontTrain.runtime.completed) {
                ItemStack stack = frontTrain.runtime.returnSchedule();
                Containers.dropItemStack(itemDropLevel, itemDropPos.x, itemDropPos.y, itemDropPos.z, stack);
                ((IIndexedSchedule) frontTrain).setIndex(((IIndexedSchedule) backTrain).getIndex() + frontTrainSize);
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
}
