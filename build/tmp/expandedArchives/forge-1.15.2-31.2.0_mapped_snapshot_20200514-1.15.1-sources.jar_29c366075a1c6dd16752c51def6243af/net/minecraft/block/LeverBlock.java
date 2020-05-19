package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LeverBlock extends HorizontalFaceBlock {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   protected static final VoxelShape LEVER_NORTH_AABB = Block.makeCuboidShape(5.0D, 4.0D, 10.0D, 11.0D, 12.0D, 16.0D);
   protected static final VoxelShape LEVER_SOUTH_AABB = Block.makeCuboidShape(5.0D, 4.0D, 0.0D, 11.0D, 12.0D, 6.0D);
   protected static final VoxelShape LEVER_WEST_AABB = Block.makeCuboidShape(10.0D, 4.0D, 5.0D, 16.0D, 12.0D, 11.0D);
   protected static final VoxelShape LEVER_EAST_AABB = Block.makeCuboidShape(0.0D, 4.0D, 5.0D, 6.0D, 12.0D, 11.0D);
   protected static final VoxelShape FLOOR_Z_SHAPE = Block.makeCuboidShape(5.0D, 0.0D, 4.0D, 11.0D, 6.0D, 12.0D);
   protected static final VoxelShape FLOOR_X_SHAPE = Block.makeCuboidShape(4.0D, 0.0D, 5.0D, 12.0D, 6.0D, 11.0D);
   protected static final VoxelShape CEILING_Z_SHAPE = Block.makeCuboidShape(5.0D, 10.0D, 4.0D, 11.0D, 16.0D, 12.0D);
   protected static final VoxelShape CEILING_X_SHAPE = Block.makeCuboidShape(4.0D, 10.0D, 5.0D, 12.0D, 16.0D, 11.0D);

   protected LeverBlock(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH).with(POWERED, Boolean.valueOf(false)).with(FACE, AttachFace.WALL));
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      switch((AttachFace)state.get(FACE)) {
      case FLOOR:
         switch(state.get(HORIZONTAL_FACING).getAxis()) {
         case X:
            return FLOOR_X_SHAPE;
         case Z:
         default:
            return FLOOR_Z_SHAPE;
         }
      case WALL:
         switch((Direction)state.get(HORIZONTAL_FACING)) {
         case EAST:
            return LEVER_EAST_AABB;
         case WEST:
            return LEVER_WEST_AABB;
         case SOUTH:
            return LEVER_SOUTH_AABB;
         case NORTH:
         default:
            return LEVER_NORTH_AABB;
         }
      case CEILING:
      default:
         switch(state.get(HORIZONTAL_FACING).getAxis()) {
         case X:
            return CEILING_X_SHAPE;
         case Z:
         default:
            return CEILING_Z_SHAPE;
         }
      }
   }

   public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      if (worldIn.isRemote) {
         BlockState blockstate1 = state.cycle(POWERED);
         if (blockstate1.get(POWERED)) {
            addParticles(blockstate1, worldIn, pos, 1.0F);
         }

         return ActionResultType.SUCCESS;
      } else {
         BlockState blockstate = this.func_226939_d_(state, worldIn, pos);
         float f = blockstate.get(POWERED) ? 0.6F : 0.5F;
         worldIn.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, f);
         return ActionResultType.SUCCESS;
      }
   }

   public BlockState func_226939_d_(BlockState p_226939_1_, World p_226939_2_, BlockPos p_226939_3_) {
      p_226939_1_ = p_226939_1_.cycle(POWERED);
      p_226939_2_.setBlockState(p_226939_3_, p_226939_1_, 3);
      this.updateNeighbors(p_226939_1_, p_226939_2_, p_226939_3_);
      return p_226939_1_;
   }

   private static void addParticles(BlockState state, IWorld worldIn, BlockPos pos, float alpha) {
      Direction direction = state.get(HORIZONTAL_FACING).getOpposite();
      Direction direction1 = getFacing(state).getOpposite();
      double d0 = (double)pos.getX() + 0.5D + 0.1D * (double)direction.getXOffset() + 0.2D * (double)direction1.getXOffset();
      double d1 = (double)pos.getY() + 0.5D + 0.1D * (double)direction.getYOffset() + 0.2D * (double)direction1.getYOffset();
      double d2 = (double)pos.getZ() + 0.5D + 0.1D * (double)direction.getZOffset() + 0.2D * (double)direction1.getZOffset();
      worldIn.addParticle(new RedstoneParticleData(1.0F, 0.0F, 0.0F, alpha), d0, d1, d2, 0.0D, 0.0D, 0.0D);
   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      if (stateIn.get(POWERED) && rand.nextFloat() < 0.25F) {
         addParticles(stateIn, worldIn, pos, 0.5F);
      }

   }

   public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!isMoving && state.getBlock() != newState.getBlock()) {
         if (state.get(POWERED)) {
            this.updateNeighbors(state, worldIn, pos);
         }

         super.onReplaced(state, worldIn, pos, newState, isMoving);
      }
   }

   /**
    * @deprecated call via {@link IBlockState#getWeakPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
    * Implementing/overriding is fine.
    */
   public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
      return blockState.get(POWERED) ? 15 : 0;
   }

   /**
    * @deprecated call via {@link IBlockState#getStrongPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
    * Implementing/overriding is fine.
    */
   public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
      return blockState.get(POWERED) && getFacing(blockState) == side ? 15 : 0;
   }

   /**
    * Can this block provide power. Only wire currently seems to have this change based on its state.
    * @deprecated call via {@link IBlockState#canProvidePower()} whenever possible. Implementing/overriding is fine.
    */
   public boolean canProvidePower(BlockState state) {
      return true;
   }

   private void updateNeighbors(BlockState p_196378_1_, World p_196378_2_, BlockPos p_196378_3_) {
      p_196378_2_.notifyNeighborsOfStateChange(p_196378_3_, this);
      p_196378_2_.notifyNeighborsOfStateChange(p_196378_3_.offset(getFacing(p_196378_1_).getOpposite()), this);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(FACE, HORIZONTAL_FACING, POWERED);
   }
}