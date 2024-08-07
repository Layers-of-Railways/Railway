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

package com.railwayteam.railways.content.buffer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BufferBlockPlaceContext extends BlockPlaceContext {
    
    public final Direction facing;
    @Nullable
    public final TrackBufferBlock<?> overrideBlock;
    public final boolean diagonal;
    
    private BufferBlockPlaceContext(Level level, @Nullable Player player, InteractionHand interactionHand,
                                    ItemStack itemStack, BlockHitResult blockHitResult,
                                    Direction facing, @Nullable TrackBufferBlock<?> overrideBlock, boolean diagonal) {
        super(level, player, interactionHand, itemStack, blockHitResult);
        this.facing = facing;
        this.overrideBlock = overrideBlock;
        this.diagonal = diagonal;
    }
    
    public static BufferBlockPlaceContext at(BlockPlaceContext context, BlockPos pos, Direction direction, Direction facing, @Nullable TrackBufferBlock<?> overrideBlock, boolean diagonal) {
        return new BufferBlockPlaceContext(
                context.getLevel(),
                context.getPlayer(),
                context.getHand(),
                context.getItemInHand(),
                new BlockHitResult(
                        new Vec3(
                                (double) pos.getX() + 0.5 + (double) direction.getStepX() * 0.5,
                                (double) pos.getY() + 0.5 + (double) direction.getStepY() * 0.5,
                                (double) pos.getZ() + 0.5 + (double) direction.getStepZ() * 0.5
                        ),
                        direction,
                        pos,
                        false
                ),
                facing, overrideBlock, diagonal
        );
    }
}
