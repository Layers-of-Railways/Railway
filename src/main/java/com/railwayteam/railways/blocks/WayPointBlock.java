package com.railwayteam.railways.blocks;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;

public class WayPointBlock extends Block {
	public static final String name = "waypoint";

	// default constructor probably isn't necessary, but it's here for legacy reasons.
	public WayPointBlock() {
		this(Properties.from(Blocks.OAK_FENCE));
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
	
//	@Override
//	public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
//		return false;
//	}
	// moved to block properties

	@Nonnull
    @Override
    public BlockRenderType getRenderType(@Nonnull BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.or(VoxelShapes.create(7.0f/16, 0, 7.0f/16, 9.0f/16, 12.0f/16, 9.0f/16),
		       VoxelShapes.create(7.5f/16, 6.5f/16, 0, 8.5f/16, 11.5f/16, 7f/16));
	}

	public ShapedRecipeBuilder recipe(DataGenContext<Block, WayPointBlock> ctx, IItemProvider A) {
		return ShapedRecipeBuilder.shapedRecipe(ctx.get())
				.patternLine(" A ")
				.patternLine(" T ")
				.key('A', A)
				.key('T', Items.STICK)
				.addCriterion("has_sail", RegistrateRecipeProvider.hasItem(A));
	}

	public void recipe(DataGenContext<Block, WayPointBlock> ctx, RegistrateRecipeProvider prov, IItemProvider A) {
		recipe(ctx, A).build(prov, new ResourceLocation("railways", "waypoint_" + A.asItem().getRegistryName().getPath()));
	}
}
