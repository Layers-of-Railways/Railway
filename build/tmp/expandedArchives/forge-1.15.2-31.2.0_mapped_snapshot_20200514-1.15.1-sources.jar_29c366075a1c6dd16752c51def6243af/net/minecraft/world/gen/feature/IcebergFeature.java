package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class IcebergFeature extends Feature<BlockStateFeatureConfig> {
   public IcebergFeature(Function<Dynamic<?>, ? extends BlockStateFeatureConfig> configFactoryIn) {
      super(configFactoryIn);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, BlockStateFeatureConfig config) {
      pos = new BlockPos(pos.getX(), worldIn.getSeaLevel(), pos.getZ());
      boolean flag = rand.nextDouble() > 0.7D;
      BlockState blockstate = config.state;
      double d0 = rand.nextDouble() * 2.0D * Math.PI;
      int i = 11 - rand.nextInt(5);
      int j = 3 + rand.nextInt(3);
      boolean flag1 = rand.nextDouble() > 0.7D;
      int k = 11;
      int l = flag1 ? rand.nextInt(6) + 6 : rand.nextInt(15) + 3;
      if (!flag1 && rand.nextDouble() > 0.9D) {
         l += rand.nextInt(19) + 7;
      }

      int i1 = Math.min(l + rand.nextInt(11), 18);
      int j1 = Math.min(l + rand.nextInt(7) - rand.nextInt(5), 11);
      int k1 = flag1 ? i : 11;

      for(int l1 = -k1; l1 < k1; ++l1) {
         for(int i2 = -k1; i2 < k1; ++i2) {
            for(int j2 = 0; j2 < l; ++j2) {
               int k2 = flag1 ? this.func_205178_b(j2, l, j1) : this.func_205183_a(rand, j2, l, j1);
               if (flag1 || l1 < k2) {
                  this.func_205181_a(worldIn, rand, pos, l, l1, j2, i2, k2, k1, flag1, j, d0, flag, blockstate);
               }
            }
         }
      }

      this.func_205186_a(worldIn, pos, j1, l, flag1, i);

      for(int i3 = -k1; i3 < k1; ++i3) {
         for(int j3 = -k1; j3 < k1; ++j3) {
            for(int k3 = -1; k3 > -i1; --k3) {
               int l3 = flag1 ? MathHelper.ceil((float)k1 * (1.0F - (float)Math.pow((double)k3, 2.0D) / ((float)i1 * 8.0F))) : k1;
               int l2 = this.func_205187_b(rand, -k3, i1, j1);
               if (i3 < l2) {
                  this.func_205181_a(worldIn, rand, pos, i1, i3, k3, j3, l2, l3, flag1, j, d0, flag, blockstate);
               }
            }
         }
      }

      boolean flag2 = flag1 ? rand.nextDouble() > 0.1D : rand.nextDouble() > 0.7D;
      if (flag2) {
         this.func_205184_a(rand, worldIn, j1, l, pos, flag1, i, d0, j);
      }

      return true;
   }

   private void func_205184_a(Random rand, IWorld worldIn, int p_205184_3_, int p_205184_4_, BlockPos pos, boolean p_205184_6_, int p_205184_7_, double p_205184_8_, int p_205184_10_) {
      int i = rand.nextBoolean() ? -1 : 1;
      int j = rand.nextBoolean() ? -1 : 1;
      int k = rand.nextInt(Math.max(p_205184_3_ / 2 - 2, 1));
      if (rand.nextBoolean()) {
         k = p_205184_3_ / 2 + 1 - rand.nextInt(Math.max(p_205184_3_ - p_205184_3_ / 2 - 1, 1));
      }

      int l = rand.nextInt(Math.max(p_205184_3_ / 2 - 2, 1));
      if (rand.nextBoolean()) {
         l = p_205184_3_ / 2 + 1 - rand.nextInt(Math.max(p_205184_3_ - p_205184_3_ / 2 - 1, 1));
      }

      if (p_205184_6_) {
         k = l = rand.nextInt(Math.max(p_205184_7_ - 5, 1));
      }

      BlockPos blockpos = new BlockPos(i * k, 0, j * l);
      double d0 = p_205184_6_ ? p_205184_8_ + (Math.PI / 2D) : rand.nextDouble() * 2.0D * Math.PI;

      for(int i1 = 0; i1 < p_205184_4_ - 3; ++i1) {
         int j1 = this.func_205183_a(rand, i1, p_205184_4_, p_205184_3_);
         this.func_205174_a(j1, i1, pos, worldIn, false, d0, blockpos, p_205184_7_, p_205184_10_);
      }

      for(int k1 = -1; k1 > -p_205184_4_ + rand.nextInt(5); --k1) {
         int l1 = this.func_205187_b(rand, -k1, p_205184_4_, p_205184_3_);
         this.func_205174_a(l1, k1, pos, worldIn, true, d0, blockpos, p_205184_7_, p_205184_10_);
      }

   }

   private void func_205174_a(int p_205174_1_, int yDiff, BlockPos p_205174_3_, IWorld worldIn, boolean placeWater, double p_205174_6_, BlockPos p_205174_8_, int p_205174_9_, int p_205174_10_) {
      int i = p_205174_1_ + 1 + p_205174_9_ / 3;
      int j = Math.min(p_205174_1_ - 3, 3) + p_205174_10_ / 2 - 1;

      for(int k = -i; k < i; ++k) {
         for(int l = -i; l < i; ++l) {
            double d0 = this.func_205180_a(k, l, p_205174_8_, i, j, p_205174_6_);
            if (d0 < 0.0D) {
               BlockPos blockpos = p_205174_3_.add(k, yDiff, l);
               Block block = worldIn.getBlockState(blockpos).getBlock();
               if (this.isIce(block) || block == Blocks.SNOW_BLOCK) {
                  if (placeWater) {
                     this.setBlockState(worldIn, blockpos, Blocks.WATER.getDefaultState());
                  } else {
                     this.setBlockState(worldIn, blockpos, Blocks.AIR.getDefaultState());
                     this.removeSnowLayer(worldIn, blockpos);
                  }
               }
            }
         }
      }

   }

   private void removeSnowLayer(IWorld worldIn, BlockPos posIn) {
      if (worldIn.getBlockState(posIn.up()).getBlock() == Blocks.SNOW) {
         this.setBlockState(worldIn, posIn.up(), Blocks.AIR.getDefaultState());
      }

   }

   private void func_205181_a(IWorld worldIn, Random rand, BlockPos pos, int p_205181_4_, int xIn, int yIn, int zIn, int p_205181_8_, int p_205181_9_, boolean p_205181_10_, int p_205181_11_, double p_205181_12_, boolean p_205181_14_, BlockState p_205181_15_) {
      double d0 = p_205181_10_ ? this.func_205180_a(xIn, zIn, BlockPos.ZERO, p_205181_9_, this.func_205176_a(yIn, p_205181_4_, p_205181_11_), p_205181_12_) : this.func_205177_a(xIn, zIn, BlockPos.ZERO, p_205181_8_, rand);
      if (d0 < 0.0D) {
         BlockPos blockpos = pos.add(xIn, yIn, zIn);
         double d1 = p_205181_10_ ? -0.5D : (double)(-6 - rand.nextInt(3));
         if (d0 > d1 && rand.nextDouble() > 0.9D) {
            return;
         }

         this.func_205175_a(blockpos, worldIn, rand, p_205181_4_ - yIn, p_205181_4_, p_205181_10_, p_205181_14_, p_205181_15_);
      }

   }

   private void func_205175_a(BlockPos pos, IWorld worldIn, Random p_205175_3_, int p_205175_4_, int p_205175_5_, boolean p_205175_6_, boolean p_205175_7_, BlockState p_205175_8_) {
      BlockState blockstate = worldIn.getBlockState(pos);
      Block block = blockstate.getBlock();
      if (blockstate.getMaterial() == Material.AIR || block == Blocks.SNOW_BLOCK || block == Blocks.ICE || block == Blocks.WATER) {
         boolean flag = !p_205175_6_ || p_205175_3_.nextDouble() > 0.05D;
         int i = p_205175_6_ ? 3 : 2;
         if (p_205175_7_ && block != Blocks.WATER && (double)p_205175_4_ <= (double)p_205175_3_.nextInt(Math.max(1, p_205175_5_ / i)) + (double)p_205175_5_ * 0.6D && flag) {
            this.setBlockState(worldIn, pos, Blocks.SNOW_BLOCK.getDefaultState());
         } else {
            this.setBlockState(worldIn, pos, p_205175_8_);
         }
      }

   }

   private int func_205176_a(int p_205176_1_, int p_205176_2_, int p_205176_3_) {
      int i = p_205176_3_;
      if (p_205176_1_ > 0 && p_205176_2_ - p_205176_1_ <= 3) {
         i = p_205176_3_ - (4 - (p_205176_2_ - p_205176_1_));
      }

      return i;
   }

   private double func_205177_a(int p_205177_1_, int p_205177_2_, BlockPos pos, int p_205177_4_, Random rand) {
      float f = 10.0F * MathHelper.clamp(rand.nextFloat(), 0.2F, 0.8F) / (float)p_205177_4_;
      return (double)f + Math.pow((double)(p_205177_1_ - pos.getX()), 2.0D) + Math.pow((double)(p_205177_2_ - pos.getZ()), 2.0D) - Math.pow((double)p_205177_4_, 2.0D);
   }

   private double func_205180_a(int xIn, int zIn, BlockPos pos, int p_205180_4_, int p_205180_5_, double p_205180_6_) {
      return Math.pow(((double)(xIn - pos.getX()) * Math.cos(p_205180_6_) - (double)(zIn - pos.getZ()) * Math.sin(p_205180_6_)) / (double)p_205180_4_, 2.0D) + Math.pow(((double)(xIn - pos.getX()) * Math.sin(p_205180_6_) + (double)(zIn - pos.getZ()) * Math.cos(p_205180_6_)) / (double)p_205180_5_, 2.0D) - 1.0D;
   }

   private int func_205183_a(Random rand, int p_205183_2_, int p_205183_3_, int p_205183_4_) {
      float f = 3.5F - rand.nextFloat();
      float f1 = (1.0F - (float)Math.pow((double)p_205183_2_, 2.0D) / ((float)p_205183_3_ * f)) * (float)p_205183_4_;
      if (p_205183_3_ > 15 + rand.nextInt(5)) {
         int i = p_205183_2_ < 3 + rand.nextInt(6) ? p_205183_2_ / 2 : p_205183_2_;
         f1 = (1.0F - (float)i / ((float)p_205183_3_ * f * 0.4F)) * (float)p_205183_4_;
      }

      return MathHelper.ceil(f1 / 2.0F);
   }

   private int func_205178_b(int p_205178_1_, int p_205178_2_, int p_205178_3_) {
      float f = 1.0F;
      float f1 = (1.0F - (float)Math.pow((double)p_205178_1_, 2.0D) / ((float)p_205178_2_ * 1.0F)) * (float)p_205178_3_;
      return MathHelper.ceil(f1 / 2.0F);
   }

   private int func_205187_b(Random rand, int p_205187_2_, int p_205187_3_, int p_205187_4_) {
      float f = 1.0F + rand.nextFloat() / 2.0F;
      float f1 = (1.0F - (float)p_205187_2_ / ((float)p_205187_3_ * f)) * (float)p_205187_4_;
      return MathHelper.ceil(f1 / 2.0F);
   }

   private boolean isIce(Block blockIn) {
      return blockIn == Blocks.PACKED_ICE || blockIn == Blocks.SNOW_BLOCK || blockIn == Blocks.BLUE_ICE;
   }

   private boolean isAirBellow(IBlockReader worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.down()).getMaterial() == Material.AIR;
   }

   private void func_205186_a(IWorld worldIn, BlockPos pos, int p_205186_3_, int p_205186_4_, boolean p_205186_5_, int p_205186_6_) {
      int i = p_205186_5_ ? p_205186_6_ : p_205186_3_ / 2;

      for(int j = -i; j <= i; ++j) {
         for(int k = -i; k <= i; ++k) {
            for(int l = 0; l <= p_205186_4_; ++l) {
               BlockPos blockpos = pos.add(j, l, k);
               Block block = worldIn.getBlockState(blockpos).getBlock();
               if (this.isIce(block) || block == Blocks.SNOW) {
                  if (this.isAirBellow(worldIn, blockpos)) {
                     this.setBlockState(worldIn, blockpos, Blocks.AIR.getDefaultState());
                     this.setBlockState(worldIn, blockpos.up(), Blocks.AIR.getDefaultState());
                  } else if (this.isIce(block)) {
                     Block[] ablock = new Block[]{worldIn.getBlockState(blockpos.west()).getBlock(), worldIn.getBlockState(blockpos.east()).getBlock(), worldIn.getBlockState(blockpos.north()).getBlock(), worldIn.getBlockState(blockpos.south()).getBlock()};
                     int i1 = 0;

                     for(Block block1 : ablock) {
                        if (!this.isIce(block1)) {
                           ++i1;
                        }
                     }

                     if (i1 >= 3) {
                        this.setBlockState(worldIn, blockpos, Blocks.AIR.getDefaultState());
                     }
                  }
               }
            }
         }
      }

   }
}