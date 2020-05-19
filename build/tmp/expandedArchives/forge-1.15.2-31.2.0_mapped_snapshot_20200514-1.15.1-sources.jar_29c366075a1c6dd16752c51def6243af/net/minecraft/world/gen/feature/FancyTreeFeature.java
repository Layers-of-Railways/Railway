package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.LogBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;

public class FancyTreeFeature extends AbstractTreeFeature<TreeFeatureConfig> {
   public FancyTreeFeature(Function<Dynamic<?>, ? extends TreeFeatureConfig> p_i225803_1_) {
      super(p_i225803_1_);
   }

   private void func_227233_a_(IWorldGenerationReader p_227233_1_, Random p_227233_2_, BlockPos p_227233_3_, float p_227233_4_, Set<BlockPos> p_227233_5_, MutableBoundingBox p_227233_6_, TreeFeatureConfig p_227233_7_) {
      int i = (int)((double)p_227233_4_ + 0.618D);

      for(int j = -i; j <= i; ++j) {
         for(int k = -i; k <= i; ++k) {
            if (Math.pow((double)Math.abs(j) + 0.5D, 2.0D) + Math.pow((double)Math.abs(k) + 0.5D, 2.0D) <= (double)(p_227233_4_ * p_227233_4_)) {
               this.func_227219_b_(p_227233_1_, p_227233_2_, p_227233_3_.add(j, 0, k), p_227233_5_, p_227233_6_, p_227233_7_);
            }
         }
      }

   }

   private float func_227231_a_(int p_227231_1_, int p_227231_2_) {
      if ((float)p_227231_2_ < (float)p_227231_1_ * 0.3F) {
         return -1.0F;
      } else {
         float f = (float)p_227231_1_ / 2.0F;
         float f1 = f - (float)p_227231_2_;
         float f2 = MathHelper.sqrt(f * f - f1 * f1);
         if (f1 == 0.0F) {
            f2 = f;
         } else if (Math.abs(f1) >= f) {
            return 0.0F;
         }

         return f2 * 0.5F;
      }
   }

   private float func_227230_a_(int p_227230_1_) {
      if (p_227230_1_ >= 0 && p_227230_1_ < 5) {
         return p_227230_1_ != 0 && p_227230_1_ != 4 ? 3.0F : 2.0F;
      } else {
         return -1.0F;
      }
   }

   private void func_227236_a_(IWorldGenerationReader p_227236_1_, Random p_227236_2_, BlockPos p_227236_3_, Set<BlockPos> p_227236_4_, MutableBoundingBox p_227236_5_, TreeFeatureConfig p_227236_6_) {
      for(int i = 0; i < 5; ++i) {
         this.func_227233_a_(p_227236_1_, p_227236_2_, p_227236_3_.up(i), this.func_227230_a_(i), p_227236_4_, p_227236_5_, p_227236_6_);
      }

   }

   private int func_227235_a_(IWorldGenerationReader p_227235_1_, Random p_227235_2_, BlockPos p_227235_3_, BlockPos p_227235_4_, boolean p_227235_5_, Set<BlockPos> p_227235_6_, MutableBoundingBox p_227235_7_, TreeFeatureConfig p_227235_8_) {
      if (!p_227235_5_ && Objects.equals(p_227235_3_, p_227235_4_)) {
         return -1;
      } else {
         BlockPos blockpos = p_227235_4_.add(-p_227235_3_.getX(), -p_227235_3_.getY(), -p_227235_3_.getZ());
         int i = this.func_227237_a_(blockpos);
         float f = (float)blockpos.getX() / (float)i;
         float f1 = (float)blockpos.getY() / (float)i;
         float f2 = (float)blockpos.getZ() / (float)i;

         for(int j = 0; j <= i; ++j) {
            BlockPos blockpos1 = p_227235_3_.add((double)(0.5F + (float)j * f), (double)(0.5F + (float)j * f1), (double)(0.5F + (float)j * f2));
            if (p_227235_5_) {
               this.func_227217_a_(p_227235_1_, blockpos1, p_227235_8_.trunkProvider.getBlockState(p_227235_2_, blockpos1).with(LogBlock.AXIS, this.func_227238_a_(p_227235_3_, blockpos1)), p_227235_7_);
               p_227235_6_.add(blockpos1);
            } else if (!canBeReplacedByLogs(p_227235_1_, blockpos1)) {
               return j;
            }
         }

         return -1;
      }
   }

