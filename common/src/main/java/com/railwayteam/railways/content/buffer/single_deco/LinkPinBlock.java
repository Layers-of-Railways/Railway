package com.railwayteam.railways.content.buffer.single_deco;

import com.railwayteam.railways.registry.CRShapes;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LinkPinBlock extends AbstractDyeableSingleBufferBlock {
    public static final BooleanProperty LINKLESS = BooleanProperty.create("linkless");

    public LinkPinBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
            .setValue(LINKLESS, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(LINKLESS));
    }

    @Override
    protected BlockState cycleStyle(BlockState originalState, Direction targetedFace) {
        return originalState.cycle(LINKLESS);
    }

    @Override
    protected VoxelShaper getShaper(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return CRShapes.LINK_PIN;
    }
}
