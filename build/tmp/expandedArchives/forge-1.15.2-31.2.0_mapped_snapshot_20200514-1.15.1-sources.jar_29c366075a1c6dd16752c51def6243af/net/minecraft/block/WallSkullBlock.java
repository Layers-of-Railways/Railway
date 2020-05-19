package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class WallSkullBlock extends AbstractSkullBlock {
   public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
   private static final Map<Direction, VoxelShape> SHAPES = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.makeCuboidShape(4.0D, 4.0D, 8.0D, 12.0D, 12.0D, 16.0D), Direction.SOUTH, Block.makeCuboidShape(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 8.0D), Direction.EAST, Block.makeCuboidShape(0.0D, 4.0D, 4.0D, 8.0D, 12.0D, 12.0D), Direction.WEST, Block.makeCuboidShape(8.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D)));

   protected WallSkullBlock(SkullBlock.ISkullType type, Block.Properties properties) {
      super(type, properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
   }

   /**
    * Returns the unlocalized name of the block with "tile." appended to the front.
    */
   public String getTranslationKey() {
      return this.asItem().getTranslationKey();
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return SHAPES.get(state.get(FACING));
   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      BlockState blockstate = this.getDefaultState();
      IBlockReader iblockreader = context.getWorld();
      BlockPos blockpos = context.getPos();
      Direction[] adirection = context.getNearestLookingDirections();

      for(Direction direction : adirection) {
         if (direction.getAxis().isHorizontal()) {
            Direction direction1 = direction.getOpposite();
            blockstate = blockstate.with(FACING, direction1);
            if (!iblockreader.getBlockState(blockpos.offset(direction)).isReplaceable(context)) {
               return blockstate;
            }
         }
      }

      return null;
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