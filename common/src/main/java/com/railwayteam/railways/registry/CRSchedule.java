package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.schedule.RedstoneLinkInstruction;
import com.railwayteam.railways.content.schedule.StationLoadedCondition;
import com.railwayteam.railways.content.schedule.WaypointDestinationInstruction;
import com.simibubi.create.content.logistics.trains.management.schedule.condition.ScheduleWaitCondition;
import com.simibubi.create.content.logistics.trains.management.schedule.destination.ScheduleInstruction;
import com.simibubi.create.foundation.utility.Pair;

import java.util.function.Supplier;

import static com.simibubi.create.content.logistics.trains.management.schedule.Schedule.CONDITION_TYPES;
import static com.simibubi.create.content.logistics.trains.management.schedule.Schedule.INSTRUCTION_TYPES;

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
