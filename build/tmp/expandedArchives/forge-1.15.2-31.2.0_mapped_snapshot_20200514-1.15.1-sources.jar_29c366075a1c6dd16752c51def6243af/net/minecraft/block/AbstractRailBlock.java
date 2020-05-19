package net.minecraft.block;

import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IProperty;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class AbstractRailBlock extends Block {
   protected static final VoxelShape FLAT_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
   protected static final VoxelShape ASCENDING_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
   private final boolean disableCorners;

   public static boolean isRail(World worldIn, BlockPos pos) {
      return isRail(worldIn.getBlockState(pos));
   }

   public static boolean isRail(BlockState state) {
      return state.isIn(BlockTags.RAILS);
   }

   protected AbstractRailBlock(boolean p_i48444_1_, Block.Properties p_i48444_2_) {
      super(p_i48444_2_);
      this.disableCorners = p_i48444_1_;
   }

   public boolean areCornersDisabled() {
      return this.disableCorners;
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      RailShape railshape = state.getBlock() == this ? getRailDirection(state, worldIn, pos, null) : null;
      return railshape != null && railshape.isAscending() ? ASCENDING_AABB : FLAT_AABB;
   }

   public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
      return hasSolidSideOnTop(worldIn, pos.down());
   }

   public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
      if (oldState.getBlock() != state.getBlock()) {
         state = this.getUpdatedState(worldIn, pos, state, true);
         if (this.disableCorners) {
            state.neighborChanged(worldIn, pos, this, pos, isMoving);
         }

      }
   }

   public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
      if (!worldIn.isRemote) {
         RailShape railshape = getRailDirection(state, worldIn, pos, null);
         boolean flag = false;
         BlockPos blockpos = pos.down();
         if (!hasSolidSideOnTop(worldIn, blockpos)) {
            flag = true;
         }

         BlockPos blockpos1 = pos.east();
         if (railshape == RailShape.ASCENDING_EAST && !hasSolidSideOnTop(worldIn, blockpos1)) {
            flag = true;
         } else {
            BlockPos blockpos2 = pos.west();
            if (railshape == RailShape.ASCENDING_WEST && !hasSolidSideOnTop(worldIn, blockpos2)) {
               flag = true;
            } else {
               BlockPos blockpos3 = pos.north();
               if (railshape == RailShape.ASCENDING_NORTH && !hasSolidSideOnTop(worldIn, blockpos3)) {
                  flag = true;
               } else {
                  BlockPos blockpos4 = pos.south();
                  if (railshape == RailShape.ASCENDING_SOUTH && !hasSolidSideOnTop(worldIn, blockpos4)) {
                     flag = true;
                  }
               }
            }
         }

         if (flag && !worldIn.isAirBlock(pos)) {
            if (!isMoving) {
               spawnDrops(state, worldIn, pos);
            }

            worldIn.removeBlock(pos, isMoving);
         } else {
            this.updateState(state, worldIn, pos, blockIn);
         }

      }
   }

   protected void updateState(BlockState state, World worldIn, BlockPos pos, Block blockIn) {
   }

   protected BlockState getUpdatedState(World worldIn, BlockPos pos, BlockState state, boolean placing) {
      if (worldIn.isRemote) {
         return state;
      } else {
         RailShape railshape = state.get(this.getShapeProperty());
         return (new RailState(worldIn, pos, state)).func_226941_a_(worldIn.isBlockPowered(pos), placing, railshape).getNewState();
      }
   }

   /**
    * @deprecated call via {@link IBlockState#getMobilityFlag()} whenever possible. Implementing/overriding is fine.
    */
   public PushReaction getPushReaction(BlockState state) {
      return PushReaction.NORMAL;
   }

   public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!isMoving) {
         super.onReplaced(state, worldIn, pos, newState, isMoving);
         if (getRailDirection(state, worldIn, pos, null).isAscending()) {
            worldIn.notifyNeighborsOfStateChange(pos.up(), this);
         }

         if (this.disableCorners) {
            worldIn.notifyNeighborsOfStateChange(pos, this);
            worldIn.notifyNeighborsOfStateChange(pos.down(), this);
         }

      }
   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      BlockState blockstate = super.getDefaultState();
      Direction direction = context.getPlacementHorizontalFacing();
      boolean flag = direction == Direction.EAST || direction == Direction.WEST;
      return blockstate.with(this.getShapeProperty(), flag ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH);
   }

   //Forge: Use getRailDirection(IBlockAccess, BlockPos, IBlockState, EntityMinecart) for enhanced ability
   public abstract IProperty<RailShape> getShapeProperty();

   /* ======================================== FORGE START =====================================*/
   /**
    * Return true if the rail can make corners.
    * Used by placement logic.
    * @param world The world.
    * @param pos Block's position in world
    * @return True if the rail can make corners.
    */
   public boolean isFlexibleRail(BlockState state, IBlockReader world, BlockPos pos)
   {
       return !this.disableCorners;
   }

   /**
    * Returns true if the rail can make up and down slopes.
    * Used by placement logic.
    * @param world The world.
    * @param pos Block's position in world
    * @return True if the rail can make slopes.
    */
   public boolean canMakeSlopes(BlockState state, IBlockReader world, BlockPos pos) {
       return true;
   }

   /**
    * Return the rail's direction.
    * Can be used to make the cart think the rail is a different shape,
    * for example when making diamond junctions or switches.
    * The cart parameter will often be null unless it it called from EntityMinecart.
    *
    * @param world The world.
    * @param pos Block's position in world
    * @param state The BlockState
    * @param cart The cart asking for the metadata, null if it is not called by EntityMinecart.
    * @return The direction.
    */
   public RailShape getRailDirection(BlockState state, IBlockReader world, BlockPos pos, @javax.annotation.Nullable net.minecraft.entity.item.minecart.AbstractMinecartEntity cart) {
       return state.get(getShapeProperty());
   }

   /**
    * Returns the max speed of the rail at the specified position.
    * @param world The world.
    * @param cart The cart on the rail, may be null.
    * @param pos Block's position in world
    * @return The max speed of the current rail.
    */
   public float getRailMaxSpeed(BlockState state, World world, BlockPos pos, net.minecraft.entity.item.minecart.AbstractMinecartEntity cart) {
       return 0.4f;
   }

   /**
    * This function is called by any minecart that passes over this rail.
    * It is called once per update tick that the minecart is on the rail.
    * @param world The world.
    * @param cart The cart on the rail.
    * @param pos Block's position in world
    */
   public void onMinecartPass(BlockState state, World world, BlockPos pos, net.minecraft.entity.item.minecart.AbstractMinecartEntity cart) { }
}