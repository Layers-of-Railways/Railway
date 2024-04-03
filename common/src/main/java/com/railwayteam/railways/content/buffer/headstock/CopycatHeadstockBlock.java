package com.railwayteam.railways.content.buffer.headstock;

import com.railwayteam.railways.content.buffer.BlockStateBlockItemGroup;
import com.railwayteam.railways.registry.CRBlockEntities;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRShapes;
import com.railwayteam.railways.util.AdventureUtils;
import com.railwayteam.railways.util.ShapeUtils;
import com.railwayteam.railways.util.client.OcclusionTestWorld;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.copycat.CopycatBlockEntity;
import com.simibubi.create.content.decoration.copycat.CopycatSpecialCases;
import com.simibubi.create.content.decoration.copycat.WaterloggedCopycatBlock;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CopycatHeadstockBlock extends WaterloggedCopycatBlock implements BlockStateBlockItemGroup.GroupedBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<HeadstockStyle> STYLE = HeadstockBlock.STYLE;
    public static final BooleanProperty UPSIDE_DOWN = HeadstockBlock.UPSIDE_DOWN;

    public CopycatHeadstockBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState()
            .setValue(FACING, Direction.NORTH)
            .setValue(STYLE, HeadstockStyle.BUFFER)
            .setValue(UPSIDE_DOWN, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING, STYLE, UPSIDE_DOWN));
    }

    @Override
    public boolean isAcceptedRegardless(BlockState material) {
        return CopycatSpecialCases.isBarsMaterial(material);
    }

    @Override
    public boolean isIgnoredConnectivitySide(BlockAndTintGetter reader, BlockState state, Direction face,
                                             BlockPos fromPos, BlockPos toPos) {
        Direction facing = state.getValue(FACING);
        BlockState toState = reader.getBlockState(toPos);

        BlockPos diff = fromPos.subtract(toPos);
        int coord = facing.getAxis()
            .choose(diff.getX(), diff.getY(), diff.getZ());

        if (!toState.is(this))
            return facing != face.getOpposite();
//            return !(coord != 0 && coord == facing.getAxisDirection().getStep());

        return facing == toState.getValue(FACING)
            .getOpposite()
            && !(coord != 0 && coord == facing.getAxisDirection()
            .getStep());
    }

    @Override
    public boolean canConnectTexturesToward(BlockAndTintGetter reader, BlockPos fromPos, BlockPos toPos, BlockState state) {
        Direction facing = state.getValue(FACING);
        BlockState toState = reader.getBlockState(toPos);

        if (toPos.equals(fromPos.relative(facing)))
            return false;

        BlockPos diff = fromPos.subtract(toPos);
        int coord = facing.getAxis()
            .choose(diff.getX(), diff.getY(), diff.getZ());

        if (!toState.is(this))
            return coord != -facing.getAxisDirection()
                .getStep();

        if (isOccluded(state, toState, facing.getOpposite()))
            return true;
        if (toState.setValue(WATERLOGGED, false) == state.setValue(WATERLOGGED, false) && coord == 0)
            return true;

        return false;
    }

    @Override
    public boolean canFaceBeOccluded(BlockState state, Direction face) {
        return state.getValue(FACING)
            .getOpposite() == face;
    }

    @Override
    public boolean shouldFaceAlwaysRender(BlockState state, Direction face) {
        if (state.getValue(FACING) == face)
            return true;

        return face.getAxis().isVertical() && (face == Direction.DOWN ^ state.getValue(UPSIDE_DOWN));
    }

    // Can't use @Override because PortingLib's interface injection doesn't exist in common, but this method is supported cross-platform because of it anyway
    @SuppressWarnings("unused")
    public boolean supportsExternalFaceHiding(BlockState state) {
        return true;
    }

    // Can't use @Override because PortingLib's interface injection doesn't exist in common, but this method is supported cross-platform because of it anyway
    @SuppressWarnings("unused")
    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState,
                                     Direction dir) {
        pos = pos.immutable();
        BlockPos otherPos = pos.relative(dir);
        BlockState material = getMaterial(level, pos);
        BlockState otherMaterial = getMaterial(level, otherPos);

        // should hopefully never happen, but just in case
        if (material == null) material = AllBlocks.COPYCAT_BASE.getDefaultState();
        if (otherMaterial == null) otherMaterial = AllBlocks.COPYCAT_BASE.getDefaultState();

        if (state.is(this) == neighborState.is(this)) {
            if (CopycatSpecialCases.isBarsMaterial(material)
                && CopycatSpecialCases.isBarsMaterial(otherMaterial))
                return state.getValue(FACING) == neighborState.getValue(FACING)
                    && state.getValue(UPSIDE_DOWN) == neighborState.getValue(UPSIDE_DOWN);
            if (material.skipRendering(otherMaterial, dir.getOpposite()))
                return isOccluded(state, neighborState, dir.getOpposite());

            // todo maybe PR this extra occlusion check to Create - vanilla Create renders solid faces between copycat panels etc
            OcclusionTestWorld occlusionTestWorld = new OcclusionTestWorld();
            occlusionTestWorld.setBlock(pos, material);
            occlusionTestWorld.setBlock(otherPos, otherMaterial);
            if (material.isSolidRender(occlusionTestWorld, pos) && otherMaterial.isSolidRender(occlusionTestWorld, otherPos))
                if(!Block.shouldRenderFace(otherMaterial, occlusionTestWorld, pos, dir.getOpposite(), otherPos))
                    return isOccluded(state, neighborState, dir.getOpposite());
        }

        return state.getValue(FACING) == dir.getOpposite()
            && material.skipRendering(neighborState, dir.getOpposite());
    }

    private static boolean isOccluded(BlockState state, BlockState other, Direction pDirection) {
        state = state.setValue(WATERLOGGED, false);
        other = other.setValue(WATERLOGGED, false);
        Direction facing = state.getValue(FACING);
        if (facing.getOpposite() == other.getValue(FACING) && pDirection == facing)
            return true;
        if (other.getValue(FACING) != facing)
            return false;
        if (other.getValue(UPSIDE_DOWN) != state.getValue(UPSIDE_DOWN))
            return false;
        return pDirection.getAxis() != facing.getAxis() && pDirection.getAxis().isHorizontal();
    }

    @Override
    public BlockEntityType<? extends CopycatBlockEntity> getBlockEntityType() {
        return CRBlockEntities.COPYCAT_HEADSTOCK.get();
    }

    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        if (targetedFace.getAxis().isVertical()) {
            return super.getRotatedBlockState(originalState, targetedFace);
        } else {
            return originalState.cycle(STYLE);
        }
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        // call the super method to only pop out the copycatted block, not cycling the style
        super.onWrenched(state, context);

        // IWrenchable default implementation, to not accidentally call onWrenched twice
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        if (world instanceof ServerLevel) {
            if (player != null && !player.isCreative())
                Block.getDrops(state, (ServerLevel) world, pos, world.getBlockEntity(pos), player, context.getItemInHand())
                    .forEach(itemStack -> {
                        player.getInventory().placeItemBackInInventory(itemStack);
                    });
            state.spawnAfterBreak((ServerLevel) world, pos, ItemStack.EMPTY, true);
            world.destroyBlock(pos, false);
            playRemoveSound(world, pos);
        }
        return InteractionResult.SUCCESS;
    }

    // copied directly from {@link IWrenchable}, because java doesn't support IWrenchable.super if we're not directly implementing it...
    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        // if the headstock part is wrenched, apply the 'super' wrench behaviour
        if (ShapeUtils.isTouching(context.getClickLocation(), context.getClickedPos(), getHeadstockShape(state))) {
            // If the style is PLAIN (so that the only place that *can* be clicked is the headstock,
            // then only allow material extraction if the clicked face is the 'back'
            if (state.getValue(STYLE) != HeadstockStyle.PLAIN || context.getClickedFace() == state.getValue(FACING).getOpposite()) {
                InteractionResult result = super.onWrenched(state, context);
                if (result.consumesAction()) return result;
            }
        }
        Level world = context.getLevel();
        BlockState rotated = getRotatedBlockState(state, context.getClickedFace());
        if (!rotated.canSurvive(world, context.getClickedPos()))
            return InteractionResult.PASS;

        KineticBlockEntity.switchToBlockState(world, context.getClickedPos(), updateAfterWrenched(rotated, context));

        BlockEntity be = context.getLevel()
            .getBlockEntity(context.getClickedPos());
        if (be instanceof GeneratingKineticBlockEntity) {
            ((GeneratingKineticBlockEntity) be).reActivateSource = true;
        }

        if (world.getBlockState(context.getClickedPos()) != state)
            playRotateSound(world, context.getClickedPos());

        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null) {
            if (context.getClickedFace().getAxis().isVertical()) {
                state = state.setValue(FACING, context.getHorizontalDirection().getOpposite());
            } else {
                state = state.setValue(FACING, context.getClickedFace());
                if (context.getClickLocation().y - (double) context.getClickedPos().getY() < 0.5) {
                    state = state.setValue(UPSIDE_DOWN, true);
                }
            }
        }
        return state; // withWater() is already handled by super
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return CRBlocks.HEADSTOCK.get().getShape(state, level, pos, context);
    }

    protected VoxelShape getHeadstockShape(BlockState state) {
        return CRShapes.HEADSTOCK_PLAIN.get(state.getValue(FACING));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
                                 BlockHitResult pHit) {
        if (AdventureUtils.isAdventure(pPlayer))
            return InteractionResult.PASS;
        InteractionResult result = onBlockEntityUse(pLevel, pPos, be -> {
            if (be instanceof CopycatHeadstockBlockEntity copycatHeadstock) {
                return copycatHeadstock.applyDyeIfValid(pPlayer.getItemInHand(pHand));
            }
            return InteractionResult.PASS;
        });
        if (result.consumesAction()) return result;
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Environment(EnvType.CLIENT)
    public static ItemColor wrappedItemColor() {
        return new WrappedItemColor();
    }

    @Environment(EnvType.CLIENT)
    public static class WrappedItemColor implements ItemColor {

        @Override
        public int getColor(ItemStack itemStack, int i) {
            return GrassColor.get(0.5D, 1.0D);
        }

    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return CRBlocks.COPYCAT_HEADSTOCK_GROUP.get(state.getValue(STYLE)).asStack();
    }
}
