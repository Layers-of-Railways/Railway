package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.mixin.AccessorTrain;
import com.railwayteam.railways.multiloader.S2CPacket;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.trains.entity.Train;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class AddTrainEndPacket implements S2CPacket {
    final UUID trainId;
    final UUID backTrainId;
    final int middleSpacing;
    final boolean doubleEnded;

    public AddTrainEndPacket(Train train, Train backTrain, int middleSpacing, boolean doubleEnded) {
        trainId = train.id;
        this.backTrainId = backTrain.id;
        this.middleSpacing = middleSpacing;
        this.doubleEnded = doubleEnded;
    }

    public AddTrainEndPacket(FriendlyByteBuf buf) {
        trainId = buf.readUUID();
        backTrainId = buf.readUUID();
        middleSpacing = buf.readInt();
        doubleEnded = buf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.trainId);
        buffer.writeUUID(this.backTrainId);
        buffer.writeInt(this.middleSpacing);
        buffer.writeBoolean(this.doubleEnded);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void handle(Minecraft mc) {
        Level level = mc.level;
        if (level != null) {
            Train train = CreateClient.RAILWAYS.trains.get(trainId);
            Train backTrain = CreateClient.RAILWAYS.trains.get(backTrainId);
            if (train != null && backTrain != null) {
                train.carriages.addAll(backTrain.carriages);
                backTrain.carriages.clear();

                train.carriageSpacing.add(middleSpacing);
                train.carriageSpacing.addAll(backTrain.carriageSpacing);
                backTrain.carriageSpacing.clear();

                double[] newStress = new double[((AccessorTrain) train).snr_getStress().length + ((AccessorTrain) backTrain).snr_getStress().length + 1];
                System.arraycopy(((AccessorTrain) train).snr_getStress(), 0, newStress, 0, ((AccessorTrain) train).snr_getStress().length);
                newStress[((AccessorTrain) train).snr_getStress().length] = 0;
                System.arraycopy(((AccessorTrain) backTrain).snr_getStress(), 0, newStress, ((AccessorTrain) train).snr_getStress().length + 1, ((AccessorTrain) backTrain).snr_getStress().length);
                ((AccessorTrain) train).snr_setStress(newStress);
                train.doubleEnded = doubleEnded;

                train.carriages.forEach(c -> c.setTrain(train));

                CreateClient.RAILWAYS.trains.remove(backTrainId);
            }
        }
    }
}
