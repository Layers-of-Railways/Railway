package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.IWorldGenerationReader;

public abstract class HugeTreesFeature<T extends BaseTreeFeatureConfig> extends AbstractTreeFeature<T> {
   public HugeTreesFeature(Function<Dynamic<?>, ? extends T> p_i225810_1_) {
      super(p_i225810_1_);
   }

   protected int func_227256_a_(Random p_227256_1_, HugeTreeFeatureConfig p_227256_2_) {
      int i = p_227256_1_.nextInt(3) + p_227256_2_.baseHeight;
      if (p_227256_2_.heightInterval > 1) {
         i += p_227256_1_.nextInt(p_227256_2_.heightInterval);
      }

      return i;
   }

   /**
    * returns whether or not there is space for a tree to grow at a certain position
    */
   private boolean isSpaceAt(IWorldGenerationBaseReader worldIn, BlockPos leavesPos, int height) {
      boolean flag = true;
      if (leavesPos.getY() >= 1 && leavesPos.getY() + height + 1 <= worldIn.getMaxHeight()) {
         for(int i = 0; i <= 1 + height; ++i) {
            int j = 2;
            if (i == 0) {
               j = 1;
            } else if (i >= 1 + height - 2) {
               j = 2;
            }

            for(int k = -j; k <= j && flag; ++k) {
               for(int l = -j; l <= j && flag; ++l) {
                  if (leavesPos.getY() + i < 0 || leavesPos.getY() + i >= worldIn.getMaxHeight() || !canBeReplacedByLogs(worldIn, leavesPos.add(k, i, l))) {
                     flag = false;
                  }
               }
            }
         }

         return flag;
      } else {
         return false;
      }
   }

   private boolean validSoil(IWorldGenerationReader worldIn, BlockPos pos, net.minecraftforge.common.IPlantable sapling) {
      BlockPos blockpos = pos.down();
      if (isSoil(worldIn, blockpos, sapling) && pos.getY() >= 2) {
         setDirtAt(worldIn, blockpos, pos);
         setDirtAt(worldIn, blockpos.east(), pos);
         setDirtAt(worldIn, blockpos.south(), pos);
         setDirtAt(worldIn, blockpos.south().east(), pos);
         return true;
      } else {
         return false;
      }
   }

   @Deprecated //Forge: Use config sensitive version
   protected boolean func_203427_a(IWorldGenerationReader worldIn, BlockPos p_203427_2_, int p_203427_3_) {
      return this.isSpaceAt(worldIn, p_203427_2_, p_203427_3_) && this.validSoil(worldIn, p_203427_2_, (net.minecraftforge.common.IPlantable)net.minecraft.block.Blocks.OAK_SAPLING);
   }

   protected boolean hasRoom(IWorldGenerationReader world, BlockPos pos, int height, BaseTreeFeatureConfig config) {
      return this.isSpaceAt(world, pos, height) && this.validSoil(world, pos, config.getSapling());
   }

   protected void func_227255_a_(IWorldGenerationReader p_227255_1_, Random p_227255_2_, BlockPos p_227255_3_, int p_227255_4_, Set<BlockPos> p_227255_5_, MutableBoundingBox p_227255_6_, BaseTreeFeatureConfig p_227255_7_) {
      int i = p_227255_4_ * p_227255_4_;

      for(int j = -p_227255_4_; j <= p_227255_4_ + 1; ++j) {
         for(int k = -p_227255_4_; k <= p_227255_4_ + 1; ++k) {
            int l = Math.min(Math.abs(j), Math.abs(j - 1));
            int i1 = Math.min(Math.abs(k), Math.abs(k - 1));
            if (l + i1 < 7 && l * l + i1 * i1 <= i) {
               this.func_227219_b_(p_227255_1_, p_227255_2_, p_227255_3_.add(j, 0, k), p_227255_5_, p_227255_6_, p_227255_7_);
            }
         }
      }

   }

   protected void func_227257_b_(IWorldGenerationReader p_227257_1_, Random p_227257_2_, BlockPos p_227257_3_, int p_227257_4_, Set<BlockPos> p_227257_5_, MutableBoundingBox p_227257_6_, BaseTreeFeatureConfig p_227257_7_) {
      int i = p_227257_4_ * p_227257_4_;

      for(int j = -p_227257_4_; j <= p_227257_4_; ++j) {
         for(int k = -p_227257_4_; k <= p_227257_4_; ++k) {
            if (j * j + k * k <= i) {
               this.func_227219_b_(p_227257_1_, p_227257_2_, p_227257_3_.add(j, 0, k), p_227257_5_, p_227257_6_, p_227257_7_);
            }
         }
      }

   }

   protected void func_227254_a_(IWorldGenerationReader p_227254_1_, Random p_227254_2_, BlockPos p_227254_3_, int p_227254_4_, Set<BlockPos> p_227254_5_, MutableBoundingBox p_227254_6_, HugeTreeFeatureConfig p_227254_7_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int i = 0; i < p_227254_4_; ++i) {
         blockpos$mutable.setPos(p_227254_3_).move(0, i, 0);
         if (canBeReplacedByLogs(p_227254_1_, blockpos$mutable)) {
            this.func_227216_a_(p_227254_1_, p_227254_2_, blockpos$mutable, p_227254_5_, p_227254_6_, p_227254_7_);
         }

         if (i < p_227254_4_ - 1) {
            blockpos$mutable.setPos(p_227254_3_).move(1, i, 0);
            if (canBeReplacedByLogs(p_227254_1_, blockpos$mutable)) {
               this.func_227216_a_(p_227254_1_, p_227254_2_, blockpos$mutable, p_227254_5_, p_227254_6_, p_227254_7_);
            }

            blockpos$mutable.setPos(p_227254_3_).move(1, i, 1);
            if (canBeReplacedByLogs(p_227254_1_, blockpos$mutable)) {
               this.func_227216_a_(p_227254_1_, p_227254_2_, blockpos$mutable, p_227254_5_, p_227254_6_, p_227254_7_);
            }

            blockpos$mutable.setPos(p_227254_3_).move(0, i, 1);
            if (canBeReplacedByLogs(p_227254_1_, blockpos$mutable)) {
               this.func_227216_a_(p_227254_1_, p_227254_2_, blockpos$mutable, p_227254_5_, p_227254_6_, p_227254_7_);
            }
         }
      }

   }
}