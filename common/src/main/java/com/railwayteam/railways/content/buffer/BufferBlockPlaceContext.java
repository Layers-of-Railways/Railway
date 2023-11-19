package com.railwayteam.railways.content.buffer;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.Nullable;

public class BufferBlockPlaceContext extends BlockPlaceContext {
    public final Direction facing;
    @Nullable
    public final TrackBufferBlock overrideBlock;
    public BufferBlockPlaceContext(UseOnContext context, Direction facing, @Nullable TrackBufferBlock overrideBlock) {
        super(context);
        this.facing = facing;
        this.overrideBlock = overrideBlock;
    }
}
