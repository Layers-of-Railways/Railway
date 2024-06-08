/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
            Vec3 vecPos = bogey.leading().getPosition(train.graph);
            pos = new BlockPos(vecPos);
            if (pos.equals(ABSENT_POS))
                pos = ABSENT_POS.above();
        }
        return new TrainMarkerData(name, carriageCount, owner, destination, dimension, pos, pos == ABSENT_POS);
    }
}
