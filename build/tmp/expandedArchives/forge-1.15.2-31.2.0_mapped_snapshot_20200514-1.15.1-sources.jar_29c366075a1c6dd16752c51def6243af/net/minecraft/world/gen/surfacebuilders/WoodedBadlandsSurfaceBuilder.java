package net.minecraft.world.gen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class WoodedBadlandsSurfaceBuilder extends BadlandsSurfaceBuilder {
   private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
   private static final BlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.getDefaultState();
   private static final BlockState TERRACOTTA = Blocks.TERRACOTTA.getDefaultState();

   public WoodedBadlandsSurfaceBuilder(Function<Dynamic<?>, ? extends SurfaceBuilderConfig> p_i51303_1_) {
      super(p_i51303_1_);
   }

   public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
      int i = x & 15;
      int j = z & 15;
      BlockState blockstate = WHITE_TERRACOTTA;
      BlockState blockstate1 = biomeIn.getSurfaceBuilderConfig().getUnder();
      int k = (int)(noise / 3.0D + 3.0D + random.nextDouble() * 0.25D);
      boolean flag = Math.cos(noise / 3.0D * Math.PI) > 0.0D;
      int l = -1;
      boolean flag1 = false;
      int i1 = 0;
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int j1 = startHeight; j1 >= 0; --j1) {
         if (i1 < 15) {
            blockpos$mutable.setPos(i, j1, j);
            BlockState blockstate2 = chunkIn.getBlockState(blockpos$mutable);
            if (blockstate2.isAir()) {
               l = -1;
            } else if (blockstate2.getBlock() == defaultBlock.getBlock()) {
               if (l == -1) {
                  flag1 = false;
                  if (k <= 0) {
                     blockstate = Blocks.AIR.getDefaultState();
                     blockstate1 = defaultBlock;
                  } else if (j1 >= seaLevel - 4 && j1 <= seaLevel + 1) {
                     blockstate = WHITE_TERRACOTTA;
                     blockstate1 = biomeIn.getSurfaceBuilderConfig().getUnder();
                  }

                  if (j1 < seaLevel && (blockstate == null || blockstate.isAir())) {
                     blockstate = defaultFluid;
                  }

                  l = k + Math.max(0, j1 - seaLevel);
                  if (j1 >= seaLevel - 1) {
                     if (j1 > 86 + k * 2) {
                        if (flag) {
                           chunkIn.setBlockState(blockpos$mutable, Blocks.COARSE_DIRT.getDefaultState(), false);
                        } else {
                           chunkIn.setBlockState(blockpos$mutable, Blocks.GRASS_BLOCK.getDefaultState(), false);
                        }
                     } else if (j1 > seaLevel + 3 + k) {
                        BlockState blockstate3;
                        if (j1 >= 64 && j1 <= 127) {
                           if (flag) {
                              blockstate3 = TERRACOTTA;
                           } else {
                              blockstate3 = this.func_215431_a(x, j1, z);
                           }
                        } else {
                           blockstate3 = ORANGE_TERRACOTTA;
                        }

                        chunkIn.setBlockState(blockpos$mutable, blockstate3, false);
                     } else {
                        chunkIn.setBlockState(blockpos$mutable, biomeIn.getSurfaceBuilderConfig().getTop(), false);
                        flag1 = true;
                     }
                  } else {
                     chunkIn.setBlockState(blockpos$mutable, blockstate1, false);
                     if (blockstate1 == WHITE_TERRACOTTA) {
                        chunkIn.setBlockState(blockpos$mutable, ORANGE_TERRACOTTA, false);
                     }
                  }
               } else if (l > 0) {
                  --l;
                  if (flag1) {
                     chunkIn.setBlockState(blockpos$mutable, ORANGE_TERRACOTTA, false);
                  } else {
                     chunkIn.setBlockState(blockpos$mutable, this.func_215431_a(x, j1, z), false);
                  }
               }

               ++i1;
            }
         }
      }

   }
}