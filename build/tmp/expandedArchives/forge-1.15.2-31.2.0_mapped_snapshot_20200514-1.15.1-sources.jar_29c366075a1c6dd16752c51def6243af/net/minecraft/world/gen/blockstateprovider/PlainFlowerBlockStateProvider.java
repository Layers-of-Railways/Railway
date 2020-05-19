package net.minecraft.world.gen.blockstateprovider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class PlainFlowerBlockStateProvider extends BlockStateProvider {
   private static final BlockState[] field_227402_b_ = new BlockState[]{Blocks.ORANGE_TULIP.getDefaultState(), Blocks.RED_TULIP.getDefaultState(), Blocks.PINK_TULIP.getDefaultState(), Blocks.WHITE_TULIP.getDefaultState()};
   private static final BlockState[] field_227403_c_ = new BlockState[]{Blocks.POPPY.getDefaultState(), Blocks.AZURE_BLUET.getDefaultState(), Blocks.OXEYE_DAISY.getDefaultState(), Blocks.CORNFLOWER.getDefaultState()};

   public PlainFlowerBlockStateProvider() {
      super(BlockStateProviderType.PLAIN_FLOWER_PROVIDER);
   }

   public <T> PlainFlowerBlockStateProvider(Dynamic<T> p_i225857_1_) {
      this();
   }

   public BlockState getBlockState(Random randomIn, BlockPos blockPosIn) {
      double d0 = Biome.INFO_NOISE.noiseAt((double)blockPosIn.getX() / 200.0D, (double)blockPosIn.getZ() / 200.0D, false);
      if (d0 < -0.8D) {
         return field_227402_b_[randomIn.nextInt(field_227402_b_.length)];
      } else {
         return randomIn.nextInt(3) > 0 ? field_227403_c_[randomIn.nextInt(field_227403_c_.length)] : Blocks.DANDELION.getDefaultState();
      }
   }

   public <T> T serialize(DynamicOps<T> p_218175_1_) {
      Builder<T, T> builder = ImmutableMap.builder();
      builder.put(p_218175_1_.createString("type"), p_218175_1_.createString(Registry.BLOCK_STATE_PROVIDER_TYPE.getKey(this.blockStateProvider).toString()));
      return (new Dynamic<>(p_218175_1_, p_218175_1_.createMap(builder.build()))).getValue();
   }
}