package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class MultipleRandomFeature extends Feature<MultipleWithChanceRandomFeatureConfig> {
   public MultipleRandomFeature(Function<Dynamic<?>, ? extends MultipleWithChanceRandomFeatureConfig> p_i51453_1_) {
      super(p_i51453_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, MultipleWithChanceRandomFeatureConfig config) {
      int i = rand.nextInt(5) - 3 + config.count;

      for(int j = 0; j < i; ++j) {
         int k = rand.nextInt(config.features.size());
         ConfiguredFeature<?, ?> configuredfeature = config.features.get(k);
         configuredfeature.place(worldIn, generator, rand, pos);
      }

      return true;
   }
}