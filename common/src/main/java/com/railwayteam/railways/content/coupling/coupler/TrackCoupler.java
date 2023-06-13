package com.railwayteam.railways.content.coupling.coupler;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.signal.SignalPropagator;
import com.simibubi.create.content.trains.signal.SingleBlockEntityEdgePoint;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.UUID;

public class TrackCoupler extends SingleBlockEntityEdgePoint {
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
    public void blockEntityAdded(BlockEntity tile, boolean front) {
        super.blockEntityAdded(tile, front);
        notifyTrains(tile.getLevel());
    }

    private void notifyTrains(Level level) {
        TrackGraph graph = Create.RAILWAYS.sided(level)
            .getGraph(level, edgeLocation.getFirst());
        if (graph == null)
            return;
        TrackEdge edge = graph.getConnection(edgeLocation.map(graph::locateNode));
        if (edge == null)
            return;
        SignalPropagator.notifyTrains(graph, edge);
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