   private int func_227237_a_(BlockPos p_227237_1_) {
      int i = MathHelper.abs(p_227237_1_.getX());
      int j = MathHelper.abs(p_227237_1_.getY());
      int k = MathHelper.abs(p_227237_1_.getZ());
      if (k > i && k > j) {
         return k;
      } else {
         return j > i ? j : i;
      }
   }

   private Direction.Axis func_227238_a_(BlockPos p_227238_1_, BlockPos p_227238_2_) {
      Direction.Axis direction$axis = Direction.Axis.Y;
      int i = Math.abs(p_227238_2_.getX() - p_227238_1_.getX());
      int j = Math.abs(p_227238_2_.getZ() - p_227238_1_.getZ());
      int k = Math.max(i, j);
      if (k > 0) {
         if (i == k) {
            direction$axis = Direction.Axis.X;
         } else if (j == k) {
            direction$axis = Direction.Axis.Z;
         }
      }

      return direction$axis;
   }

   private void func_227232_a_(IWorldGenerationReader p_227232_1_, Random p_227232_2_, int p_227232_3_, BlockPos p_227232_4_, List<FancyTreeFeature.ExtendedPos> p_227232_5_, Set<BlockPos> p_227232_6_, MutableBoundingBox p_227232_7_, TreeFeatureConfig p_227232_8_) {
      for(FancyTreeFeature.ExtendedPos fancytreefeature$extendedpos : p_227232_5_) {
         if (this.func_227239_b_(p_227232_3_, fancytreefeature$extendedpos.func_227243_r_() - p_227232_4_.getY())) {
            this.func_227236_a_(p_227232_1_, p_227232_2_, fancytreefeature$extendedpos, p_227232_6_, p_227232_7_, p_227232_8_);
         }
      }

   }

   private boolean func_227239_b_(int p_227239_1_, int p_227239_2_) {
      return (double)p_227239_2_ >= (double)p_227239_1_ * 0.2D;
   }

   private void func_227234_a_(IWorldGenerationReader p_227234_1_, Random p_227234_2_, BlockPos p_227234_3_, int p_227234_4_, Set<BlockPos> p_227234_5_, MutableBoundingBox p_227234_6_, TreeFeatureConfig p_227234_7_) {
      this.func_227235_a_(p_227234_1_, p_227234_2_, p_227234_3_, p_227234_3_.up(p_227234_4_), true, p_227234_5_, p_227234_6_, p_227234_7_);
   }

   private void func_227240_b_(IWorldGenerationReader p_227240_1_, Random p_227240_2_, int p_227240_3_, BlockPos p_227240_4_, List<FancyTreeFeature.ExtendedPos> p_227240_5_, Set<BlockPos> p_227240_6_, MutableBoundingBox p_227240_7_, TreeFeatureConfig p_227240_8_) {
      for(FancyTreeFeature.ExtendedPos fancytreefeature$extendedpos : p_227240_5_) {
         int i = fancytreefeature$extendedpos.func_227243_r_();
         BlockPos blockpos = new BlockPos(p_227240_4_.getX(), i, p_227240_4_.getZ());
         if (!blockpos.equals(fancytreefeature$extendedpos) && this.func_227239_b_(p_227240_3_, i - p_227240_4_.getY())) {
            this.func_227235_a_(p_227240_1_, p_227240_2_, blockpos, fancytreefeature$extendedpos, true, p_227240_6_, p_227240_7_, p_227240_8_);
         }
      }

   }

