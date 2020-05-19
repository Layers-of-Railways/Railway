package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public interface IPlacementConfig {
   NoPlacementConfig NO_PLACEMENT_CONFIG = new NoPlacementConfig();

   <T> Dynamic<T> serialize(DynamicOps<T> ops);
}