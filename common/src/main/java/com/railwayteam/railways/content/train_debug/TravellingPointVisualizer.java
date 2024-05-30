/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.train_debug;

import com.simibubi.create.CreateClient;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

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
                        Vec3 leadPos = bogey.leading().getPosition(train.graph);
                        Vec3 trailPos = bogey.trailing().getPosition(train.graph);
                        mc.level.addParticle(new DustParticleOptions(new Vector3f(0, 1, 0), 2.0f), leadPos.x, leadPos.y+2, leadPos.z, 0, 0, 0);
                        mc.level.addParticle(new DustParticleOptions(new Vector3f(1, 0, 0), 2.0f), trailPos.x, trailPos.y+2, trailPos.z, 0, 0, 0);
                        CreateClient.OUTLINER.showLine(Integer.valueOf(carriage.id * 2 + (bogey == carriage.leadingBogey() ? 0 : 1)), bogey.leading().getPosition(train.graph), bogey.trailing().getPosition(train.graph))
                            .colored(color)
                            .lineWidth(2/16f);
                        /*int extent = 2;
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
                            .lineWidth(4/16f);*/
                    }
                }
            }
        }
    }
}
