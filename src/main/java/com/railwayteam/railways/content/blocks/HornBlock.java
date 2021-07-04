package com.railwayteam.railways.content.blocks;

import com.railwayteam.railways.util.VoxelUtils;
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

    protected static class HornShape extends VoxelUtils.Shape {
        public final VoxelShape north2;
        public final VoxelShape north3;

        public HornShape(VoxelShape north, VoxelShape north2, VoxelShape north3) {
            super(north);
            this.north2 = north2;
            this.north3 = north3;
        }

        public HornShape(double x, double y, double z, double sizeX, double sizeY, double sizeZ, double x2, double y2, double z2, double sizeX2, double sizeY2, double sizeZ2, double x3, double y3, double z3, double sizeX3, double sizeY3, double sizeZ3) {
            this(Block.makeCuboidShape(x, y, z, sizeX, sizeY, sizeZ), Block.makeCuboidShape(x2, y2, z2, sizeX2, sizeY2, sizeZ2), Block.makeCuboidShape(x3, y3, z3, sizeX3, sizeY3, sizeZ3));
        }

        @Override
        public VoxelShape get(BlockState blockState, IBlockReader blockReader, BlockPos pos, ISelectionContext context) {
            Direction d = blockState.get(HORIZONTAL_FACING);
            switch (blockState.get(HORNS)) {
                case 2:
                    return forDir(north2, d);
                case 3:
                    return forDir(north3, d);
                default:
                    return forDir(north, d);
            }
        }
    }

    public static HornShape BottomShapes = new HornShape(
            4, 0, 1, 12, 13, 15, // 1 horn
            0, 0, 1, 16, 12, 15, // 2 horns
            0, 0, 1, 16, 15, 15 // 3 horns
    );

    public static HornShape SideShapes = new HornShape(
            4, 4, 1, 12, 12, 16, // 1 horn
            0, 4, 1, 16, 12, 16, // 2 horns
            0, 1, 1, 16, 15, 16 // 3 horns
    );

    public static HornShape TopShapes = new HornShape(
            4, 3, 1, 12, 16, 15, // 1 horn
            0, 4, 1, 16, 16, 15, // 2 horns
            0, 1, 1, 16, 16, 15 // 3 horns
    );

    @Override
    public VoxelShape getShape(BlockState blockState, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        Direction direction = blockState.get(HORIZONTAL_FACING);
        int horns = blockState.get(HORNS);
        switch(blockState.get(FACE)) {
            case FLOOR:
                return BottomShapes.get(blockState, p_220053_2_, p_220053_3_, p_220053_4_);
            case CEILING:
                return TopShapes.get(blockState, p_220053_2_, p_220053_3_, p_220053_4_);
            case WALL:
               return SideShapes.get(blockState, p_220053_2_, p_220053_3_, p_220053_4_);
            default: return SideShapes.north;
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(HORIZONTAL_FACING, HORNS, FACE);
    }
}
