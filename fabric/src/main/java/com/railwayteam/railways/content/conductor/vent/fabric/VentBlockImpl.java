package com.railwayteam.railways.content.conductor.vent.fabric;

import com.railwayteam.railways.content.conductor.vent.VentBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class VentBlockImpl extends VentBlock {
    public VentBlockImpl(Properties properties) {
        super(properties);
    }

    @Override
    public boolean supportsExternalFaceHiding(BlockState state) {
        return true;
    }

    @Override
    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState,
                                     Direction dir) {
        if (state.is(this) == neighborState.is(this)) {
            if (getMaterial(level, pos).skipRendering(getMaterial(level, pos.relative(dir)), dir.getOpposite()))
                return true;
        }

        return getMaterial(level, pos).skipRendering(neighborState, dir.getOpposite());
    }

    public static VentBlock create(Properties properties) {
        return new VentBlockImpl(properties);
    }
}
