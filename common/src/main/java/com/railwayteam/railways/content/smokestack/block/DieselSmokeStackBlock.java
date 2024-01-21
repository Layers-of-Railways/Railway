package com.railwayteam.railways.content.smokestack.block;

import com.railwayteam.railways.content.smokestack.block.be.DieselSmokeStackBlockEntity;
import com.railwayteam.railways.registry.CRBlockEntities;
import com.railwayteam.railways.util.ShapeWrapper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DieselSmokeStackBlock extends AbstractSmokeStackBlock<DieselSmokeStackBlockEntity> {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public DieselSmokeStackBlock(Properties properties, ShapeWrapper shape) {
        super(properties, shape, "diesel");
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.DOWN));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    @Override
    public Class<DieselSmokeStackBlockEntity> getBlockEntityClass() {
        return DieselSmokeStackBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends DieselSmokeStackBlockEntity> getBlockEntityType() {
        return CRBlockEntities.DIESEL_SMOKE_STACK.get();
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shape.get(pState.getValue(FACING));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) return null;
        return state.setValue(FACING, context.getClickedFace());
    }
}
