package com.railwayteam.railways.compat.journeymap;

import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

public record TrainMarkerData(String name, int carriageCount, UUID owner, String destination, ResourceKey<Level> dimension, BlockPos pos, boolean incomplete) {

    //I don't know if this is the intended fix, but we'll just have to wait and see
    public static Carriage carriage;

    public static final BlockPos ABSENT_POS = new BlockPos(1331, 0, 1331);

    public static TrainMarkerData make(Train train) {
        String name = train.name.getString();// + "NO YOU DONT";
        int carriageCount = train.carriages.size();
        UUID owner = train.owner;
        String destination = Optional.ofNullable(train.navigation.destination).map(s -> s.name).orElse("Unknown/Not Present");

        Carriage primary = train.carriages.get(0);
        CarriageBogey bogey = primary.leadingBogey();

        ResourceKey<Level> dimension = Level.END;
        BlockPos pos = ABSENT_POS;

        if (bogey.leading().node1 != null && bogey.leading().node2 != null) {
            dimension = bogey.leading().node1.getLocation().dimension;
            Vec3 vecPos = bogey.leading().getPosition(carriage.train.graph);
            pos = new BlockPos(vecPos);
            if (pos.equals(ABSENT_POS))
                pos = ABSENT_POS.above();
        }
        return new TrainMarkerData(name, carriageCount, owner, destination, dimension, pos, pos == ABSENT_POS);
    }
}
