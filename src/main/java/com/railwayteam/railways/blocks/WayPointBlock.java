package com.railwayteam.railways.blocks;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import net.minecraft.block.AbstractBlock.Properties;

public class WayPointBlock extends Block {
	public static final String name = "waypoint";

	// default constructor probably isn't necessary, but it's here for legacy reasons.
	public WayPointBlock() {
		this(Properties.copy(Blocks.OAK_FENCE));
	}

	// Registrate will always use this constructor, and properties are set inline during registration
	public WayPointBlock (Properties properties) {
		super(properties);
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos,
			ISelectionContext context) {
		return VoxelShapes.empty();
	}
	
	@Nonnull
    @Override
    public BlockRenderType getRenderShape(@Nonnull BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.or(VoxelShapes.box(7.0f/16, 0, 7.0f/16, 9.0f/16, 12.0f/16, 9.0f/16),
		       VoxelShapes.box(7.5f/16, 6.5f/16, 0, 8.5f/16, 11.5f/16, 7f/16));
	}
}
