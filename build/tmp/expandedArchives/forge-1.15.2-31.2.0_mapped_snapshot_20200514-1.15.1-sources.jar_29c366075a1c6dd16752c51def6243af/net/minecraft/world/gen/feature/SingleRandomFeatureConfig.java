package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class SingleRandomFeatureConfig extends Feature<SingleRandomFeature> {
   public SingleRandomFeatureConfig(Function<Dynamic<?>, ? extends SingleRandomFeature> p_i51436_1_) {
      super(p_i51436_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, SingleRandomFeature config) {
      int i = rand.nextInt(config.features.size());
      ConfiguredFeature<?, ?> configuredfeature = config.features.get(i);
      return configuredfeature.place(worldIn, generator, rand, pos);
   }
}