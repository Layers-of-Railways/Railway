package com.railwayteam.railways.mixin_interfaces;

import com.simibubi.create.content.logistics.trains.management.edgePoint.station.GlobalStation;
import com.simibubi.create.content.logistics.trains.management.schedule.ScheduleRuntime;

public interface ICustomExecutableInstruction {
    void execute(ScheduleRuntime runtime);

    default GlobalStation executeWithStation(ScheduleRuntime runtime) {
        execute(runtime);
        return null;
    }
}
