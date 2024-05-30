/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.semaphore;

import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.registry.CRBlockEntities;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRShapes;
import com.railwayteam.railways.registry.CRTags;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.placement.IPlacementHelper;
import com.simibubi.create.foundation.placement.PlacementHelpers;
import com.simibubi.create.foundation.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Predicate;

public class SemaphoreBlock extends HorizontalDirectionalBlock implements IBE<SemaphoreBlockEntity>, IWrenchable {

    public static final int placementHelperId = PlacementHelpers.register(new PlacementHelper());
    public static final int girderPlacementHelperId = PlacementHelpers.register(new GirderPlacementHelper());
    public static final BooleanProperty FLIPPED = BooleanProperty.create("flipped");
    public static final BooleanProperty FULL = BooleanProperty.create("full");
    public static final BooleanProperty UPSIDE_DOWN = BooleanProperty.create("upside_down");

    public SemaphoreBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState().setValue(FLIPPED,false).setValue(FULL,false).setValue(UPSIDE_DOWN, false));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACING).add(FLIPPED).add(FULL).add(UPSIDE_DOWN));
    }
    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult ray) {
        ItemStack heldItem = player.getItemInHand(hand);

        //IPlacementHelper placementHelper = PlacementHelpers.get(placementHelperId);


        ItemStack itemInHand = player.getItemInHand(hand);
        IPlacementHelper helper = PlacementHelpers.get(SemaphoreBlock.girderPlacementHelperId);

        if (helper.matchesItem(itemInHand))
            return helper.getOffset(player, world, state, pos, ray)
                            .placeInWorld(world, (BlockItem) itemInHand.getItem(), player, hand, ray);
        return InteractionResult.PASS;
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if(state==null)
            return null;

        Direction facing = context.getHorizontalDirection().getOpposite();

        Vec3 look = context.getPlayer().getLookAngle();
        Vec3 cross = look.cross(new Vec3(facing.step()));
        boolean flipped = cross.y<0;
        boolean upside_down = context.getClickedFace() == Direction.DOWN && !CRConfigs.server().semaphores.simplifiedPlacement.get();

        return state.setValue(FACING,facing).setValue(FLIPPED,flipped).setValue(UPSIDE_DOWN,upside_down);
    }
    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        BlockState rotated;
        boolean upsideDownChanged = false;

        if(context.getClickedFace().getAxis() != Direction.Axis.Y)
        {
            if (context.getClickedFace() == state.getValue(FACING))
            {
                rotated = state.cycle(FLIPPED);
            } else if (context.getClickedFace() == state.getValue(FACING).getOpposite() && !CRConfigs.server().semaphores.simplifiedPlacement.get()) {
                rotated = state.cycle(UPSIDE_DOWN);
                upsideDownChanged = true;
            }
            else
                rotated = state.setValue(FACING,context.getClickedFace());
        }else
        {
            rotated = getRotatedBlockState(state, context.getClickedFace());
        }

        if (!rotated.canSurvive(world, context.getClickedPos()))
            return InteractionResult.PASS;


        KineticBlockEntity.switchToBlockState(world, context.getClickedPos(), updateAfterWrenched(rotated, context));

        BlockEntity te = context.getLevel()
                .getBlockEntity(context.getClickedPos());

        if (upsideDownChanged) {
            BlockPos currentPos = context.getClickedPos().below();
            for (int i = 0; i < 16; i++) {
                BlockState blockState = world.getBlockState(currentPos);
                if (CRBlocks.SEMAPHORE.has(blockState)) {
                    BlockState rotatedState = blockState.setValue(UPSIDE_DOWN, rotated.getValue(UPSIDE_DOWN));
                    KineticBlockEntity.switchToBlockState(world, currentPos, Block.updateFromNeighbourShapes(rotatedState, world, currentPos));
                } else if (!CRTags.AllBlockTags.SEMAPHORE_POLES.matches(blockState)) {
                    break;
                }
                currentPos = currentPos.below();
            }

            currentPos = context.getClickedPos().above();
            for (int i = 0; i < 16; i++) {
                BlockState blockState = world.getBlockState(currentPos);
                if (CRBlocks.SEMAPHORE.has(blockState)) {
                    BlockState rotatedState = blockState.setValue(UPSIDE_DOWN, rotated.getValue(UPSIDE_DOWN));
                    KineticBlockEntity.switchToBlockState(world, currentPos, Block.updateFromNeighbourShapes(rotatedState, world, currentPos));
                } else if (!CRTags.AllBlockTags.SEMAPHORE_POLES.matches(blockState)) {
                    break;
                }
                currentPos = currentPos.above();
            }
        }


        if (world.getBlockState(context.getClickedPos()) != state)
            playRotateSound(world, context.getClickedPos());

        return InteractionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, world, pos, oldState, isMoving);
        BlockPos currentPos = pos.below();
        for (int i = 0; i < 16; i++) {
            BlockState blockState = world.getBlockState(currentPos);
            if (CRBlocks.SEMAPHORE.has(blockState)) {
                BlockState rotatedState = blockState.setValue(UPSIDE_DOWN, state.getValue(UPSIDE_DOWN));
                KineticBlockEntity.switchToBlockState(world, currentPos, Block.updateFromNeighbourShapes(rotatedState, world, currentPos));
            } else if (!CRTags.AllBlockTags.SEMAPHORE_POLES.matches(blockState)) {
                break;
            }
            currentPos = currentPos.below();
        }

        currentPos = pos.above();
        for (int i = 0; i < 16; i++) {
            BlockState blockState = world.getBlockState(currentPos);
            if (CRBlocks.SEMAPHORE.has(blockState)) {
                BlockState rotatedState = blockState.setValue(UPSIDE_DOWN, state.getValue(UPSIDE_DOWN));
                KineticBlockEntity.switchToBlockState(world, currentPos, Block.updateFromNeighbourShapes(rotatedState, world, currentPos));
            } else if (!CRTags.AllBlockTags.SEMAPHORE_POLES.matches(blockState)) {
                break;
            }
            currentPos = currentPos.above();
        }
    }

    public static class PlacementHelper implements IPlacementHelper {

        public PlacementHelper() {

        }

        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return CRBlocks.SEMAPHORE::isIn;
        }

        @Override
        public Predicate<BlockState> getStatePredicate() {
            return state -> CRBlocks.SEMAPHORE.has(state) || CRTags.AllBlockTags.SEMAPHORE_POLES.matches(state);
        }

        @Override
        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos,
                                         BlockHitResult ray) {

            Direction offsetDirection = ray.getLocation().subtract(Vec3.atCenterOf(pos)).y < 0 ? Direction.DOWN : Direction.UP;

            BlockPos newPos = pos.relative(offsetDirection);
            BlockState newState = world.getBlockState(newPos);

            if (!newState.canBeReplaced()) {
                newPos = pos.relative(offsetDirection.getOpposite());
                newState = world.getBlockState(newPos);
            }

            if (newState.canBeReplaced()) {

                Direction facing = ray.getDirection();
                if(facing.getAxis()== Direction.Axis.Y)
                    return PlacementOffset.fail();

                Vec3 look = player.getLookAngle();
                Vec3 cross = look.cross(new Vec3(facing.step()));
                boolean flipped = cross.y<0;
                boolean upsideDown = offsetDirection == Direction.DOWN && !CRConfigs.server().semaphores.simplifiedPlacement.get();

                return PlacementOffset.success(newPos, x -> x.setValue(FLIPPED,flipped).setValue(FACING,facing).setValue(UPSIDE_DOWN,upsideDown));
            }

            return PlacementOffset.fail();
        }

        @Override
        public void displayGhost(PlacementOffset offset) {
            if (!offset.hasGhostState())
                return;

            CreateClient.GHOST_BLOCKS.showGhostState(this, offset.getTransform().apply(offset.getGhostState().setValue(FULL,true)))
                    .at(offset.getBlockPos())
                    .breathingAlpha();
        }
    }

    public static class GirderPlacementHelper implements IPlacementHelper {

        public GirderPlacementHelper() {

        }

        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return CRTags.AllBlockTags.SEMAPHORE_POLES::matches;
        }

        @Override
        public Predicate<BlockState> getStatePredicate() {
            return CRBlocks.SEMAPHORE::has;
        }

        @Override
        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos,
                                         BlockHitResult ray) {

            Direction offsetDirection = ray.getLocation().subtract(Vec3.atCenterOf(pos)).y < 0 ? Direction.DOWN : Direction.UP;

            BlockPos newPos = pos.relative(offsetDirection);
            BlockState newState = world.getBlockState(newPos);

            if (!newState.canBeReplaced()) {
                newPos = pos.relative(offsetDirection.getOpposite());
                newState = world.getBlockState(newPos);
            }

            if (newState.canBeReplaced()) {

                Direction facing = ray.getDirection();
                if(facing.getAxis()== Direction.Axis.Y)
                    return PlacementOffset.fail();

                return PlacementOffset.success(newPos);
            }

            return PlacementOffset.fail();
        }
    }

    @Override
    public Class<SemaphoreBlockEntity> getBlockEntityClass() {
        return SemaphoreBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SemaphoreBlockEntity> getBlockEntityType() {
        return CRBlockEntities.SEMAPHORE.get();
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return CRShapes.SEMAPHORE.get(pState.getValue(FACING));
    }
}
