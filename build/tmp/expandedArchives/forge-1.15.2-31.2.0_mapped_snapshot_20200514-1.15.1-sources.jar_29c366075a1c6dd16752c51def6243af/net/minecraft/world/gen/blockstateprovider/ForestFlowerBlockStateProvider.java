package net.minecraft.world.gen.blockstateprovider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class ForestFlowerBlockStateProvider extends BlockStateProvider {
   private static final BlockState[] field_227401_b_ = new BlockState[]{Blocks.DANDELION.getDefaultState(), Blocks.POPPY.getDefaultState(), Blocks.ALLIUM.getDefaultState(), Blocks.AZURE_BLUET.getDefaultState(), Blocks.RED_TULIP.getDefaultState(), Blocks.ORANGE_TULIP.getDefaultState(), Blocks.WHITE_TULIP.getDefaultState(), Blocks.PINK_TULIP.getDefaultState(), Blocks.OXEYE_DAISY.getDefaultState(), Blocks.CORNFLOWER.getDefaultState(), Blocks.LILY_OF_THE_VALLEY.getDefaultState()};

   public ForestFlowerBlockStateProvider() {
      super(BlockStateProviderType.FOREST_FLOWER_PROVIDER);
   }

   public <T> ForestFlowerBlockStateProvider(Dynamic<T> p_i225856_1_) {
      this();
   }

   public BlockState getBlockState(Random randomIn, BlockPos blockPosIn) {
      double d0 = MathHelper.clamp((1.0D + Biome.INFO_NOISE.noiseAt((double)blockPosIn.getX() / 48.0D, (double)blockPosIn.getZ() / 48.0D, false)) / 2.0D, 0.0D, 0.9999D);
      return field_227401_b_[(int)(d0 * (double)field_227401_b_.length)];
   }

   public <T> T serialize(DynamicOps<T> p_218175_1_) {
      Builder<T, T> builder = ImmutableMap.builder();
      builder.put(p_218175_1_.createString("type"), p_218175_1_.createString(Registry.BLOCK_STATE_PROVIDER_TYPE.getKey(this.blockStateProvider).toString()));
      return (new Dynamic<>(p_218175_1_, p_218175_1_.createMap(builder.build()))).getValue();
   }
}