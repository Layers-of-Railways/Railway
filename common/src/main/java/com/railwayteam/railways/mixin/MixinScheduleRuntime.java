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

package com.railwayteam.railways.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.schedule.WaypointDestinationInstruction;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleEntry;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
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
	public Schedule schedule;

    @Shadow public int currentEntry;

    @Shadow public ScheduleRuntime.State state;

    @Shadow public boolean isAutoSchedule;

    @Shadow public abstract void discardSchedule();

    @Shadow
	public Train train;

    @Inject(method = "tickConditions", at = @At("HEAD"), cancellable = true)
    private void tickWhenNoConditions(Level level, CallbackInfo ci) {
        if (schedule.entries.get(currentEntry).conditions.isEmpty()) {
            state = ScheduleRuntime.State.PRE_TRANSIT;
            currentEntry++;
            ci.cancel();
        }
    }
    
    @Inject(method = "checkEndOfScheduleReached", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/trains/schedule/ScheduleRuntime;completed:Z", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void discardAutoSchedule(CallbackInfoReturnable<Boolean> cir) {
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
