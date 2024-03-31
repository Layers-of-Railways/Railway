package com.railwayteam.railways.content.buffer.headstock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class CopycatHeadstockBarsBlock extends HorizontalDirectionalBlock {
    public static final BooleanProperty UPSIDE_DOWN = CopycatHeadstockBlock.UPSIDE_DOWN;

    public CopycatHeadstockBarsBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(UPSIDE_DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING, UPSIDE_DOWN));
    }
}
