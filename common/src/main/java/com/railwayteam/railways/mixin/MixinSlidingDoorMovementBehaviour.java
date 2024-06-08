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

import com.railwayteam.railways.content.extended_sliding_doors.SlidingDoorMode;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorMovementBehaviour;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SlidingDoorMovementBehaviour.class, remap = false)
public class MixinSlidingDoorMovementBehaviour {
    private SlidingDoorMode mode(MovementContext context) {
        return SlidingDoorMode.fromNbt(context.blockEntityData);
    }

    @Inject(method = "shouldUpdate", at = @At("HEAD"), cancellable = true)
    private void cancelUpdateForManual(MovementContext context, boolean shouldOpen, CallbackInfoReturnable<Boolean> cir) {
        if (context.state != null && context.state.hasProperty(SlidingDoorBlock.HALF) && context.state.getValue(SlidingDoorBlock.HALF) == DoubleBlockHalf.UPPER) {
            cir.setReturnValue(false);
            return;
        }
        if (!mode(context).canOpenSpecially())
            cir.setReturnValue(false);
    }
}
