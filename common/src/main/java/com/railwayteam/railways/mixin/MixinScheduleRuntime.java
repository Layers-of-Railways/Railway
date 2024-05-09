package com.railwayteam.railways.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.schedule.WaypointDestinationInstruction;
import com.railwayteam.railways.mixin_interfaces.ICustomExecutableInstruction;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleEntry;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.simibubi.create.content.trains.station.GlobalStation;
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
    @Shadow
    Schedule schedule;

    @Shadow public int currentEntry;

    @Shadow public ScheduleRuntime.State state;

    @Shadow public boolean isAutoSchedule;

    @Shadow public abstract void discardSchedule();

    @Shadow Train train;

    @Inject(method = "startCurrentInstruction", at = @At("HEAD"), cancellable = true)
    private void startCustomInstruction(CallbackInfoReturnable<GlobalStation> cir) {
        // https://github.com/Creators-of-Create/Create/issues/5818
        if (schedule.entries.size() < currentEntry) return;

        ScheduleEntry entry = schedule.entries.get(currentEntry);
        ScheduleInstruction instruction = entry.instruction;

        if (instruction instanceof ICustomExecutableInstruction customExecutableInstruction) {
            cir.setReturnValue(customExecutableInstruction.executeWithStation((ScheduleRuntime) (Object) this));
        }
    }

    @Inject(method = "tickConditions", at = @At("HEAD"), cancellable = true)
    private void tickWhenNoConditions(Level level, CallbackInfo ci) {
        if (schedule.entries.get(currentEntry).conditions.isEmpty()) {
            state = ScheduleRuntime.State.PRE_TRANSIT;
            currentEntry++;
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/trains/schedule/ScheduleRuntime;completed:Z", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void discardAutoSchedule(Level level, CallbackInfo ci) {
        if (isAutoSchedule) {
            Railways.LOGGER.info("[DISCARD_SCHEDULE] on train {} called in MixinScheduleRuntime#discardAutoSchedule because a non-looping auto schedule was completed", this.train.name.getString());
            discardSchedule();
        }
    }

    // waypoint instructions have no conditions, and so are handled as 'invalid' by the vanilla code (https://github.com/Layers-of-Railways/Railway/issues/329)
    @Inject(method = "estimateStayDuration",
        at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;get(I)Ljava/lang/Object;", shift = At.Shift.BY, by=2),
        cancellable = true)
    private void waypointsAreValid(int index, CallbackInfoReturnable<Integer> cir, @Local(name = "scheduleEntry") ScheduleEntry scheduleEntry) {
        if (scheduleEntry.instruction instanceof WaypointDestinationInstruction)
            cir.setReturnValue(0);
    }
}
