package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class DragonEggBlock extends FallingBlock {
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

   public DragonEggBlock(Block.Properties properties) {
      super(properties);
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return SHAPE;
   }

   public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      this.teleport(state, worldIn, pos);
      return ActionResultType.SUCCESS;
   }

   public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
      this.teleport(state, worldIn, pos);
   }

   private void teleport(BlockState p_196443_1_, World p_196443_2_, BlockPos p_196443_3_) {
      for(int i = 0; i < 1000; ++i) {
         BlockPos blockpos = p_196443_3_.add(p_196443_2_.rand.nextInt(16) - p_196443_2_.rand.nextInt(16), p_196443_2_.rand.nextInt(8) - p_196443_2_.rand.nextInt(8), p_196443_2_.rand.nextInt(16) - p_196443_2_.rand.nextInt(16));
         if (p_196443_2_.getBlockState(blockpos).isAir()) {
            if (p_196443_2_.isRemote) {
               for(int j = 0; j < 128; ++j) {
                  double d0 = p_196443_2_.rand.nextDouble();
                  float f = (p_196443_2_.rand.nextFloat() - 0.5F) * 0.2F;
                  float f1 = (p_196443_2_.rand.nextFloat() - 0.5F) * 0.2F;
                  float f2 = (p_196443_2_.rand.nextFloat() - 0.5F) * 0.2F;
                  double d1 = MathHelper.lerp(d0, (double)blockpos.getX(), (double)p_196443_3_.getX()) + (p_196443_2_.rand.nextDouble() - 0.5D) + 0.5D;
                  double d2 = MathHelper.lerp(d0, (double)blockpos.getY(), (double)p_196443_3_.getY()) + p_196443_2_.rand.nextDouble() - 0.5D;
                  double d3 = MathHelper.lerp(d0, (double)blockpos.getZ(), (double)p_196443_3_.getZ()) + (p_196443_2_.rand.nextDouble() - 0.5D) + 0.5D;
                  p_196443_2_.addParticle(ParticleTypes.PORTAL, d1, d2, d3, (double)f, (double)f1, (double)f2);
               }
            } else {
               p_196443_2_.setBlockState(blockpos, p_196443_1_, 2);
               p_196443_2_.removeBlock(p_196443_3_, false);
            }

            return;
         }
      }

   }

   /**
    * How many world ticks before ticking
    */
   public int tickRate(IWorldReader worldIn) {
      return 5;
   }

   public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
}