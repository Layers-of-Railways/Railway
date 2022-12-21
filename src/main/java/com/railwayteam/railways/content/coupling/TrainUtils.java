package com.railwayteam.railways.content.coupling;

import com.railwayteam.railways.mixin.AccessorTrain;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.util.packet.CarriageContraptionEntityUpdatePacket;
import com.railwayteam.railways.util.packet.ChopTrainEndPacket;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.trains.entity.*;
import com.simibubi.create.foundation.networking.AllPackets;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.UUID;

public class TrainUtils {
    /**
     * Splits the train into two trains, with the last carriage of the original train being the first carriage of the new train.
     * @param train The train to split.
     * @return The new train.
     */
    public static Train splitTrain(Train train) {
        if (train.carriages.size() <= 1)
            return train;
        /*things needing changing:
            train.stress
                original train: done
                new train:      unnecessary
            train.carriageSpacing
                original train: done
                new train:      done

            lastCarriage.train   done
            lastCarriage.storage
            lastCarriage.forEachPresentEntity(e -> e.trainId);

            newTrain
        */
        Carriage lastCarriage = train.carriages.remove(train.carriages.size() - 1);
        double[] originalStress = ((AccessorTrain) train).snr_getStress();
        double[] newStress = new double[originalStress.length - 1];
        System.arraycopy(originalStress, 0, newStress, 0, newStress.length);
        ((AccessorTrain) train).snr_setStress(newStress);
        train.carriageSpacing.remove(train.carriageSpacing.size() - 1);

        Train newTrain = new Train(UUID.randomUUID(), train.owner, train.graph, List.of(lastCarriage), List.of(), train.doubleEnded);
        newTrain.name = Component.literal("Split off from: "+train.name.getString());
//        lastCarriage.setTrain(newTrain);
//        lastCarriage.storage = null; //since storage is per-carriage, not per-train, this should be fine
        lastCarriage.forEachPresentEntity(cce -> {
            cce.carriageIndex = 0;
            cce.trainId = newTrain.id;
            cce.setCarriage(lastCarriage);
//            CarriageContraption cc = (CarriageContraption) cce.getContraption();
            cce.syncCarriage();
        });
        newTrain.collectInitiallyOccupiedSignalBlocks();
        Create.RAILWAYS.addTrain(newTrain);
        AllPackets.channel.send(PacketDistributor.ALL.noArg(), new TrainPacket(newTrain, true));
//        AllPackets.channel.send(PacketDistributor.ALL.noArg(), new TrainPacket(train, true));

        lastCarriage.forEachPresentEntity(CarriageContraptionEntity::syncCarriage);
        train.carriages.forEach(carriage -> carriage.forEachPresentEntity(CarriageContraptionEntity::syncCarriage));
        newTrain.carriages.forEach(carriage -> carriage.forEachPresentEntity(CarriageContraptionEntity::syncCarriage));

        //TODO clientside carriages need to update carriage.train and cce.trainId
        // if we update cce.trainId and set cce.carriage to null and call cce.bindCarriage() and then
        // set cce.carriage.train to the correct train, we should be good (try skipping this last line to test some stuff)
        lastCarriage.forEachPresentEntity(cce -> CRPackets.channel.send(PacketDistributor.ALL.noArg(), new CarriageContraptionEntityUpdatePacket(cce, newTrain)));
        CRPackets.channel.send(PacketDistributor.ALL.noArg(), new ChopTrainEndPacket(train, 1));

        return newTrain;
    }
}
