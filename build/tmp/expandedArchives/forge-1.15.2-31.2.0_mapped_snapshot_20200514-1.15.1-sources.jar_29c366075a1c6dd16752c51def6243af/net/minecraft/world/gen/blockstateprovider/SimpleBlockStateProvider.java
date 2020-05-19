package net.minecraft.world.gen.blockstateprovider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class SimpleBlockStateProvider extends BlockStateProvider {
   private final BlockState state;

   public SimpleBlockStateProvider(BlockState p_i225860_1_) {
      super(BlockStateProviderType.SIMPLE_STATE_PROVIDER);
      this.state = p_i225860_1_;
   }

   public <T> SimpleBlockStateProvider(Dynamic<T> p_i225861_1_) {
      this(BlockState.deserialize(p_i225861_1_.get("state").orElseEmptyMap()));
   }

   public BlockState getBlockState(Random randomIn, BlockPos blockPosIn) {
      return this.state;
   }

   public <T> T serialize(DynamicOps<T> p_218175_1_) {
      Builder<T, T> builder = ImmutableMap.builder();
      builder.put(p_218175_1_.createString("type"), p_218175_1_.createString(Registry.BLOCK_STATE_PROVIDER_TYPE.getKey(this.blockStateProvider).toString())).put(p_218175_1_.createString("state"), BlockState.serialize(p_218175_1_, this.state).getValue());
      return (new Dynamic<>(p_218175_1_, p_218175_1_.createMap(builder.build()))).getValue();
   }
}