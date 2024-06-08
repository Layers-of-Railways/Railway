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
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.DoorMovingInteraction;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DoorMovingInteraction.class, remap = false)
public class MixinDoorMovingInteraction {
    /*
    prevent players from just opening special doors unless sneaking
     */
    @Inject(
        method = "handle",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;", ordinal = 1, remap = true),
        cancellable = true
    )
    private void railways$lockSpecial(Player player, Contraption contraption, BlockPos pos,
                                 BlockState currentState, CallbackInfoReturnable<BlockState> cir) {
        if (player == null) return;
        if (!(currentState.getBlock() instanceof SlidingDoorBlock)) return;
        boolean lower = currentState.getValue(SlidingDoorBlock.HALF) == DoubleBlockHalf.LOWER;
        StructureTemplate.StructureBlockInfo info = contraption.getBlocks().get(lower ? pos : pos.below());
        if (info != null && !SlidingDoorMode.fromNbt(info.nbt()).canOpenManually() && !player.isShiftKeyDown())
            cir.setReturnValue(currentState);
    }
}
