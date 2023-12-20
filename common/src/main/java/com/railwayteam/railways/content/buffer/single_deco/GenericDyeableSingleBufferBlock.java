package com.railwayteam.railways.content.buffer.single_deco;

import com.simibubi.create.foundation.utility.VoxelShaper;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GenericDyeableSingleBufferBlock extends AbstractDyeableSingleBufferBlock {
    protected final VoxelShaper shaper;

    public GenericDyeableSingleBufferBlock(Properties properties, VoxelShaper shaper) {
        super(properties);
        this.shaper = shaper;
    }

    public static NonNullFunction<Properties, GenericDyeableSingleBufferBlock> createFactory(VoxelShaper shaper) {
        return properties -> new GenericDyeableSingleBufferBlock(properties, shaper);
    }

    @Override
    protected BlockState cycleStyle(BlockState originalState, Direction targetedFace) {
        return originalState;
    }

    @Override
    protected VoxelShaper getShaper(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shaper;
    }
}
