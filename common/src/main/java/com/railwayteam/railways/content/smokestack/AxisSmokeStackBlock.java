package com.railwayteam.railways.content.smokestack;

import com.railwayteam.railways.util.ShapeWrapper;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AxisSmokeStackBlock extends SmokeStackBlock {

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    public AxisSmokeStackBlock(Properties properties, SmokeStackType type, ShapeWrapper shape) {
        super(properties, type, shape);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(AXIS, rot.rotate(state.getValue(AXIS) == Direction.Axis.X ? Direction.WEST : Direction.NORTH).getAxis());
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shape.get(pState.getValue(AXIS));
    }

    @Override
    protected BlockState makeDefaultState() {
        return super.makeDefaultState().setValue(AXIS, Direction.Axis.X);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AXIS);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = super.getStateForPlacement(context);
        if (blockstate == null) return null;
        Direction direction = context.getHorizontalDirection();
        if (direction.getAxis().isHorizontal())
            blockstate = blockstate.setValue(AXIS, direction.getAxis());

        return blockstate;
    }

    @Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        return originalState.setValue(AXIS, originalState.getValue(AXIS) == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X);
    }
}
