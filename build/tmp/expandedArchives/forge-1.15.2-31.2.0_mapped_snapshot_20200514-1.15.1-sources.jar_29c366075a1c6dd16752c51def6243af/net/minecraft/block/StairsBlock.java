package net.minecraft.block;

import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StairsBlock extends Block implements IWaterLoggable {
   public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
   public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
   public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape AABB_SLAB_TOP = SlabBlock.TOP_SHAPE;
   protected static final VoxelShape AABB_SLAB_BOTTOM = SlabBlock.BOTTOM_SHAPE;
   protected static final VoxelShape NWD_CORNER = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 8.0D, 8.0D, 8.0D);
   protected static final VoxelShape SWD_CORNER = Block.makeCuboidShape(0.0D, 0.0D, 8.0D, 8.0D, 8.0D, 16.0D);
   protected static final VoxelShape NWU_CORNER = Block.makeCuboidShape(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 8.0D);
   protected static final VoxelShape SWU_CORNER = Block.makeCuboidShape(0.0D, 8.0D, 8.0D, 8.0D, 16.0D, 16.0D);
   protected static final VoxelShape NED_CORNER = Block.makeCuboidShape(8.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
   protected static final VoxelShape SED_CORNER = Block.makeCuboidShape(8.0D, 0.0D, 8.0D, 16.0D, 8.0D, 16.0D);
   protected static final VoxelShape NEU_CORNER = Block.makeCuboidShape(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
   protected static final VoxelShape SEU_CORNER = Block.makeCuboidShape(8.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape[] SLAB_TOP_SHAPES = makeShapes(AABB_SLAB_TOP, NWD_CORNER, NED_CORNER, SWD_CORNER, SED_CORNER);
   protected static final VoxelShape[] SLAB_BOTTOM_SHAPES = makeShapes(AABB_SLAB_BOTTOM, NWU_CORNER, NEU_CORNER, SWU_CORNER, SEU_CORNER);
   private static final int[] field_196522_K = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};
   private final Block modelBlock;
   private final BlockState modelState;

   private static VoxelShape[] makeShapes(VoxelShape slabShape, VoxelShape nwCorner, VoxelShape neCorner, VoxelShape swCorner, VoxelShape seCorner) {
      return IntStream.range(0, 16).mapToObj((p_199780_5_) -> {
         return combineShapes(p_199780_5_, slabShape, nwCorner, neCorner, swCorner, seCorner);
      }).toArray((p_199778_0_) -> {
         return new VoxelShape[p_199778_0_];
      });
   }

   /**
    * combines the shapes according to the mode set in the bitfield
    */
   private static VoxelShape combineShapes(int bitfield, VoxelShape slabShape, VoxelShape nwCorner, VoxelShape neCorner, VoxelShape swCorner, VoxelShape seCorner) {
      VoxelShape voxelshape = slabShape;
      if ((bitfield & 1) != 0) {
         voxelshape = VoxelShapes.or(slabShape, nwCorner);
      }

      if ((bitfield & 2) != 0) {
         voxelshape = VoxelShapes.or(voxelshape, neCorner);
      }

      if ((bitfield & 4) != 0) {
         voxelshape = VoxelShapes.or(voxelshape, swCorner);
      }

      if ((bitfield & 8) != 0) {
         voxelshape = VoxelShapes.or(voxelshape, seCorner);
      }

      return voxelshape;
   }

   // Forge: Use the other constructor that takes a Supplier
   @Deprecated
   protected StairsBlock(BlockState state, Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(HALF, Half.BOTTOM).with(SHAPE, StairsShape.STRAIGHT).with(WATERLOGGED, Boolean.valueOf(false)));
      this.modelBlock = state.getBlock();
      this.modelState = state;
      this.stateSupplier = () -> state;
   }

   public StairsBlock(java.util.function.Supplier<BlockState> state, Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(HALF, Half.BOTTOM).with(SHAPE, StairsShape.STRAIGHT).with(WATERLOGGED, Boolean.valueOf(false)));
      this.modelBlock = Blocks.AIR; // These are unused, fields are redirected
      this.modelState = Blocks.AIR.getDefaultState();
      this.stateSupplier = state;
   }

   public boolean isTransparent(BlockState state) {
      return true;
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return (state.get(HALF) == Half.TOP ? SLAB_TOP_SHAPES : SLAB_BOTTOM_SHAPES)[field_196522_K[this.func_196511_x(state)]];
   }

   private int func_196511_x(BlockState state) {
      return state.get(SHAPE).ordinal() * 4 + state.get(FACING).getHorizontalIndex();
   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      this.modelBlock.animateTick(stateIn, worldIn, pos, rand);
   }

   public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
      this.modelState.onBlockClicked(worldIn, pos, player);
   }

   /**
    * Called after a player destroys this Block - the posiiton pos may no longer hold the state indicated.
    */
   public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
      this.modelBlock.onPlayerDestroy(worldIn, pos, state);
   }

   /**
    * Returns how much this block can resist explosions from the passed in entity.
    */
   public float getExplosionResistance() {
      return this.modelBlock.getExplosionResistance();
   }

   /**
    * How many world ticks before ticking
    */
   public int tickRate(IWorldReader worldIn) {
      return this.modelBlock.tickRate(worldIn);
   }

   public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
      if (state.getBlock() != state.getBlock()) {
         this.modelState.neighborChanged(worldIn, pos, Blocks.AIR, pos, false);
         this.modelBlock.onBlockAdded(this.modelState, worldIn, pos, oldState, false);
      }
   }

   public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         this.modelState.onReplaced(worldIn, pos, newState, isMoving);
      }
   }

   /**
    * Called when the given entity walks on this Block
    */
   public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
      this.modelBlock.onEntityWalk(worldIn, pos, entityIn);
   }

   public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
      this.modelBlock.tick(state, worldIn, pos, rand);
   }

   public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      return this.modelState.onBlockActivated(worldIn, player, handIn, hit);
   }

   /**
    * Called when this Block is destroyed by an Explosion
    */
   public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
      this.modelBlock.onExplosionDestroy(worldIn, pos, explosionIn);
   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      Direction direction = context.getFace();
      BlockPos blockpos = context.getPos();
      IFluidState ifluidstate = context.getWorld().getFluidState(blockpos);
      BlockState blockstate = this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing()).with(HALF, direction != Direction.DOWN && (direction == Direction.UP || !(context.getHitVec().y - (double)blockpos.getY() > 0.5D)) ? Half.BOTTOM : Half.TOP).with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
      return blockstate.with(SHAPE, getShapeProperty(blockstate, context.getWorld(), blockpos));
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (stateIn.get(WATERLOGGED)) {
         worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
      }

      return facing.getAxis().isHorizontal() ? stateIn.with(SHAPE, getShapeProperty(stateIn, worldIn, currentPos)) : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   /**
    * Returns a stair shape property based on the surrounding stairs from the given blockstate and position
    */
   private static StairsShape getShapeProperty(BlockState state, IBlockReader worldIn, BlockPos pos) {
      Direction direction = state.get(FACING);
      BlockState blockstate = worldIn.getBlockState(pos.offset(direction));
      if (isBlockStairs(blockstate) && state.get(HALF) == blockstate.get(HALF)) {
         Direction direction1 = blockstate.get(FACING);
         if (direction1.getAxis() != state.get(FACING).getAxis() && isDifferentStairs(state, worldIn, pos, direction1.getOpposite())) {
            if (direction1 == direction.rotateYCCW()) {
               return StairsShape.OUTER_LEFT;
            }

            return StairsShape.OUTER_RIGHT;
         }
      }

      BlockState blockstate1 = worldIn.getBlockState(pos.offset(direction.getOpposite()));
      if (isBlockStairs(blockstate1) && state.get(HALF) == blockstate1.get(HALF)) {
         Direction direction2 = blockstate1.get(FACING);
         if (direction2.getAxis() != state.get(FACING).getAxis() && isDifferentStairs(state, worldIn, pos, direction2)) {
            if (direction2 == direction.rotateYCCW()) {
               return StairsShape.INNER_LEFT;
            }

            return StairsShape.INNER_RIGHT;
         }
      }

      return StairsShape.STRAIGHT;
   }

   private static boolean isDifferentStairs(BlockState state, IBlockReader worldIn, BlockPos pos, Direction face) {
      BlockState blockstate = worldIn.getBlockState(pos.offset(face));
      return !isBlockStairs(blockstate) || blockstate.get(FACING) != state.get(FACING) || blockstate.get(HALF) != state.get(HALF);
   }

   public static boolean isBlockStairs(BlockState state) {
      return state.getBlock() instanceof StairsBlock;
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
      Direction direction = state.get(FACING);
      StairsShape stairsshape = state.get(SHAPE);
      switch(mirrorIn) {
      case LEFT_RIGHT:
         if (direction.getAxis() == Direction.Axis.Z) {
            switch(stairsshape) {
            case INNER_LEFT:
               return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.INNER_RIGHT);
            case INNER_RIGHT:
               return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.INNER_LEFT);
            case OUTER_LEFT:
               return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.OUTER_RIGHT);
            case OUTER_RIGHT:
               return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.OUTER_LEFT);
            default:
               return state.rotate(Rotation.CLOCKWISE_180);
            }
         }
         break;
      case FRONT_BACK:
         if (direction.getAxis() == Direction.Axis.X) {
            switch(stairsshape) {
            case INNER_LEFT:
               return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.INNER_LEFT);
            case INNER_RIGHT:
               return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.INNER_RIGHT);
            case OUTER_LEFT:
               return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.OUTER_RIGHT);
            case OUTER_RIGHT:
               return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.OUTER_LEFT);
            case STRAIGHT:
               return state.rotate(Rotation.CLOCKWISE_180);
            }
         }
      }

      return super.mirror(state, mirrorIn);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(FACING, HALF, SHAPE, WATERLOGGED);
   }

   public IFluidState getFluidState(BlockState state) {
      return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
   }

   public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }

   private final java.util.function.Supplier<BlockState> stateSupplier;
   private Block getModelBlock() {
       return getModelState().getBlock();
   }
   private BlockState getModelState() {
       return stateSupplier.get();
   }
}