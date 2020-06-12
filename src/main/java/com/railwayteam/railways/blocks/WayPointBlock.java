package com.railwayteam.railways.blocks;

import javax.annotation.Nonnull;

import com.railwayteam.railways.Railways;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class WayPointBlock extends Block{
	public static final String name = "waypoint";
	
	public WayPointBlock() {
		super(Properties.from(Blocks.OAK_FENCE));
		setRegistryName(Railways.createResourceLocation(name));
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos,
			ISelectionContext context) {
		return VoxelShapes.empty();
	}
	
	@Override
	public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return false;
	}
	
	@Nonnull
    @Override
    public BlockRenderType getRenderType(@Nonnull BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.or(VoxelShapes.create(7.0f/16, 0, 7.0f/16, 9.0f/16, 12.0f/16, 9.0f/16), VoxelShapes.create(7.5f/16, 6.5f/16, 0, 8.5f/16, 11.5f/16, 7f/16));
	}
}
