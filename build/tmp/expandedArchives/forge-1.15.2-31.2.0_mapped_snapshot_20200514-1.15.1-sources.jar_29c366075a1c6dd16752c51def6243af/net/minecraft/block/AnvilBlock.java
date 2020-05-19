package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class AnvilBlock extends FallingBlock {
   public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
   private static final VoxelShape PART_BASE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
   private static final VoxelShape PART_LOWER_X = Block.makeCuboidShape(3.0D, 4.0D, 4.0D, 13.0D, 5.0D, 12.0D);
   private static final VoxelShape PART_MID_X = Block.makeCuboidShape(4.0D, 5.0D, 6.0D, 12.0D, 10.0D, 10.0D);
   private static final VoxelShape PART_UPPER_X = Block.makeCuboidShape(0.0D, 10.0D, 3.0D, 16.0D, 16.0D, 13.0D);
   private static final VoxelShape PART_LOWER_Z = Block.makeCuboidShape(4.0D, 4.0D, 3.0D, 12.0D, 5.0D, 13.0D);
   private static final VoxelShape PART_MID_Z = Block.makeCuboidShape(6.0D, 5.0D, 4.0D, 10.0D, 10.0D, 12.0D);
   private static final VoxelShape PART_UPPER_Z = Block.makeCuboidShape(3.0D, 10.0D, 0.0D, 13.0D, 16.0D, 16.0D);
   private static final VoxelShape X_AXIS_AABB = VoxelShapes.or(PART_BASE, PART_LOWER_X, PART_MID_X, PART_UPPER_X);
   private static final VoxelShape Z_AXIS_AABB = VoxelShapes.or(PART_BASE, PART_LOWER_Z, PART_MID_Z, PART_UPPER_Z);
   private static final TranslationTextComponent field_220273_k = new TranslationTextComponent("container.repair");

   public AnvilBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().rotateY());
   }

   public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      if (worldIn.isRemote) {
         return ActionResultType.SUCCESS;
      } else {
         player.openContainer(state.getContainer(worldIn, pos));
         player.addStat(Stats.INTERACT_WITH_ANVIL);
         return ActionResultType.SUCCESS;
      }
   }

   @Nullable
   public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
      return new SimpleNamedContainerProvider((p_220272_2_, p_220272_3_, p_220272_4_) -> {
         return new RepairContainer(p_220272_2_, p_220272_3_, IWorldPosCallable.of(worldIn, pos));
      }, field_220273_k);
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      Direction direction = state.get(FACING);
      return direction.getAxis() == Direction.Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
   }

   protected void onStartFalling(FallingBlockEntity fallingEntity) {
      fallingEntity.setHurtEntities(true);
   }

   public void onEndFalling(World worldIn, BlockPos pos, BlockState fallingState, BlockState hitState) {
      worldIn.playEvent(1031, pos, 0);
   }

   public void onBroken(World worldIn, BlockPos pos) {
      worldIn.playEvent(1029, pos, 0);
   }

   @Nullable
   public static BlockState damage(BlockState state) {
      Block block = state.getBlock();
      if (block == Blocks.ANVIL) {
         return Blocks.CHIPPED_ANVIL.getDefaultState().with(FACING, state.get(FACING));
      } else {
         return block == Blocks.CHIPPED_ANVIL ? Blocks.DAMAGED_ANVIL.getDefaultState().with(FACING, state.get(FACING)) : null;
      }
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

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(FACING);
   }

   public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
}