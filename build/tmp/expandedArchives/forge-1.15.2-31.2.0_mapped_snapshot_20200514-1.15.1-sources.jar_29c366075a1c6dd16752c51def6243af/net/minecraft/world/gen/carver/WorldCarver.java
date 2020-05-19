package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.ProbabilityConfig;

public abstract class WorldCarver<C extends ICarverConfig> extends net.minecraftforge.registries.ForgeRegistryEntry<WorldCarver<?>> {
   public static final WorldCarver<ProbabilityConfig> CAVE = register("cave", new CaveWorldCarver(ProbabilityConfig::deserialize, 256));
   public static final WorldCarver<ProbabilityConfig> HELL_CAVE = register("hell_cave", new NetherCaveWorldCarver(ProbabilityConfig::deserialize));
   public static final WorldCarver<ProbabilityConfig> CANYON = register("canyon", new CanyonWorldCarver(ProbabilityConfig::deserialize));
   public static final WorldCarver<ProbabilityConfig> UNDERWATER_CANYON = register("underwater_canyon", new UnderwaterCanyonWorldCarver(ProbabilityConfig::deserialize));
   public static final WorldCarver<ProbabilityConfig> UNDERWATER_CAVE = register("underwater_cave", new UnderwaterCaveWorldCarver(ProbabilityConfig::deserialize));
   protected static final BlockState AIR = Blocks.AIR.getDefaultState();
   protected static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
   protected static final IFluidState WATER = Fluids.WATER.getDefaultState();
   protected static final IFluidState LAVA = Fluids.LAVA.getDefaultState();
   protected Set<Block> carvableBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.PACKED_ICE);
   protected Set<Fluid> carvableFluids = ImmutableSet.of(Fluids.WATER);
   private final Function<Dynamic<?>, ? extends C> config;
   protected final int maxHeight;

   private static <C extends ICarverConfig, F extends WorldCarver<C>> F register(String key, F carver) {
      return (F)(Registry.<WorldCarver<?>>register(Registry.CARVER, key, carver));
   }

   public WorldCarver(Function<Dynamic<?>, ? extends C> p_i49921_1_, int p_i49921_2_) {
      this.config = p_i49921_1_;
      this.maxHeight = p_i49921_2_;
   }

   public int func_222704_c() {
      return 4;
   }

   protected boolean func_227208_a_(IChunk p_227208_1_, Function<BlockPos, Biome> p_227208_2_, long p_227208_3_, int p_227208_5_, int p_227208_6_, int p_227208_7_, double p_227208_8_, double p_227208_10_, double p_227208_12_, double p_227208_14_, double p_227208_16_, BitSet p_227208_18_) {
      Random random = new Random(p_227208_3_ + (long)p_227208_6_ + (long)p_227208_7_);
      double d0 = (double)(p_227208_6_ * 16 + 8);
      double d1 = (double)(p_227208_7_ * 16 + 8);
      if (!(p_227208_8_ < d0 - 16.0D - p_227208_14_ * 2.0D) && !(p_227208_12_ < d1 - 16.0D - p_227208_14_ * 2.0D) && !(p_227208_8_ > d0 + 16.0D + p_227208_14_ * 2.0D) && !(p_227208_12_ > d1 + 16.0D + p_227208_14_ * 2.0D)) {
         int i = Math.max(MathHelper.floor(p_227208_8_ - p_227208_14_) - p_227208_6_ * 16 - 1, 0);
         int j = Math.min(MathHelper.floor(p_227208_8_ + p_227208_14_) - p_227208_6_ * 16 + 1, 16);
         int k = Math.max(MathHelper.floor(p_227208_10_ - p_227208_16_) - 1, 1);
         int l = Math.min(MathHelper.floor(p_227208_10_ + p_227208_16_) + 1, this.maxHeight - 8);
         int i1 = Math.max(MathHelper.floor(p_227208_12_ - p_227208_14_) - p_227208_7_ * 16 - 1, 0);
         int j1 = Math.min(MathHelper.floor(p_227208_12_ + p_227208_14_) - p_227208_7_ * 16 + 1, 16);
         if (this.func_222700_a(p_227208_1_, p_227208_6_, p_227208_7_, i, j, k, l, i1, j1)) {
            return false;
         } else {
            boolean flag = false;
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
            BlockPos.Mutable blockpos$mutable1 = new BlockPos.Mutable();
            BlockPos.Mutable blockpos$mutable2 = new BlockPos.Mutable();

            for(int k1 = i; k1 < j; ++k1) {
               int l1 = k1 + p_227208_6_ * 16;
               double d2 = ((double)l1 + 0.5D - p_227208_8_) / p_227208_14_;

               for(int i2 = i1; i2 < j1; ++i2) {
                  int j2 = i2 + p_227208_7_ * 16;
                  double d3 = ((double)j2 + 0.5D - p_227208_12_) / p_227208_14_;
                  if (!(d2 * d2 + d3 * d3 >= 1.0D)) {
                     AtomicBoolean atomicboolean = new AtomicBoolean(false);

                     for(int k2 = l; k2 > k; --k2) {
                        double d4 = ((double)k2 - 0.5D - p_227208_10_) / p_227208_16_;
                        if (!this.func_222708_a(d2, d4, d3, k2)) {
                           flag |= this.func_225556_a_(p_227208_1_, p_227208_2_, p_227208_18_, random, blockpos$mutable, blockpos$mutable1, blockpos$mutable2, p_227208_5_, p_227208_6_, p_227208_7_, l1, j2, k1, k2, i2, atomicboolean);
                        }
                     }
                  }
               }
            }

            return flag;
         }
      } else {
         return false;
      }
   }

   protected boolean func_225556_a_(IChunk p_225556_1_, Function<BlockPos, Biome> p_225556_2_, BitSet p_225556_3_, Random p_225556_4_, BlockPos.Mutable p_225556_5_, BlockPos.Mutable p_225556_6_, BlockPos.Mutable p_225556_7_, int p_225556_8_, int p_225556_9_, int p_225556_10_, int p_225556_11_, int p_225556_12_, int p_225556_13_, int p_225556_14_, int p_225556_15_, AtomicBoolean p_225556_16_) {
      int i = p_225556_13_ | p_225556_15_ << 4 | p_225556_14_ << 8;
      if (p_225556_3_.get(i)) {
         return false;
      } else {
         p_225556_3_.set(i);
         p_225556_5_.setPos(p_225556_11_, p_225556_14_, p_225556_12_);
         BlockState blockstate = p_225556_1_.getBlockState(p_225556_5_);
         BlockState blockstate1 = p_225556_1_.getBlockState(p_225556_6_.setPos(p_225556_5_).move(Direction.UP));
         if (blockstate.getBlock() == Blocks.GRASS_BLOCK || blockstate.getBlock() == Blocks.MYCELIUM) {
            p_225556_16_.set(true);
         }

         if (!this.canCarveBlock(blockstate, blockstate1)) {
            return false;
         } else {
            if (p_225556_14_ < 11) {
               p_225556_1_.setBlockState(p_225556_5_, LAVA.getBlockState(), false);
            } else {
               p_225556_1_.setBlockState(p_225556_5_, CAVE_AIR, false);
               if (p_225556_16_.get()) {
                  p_225556_7_.setPos(p_225556_5_).move(Direction.DOWN);
                  if (p_225556_1_.getBlockState(p_225556_7_).getBlock() == Blocks.DIRT) {
                     p_225556_1_.setBlockState(p_225556_7_, p_225556_2_.apply(p_225556_5_).getSurfaceBuilderConfig().getTop(), false);
                  }
               }
            }

            return true;
         }
      }
   }

   public abstract boolean func_225555_a_(IChunk p_225555_1_, Function<BlockPos, Biome> p_225555_2_, Random p_225555_3_, int p_225555_4_, int p_225555_5_, int p_225555_6_, int p_225555_7_, int p_225555_8_, BitSet p_225555_9_, C p_225555_10_);

   public abstract boolean shouldCarve(Random rand, int chunkX, int chunkZ, C config);

   protected boolean isCarvable(BlockState p_222706_1_) {
      return this.carvableBlocks.contains(p_222706_1_.getBlock());
   }

   protected boolean canCarveBlock(BlockState state, BlockState aboveState) {
      Block block = state.getBlock();
      return this.isCarvable(state) || (block == Blocks.SAND || block == Blocks.GRAVEL) && !aboveState.getFluidState().isTagged(FluidTags.WATER);
   }

   protected boolean func_222700_a(IChunk chunkIn, int chunkX, int chunkZ, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int i = minX; i < maxX; ++i) {
         for(int j = minZ; j < maxZ; ++j) {
            for(int k = minY - 1; k <= maxY + 1; ++k) {
               if (this.carvableFluids.contains(chunkIn.getFluidState(blockpos$mutable.setPos(i + chunkX * 16, k, j + chunkZ * 16)).getFluid())) {
                  return true;
               }

               if (k != maxY + 1 && !this.isOnEdge(minX, maxX, minZ, maxZ, i, j)) {
                  k = maxY;
               }
            }
         }
      }

      return false;
   }

   private boolean isOnEdge(int minX, int maxX, int minZ, int maxZ, int x, int z) {
      return x == minX || x == maxX - 1 || z == minZ || z == maxZ - 1;
   }

   protected boolean func_222702_a(int p_222702_1_, int p_222702_2_, double p_222702_3_, double p_222702_5_, int p_222702_7_, int p_222702_8_, float p_222702_9_) {
      double d0 = (double)(p_222702_1_ * 16 + 8);
      double d1 = (double)(p_222702_2_ * 16 + 8);
      double d2 = p_222702_3_ - d0;
      double d3 = p_222702_5_ - d1;
      double d4 = (double)(p_222702_8_ - p_222702_7_);
      double d5 = (double)(p_222702_9_ + 2.0F + 16.0F);
      return d2 * d2 + d3 * d3 - d4 * d4 <= d5 * d5;
   }

   protected abstract boolean func_222708_a(double p_222708_1_, double p_222708_3_, double p_222708_5_, int p_222708_7_);
}