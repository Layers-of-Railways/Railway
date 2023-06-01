package com.railwayteam.railways.content.train_debug;

import com.simibubi.create.CreateClient;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

public class TravellingPointVisualizer {
    @SuppressWarnings("UnnecessaryBoxing")
    public static void debugTrain(Train train) {
        Minecraft mc = Minecraft.getInstance();
        Entity cameraEntity = mc.cameraEntity;
        if (cameraEntity == null)
            return;
        for (Carriage carriage : train.carriages) {
            Color color = Color.rainbowColor(carriage.hashCode());
            AABB box = (carriage.getDimensional(cameraEntity.level).entity.get() == null) ? null : carriage.getDimensional(cameraEntity.level).entity.get().getBoundingBox();
            if (true || box == null || box.intersects(cameraEntity.getBoundingBox()
                .inflate(50))) {
                for (CarriageBogey bogey : carriage.bogeys) {
                    if (bogey != null && bogey.leading() != null && bogey.trailing() != null && bogey.leading().edge != null && bogey.trailing().edge != null) {
                    /*for (TravellingPoint travellingPoint : new TravellingPoint[]{bogey.leading(), bogey.trailing()}) {
                        if (travellingPoint.node1.getLocation().getDimension() == cameraEntity.level.dimension() &&
                            travellingPoint.node2.getLocation().getDimension() == cameraEntity.level.dimension()) {
                            CreateClient.OUTLINER.showLine()
                        }
                    }*/
                        /*CreateClient.OUTLINER.showLine(Integer.valueOf(carriage.id * 2 + (bogey == carriage.leadingBogey() ? 0 : 1)), bogey.leading().getPosition(), bogey.trailing().getPosition())
                            .colored(color)
                            .lineWidth(18/16f);*/
                        int extent = 2;
                        CreateClient.OUTLINER.showLine(Integer.valueOf(carriage.id * 8 + (bogey == carriage.leadingBogey() ? 0 : 1) * 4 + 0),
                                bogey.getAnchorPosition().add(0, extent, 0), bogey.getAnchorPosition().add(0, -extent, 0))
                            .colored(color)
                            .lineWidth(4/16f);
                        CreateClient.OUTLINER.showLine(Integer.valueOf(carriage.id * 8 + (bogey == carriage.leadingBogey() ? 0 : 1) * 4 + 1),
                                bogey.getAnchorPosition().add(extent, 0, 0), bogey.getAnchorPosition().add(-extent, 0, 0))
                            .colored(color)
                            .lineWidth(4/16f);
                        CreateClient.OUTLINER.showLine(Integer.valueOf(carriage.id * 8 + (bogey == carriage.leadingBogey() ? 0 : 1) * 4 + 2),
                                bogey.getAnchorPosition().add(0, 0, extent), bogey.getAnchorPosition().add(0, 0, -extent))
                            .colored(color)
                            .lineWidth(4/16f);
                    }
                }
            }
        }
    }
}
