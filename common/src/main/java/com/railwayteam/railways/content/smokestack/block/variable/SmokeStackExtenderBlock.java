/*
 * Steam 'n' Rails
 * Copyright (c) 2026 The Railways Team
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

package com.railwayteam.railways.content.smokestack.block.variable;

import com.railwayteam.railways.content.buffer.BlockStateBlockItemGroup;
import com.railwayteam.railways.content.smokestack.RotationType;
import com.railwayteam.railways.content.smokestack.SmokestackStyle;
import com.railwayteam.railways.util.ShapeWrapper;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.utility.Couple;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumMap;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public non-sealed class SmokeStackExtenderBlock extends Block implements ProperWaterloggedBlock, IWrenchable, VariableStack {
    public static final EnumProperty<SmokestackStyle> STYLE = VariableSmokeStackBlock.STYLE;
    public static final EnumProperty<VariableStackPart> PART = VariableSmokeStackBlock.PART;

    protected final RotationType rotationType;
    protected final EnumMap<VariableStackPart, ShapeWrapper> shape;
    protected final Supplier<BlockStateBlockItemGroup<Couple<String>, SmokestackStyle>> cycleGroup;
    protected final Supplier<VariableSmokeStackBlock> baseBlock;

    // because createBlockStateDefinition is called from the super constructor, and it needs a rotation type
    private static final ThreadLocal<RotationType> definitionRotationType = new ThreadLocal<>();

    private static Properties setDefinitionRotationType(Properties properties, RotationType rotationType) {
        definitionRotationType.set(rotationType);
        return properties;
    }

    public SmokeStackExtenderBlock(Properties properties, RotationType rotationType, EnumMap<VariableStackPart, ShapeWrapper> shape, Supplier<BlockStateBlockItemGroup<Couple<String>, SmokestackStyle>> cycleGroup, Supplier<VariableSmokeStackBlock> baseBlock) {
        super(setDefinitionRotationType(properties, rotationType));
        this.rotationType = rotationType;
        this.shape = shape;
        this.cycleGroup = cycleGroup;
        this.baseBlock = baseBlock;

        registerDefaultState(defaultBlockState()
            .setValue(WATERLOGGED, false)
            .setValue(STYLE, SmokestackStyle.STEEL)
            .setValue(PART, VariableStackPart.SINGLE));
    }

    public VariableSmokeStackBlock baseBlock() {
        return baseBlock.get();
    }

    protected RotationType getConstructSafeRotationType() {
        return rotationType != null ? rotationType : definitionRotationType.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(WATERLOGGED, STYLE, PART));
        getConstructSafeRotationType().createBlockStateDefinition(builder);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return cycleGroup.get().get(state.getValue(STYLE)).asStack();
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return fluidState(state);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = rotationType.getStateForPlacement(context, defaultBlockState());

        return withWater(state, context);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return rotationType.rotate(state, rotation);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return rotationType.mirror(state, mirror);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return rotationType.getShape(state, shape.get(state.getValue(PART)));
    }

    public static BlockPos findRoot(LevelAccessor pLevel, BlockPos pPos) {
        BlockPos currentPos = pPos.below();
        while (true) {
            BlockState blockState = pLevel.getBlockState(currentPos);
            if (AllBlocks.STEAM_WHISTLE_EXTENSION.has(blockState)) {
                currentPos = currentPos.below();
                continue;
            }
            return currentPos;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockPos rootPos = findRoot(level, pos);
        BlockState rootState = level.getBlockState(rootPos);
        if (rootState.getBlock() instanceof VariableSmokeStackBlock rootBlock)
            return rootBlock.use(rootState, level, rootPos, player, hand, new BlockHitResult(
                hit.getLocation(),
                hit.getDirection(),
                rootPos,
                hit.isInside()
            ));

        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return (below.is(this) || below.is(baseBlock())) && below.getValue(PART) != VariableStackPart.SINGLE && below.getValue(STYLE) == state.getValue(STYLE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        updateWater(level, state, currentPos);

        if (direction.getAxis() != Axis.Y)
            return state;

        if (direction == Direction.UP) {
            boolean connected = state.getValue(PART).isSegment();
            boolean shouldConnect = level.getBlockState(currentPos.above()).is(this);
            if (!connected && shouldConnect)
                return state.setValue(PART, VariableStackPart.SEGMENT);
            if (connected && !shouldConnect)
                return state.setValue(PART, VariableStackPart.DOUBLE);
            return state;
        }

        if (!state.canSurvive(level, currentPos))
            return Blocks.AIR.defaultBlockState();

        BlockState below = level.getBlockState(currentPos.below());
        return rotationType.cloneRotation(state.setValue(STYLE, below.getValue(STYLE)), below);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (oldState.getBlock() != this || oldState.getValue(PART) != state.getValue(PART))
            VariableSmokeStackBlock.queueHeightUpdate(level, pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (newState.getBlock() != this)
            VariableSmokeStackBlock.queueHeightUpdate(level, pos);
    }
}
