package com.railwayteam.railways.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class HornBlock extends HorizontalFaceBlock {
    public static final IntegerProperty HORNS = IntegerProperty.create("horns", 1, 3);

    public HornBlock(Properties p_i48402_1_) {
        super(p_i48402_1_);
        this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH).with(HORNS, 1).with(FACE, AttachFace.WALL));
    }

    // couldnt figure out how to datagen a loot table that uses blockstate properties so ill just hardcode it for now
    // its definetely better to use a json one for datapack compat
    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder p_220076_2_) {
        return Arrays.asList(new ItemStack(this, state.get(HORNS)));
    }

    public void setHorns(World world, BlockPos pos, int horns) {
        world.setBlockState(pos, world.getBlockState(pos).with(HORNS, horns), 1);
//        world.notifyNeighborsOfStateChange(pos, this);
    }

    @Override
    public ActionResultType onUse(BlockState blockState, World world, BlockPos pos, PlayerEntity plr, Hand hand, BlockRayTraceResult raytrace) {
        if(plr.isSneaking() || !plr.isAllowEdit()) return ActionResultType.PASS;
        ItemStack stack = plr.getHeldItem(hand);
        int horns = blockState.get(HORNS);
        if(stack.getItem().getRegistryName().equals(this.getRegistryName()) && horns < 3) {
            setHorns(world, pos,  horns+ 1);
            if(!plr.isCreative()) {
                stack.shrink(1);
            }
        } else {
            // TODO: when the horn sound is done, use that sound here
            world.playSound(null, pos, SoundEvents.BLOCK_BELL_USE, SoundCategory.BLOCKS, 2.0F, 1.0F);
        }
        return ActionResultType.SUCCESS;
    }

    // shape stuff
    public static VoxelShape ShapeBottomNorthSouth = Block.makeCuboidShape(0, 0, 1, 16, 15, 15);
    public static VoxelShape ShapeBottomWestEast = Block.makeCuboidShape(1, 0, 0, 15, 15, 16);

    public static VoxelShape ShapeSideNorth = Block.makeCuboidShape(0, 1, 1, 16, 15, 16);
    public static VoxelShape ShapeSideSouth = Block.makeCuboidShape(0, 1, 0, 16, 15, 15);
    public static VoxelShape ShapeSideWest = Block.makeCuboidShape(1, 1, 0, 16, 15, 16);
    public static VoxelShape ShapeSideEast = Block.makeCuboidShape(0, 1, 0, 15, 15, 16);

    @Override
    public VoxelShape getShape(BlockState blockState, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        Direction direction = blockState.get(HORIZONTAL_FACING);
        switch(blockState.get(FACE)) {
            case FLOOR:
                return direction.getAxis() == Direction.Axis.X ? ShapeBottomNorthSouth : ShapeBottomWestEast;
            case WALL:
                switch (direction) {
                    case WEST:
                        return ShapeSideWest;
                    case EAST:
                        return ShapeSideEast;
                    case NORTH:
                        return ShapeSideNorth;
                    case SOUTH:
                        return ShapeSideSouth;
                }
            default: return ShapeSideNorth;
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(HORIZONTAL_FACING, HORNS, FACE);
    }
}
