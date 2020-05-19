package net.minecraft.block;

import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RailBlock extends AbstractRailBlock {
   public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE;

   protected RailBlock(Block.Properties builder) {
      super(false, builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(SHAPE, RailShape.NORTH_SOUTH));
   }

   protected void updateState(BlockState state, World worldIn, BlockPos pos, Block blockIn) {
      if (blockIn.getDefaultState().canProvidePower() && (new RailState(worldIn, pos, state)).countAdjacentRails() == 3) {
         this.getUpdatedState(worldIn, pos, state, false);
      }

   }

   public IProperty<RailShape> getShapeProperty() {
      return SHAPE;
   }

   /**
    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
    * fine.
    */
   public BlockState rotate(BlockState state, Rotation rot) {
      switch(rot) {
      case CLOCKWISE_180:
         switch((RailShape)state.get(SHAPE)) {
         case ASCENDING_EAST:
            return state.with(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return state.with(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
            return state.with(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return state.with(SHAPE, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return state.with(SHAPE, RailShape.NORTH_WEST);
         case SOUTH_WEST:
            return state.with(SHAPE, RailShape.NORTH_EAST);
         case NORTH_WEST:
            return state.with(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_EAST:
            return state.with(SHAPE, RailShape.SOUTH_WEST);
         }
      case COUNTERCLOCKWISE_90:
         switch((RailShape)state.get(SHAPE)) {
         case ASCENDING_EAST:
            return state.with(SHAPE, RailShape.ASCENDING_NORTH);
         case ASCENDING_WEST:
            return state.with(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_NORTH:
            return state.with(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_SOUTH:
            return state.with(SHAPE, RailShape.ASCENDING_EAST);
         case SOUTH_EAST:
            return state.with(SHAPE, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return state.with(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return state.with(SHAPE, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return state.with(SHAPE, RailShape.NORTH_WEST);
         case NORTH_SOUTH:
            return state.with(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return state.with(SHAPE, RailShape.NORTH_SOUTH);
         }
      case CLOCKWISE_90:
         switch((RailShape)state.get(SHAPE)) {
         case ASCENDING_EAST:
            return state.with(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_WEST:
            return state.with(SHAPE, RailShape.ASCENDING_NORTH);
         case ASCENDING_NORTH:
            return state.with(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_SOUTH:
            return state.with(SHAPE, RailShape.ASCENDING_WEST);
         case SOUTH_EAST:
            return state.with(SHAPE, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return state.with(SHAPE, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return state.with(SHAPE, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return state.with(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_SOUTH:
            return state.with(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return state.with(SHAPE, RailShape.NORTH_SOUTH);
         }
      default:
         return state;
      }
   }

   /**
    * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
    */
   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      RailShape railshape = state.get(SHAPE);
      switch(mirrorIn) {
      case LEFT_RIGHT:
         switch(railshape) {
         case ASCENDING_NORTH:
            return state.with(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return state.with(SHAPE, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return state.with(SHAPE, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return state.with(SHAPE, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return state.with(SHAPE, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return state.with(SHAPE, RailShape.SOUTH_EAST);
         default:
            return super.mirror(state, mirrorIn);
         }
      case FRONT_BACK:
         switch(railshape) {
         case ASCENDING_EAST:
            return state.with(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return state.with(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
         case ASCENDING_SOUTH:
         default:
            break;
         case SOUTH_EAST:
            return state.with(SHAPE, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return state.with(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return state.with(SHAPE, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return state.with(SHAPE, RailShape.NORTH_WEST);
         }
      }

      return super.mirror(state, mirrorIn);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(SHAPE);
   }
}