   /**
    * Called when placing the tree feature.
    */
   public boolean place(IWorldGenerationReader generationReader, Random rand, BlockPos positionIn, Set<BlockPos> p_225557_4_, Set<BlockPos> p_225557_5_, MutableBoundingBox boundingBoxIn, TreeFeatureConfig configIn) {
      Random random = new Random(rand.nextLong());
      int i = this.func_227241_b_(generationReader, rand, positionIn, 5 + random.nextInt(12), p_225557_4_, boundingBoxIn, configIn);
      if (i == -1) {
         return false;
      } else {
         this.setDirt(generationReader, positionIn.down());
         int j = (int)((double)i * 0.618D);
         if (j >= i) {
            j = i - 1;
         }

         double d0 = 1.0D;
         int k = (int)(1.382D + Math.pow(1.0D * (double)i / 13.0D, 2.0D));
         if (k < 1) {
            k = 1;
         }

         int l = positionIn.getY() + j;
         int i1 = i - 5;
         List<FancyTreeFeature.ExtendedPos> list = Lists.newArrayList();
         list.add(new FancyTreeFeature.ExtendedPos(positionIn.up(i1), l));

         for(; i1 >= 0; --i1) {
            float f = this.func_227231_a_(i, i1);
            if (!(f < 0.0F)) {
               for(int j1 = 0; j1 < k; ++j1) {
                  double d1 = 1.0D;
                  double d2 = 1.0D * (double)f * ((double)random.nextFloat() + 0.328D);
                  double d3 = (double)(random.nextFloat() * 2.0F) * Math.PI;
                  double d4 = d2 * Math.sin(d3) + 0.5D;
                  double d5 = d2 * Math.cos(d3) + 0.5D;
                  BlockPos blockpos = positionIn.add(d4, (double)(i1 - 1), d5);
                  BlockPos blockpos1 = blockpos.up(5);
                  if (this.func_227235_a_(generationReader, rand, blockpos, blockpos1, false, p_225557_4_, boundingBoxIn, configIn) == -1) {
                     int k1 = positionIn.getX() - blockpos.getX();
                     int l1 = positionIn.getZ() - blockpos.getZ();
                     double d6 = (double)blockpos.getY() - Math.sqrt((double)(k1 * k1 + l1 * l1)) * 0.381D;
                     int i2 = d6 > (double)l ? l : (int)d6;
                     BlockPos blockpos2 = new BlockPos(positionIn.getX(), i2, positionIn.getZ());
                     if (this.func_227235_a_(generationReader, rand, blockpos2, blockpos, false, p_225557_4_, boundingBoxIn, configIn) == -1) {
                        list.add(new FancyTreeFeature.ExtendedPos(blockpos, blockpos2.getY()));
                     }
                  }
               }
            }
         }

         this.func_227232_a_(generationReader, rand, i, positionIn, list, p_225557_5_, boundingBoxIn, configIn);
         this.func_227234_a_(generationReader, rand, positionIn, j, p_225557_4_, boundingBoxIn, configIn);
         this.func_227240_b_(generationReader, rand, i, positionIn, list, p_225557_4_, boundingBoxIn, configIn);
         return true;
      }
   }

   private int func_227241_b_(IWorldGenerationReader p_227241_1_, Random p_227241_2_, BlockPos p_227241_3_, int p_227241_4_, Set<BlockPos> p_227241_5_, MutableBoundingBox p_227241_6_, TreeFeatureConfig p_227241_7_) {
      if (!isDirtOrGrassBlockOrFarmland(p_227241_1_, p_227241_3_.down())) {
         return -1;
      } else {
         int i = this.func_227235_a_(p_227241_1_, p_227241_2_, p_227241_3_, p_227241_3_.up(p_227241_4_ - 1), false, p_227241_5_, p_227241_6_, p_227241_7_);
         if (i == -1) {
            return p_227241_4_;
         } else {
            return i < 6 ? -1 : i;
         }
      }
   }

   static class ExtendedPos extends BlockPos {
      private final int field_227242_b_;

      public ExtendedPos(BlockPos p_i225804_1_, int p_i225804_2_) {
         super(p_i225804_1_.getX(), p_i225804_1_.getY(), p_i225804_1_.getZ());
         this.field_227242_b_ = p_i225804_2_;
      }

      public int func_227243_r_() {
         return this.field_227242_b_;
      }
   }
}