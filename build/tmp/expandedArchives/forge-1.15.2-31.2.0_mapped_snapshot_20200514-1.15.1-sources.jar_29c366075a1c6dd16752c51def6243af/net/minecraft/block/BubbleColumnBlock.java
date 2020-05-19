package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BubbleColumnBlock extends Block implements IBucketPickupHandler {
   public static final BooleanProperty DRAG = BlockStateProperties.DRAG;

   public BubbleColumnBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(DRAG, Boolean.valueOf(true)));
   }

   public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
      BlockState blockstate = worldIn.getBlockState(pos.up());
      if (blockstate.isAir()) {
         entityIn.onEnterBubbleColumnWithAirAbove(state.get(DRAG));
         if (!worldIn.isRemote) {
            ServerWorld serverworld = (ServerWorld)worldIn;

            for(int i = 0; i < 2; ++i) {
               serverworld.spawnParticle(ParticleTypes.SPLASH, (double)((float)pos.getX() + worldIn.rand.nextFloat()), (double)(pos.getY() + 1), (double)((float)pos.getZ() + worldIn.rand.nextFloat()), 1, 0.0D, 0.0D, 0.0D, 1.0D);
               serverworld.spawnParticle(ParticleTypes.BUBBLE, (double)((float)pos.getX() + worldIn.rand.nextFloat()), (double)(pos.getY() + 1), (double)((float)pos.getZ() + worldIn.rand.nextFloat()), 1, 0.0D, 0.01D, 0.0D, 0.2D);
            }
         }
      } else {
         entityIn.onEnterBubbleColumn(state.get(DRAG));
      }

   }

   public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
      placeBubbleColumn(worldIn, pos.up(), getDrag(worldIn, pos.down()));
   }

   public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
      placeBubbleColumn(worldIn, pos.up(), getDrag(worldIn, pos));
   }

   public IFluidState getFluidState(BlockState state) {
      return Fluids.WATER.getStillFluidState(false);
   }

   public static void placeBubbleColumn(IWorld p_203159_0_, BlockPos p_203159_1_, boolean drag) {
      if (canHoldBubbleColumn(p_203159_0_, p_203159_1_)) {
         p_203159_0_.setBlockState(p_203159_1_, Blocks.BUBBLE_COLUMN.getDefaultState().with(DRAG, Boolean.valueOf(drag)), 2);
      }

   }

   public static boolean canHoldBubbleColumn(IWorld p_208072_0_, BlockPos p_208072_1_) {
      IFluidState ifluidstate = p_208072_0_.getFluidState(p_208072_1_);
      return p_208072_0_.getBlockState(p_208072_1_).getBlock() == Blocks.WATER && ifluidstate.getLevel() >= 8 && ifluidstate.isSource();
   }

   private static boolean getDrag(IBlockReader p_203157_0_, BlockPos p_203157_1_) {
      BlockState blockstate = p_203157_0_.getBlockState(p_203157_1_);
      Block block = blockstate.getBlock();
      if (block == Blocks.BUBBLE_COLUMN) {
         return blockstate.get(DRAG);
      } else {
         return block != Blocks.SOUL_SAND;
      }
   }

   /**
    * How many world ticks before ticking
    */
   public int tickRate(IWorldReader worldIn) {
      return 5;
   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      double d0 = (double)pos.getX();
      double d1 = (double)pos.getY();
      double d2 = (double)pos.getZ();
      if (stateIn.get(DRAG)) {
         worldIn.addOptionalParticle(ParticleTypes.CURRENT_DOWN, d0 + 0.5D, d1 + 0.8D, d2, 0.0D, 0.0D, 0.0D);
         if (rand.nextInt(200) == 0) {
            worldIn.playSound(d0, d1, d2, SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundCategory.BLOCKS, 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
         }
      } else {
         worldIn.addOptionalParticle(ParticleTypes.BUBBLE_COLUMN_UP, d0 + 0.5D, d1, d2 + 0.5D, 0.0D, 0.04D, 0.0D);
         worldIn.addOptionalParticle(ParticleTypes.BUBBLE_COLUMN_UP, d0 + (double)rand.nextFloat(), d1 + (double)rand.nextFloat(), d2 + (double)rand.nextFloat(), 0.0D, 0.04D, 0.0D);
         if (rand.nextInt(200) == 0) {
            worldIn.playSound(d0, d1, d2, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
         }
      }

   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (!stateIn.isValidPosition(worldIn, currentPos)) {
         return Blocks.WATER.getDefaultState();
      } else {
         if (facing == Direction.DOWN) {
            worldIn.setBlockState(currentPos, Blocks.BUBBLE_COLUMN.getDefaultState().with(DRAG, Boolean.valueOf(getDrag(worldIn, facingPos))), 2);
         } else if (facing == Direction.UP && facingState.getBlock() != Blocks.BUBBLE_COLUMN && canHoldBubbleColumn(worldIn, facingPos)) {
            worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, this.tickRate(worldIn));
         }

         worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
         return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      }
   }

   public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
      Block block = worldIn.getBlockState(pos.down()).getBlock();
      return block == Blocks.BUBBLE_COLUMN || block == Blocks.MAGMA_BLOCK || block == Blocks.SOUL_SAND;
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return VoxelShapes.empty();
   }

   /**
    * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
    * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
    * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
    */
   public BlockRenderType getRenderType(BlockState state) {
      return BlockRenderType.INVISIBLE;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(DRAG);
   }

   public Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state) {
      worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
      return Fluids.WATER;
   }
}