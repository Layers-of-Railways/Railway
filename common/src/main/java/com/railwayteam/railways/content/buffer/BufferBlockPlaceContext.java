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
    private BufferBlockPlaceContext(Level level, @Nullable Player player, InteractionHand interactionHand,
                                    ItemStack itemStack, BlockHitResult blockHitResult,
                                    Direction facing, @Nullable TrackBufferBlock<?> overrideBlock) {
        super(level, player, interactionHand, itemStack, blockHitResult);
        this.facing = facing;
        this.overrideBlock = overrideBlock;
    }

    public static BufferBlockPlaceContext at(BlockPlaceContext context, BlockPos pos, Direction direction, Direction facing, @Nullable TrackBufferBlock<?> overrideBlock) {
        return new BufferBlockPlaceContext(
            context.getLevel(),
            context.getPlayer(),
            context.getHand(),
            context.getItemInHand(),
            new BlockHitResult(
                new Vec3(
                    (double)pos.getX() + 0.5 + (double)direction.getStepX() * 0.5,
                    (double)pos.getY() + 0.5 + (double)direction.getStepY() * 0.5,
                    (double)pos.getZ() + 0.5 + (double)direction.getStepZ() * 0.5
                ),
                direction,
                pos,
                false
            ),
            facing,
            overrideBlock
        );
    }
}
