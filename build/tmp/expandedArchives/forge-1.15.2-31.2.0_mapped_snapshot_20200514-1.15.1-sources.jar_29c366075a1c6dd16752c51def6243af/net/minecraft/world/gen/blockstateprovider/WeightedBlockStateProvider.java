package net.minecraft.world.gen.blockstateprovider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.WeightedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class WeightedBlockStateProvider extends BlockStateProvider {
   private final WeightedList<BlockState> weightedStates;

   private WeightedBlockStateProvider(WeightedList<BlockState> p_i225862_1_) {
      super(BlockStateProviderType.WEIGHTED_STATE_PROVIDER);
      this.weightedStates = p_i225862_1_;
   }

   public WeightedBlockStateProvider() {
      this(new WeightedList<>());
   }

   public <T> WeightedBlockStateProvider(Dynamic<T> p_i225863_1_) {
      this(new WeightedList<>(p_i225863_1_.get("entries").orElseEmptyList(), BlockState::deserialize));
   }

   /**
    * Adds the blockstate with the specified weight to the weighted states of the provider.
    */
   public WeightedBlockStateProvider addWeightedBlockstate(BlockState blockStateIn, int weightIn) {
      this.weightedStates.func_226313_a_(blockStateIn, weightIn);
      return this;
   }

   public BlockState getBlockState(Random randomIn, BlockPos blockPosIn) {
      return this.weightedStates.func_226318_b_(randomIn);
   }

   public <T> T serialize(DynamicOps<T> p_218175_1_) {
      Builder<T, T> builder = ImmutableMap.builder();
      builder.put(p_218175_1_.createString("type"), p_218175_1_.createString(Registry.BLOCK_STATE_PROVIDER_TYPE.getKey(this.blockStateProvider).toString())).put(p_218175_1_.createString("entries"), this.weightedStates.func_226310_a_(p_218175_1_, (p_227408_1_) -> {
         return BlockState.serialize(p_218175_1_, p_227408_1_);
      }));
      return (new Dynamic<>(p_218175_1_, p_218175_1_.createMap(builder.build()))).getValue();
   }
}