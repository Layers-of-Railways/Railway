package net.minecraft.block;

import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PoweredRailBlock extends AbstractRailBlock {
   public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   private final boolean isActivator;  // TRUE for an Activator Rail, FALSE for Powered Rail

   protected PoweredRailBlock(Block.Properties builder) {
      this(builder, false);
   }

   protected PoweredRailBlock(Block.Properties builder, boolean isPoweredRail) {
      super(true, builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(SHAPE, RailShape.NORTH_SOUTH).with(POWERED, Boolean.valueOf(false)));
      this.isActivator = !isPoweredRail;
   }

   protected boolean findPoweredRailSignal(World worldIn, BlockPos pos, BlockState state, boolean p_176566_4_, int p_176566_5_) {
      if (p_176566_5_ >= 8) {
         return false;
      } else {
         int i = pos.getX();
         int j = pos.getY();
         int k = pos.getZ();
         boolean flag = true;
         RailShape railshape = state.get(SHAPE);
         switch(railshape) {
         case NORTH_SOUTH:
            if (p_176566_4_) {
               ++k;
            } else {
               --k;
            }
            break;
         case EAST_WEST:
            if (p_176566_4_) {
               --i;
            } else {
               ++i;
            }
            break;
         case ASCENDING_EAST:
            if (p_176566_4_) {
               --i;
            } else {
               ++i;
               ++j;
               flag = false;
            }

            railshape = RailShape.EAST_WEST;
            break;
         case ASCENDING_WEST:
            if (p_176566_4_) {
               --i;
               ++j;
               flag = false;
            } else {
               ++i;
            }

            railshape = RailShape.EAST_WEST;
            break;
         case ASCENDING_NORTH:
            if (p_176566_4_) {
               ++k;
            } else {
               --k;
               ++j;
               flag = false;
            }

            railshape = RailShape.NORTH_SOUTH;
            break;
         case ASCENDING_SOUTH:
            if (p_176566_4_) {
               ++k;
               ++j;
               flag = false;
            } else {
               --k;
            }

            railshape = RailShape.NORTH_SOUTH;
         }

         if (this.func_208071_a(worldIn, new BlockPos(i, j, k), p_176566_4_, p_176566_5_, railshape)) {
            return true;
         } else {
            return flag && this.func_208071_a(worldIn, new BlockPos(i, j - 1, k), p_176566_4_, p_176566_5_, railshape);
         }
      }
   }

   protected boolean func_208071_a(World p_208071_1_, BlockPos p_208071_2_, boolean p_208071_3_, int p_208071_4_, RailShape p_208071_5_) {
      BlockState blockstate = p_208071_1_.getBlockState(p_208071_2_);
      if (!(blockstate.getBlock() instanceof PoweredRailBlock)) {
         return false;
      } else {
         RailShape railshape = getRailDirection(blockstate, p_208071_1_, p_208071_2_, null);
         if (p_208071_5_ != RailShape.EAST_WEST || railshape != RailShape.NORTH_SOUTH && railshape != RailShape.ASCENDING_NORTH && railshape != RailShape.ASCENDING_SOUTH) {
            if (p_208071_5_ != RailShape.NORTH_SOUTH || railshape != RailShape.EAST_WEST && railshape != RailShape.ASCENDING_EAST && railshape != RailShape.ASCENDING_WEST) {
               if (isActivator == ((PoweredRailBlock)blockstate.getBlock()).isActivator) {
                  return p_208071_1_.isBlockPowered(p_208071_2_) ? true : this.findPoweredRailSignal(p_208071_1_, p_208071_2_, blockstate, p_208071_3_, p_208071_4_ + 1);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected void updateState(BlockState state, World worldIn, BlockPos pos, Block blockIn) {
      boolean flag = state.get(POWERED);
      boolean flag1 = worldIn.isBlockPowered(pos) || this.findPoweredRailSignal(worldIn, pos, state, true, 0) || this.findPoweredRailSignal(worldIn, pos, state, false, 0);
      if (flag1 != flag) {
         worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(flag1)), 3);
         worldIn.notifyNeighborsOfStateChange(pos.down(), this);
         if (state.get(SHAPE).isAscending()) {
            worldIn.notifyNeighborsOfStateChange(pos.up(), this);
         }
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
         case NORTH_SOUTH:
            return state.with(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return state.with(SHAPE, RailShape.NORTH_SOUTH);
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
         }
      case CLOCKWISE_90:
         switch((RailShape)state.get(SHAPE)) {
         case NORTH_SOUTH:
            return state.with(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return state.with(SHAPE, RailShape.NORTH_SOUTH);
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
      builder.add(SHAPE, POWERED);
   }

   public boolean isActivatorRail() {
      return isActivator;
   }
}