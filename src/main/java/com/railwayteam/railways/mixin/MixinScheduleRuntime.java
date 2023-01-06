package com.railwayteam.railways.mixin;

import com.railwayteam.railways.mixin_interfaces.ICustomExecutableInstruction;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.GlobalStation;
import com.simibubi.create.content.logistics.trains.management.schedule.Schedule;
import com.simibubi.create.content.logistics.trains.management.schedule.ScheduleEntry;
import com.simibubi.create.content.logistics.trains.management.schedule.ScheduleRuntime;
import com.simibubi.create.content.logistics.trains.management.schedule.destination.ScheduleInstruction;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ScheduleRuntime.class, remap = false)
public class MixinScheduleRuntime {
    @Shadow Schedule schedule;

    @Shadow public int currentEntry;

    @Shadow public ScheduleRuntime.State state;

    @Inject(method = "startCurrentInstruction", at = @At("HEAD"), cancellable = true)
    private void startCustomInstruction(CallbackInfoReturnable<GlobalStation> cir) {
        ScheduleEntry entry = schedule.entries.get(currentEntry);
        ScheduleInstruction instruction = entry.instruction;

        if (instruction instanceof ICustomExecutableInstruction customExecutableInstruction) {
            cir.setReturnValue(customExecutableInstruction.executeWithStation((ScheduleRuntime) (Object) this));
        }
    }

    @Inject(method = "tickConditions", at = @At("HEAD"), cancellable = true)
    private void tickWhenNoConditions(Level level, CallbackInfo ci) {
        if (schedule.entries.get(currentEntry).conditions.size() == 0) {
            state = ScheduleRuntime.State.PRE_TRANSIT;
            currentEntry++;
            ci.cancel();
        }
    }
}
