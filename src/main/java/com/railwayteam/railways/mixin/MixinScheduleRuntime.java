package com.railwayteam.railways.mixin;

import com.railwayteam.railways.mixin_interfaces.ICustomExecutableInstruction;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.GlobalStation;
import com.simibubi.create.content.logistics.trains.management.schedule.Schedule;
import com.simibubi.create.content.logistics.trains.management.schedule.ScheduleEntry;
import com.simibubi.create.content.logistics.trains.management.schedule.ScheduleRuntime;
import com.simibubi.create.content.logistics.trains.management.schedule.destination.ScheduleInstruction;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ScheduleRuntime.class, remap = false)
public abstract class MixinScheduleRuntime {
    @Shadow Schedule schedule;

    @Shadow public int currentEntry;

    @Shadow public ScheduleRuntime.State state;

    @Shadow public boolean isAutoSchedule;

    @Shadow public abstract void discardSchedule();

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

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/logistics/trains/management/schedule/ScheduleRuntime;completed:Z", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void discardAutoSchedule(Level level, CallbackInfo ci) {
        if (isAutoSchedule) {
            discardSchedule();
        }
    }
}
