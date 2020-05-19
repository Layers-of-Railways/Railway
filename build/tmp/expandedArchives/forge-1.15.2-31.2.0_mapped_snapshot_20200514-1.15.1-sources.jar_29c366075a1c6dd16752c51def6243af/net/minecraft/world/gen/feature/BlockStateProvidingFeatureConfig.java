package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;

public class BlockStateProvidingFeatureConfig implements IFeatureConfig {
   public final BlockStateProvider field_227268_a_;

   public BlockStateProvidingFeatureConfig(BlockStateProvider p_i225830_1_) {
      this.field_227268_a_ = p_i225830_1_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      Builder<T, T> builder = ImmutableMap.builder();
      builder.put(ops.createString("state_provider"), this.field_227268_a_.serialize(ops));
      return new Dynamic<>(ops, ops.createMap(builder.build()));
   }

   public static <T> BlockStateProvidingFeatureConfig deserialize(Dynamic<T> p_227269_0_) {
      BlockStateProviderType<?> blockstateprovidertype = Registry.BLOCK_STATE_PROVIDER_TYPE.getOrDefault(new ResourceLocation(p_227269_0_.get("state_provider").get("type").asString().orElseThrow(RuntimeException::new)));
      return new BlockStateProvidingFeatureConfig(blockstateprovidertype.func_227399_a_(p_227269_0_.get("state_provider").orElseEmptyMap()));
   }
}