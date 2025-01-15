/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.schedule.RedstoneLinkInstruction;
import com.railwayteam.railways.content.schedule.StationLoadedCondition;
import com.railwayteam.railways.content.schedule.WaypointDestinationInstruction;
import com.simibubi.create.content.trains.schedule.condition.ScheduleWaitCondition;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import net.createmod.catnip.data.Pair;

import java.util.function.Supplier;

import static com.simibubi.create.content.trains.schedule.Schedule.CONDITION_TYPES;
import static com.simibubi.create.content.trains.schedule.Schedule.INSTRUCTION_TYPES;

public class CRSchedule {
    static {
        registerInstruction("redstone_link", RedstoneLinkInstruction::new);
        registerInstruction("waypoint_destination", WaypointDestinationInstruction::new);
        registerCondition("loaded", StationLoadedCondition::new);
    }

    private static void registerInstruction(String name, Supplier<? extends ScheduleInstruction> factory) {
        INSTRUCTION_TYPES.add(Pair.of(Railways.asResource(name), factory));
    }

    private static void registerCondition(String name, Supplier<? extends ScheduleWaitCondition> factory) {
        CONDITION_TYPES.add(Pair.of(Railways.asResource(name), factory));
    }

    public static void register() {}
}
