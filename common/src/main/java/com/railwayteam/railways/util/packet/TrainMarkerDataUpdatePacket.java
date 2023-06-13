package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.compat.journeymap.DummyRailwayMarkerHandler;
import com.railwayteam.railways.compat.journeymap.TrainMarkerData;
import com.railwayteam.railways.multiloader.S2CPacket;
import com.simibubi.create.content.trains.entity.Train;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;

import java.util.Optional;
import java.util.UUID;

public class TrainMarkerDataUpdatePacket implements S2CPacket { //TODO partial sync with only pos + dimension

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
    @Environment(EnvType.CLIENT)
    public void handle(Minecraft mc) {
        if (!data.incomplete())
            DummyRailwayMarkerHandler.getInstance().registerData(id, data);
    }
}
