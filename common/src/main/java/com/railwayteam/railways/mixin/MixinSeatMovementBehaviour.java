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

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.actors.seat.SeatMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(value = SeatMovementBehaviour.class, remap = false)
public class MixinSeatMovementBehaviour {
    @Inject(method = "visitNewPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;stopRiding()V", remap = true),
            cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void keepConductors(MovementContext context, BlockPos pos, CallbackInfo ci,
                                AbstractContraptionEntity contraptionEntity, int index, Map<?, ?> seatMapping,
                                BlockState blockState, boolean slab, boolean solid, Entity toDismount) {
        if (toDismount instanceof ConductorEntity)
            ci.cancel();
    }
}
