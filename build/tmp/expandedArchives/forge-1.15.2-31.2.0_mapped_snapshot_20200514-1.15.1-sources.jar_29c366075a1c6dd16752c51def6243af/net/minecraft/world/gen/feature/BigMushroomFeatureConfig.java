package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;

public class BigMushroomFeatureConfig implements IFeatureConfig {
   public final BlockStateProvider field_227272_a_;
   public final BlockStateProvider field_227273_b_;
   public final int field_227274_c_;

   public BigMushroomFeatureConfig(BlockStateProvider p_i225832_1_, BlockStateProvider p_i225832_2_, int p_i225832_3_) {
      this.field_227272_a_ = p_i225832_1_;
      this.field_227273_b_ = p_i225832_2_;
      this.field_227274_c_ = p_i225832_3_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      Builder<T, T> builder = ImmutableMap.builder();
      builder.put(ops.createString("cap_provider"), this.field_227272_a_.serialize(ops)).put(ops.createString("stem_provider"), this.field_227273_b_.serialize(ops)).put(ops.createString("foliage_radius"), ops.createInt(this.field_227274_c_));
      return new Dynamic<>(ops, ops.createMap(builder.build()));
   }

   public static <T> BigMushroomFeatureConfig deserialize(Dynamic<T> p_222853_0_) {
      BlockStateProviderType<?> blockstateprovidertype = Registry.BLOCK_STATE_PROVIDER_TYPE.getOrDefault(new ResourceLocation(p_222853_0_.get("cap_provider").get("type").asString().orElseThrow(RuntimeException::new)));
      BlockStateProviderType<?> blockstateprovidertype1 = Registry.BLOCK_STATE_PROVIDER_TYPE.getOrDefault(new ResourceLocation(p_222853_0_.get("stem_provider").get("type").asString().orElseThrow(RuntimeException::new)));
      return new BigMushroomFeatureConfig(blockstateprovidertype.func_227399_a_(p_222853_0_.get("cap_provider").orElseEmptyMap()), blockstateprovidertype1.func_227399_a_(p_222853_0_.get("stem_provider").orElseEmptyMap()), p_222853_0_.get("foliage_radius").asInt(2));
   }
}