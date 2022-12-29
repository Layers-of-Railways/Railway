package com.railwayteam.railways.content.coupling.coupler;

import com.simibubi.create.content.logistics.trains.DimensionPalette;
import com.simibubi.create.content.logistics.trains.TrackGraph;
import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SingleTileEdgePoint;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class TrackCoupler extends SingleTileEdgePoint {
    private int activated;
    private UUID currentTrain;

    public TrackCoupler() {
        activated = 0;
        currentTrain = null;
    }

    @Override
    public void tick(TrackGraph graph, boolean preTrains) {
        super.tick(graph, preTrains);
        if (isActivated())
            activated--;
        if (!isActivated())
            currentTrain = null;
    }

    public UUID getCurrentTrain() {
        return currentTrain;
    }

    public boolean isActivated() {
        return activated > 0;
    }

    public void keepAlive(Train train) {
        activated = 8;
        currentTrain = train.id;
    }

    @Override
    public void read(CompoundTag nbt, boolean migration, DimensionPalette dimensions) {
        super.read(nbt, migration, dimensions);
        activated = nbt.getInt("Activated");
        if (nbt.contains("TrainId"))
            currentTrain = nbt.getUUID("TrainId");
    }

    @Override
    public void read(FriendlyByteBuf buffer, DimensionPalette dimensions) {
        super.read(buffer, dimensions);
    }

    @Override
    public void write(CompoundTag nbt, DimensionPalette dimensions) {
        super.write(nbt, dimensions);
        nbt.putInt("Activated", activated);
        if (currentTrain != null)
            nbt.putUUID("TrainId", currentTrain);
    }

    @Override
    public void write(FriendlyByteBuf buffer, DimensionPalette dimensions) {
        super.write(buffer, dimensions);
    }
}
