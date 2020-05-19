package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class DecoratedFeature extends Feature<DecoratedFeatureConfig> {
   public DecoratedFeature(Function<Dynamic<?>, ? extends DecoratedFeatureConfig> p_i49893_1_) {
      super(p_i49893_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, DecoratedFeatureConfig config) {
      return config.decorator.place(worldIn, generator, rand, pos, config.feature);
   }

   public String toString() {
      return String.format("< %s [%s] >", this.getClass().getSimpleName(), Registry.FEATURE.getKey(this));
   }
}