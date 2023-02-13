package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.compat.journeymap.DummyRailwayMarkerHandler;
import com.railwayteam.railways.compat.journeymap.TrainMarkerData;
import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class TrainMarkerDataUpdatePacket extends SimplePacketBase { //TODO partial sync with only pos + dimension

    private static final UUID NULL_ID = new UUID(0, 0);

    final UUID id;
    final TrainMarkerData data;

    public TrainMarkerDataUpdatePacket(Train train) {
        this.id = train.id;
        this.data = TrainMarkerData.make(train);
    }

    private static Optional<String> optionalString(String string) {
        return (string == null || string.isEmpty()) ? Optional.empty() : Optional.of(string);
    }

    public TrainMarkerDataUpdatePacket(FriendlyByteBuf buf) {
        id = buf.readUUID();
        data = new TrainMarkerData(
                buf.readUtf(),
                buf.readInt(),
                buf.readUUID(),
                buf.readUtf(),
                ResourceKey.create(Registry.DIMENSION_REGISTRY, buf.readResourceLocation()),
                buf.readBlockPos(),
                buf.readBoolean()
        );
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(id);
        buffer.writeUtf(data.name());
        buffer.writeInt(data.carriageCount());
        buffer.writeUUID(Optional.ofNullable(data.owner()).orElse(NULL_ID));
        buffer.writeUtf(data.destination());
        buffer.writeResourceLocation(data.dimension().location());
        buffer.writeBlockPos(data.pos());
        buffer.writeBoolean(data.incomplete());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(()-> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()-> ()-> this.__handle(context)));
        context.get().setPacketHandled(true);
    }

    @Environment(EnvType.CLIENT)
    private void __handle(Supplier<NetworkEvent.Context> supplier) {
        if (!data.incomplete())
            DummyRailwayMarkerHandler.getInstance().registerData(id, data);
    }
}
