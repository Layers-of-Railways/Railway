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
import com.railwayteam.railways.content.smokestack.SmokeEmissionParams;
import com.railwayteam.railways.content.smokestack.SmokestackStyle;
import com.railwayteam.railways.content.smokestack.block.StyledSmokeStackBlock;
import com.railwayteam.railways.content.smokestack.block.be.SmokeStackBlockEntity;
import com.railwayteam.railways.util.ShapeWrapper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumMap;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public non-sealed class VariableSmokeStackBlock extends StyledSmokeStackBlock implements VariableStack {
    public static final EnumProperty<VariableStackPart> PART = EnumProperty.create("part", VariableStackPart.class);
    public static final EnumProperty<VariableStackPart> PART_NO_HALF = EnumProperty.create("part", VariableStackPart.class, VariableStackPart::isFullHeight);

    protected final EnumMap<VariableStackPart, ShapeWrapper> shape;
    protected final Supplier<SmokeStackExtenderBlock> extenderBlock;
    protected final EnumProperty<VariableStackPart> partProperty;
    protected final VariableStackPart defaultPart;

    // because createBlockStateDefinition is called from the super constructor, and it needs a part property
    private static final ThreadLocal<EnumProperty<VariableStackPart>> definitionPartProperty = new ThreadLocal<>();
    private static final ThreadLocal<VariableStackPart> definitionDefaultPart = new ThreadLocal<>();

    private static Properties setDefinitionValues(Properties properties, EnumProperty<VariableStackPart> partProperty, VariableStackPart defaultPart) {
        definitionPartProperty.set(partProperty);
        definitionDefaultPart.set(defaultPart);
        return properties;
    }

    public VariableSmokeStackBlock(Properties properties, RotationType rotationType, SmokeEmissionParams emissionParams, EnumMap<VariableStackPart, ShapeWrapper> shape, boolean createsStationarySmoke, Supplier<BlockStateBlockItemGroup<SmokestackStyle.Context, SmokestackStyle>> cycleGroup, Supplier<SmokeStackExtenderBlock> extenderBlock, EnumProperty<VariableStackPart> partProperty, VariableStackPart defaultPart) {
        super(setDefinitionValues(properties, partProperty, defaultPart), rotationType, emissionParams, shape.get(defaultPart), createsStationarySmoke, cycleGroup);
        this.shape = shape;
        this.extenderBlock = extenderBlock;
        this.partProperty = partProperty;
        this.defaultPart = defaultPart;
    }

    @Override
    public EnumProperty<VariableStackPart> partProperty() {
        return partProperty;
    }

    @Override
    public VariableStackPart defaultPart() {
        return defaultPart;
    }

    public SmokeStackExtenderBlock extenderBlock() {
        return extenderBlock.get();
    }

    protected EnumProperty<VariableStackPart> getConstructSafePartProperty() {
        return partProperty != null ? partProperty : definitionPartProperty.get();
    }

    protected VariableStackPart getConstructSafeDefaultPart() {
        return defaultPart != null ? defaultPart : definitionDefaultPart.get();
    }

    @Override
    protected BlockState makeDefaultState() {
        return super.makeDefaultState().setValue(getConstructSafePartProperty(), getConstructSafeDefaultPart());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(getConstructSafePartProperty()));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return rotationType.getShape(state, shape.get(state.getValue(partProperty)));
    }

    /*@Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return !(below.isAir() || below.is(this) || below.is(extenderBlock()));
    }*/

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        state = super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);

        if (direction.getAxis() != Axis.Y)
            return state;

        if (direction == Direction.UP) {
            boolean connected = state.getValue(partProperty).isSegment();
            BlockState above = level.getBlockState(currentPos.above());
            boolean shouldConnect = above.is(extenderBlock());
            if (!connected && shouldConnect)
                return state.setValue(partProperty, VariableStackPart.SEGMENT);
            if (connected && !shouldConnect)
                return state.setValue(partProperty, VariableStackPart.DOUBLE);
        }

        return state.canSurvive(level, currentPos) ? state : Blocks.AIR.defaultBlockState();
    }

    public static void queueHeightUpdate(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof VariableSmokeStackBlock block && !level.getBlockTicks().hasScheduledTick(pos, block))
            level.scheduleTick(pos, block, 1);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        withBlockEntityDo(level, pos, SmokeStackBlockEntity::updateHeight);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (oldState.getBlock() != this || oldState.getValue(partProperty) != state.getValue(partProperty))
            queueHeightUpdate(level, pos);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack heldItem = pPlayer.getItemInHand(pHand);
        if (heldItem.is(getCloneItemStack(pLevel, pPos, pState).getItem())) {
            incrementSize(pLevel, pPos);
            return InteractionResult.SUCCESS;
        }

        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    public static void incrementSize(LevelAccessor level, BlockPos pos) {
        if (level.isClientSide()) return;

        BlockState base = level.getBlockState(pos);
        if (!(base.getBlock() instanceof VariableSmokeStackBlock baseBlock)) return;

        SoundType soundtype = base.getSoundType();
        float volume = (soundtype.getVolume() + 1.0F) / 2.0F;
        SoundEvent growSound = SoundEvents.NOTE_BLOCK_XYLOPHONE.value();
        SoundEvent hitSound = soundtype.getHitSound();

        EnumProperty<VariableStackPart> basePartProperty = baseBlock.partProperty();
        VariableStackPart basePart = base.getValue(basePartProperty);
        if (basePart == VariableStackPart.SINGLE) {
            level.setBlock(pos, base.setValue(basePartProperty, VariableStackPart.DOUBLE), UPDATE_ALL);

            float pitch = (float) Math.pow(2, 1 / 12.0);
            level.playSound(null, pos, growSound, SoundSource.BLOCKS, volume / 4f, pitch);
            level.playSound(null, pos, hitSound, SoundSource.BLOCKS, volume, pitch);
            return;
        }

        SmokeStackExtenderBlock extenderBlock = baseBlock.extenderBlock();
        EnumProperty<VariableStackPart> extenderPartProperty = extenderBlock.partProperty();

        MutableBlockPos currentPos = pos.mutable().move(Direction.UP);
        for (int i = 0; i <= 6; i++) {
            BlockState state = level.getBlockState(currentPos);

            if (state.is(extenderBlock)) {
                if (state.getValue(extenderPartProperty) == VariableStackPart.SINGLE) {
                    level.setBlock(currentPos, state.setValue(extenderPartProperty, VariableStackPart.DOUBLE), UPDATE_ALL);

                    float pitch = (float) Math.pow(2, -(i * 2) / 12.0);
                    level.playSound(null, currentPos, growSound, SoundSource.BLOCKS, volume / 4f, pitch);
                    level.playSound(null, currentPos, hitSound, SoundSource.BLOCKS, volume, pitch);
                    return;
                }
                currentPos.move(Direction.UP);
                continue;
            }

            if (!state.canBeReplaced())
                return;

            BlockState newState = extenderBlock.defaultBlockState()
                .setValue(extenderPartProperty, extenderBlock.defaultPart())
                .setValue(STYLE, base.getValue(STYLE));
            level.setBlock(currentPos, baseBlock.rotationType.cloneRotation(newState, base), UPDATE_ALL);

            float pitch = (float) Math.pow(2, -(i * 2 - 1) / 12.0);
            level.playSound(null, currentPos, growSound, SoundSource.BLOCKS, volume / 4f, pitch);
            level.playSound(null, currentPos, hitSound, SoundSource.BLOCKS, volume, pitch);
            return;
        }
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (context.getClickLocation().y < context.getClickedPos().getY() + .5f || defaultPart.isFullHeight() || state.getValue(partProperty) == VariableStackPart.SINGLE)
            return super.onSneakWrenched(state, context);

        if (!(world instanceof ServerLevel))
            return InteractionResult.SUCCESS;

        world.setBlock(pos, state.setValue(PART, VariableStackPart.SINGLE), 3);
        playRemoveSound(world, pos);

        return InteractionResult.SUCCESS;
    }
}
