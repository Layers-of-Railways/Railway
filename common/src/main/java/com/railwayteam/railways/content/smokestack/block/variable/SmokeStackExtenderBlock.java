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
import com.simibubi.create.content.equipment.goggles.IProxyHoveringInformation;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
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
public non-sealed class SmokeStackExtenderBlock extends Block implements ProperWaterloggedBlock, IWrenchable, VariableStack, IProxyHoveringInformation {
    public static final EnumProperty<SmokestackStyle> STYLE = VariableSmokeStackBlock.STYLE;

    protected final RotationType rotationType;
    protected final EnumMap<VariableStackPart, ShapeWrapper> shape;
    protected final Supplier<BlockStateBlockItemGroup<SmokestackStyle.Context, SmokestackStyle>> cycleGroup;
    protected final Supplier<VariableSmokeStackBlock> baseBlock;
    protected final EnumProperty<VariableStackPart> partProperty;
    protected final VariableStackPart defaultPart;

    // because createBlockStateDefinition is called from the super constructor, and it needs a rotation type
    private static final ThreadLocal<RotationType> definitionRotationType = new ThreadLocal<>();
    // ditto
    private static final ThreadLocal<EnumProperty<VariableStackPart>> definitionPartProperty = new ThreadLocal<>();

    private static Properties setDefinitionValues(Properties properties, RotationType rotationType, EnumProperty<VariableStackPart> partProperty) {
        definitionRotationType.set(rotationType);
        definitionPartProperty.set(partProperty);
        return properties;
    }

    public SmokeStackExtenderBlock(Properties properties, RotationType rotationType, EnumMap<VariableStackPart, ShapeWrapper> shape, Supplier<BlockStateBlockItemGroup<SmokestackStyle.Context, SmokestackStyle>> cycleGroup, Supplier<VariableSmokeStackBlock> baseBlock, EnumProperty<VariableStackPart> partProperty, VariableStackPart defaultPart) {
        super(setDefinitionValues(properties, rotationType, partProperty));
        this.rotationType = rotationType;
        this.shape = shape;
        this.cycleGroup = cycleGroup;
        this.baseBlock = baseBlock;
        this.partProperty = partProperty;
        this.defaultPart = defaultPart;

        registerDefaultState(defaultBlockState()
            .setValue(WATERLOGGED, false)
            .setValue(STYLE, SmokestackStyle.STEEL)
            .setValue(partProperty, defaultPart));
    }

    @Override
    public EnumProperty<VariableStackPart> partProperty() {
        return partProperty;
    }

    @Override
    public VariableStackPart defaultPart() {
        return defaultPart;
    }

    public VariableSmokeStackBlock baseBlock() {
        return baseBlock.get();
    }

    protected RotationType getConstructSafeRotationType() {
        return rotationType != null ? rotationType : definitionRotationType.get();
    }

    protected EnumProperty<VariableStackPart> getConstructSafePartProperty() {
        return partProperty != null ? partProperty : definitionPartProperty.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(WATERLOGGED, STYLE, getConstructSafePartProperty()));
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
        return rotationType.getShape(state, shape.get(state.getValue(partProperty)));
    }

    public BlockPos findRoot(LevelAccessor pLevel, BlockPos pPos) {
        BlockPos currentPos = pPos.below();
        while (true) {
            BlockState blockState = pLevel.getBlockState(currentPos);
            if (blockState.is(this)) {
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
        return (below.is(this) || below.is(baseBlock())) && below.getValue(partProperty).isFullHeight();
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        updateWater(level, state, currentPos);

        if (direction.getAxis() != Axis.Y)
            return state;

        if (direction == Direction.UP) {
            boolean connected = state.getValue(partProperty).isSegment();
            boolean shouldConnect = level.getBlockState(currentPos.above()).is(this);
            if (!connected && shouldConnect)
                return state.setValue(partProperty, VariableStackPart.SEGMENT);
            if (connected && !shouldConnect)
                return state.setValue(partProperty, VariableStackPart.DOUBLE);
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
        if (oldState.getBlock() != this || oldState.getValue(partProperty) != state.getValue(partProperty))
            VariableSmokeStackBlock.queueHeightUpdate(level, findRoot(level, pos));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (newState.getBlock() != this)
            VariableSmokeStackBlock.queueHeightUpdate(level, findRoot(level, pos));
    }

    @Override
    public BlockPos getInformationSource(Level level, BlockPos pos, BlockState state) {
        return findRoot(level, pos);
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (context.getClickLocation().y < context.getClickedPos().getY() + .5f || defaultPart.isFullHeight() || state.getValue(partProperty) == VariableStackPart.SINGLE)
            return IWrenchable.super.onSneakWrenched(state, context);

        if (!(world instanceof ServerLevel))
            return InteractionResult.SUCCESS;

        world.setBlock(pos, state.setValue(partProperty, VariableStackPart.SINGLE), 3);
        playRemoveSound(world, pos);

        return InteractionResult.SUCCESS;
    }

    protected UseOnContext relocateContext(UseOnContext context, BlockPos target) {
        //noinspection DataFlowIssue
        return new UseOnContext(context.getPlayer(), context.getHand(),
            new BlockHitResult(context.getClickLocation(), context.getClickedFace(), target, context.isInside()));
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos rootPos = findRoot(level, context.getClickedPos());
        BlockState rootState = level.getBlockState(rootPos);
        VariableSmokeStackBlock baseBlock = baseBlock();

        if (rootState.is(baseBlock))
            return baseBlock.onWrenched(rootState, relocateContext(context, rootPos));

        return IWrenchable.super.onWrenched(state, context);
    }
}
