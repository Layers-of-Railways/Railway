package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

public class NetherWartBlock extends BushBlock {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_0_3;
   private static final VoxelShape[] SHAPES = new VoxelShape[]{Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 11.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D)};

   protected NetherWartBlock(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(AGE, Integer.valueOf(0)));
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return SHAPES[state.get(AGE)];
   }

   protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
      return state.getBlock() == Blocks.SOUL_SAND;
   }

   public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
      int i = state.get(AGE);
      if (i < 3 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, rand.nextInt(10) == 0)) {
         state = state.with(AGE, Integer.valueOf(i + 1));
         worldIn.setBlockState(pos, state, 2);
         net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
      }

      super.tick(state, worldIn, pos, rand);
   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
      return new ItemStack(Items.NETHER_WART);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(AGE);
   }
}