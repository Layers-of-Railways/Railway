package net.minecraft.world.gen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.PerlinNoiseGenerator;

public class FrozenOceanSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {
   protected static final BlockState PACKED_ICE = Blocks.PACKED_ICE.getDefaultState();
   protected static final BlockState SNOW_BLOCK = Blocks.SNOW_BLOCK.getDefaultState();
   private static final BlockState AIR = Blocks.AIR.getDefaultState();
   private static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
   private static final BlockState ICE = Blocks.ICE.getDefaultState();
   private PerlinNoiseGenerator field_205199_h;
   private PerlinNoiseGenerator field_205200_i;
   private long seed;

   public FrozenOceanSurfaceBuilder(Function<Dynamic<?>, ? extends SurfaceBuilderConfig> p_i51313_1_) {
      super(p_i51313_1_);
   }

   public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
      double d0 = 0.0D;
      double d1 = 0.0D;
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
      float f = biomeIn.getTemperature(blockpos$mutable.setPos(x, 63, z));
      double d2 = Math.min(Math.abs(noise), this.field_205199_h.noiseAt((double)x * 0.1D, (double)z * 0.1D, false) * 15.0D);
      if (d2 > 1.8D) {
         double d3 = 0.09765625D;
         double d4 = Math.abs(this.field_205200_i.noiseAt((double)x * 0.09765625D, (double)z * 0.09765625D, false));
         d0 = d2 * d2 * 1.2D;
         double d5 = Math.ceil(d4 * 40.0D) + 14.0D;
         if (d0 > d5) {
            d0 = d5;
         }

         if (f > 0.1F) {
            d0 -= 2.0D;
         }

         if (d0 > 2.0D) {
            d1 = (double)seaLevel - d0 - 7.0D;
            d0 = d0 + (double)seaLevel;
         } else {
            d0 = 0.0D;
         }
      }

      int k1 = x & 15;
      int i = z & 15;
      BlockState blockstate2 = biomeIn.getSurfaceBuilderConfig().getUnder();
      BlockState blockstate = biomeIn.getSurfaceBuilderConfig().getTop();
      int l1 = (int)(noise / 3.0D + 3.0D + random.nextDouble() * 0.25D);
      int j = -1;
      int k = 0;
      int l = 2 + random.nextInt(4);
      int i1 = seaLevel + 18 + random.nextInt(10);

      for(int j1 = Math.max(startHeight, (int)d0 + 1); j1 >= 0; --j1) {
         blockpos$mutable.setPos(k1, j1, i);
         if (chunkIn.getBlockState(blockpos$mutable).isAir() && j1 < (int)d0 && random.nextDouble() > 0.01D) {
            chunkIn.setBlockState(blockpos$mutable, PACKED_ICE, false);
         } else if (chunkIn.getBlockState(blockpos$mutable).getMaterial() == Material.WATER && j1 > (int)d1 && j1 < seaLevel && d1 != 0.0D && random.nextDouble() > 0.15D) {
            chunkIn.setBlockState(blockpos$mutable, PACKED_ICE, false);
         }

         BlockState blockstate1 = chunkIn.getBlockState(blockpos$mutable);
         if (blockstate1.isAir()) {
            j = -1;
         } else if (blockstate1.getBlock() != defaultBlock.getBlock()) {
            if (blockstate1.getBlock() == Blocks.PACKED_ICE && k <= l && j1 > i1) {
               chunkIn.setBlockState(blockpos$mutable, SNOW_BLOCK, false);
               ++k;
            }
         } else if (j == -1) {
            if (l1 <= 0) {
               blockstate = AIR;
               blockstate2 = defaultBlock;
            } else if (j1 >= seaLevel - 4 && j1 <= seaLevel + 1) {
               blockstate = biomeIn.getSurfaceBuilderConfig().getTop();
               blockstate2 = biomeIn.getSurfaceBuilderConfig().getUnder();
            }

            if (j1 < seaLevel && (blockstate == null || blockstate.isAir())) {
               if (biomeIn.getTemperature(blockpos$mutable.setPos(x, j1, z)) < 0.15F) {
                  blockstate = ICE;
               } else {
                  blockstate = defaultFluid;
               }
            }

            j = l1;
            if (j1 >= seaLevel - 1) {
               chunkIn.setBlockState(blockpos$mutable, blockstate, false);
            } else if (j1 < seaLevel - 7 - l1) {
               blockstate = AIR;
               blockstate2 = defaultBlock;
               chunkIn.setBlockState(blockpos$mutable, GRAVEL, false);
            } else {
               chunkIn.setBlockState(blockpos$mutable, blockstate2, false);
            }
         } else if (j > 0) {
            --j;
            chunkIn.setBlockState(blockpos$mutable, blockstate2, false);
            if (j == 0 && blockstate2.getBlock() == Blocks.SAND && l1 > 1) {
               j = random.nextInt(4) + Math.max(0, j1 - 63);
               blockstate2 = blockstate2.getBlock() == Blocks.RED_SAND ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
            }
         }
      }

   }

   public void setSeed(long seed) {
      if (this.seed != seed || this.field_205199_h == null || this.field_205200_i == null) {
         SharedSeedRandom sharedseedrandom = new SharedSeedRandom(seed);
         this.field_205199_h = new PerlinNoiseGenerator(sharedseedrandom, 3, 0);
         this.field_205200_i = new PerlinNoiseGenerator(sharedseedrandom, 0, 0);
      }

      this.seed = seed;
   }
}