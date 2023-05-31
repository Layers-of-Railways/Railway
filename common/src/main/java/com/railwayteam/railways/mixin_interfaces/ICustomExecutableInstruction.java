package com.railwayteam.railways.mixin_interfaces;


import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.station.GlobalStation;

public interface ICustomExecutableInstruction {
    void execute(ScheduleRuntime runtime);

    default GlobalStation executeWithStation(ScheduleRuntime runtime) {
        execute(runtime);
        return null;
    }
}
