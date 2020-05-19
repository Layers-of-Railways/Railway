package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class VillageConfig implements IFeatureConfig {
   public final ResourceLocation startPool;
   public final int size;

   public VillageConfig(String startPool, int size) {
      this.startPool = new ResourceLocation(startPool);
      this.size = size;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("start_pool"), ops.createString(this.startPool.toString()), ops.createString("size"), ops.createInt(this.size))));
   }

   public static <T> VillageConfig deserialize(Dynamic<T> data) {
      String s = data.get("start_pool").asString("");
      int i = data.get("size").asInt(6);
      return new VillageConfig(s, i);
   }
}