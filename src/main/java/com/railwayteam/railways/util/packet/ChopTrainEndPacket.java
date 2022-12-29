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

public class ChopTrainEndPacket extends SimplePacketBase {
    final UUID trainId;
    final int numberOfCarriages;
    final boolean doubleEnded;

    public ChopTrainEndPacket(Train train, int numberOfCarriages, boolean doubleEnded) {
        trainId = train.id;
        this.numberOfCarriages = numberOfCarriages;
        this.doubleEnded = doubleEnded;
    }

    public ChopTrainEndPacket(FriendlyByteBuf buf) {
        trainId = buf.readUUID();
        numberOfCarriages = buf.readInt();
        doubleEnded = buf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.trainId);
        buffer.writeInt(this.numberOfCarriages);
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
            if (train != null) {
                for (int i = 0; i < numberOfCarriages; i++) {
                    train.carriages.remove(train.carriages.size() - 1);
                    train.carriageSpacing.remove(train.carriageSpacing.size() - 1);
                }
                double[] originalStress = ((AccessorTrain) train).snr_getStress();
                double[] newStress = new double[originalStress.length - numberOfCarriages];
                System.arraycopy(originalStress, 0, newStress, 0, newStress.length);
                ((AccessorTrain) train).snr_setStress(newStress);
                train.doubleEnded = doubleEnded;
            }
        }
    }
}
