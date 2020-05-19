package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AttachedStemBlock extends BushBlock {
   public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
   private final StemGrownBlock grownFruit;
   private static final Map<Direction, VoxelShape> SHAPES = Maps.newEnumMap(ImmutableMap.of(Direction.SOUTH, Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 16.0D), Direction.WEST, Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D), Direction.NORTH, Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 10.0D, 10.0D), Direction.EAST, Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 16.0D, 10.0D, 10.0D)));

   protected AttachedStemBlock(StemGrownBlock p_i48449_1_, Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
      this.grownFruit = p_i48449_1_;
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return SHAPES.get(state.get(FACING));
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      return facingState.getBlock() != this.grownFruit && facing == stateIn.get(FACING) ? this.grownFruit.getStem().getDefaultState().with(StemBlock.AGE, Integer.valueOf(7)) : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
      return state.getBlock() == Blocks.FARMLAND;
   }

   @OnlyIn(Dist.CLIENT)
   protected Item getSeeds() {
      if (this.grownFruit == Blocks.PUMPKIN) {
         return Items.PUMPKIN_SEEDS;
      } else {
         return this.grownFruit == Blocks.MELON ? Items.MELON_SEEDS : Items.AIR;
      }
   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
      return new ItemStack(this.getSeeds());
   }

   /**
    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
    * fine.
    */
   public BlockState rotate(BlockState state, Rotation rot) {
      return state.with(FACING, rot.rotate(state.get(FACING)));
   }

   /**
    * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
    */
   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.toRotation(state.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(FACING);
   }
}