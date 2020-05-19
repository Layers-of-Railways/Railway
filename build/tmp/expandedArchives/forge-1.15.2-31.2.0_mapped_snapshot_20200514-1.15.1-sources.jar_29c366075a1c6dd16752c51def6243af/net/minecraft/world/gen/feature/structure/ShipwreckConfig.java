package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class ShipwreckConfig implements IFeatureConfig {
   public final boolean isBeached;

   public ShipwreckConfig(boolean isBeachedIn) {
      this.isBeached = isBeachedIn;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("is_beached"), ops.createBoolean(this.isBeached))));
   }

   public static <T> ShipwreckConfig deserialize(Dynamic<T> p_214658_0_) {
      boolean flag = p_214658_0_.get("is_beached").asBoolean(false);
      return new ShipwreckConfig(flag);
   }
}