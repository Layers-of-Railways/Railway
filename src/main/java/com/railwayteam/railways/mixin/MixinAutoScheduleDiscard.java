package com.railwayteam.railways.mixin;

import com.railwayteam.railways.mixin_interfaces.ICustomExecutableInstruction;
import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.GlobalStation;
import com.simibubi.create.content.logistics.trains.management.schedule.Schedule;
import com.simibubi.create.content.logistics.trains.management.schedule.ScheduleEntry;
import com.simibubi.create.content.logistics.trains.management.schedule.ScheduleRuntime;
import com.simibubi.create.content.logistics.trains.management.schedule.destination.ScheduleInstruction;
import net.minecraft.world.level.Level;
import org.checkerframework.checker.signedness.qual.SignednessGlb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ScheduleRuntime.class, remap = false)
public class MixinAutoScheduleDiscard {
    @Shadow
    Schedule schedule;

    @Shadow
    public
    int currentEntry;

    @Shadow
    public
    boolean paused;

    @Shadow
    Train train;

    @Shadow
    int ticksInTransit;

    @Shadow
    public
    boolean completed;

    @Shadow
    public
    boolean isAutoSchedule;

    @Shadow
    public void discardSchedule(){}

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(Level level, CallbackInfo ci) {
        if (schedule == null)
            return;
        if (paused)
            return;
        if (train.derailed)
            return;
        if (train.navigation.destination != null) {
            ticksInTransit++;
            return;
        }
        if (currentEntry >= schedule.entries.size()) {
            currentEntry = 0;
            if (!schedule.cyclic) {
                paused = true;
                completed = true;
                if(isAutoSchedule){
                    discardSchedule();
                }
            }
        }
    }
}
