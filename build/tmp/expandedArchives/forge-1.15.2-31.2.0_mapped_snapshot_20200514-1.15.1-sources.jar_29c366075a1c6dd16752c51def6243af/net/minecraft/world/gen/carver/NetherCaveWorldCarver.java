package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.ProbabilityConfig;

public class NetherCaveWorldCarver extends CaveWorldCarver {
   public NetherCaveWorldCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> p_i49927_1_) {
      super(p_i49927_1_, 128);
      this.carvableBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.NETHERRACK);
      this.carvableFluids = ImmutableSet.of(Fluids.LAVA, Fluids.WATER);
   }

   protected int func_222724_a() {
      return 10;
   }

   protected float generateCaveRadius(Random rand) {
      return (rand.nextFloat() * 2.0F + rand.nextFloat()) * 2.0F;
   }

   protected double func_222725_b() {
      return 5.0D;
   }

   protected int generateCaveStartY(Random p_222726_1_) {
      return p_222726_1_.nextInt(this.maxHeight);
   }

   protected boolean func_225556_a_(IChunk p_225556_1_, Function<BlockPos, Biome> p_225556_2_, BitSet p_225556_3_, Random p_225556_4_, BlockPos.Mutable p_225556_5_, BlockPos.Mutable p_225556_6_, BlockPos.Mutable p_225556_7_, int p_225556_8_, int p_225556_9_, int p_225556_10_, int p_225556_11_, int p_225556_12_, int p_225556_13_, int p_225556_14_, int p_225556_15_, AtomicBoolean p_225556_16_) {
      int i = p_225556_13_ | p_225556_15_ << 4 | p_225556_14_ << 8;
      if (p_225556_3_.get(i)) {
         return false;
      } else {
         p_225556_3_.set(i);
         p_225556_5_.setPos(p_225556_11_, p_225556_14_, p_225556_12_);
         if (this.isCarvable(p_225556_1_.getBlockState(p_225556_5_))) {
            BlockState blockstate;
            if (p_225556_14_ <= 31) {
               blockstate = LAVA.getBlockState();
            } else {
               blockstate = CAVE_AIR;
            }

            p_225556_1_.setBlockState(p_225556_5_, blockstate, false);
            return true;
         } else {
            return false;
         }
      }
   }
}