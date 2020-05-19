package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WetSpongeBlock extends Block {
   protected WetSpongeBlock(Block.Properties properties) {
      super(properties);
   }

   public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
      if (worldIn.getDimension().doesWaterVaporize()) {
         worldIn.setBlockState(pos, Blocks.SPONGE.getDefaultState(), 3);
         worldIn.playEvent(2009, pos, 0);
         worldIn.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, (1.0F + worldIn.getRandom().nextFloat() * 0.2F) * 0.7F);
      }

   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      Direction direction = Direction.random(rand);
      if (direction != Direction.UP) {
         BlockPos blockpos = pos.offset(direction);
         BlockState blockstate = worldIn.getBlockState(blockpos);
         if (!stateIn.isSolid() || !blockstate.isSolidSide(worldIn, blockpos, direction.getOpposite())) {
            double d0 = (double)pos.getX();
            double d1 = (double)pos.getY();
            double d2 = (double)pos.getZ();
            if (direction == Direction.DOWN) {
               d1 = d1 - 0.05D;
               d0 += rand.nextDouble();
               d2 += rand.nextDouble();
            } else {
               d1 = d1 + rand.nextDouble() * 0.8D;
               if (direction.getAxis() == Direction.Axis.X) {
                  d2 += rand.nextDouble();
                  if (direction == Direction.EAST) {
                     ++d0;
                  } else {
                     d0 += 0.05D;
                  }
               } else {
                  d0 += rand.nextDouble();
                  if (direction == Direction.SOUTH) {
                     ++d2;
                  } else {
                     d2 += 0.05D;
                  }
               }
            }

            worldIn.addParticle(ParticleTypes.DRIPPING_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
         }
      }
   }
}