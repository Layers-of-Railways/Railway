package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class CakeBlock extends Block {
   public static final IntegerProperty BITES = BlockStateProperties.BITES_0_6;
   protected static final VoxelShape[] SHAPES = new VoxelShape[]{Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.makeCuboidShape(3.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.makeCuboidShape(5.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.makeCuboidShape(7.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.makeCuboidShape(9.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.makeCuboidShape(11.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.makeCuboidShape(13.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D)};

   protected CakeBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(BITES, Integer.valueOf(0)));
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return SHAPES[state.get(BITES)];
   }

   public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      if (worldIn.isRemote) {
         ItemStack itemstack = player.getHeldItem(handIn);
         if (this.func_226911_a_(worldIn, pos, state, player) == ActionResultType.SUCCESS) {
            return ActionResultType.SUCCESS;
         }

         if (itemstack.isEmpty()) {
            return ActionResultType.CONSUME;
         }
      }

      return this.func_226911_a_(worldIn, pos, state, player);
   }

   private ActionResultType func_226911_a_(IWorld p_226911_1_, BlockPos p_226911_2_, BlockState p_226911_3_, PlayerEntity p_226911_4_) {
      if (!p_226911_4_.canEat(false)) {
         return ActionResultType.PASS;
      } else {
         p_226911_4_.addStat(Stats.EAT_CAKE_SLICE);
         p_226911_4_.getFoodStats().addStats(2, 0.1F);
         int i = p_226911_3_.get(BITES);
         if (i < 6) {
            p_226911_1_.setBlockState(p_226911_2_, p_226911_3_.with(BITES, Integer.valueOf(i + 1)), 3);
         } else {
            p_226911_1_.removeBlock(p_226911_2_, false);
         }

         return ActionResultType.SUCCESS;
      }
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      return facing == Direction.DOWN && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.down()).getMaterial().isSolid();
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(BITES);
   }

   /**
    * @deprecated call via {@link IBlockState#getComparatorInputOverride(World,BlockPos)} whenever possible.
    * Implementing/overriding is fine.
    */
   public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
      return (7 - blockState.get(BITES)) * 2;
   }

   /**
    * @deprecated call via {@link IBlockState#hasComparatorInputOverride()} whenever possible. Implementing/overriding
    * is fine.
    */
   public boolean hasComparatorInputOverride(BlockState state) {
      return true;
   }

   public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
}