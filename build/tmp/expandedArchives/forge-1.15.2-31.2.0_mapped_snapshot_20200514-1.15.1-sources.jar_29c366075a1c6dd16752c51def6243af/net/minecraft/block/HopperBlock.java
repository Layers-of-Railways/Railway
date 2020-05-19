package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class HopperBlock extends ContainerBlock {
   public static final DirectionProperty FACING = BlockStateProperties.FACING_EXCEPT_UP;
   public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;
   private static final VoxelShape INPUT_SHAPE = Block.makeCuboidShape(0.0D, 10.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   private static final VoxelShape MIDDLE_SHAPE = Block.makeCuboidShape(4.0D, 4.0D, 4.0D, 12.0D, 10.0D, 12.0D);
   private static final VoxelShape INPUT_MIDDLE_SHAPE = VoxelShapes.or(MIDDLE_SHAPE, INPUT_SHAPE);
   private static final VoxelShape field_196326_A = VoxelShapes.combineAndSimplify(INPUT_MIDDLE_SHAPE, IHopper.INSIDE_BOWL_SHAPE, IBooleanFunction.ONLY_FIRST);
   private static final VoxelShape DOWN_SHAPE = VoxelShapes.or(field_196326_A, Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 4.0D, 10.0D));
   private static final VoxelShape EAST_SHAPE = VoxelShapes.or(field_196326_A, Block.makeCuboidShape(12.0D, 4.0D, 6.0D, 16.0D, 8.0D, 10.0D));
   private static final VoxelShape NORTH_SHAPE = VoxelShapes.or(field_196326_A, Block.makeCuboidShape(6.0D, 4.0D, 0.0D, 10.0D, 8.0D, 4.0D));
   private static final VoxelShape SOUTH_SHAPE = VoxelShapes.or(field_196326_A, Block.makeCuboidShape(6.0D, 4.0D, 12.0D, 10.0D, 8.0D, 16.0D));
   private static final VoxelShape WEST_SHAPE = VoxelShapes.or(field_196326_A, Block.makeCuboidShape(0.0D, 4.0D, 6.0D, 4.0D, 8.0D, 10.0D));
   private static final VoxelShape DOWN_RAYTRACE_SHAPE = IHopper.INSIDE_BOWL_SHAPE;
   private static final VoxelShape EAST_RAYTRACE_SHAPE = VoxelShapes.or(IHopper.INSIDE_BOWL_SHAPE, Block.makeCuboidShape(12.0D, 8.0D, 6.0D, 16.0D, 10.0D, 10.0D));
   private static final VoxelShape NORTH_RAYTRACE_SHAPE = VoxelShapes.or(IHopper.INSIDE_BOWL_SHAPE, Block.makeCuboidShape(6.0D, 8.0D, 0.0D, 10.0D, 10.0D, 4.0D));
   private static final VoxelShape SOUTH_RAYTRACE_SHAPE = VoxelShapes.or(IHopper.INSIDE_BOWL_SHAPE, Block.makeCuboidShape(6.0D, 8.0D, 12.0D, 10.0D, 10.0D, 16.0D));
   private static final VoxelShape WEST_RAYTRACE_SHAPE = VoxelShapes.or(IHopper.INSIDE_BOWL_SHAPE, Block.makeCuboidShape(0.0D, 8.0D, 6.0D, 4.0D, 10.0D, 10.0D));

   public HopperBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.DOWN).with(ENABLED, Boolean.valueOf(true)));
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      switch((Direction)state.get(FACING)) {
      case DOWN:
         return DOWN_SHAPE;
      case NORTH:
         return NORTH_SHAPE;
      case SOUTH:
         return SOUTH_SHAPE;
      case WEST:
         return WEST_SHAPE;
      case EAST:
         return EAST_SHAPE;
      default:
         return field_196326_A;
      }
   }

   public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
      switch((Direction)state.get(FACING)) {
      case DOWN:
         return DOWN_RAYTRACE_SHAPE;
      case NORTH:
         return NORTH_RAYTRACE_SHAPE;
      case SOUTH:
         return SOUTH_RAYTRACE_SHAPE;
      case WEST:
         return WEST_RAYTRACE_SHAPE;
      case EAST:
         return EAST_RAYTRACE_SHAPE;
      default:
         return IHopper.INSIDE_BOWL_SHAPE;
      }
   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      Direction direction = context.getFace().getOpposite();
      return this.getDefaultState().with(FACING, direction.getAxis() == Direction.Axis.Y ? Direction.DOWN : direction).with(ENABLED, Boolean.valueOf(true));
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new HopperTileEntity();
   }

   /**
    * Called by ItemBlocks after a block is set in the world, to allow post-place logic
    */
   public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
      if (stack.hasDisplayName()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof HopperTileEntity) {
            ((HopperTileEntity)tileentity).setCustomName(stack.getDisplayName());
         }
      }

   }

   public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
      if (oldState.getBlock() != state.getBlock()) {
         this.updateState(worldIn, pos, state);
      }
   }

   public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      if (worldIn.isRemote) {
         return ActionResultType.SUCCESS;
      } else {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof HopperTileEntity) {
            player.openContainer((HopperTileEntity)tileentity);
            player.addStat(Stats.INSPECT_HOPPER);
         }

         return ActionResultType.SUCCESS;
      }
   }

   public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
      this.updateState(worldIn, pos, state);
   }

   private void updateState(World worldIn, BlockPos pos, BlockState state) {
      boolean flag = !worldIn.isBlockPowered(pos);
      if (flag != state.get(ENABLED)) {
         worldIn.setBlockState(pos, state.with(ENABLED, Boolean.valueOf(flag)), 4);
      }

   }

   public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof HopperTileEntity) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (HopperTileEntity)tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
         }

         super.onReplaced(state, worldIn, pos, newState, isMoving);
      }
   }

   /**
    * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
    * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
    * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
    */
   public BlockRenderType getRenderType(BlockState state) {
      return BlockRenderType.MODEL;
   }

   /**
    * @deprecated call via {@link IBlockState#hasComparatorInputOverride()} whenever possible. Implementing/overriding
    * is fine.
    */
   public boolean hasComparatorInputOverride(BlockState state) {
      return true;
   }

   /**
    * @deprecated call via {@link IBlockState#getComparatorInputOverride(World,BlockPos)} whenever possible.
    * Implementing/overriding is fine.
    */
   public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
      return Container.calcRedstone(worldIn.getTileEntity(pos));
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
      builder.add(FACING, ENABLED);
   }

   public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof HopperTileEntity) {
         ((HopperTileEntity)tileentity).onEntityCollision(entityIn);
      }

   }

   public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
}