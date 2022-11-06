package com.railwayteam.railways.content.semaphore;

import com.railwayteam.railways.registry.CRBlockEntities;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRShapes;
import com.railwayteam.railways.registry.CRTags;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTileEntities;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.contraptions.base.GeneratingKineticTileEntity;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.relays.advanced.GantryShaftBlock;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.content.logistics.block.redstone.DoubleFaceAttachedBlock;
import com.simibubi.create.content.logistics.block.redstone.NixieTubeTileEntity;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.placement.IPlacementHelper;
import com.simibubi.create.foundation.utility.placement.PlacementHelpers;
import com.simibubi.create.foundation.utility.placement.PlacementOffset;
import com.simibubi.create.foundation.utility.placement.util.PoleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
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
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.checkerframework.checker.units.qual.A;

import java.util.function.Predicate;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class SemaphoreBlock  extends HorizontalDirectionalBlock implements ITE<SemaphoreBlockEntity>,IWrenchable {

    public static final int placementHelperId = PlacementHelpers.register(new PlacementHelper());
    public static final int girderPlacementHelperId = PlacementHelpers.register(new GirderPlacementHelper());
    public static final BooleanProperty FLIPPED = BooleanProperty.create("flipped");
    public static final BooleanProperty FULL = BooleanProperty.create("full");

    public SemaphoreBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState().setValue(FLIPPED,false).setValue(FULL,false));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACING).add(FLIPPED).add(FULL));
    }
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

        return state.setValue(FACING,facing).setValue(FLIPPED,flipped);
    }
    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        BlockState rotated;

        if(context.getClickedFace().getAxis() != Direction.Axis.Y)
        {
            if (context.getClickedFace() == state.getValue(FACING))
            {
                rotated = state.cycle(FLIPPED);
            }else
                rotated = state.setValue(FACING,context.getClickedFace());
        }else
        {
            rotated = getRotatedBlockState(state, context.getClickedFace());
        }

        if (!rotated.canSurvive(world, context.getClickedPos()))
            return InteractionResult.PASS;


        KineticTileEntity.switchToBlockState(world, context.getClickedPos(), updateAfterWrenched(rotated, context));

        BlockEntity te = context.getLevel()
                .getBlockEntity(context.getClickedPos());


        if (world.getBlockState(context.getClickedPos()) != state)
            playRotateSound(world, context.getClickedPos());

        return InteractionResult.SUCCESS;
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
            return state -> CRBlocks.SEMAPHORE.is(state.getBlock()) || CRTags.AllBlockTags.SEMAPHORE_POLES.matches(state);
        }

        @Override
        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos,
                                         BlockHitResult ray) {

            BlockPos newPos = pos.relative(Direction.UP);
            BlockState newState = world.getBlockState(newPos);

            if (newState.getMaterial().isReplaceable()) {

                Direction facing = ray.getDirection();
                if(facing.getAxis()== Direction.Axis.Y)
                    return PlacementOffset.fail();

                Vec3 look = player.getLookAngle();
                Vec3 cross = look.cross(new Vec3(facing.step()));
                boolean flipped = cross.y<0;

                return PlacementOffset.success(newPos, x -> x.setValue(FLIPPED,flipped).setValue(FACING,facing));
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
            return AllBlocks.METAL_GIRDER::isIn;
        }

        @Override
        public Predicate<BlockState> getStatePredicate() {
            return state -> CRBlocks.SEMAPHORE.is(state.getBlock());
        }

        @Override
        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos,
                                         BlockHitResult ray) {

            BlockPos newPos = pos.relative(Direction.UP);
            BlockState newState = world.getBlockState(newPos);

            if (newState.getMaterial().isReplaceable()) {

                Direction facing = ray.getDirection();
                if(facing.getAxis()== Direction.Axis.Y)
                    return PlacementOffset.fail();

                return PlacementOffset.success(newPos);
            }

            return PlacementOffset.fail();
        }
    }

    @Override
    public Class<SemaphoreBlockEntity> getTileEntityClass() {
        return SemaphoreBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SemaphoreBlockEntity> getTileEntityType() {
        return CRBlockEntities.SEMAPHORE.get();
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return CRShapes.SEMAPHORE.get(pState.getValue(FACING));
    }
}
