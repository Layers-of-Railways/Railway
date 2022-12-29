package com.railwayteam.railways.mixin;

import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.content.logistics.trains.management.schedule.ScheduleRuntime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ScheduleRuntime.class, remap = false)
public interface AccessorScheduleRuntime {
    @Accessor
    void setCooldown(int cooldown);

    @Accessor
    Train getTrain();
}
