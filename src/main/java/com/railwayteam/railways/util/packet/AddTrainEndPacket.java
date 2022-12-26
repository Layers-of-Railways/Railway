package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.mixin.AccessorTrain;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class AddTrainEndPacket extends SimplePacketBase {
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
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> this.__handle(context)));
        context.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void __handle (Supplier<NetworkEvent.Context> supplier) {
        Level level = Minecraft.getInstance().level;
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
