package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class DecoratedFlowerFeature extends DecoratedFeature {
   public DecoratedFlowerFeature(Function<Dynamic<?>, ? extends DecoratedFeatureConfig> p_i49890_1_) {
      super(p_i49890_1_);
   }
}