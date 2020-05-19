package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class NoPlacementConfig implements IPlacementConfig {
   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.emptyMap());
   }

   public static NoPlacementConfig deserialize(Dynamic<?> p_214735_0_) {
      return new NoPlacementConfig();
   }
}