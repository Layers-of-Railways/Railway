package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class MultipleWithChanceRandomFeature extends Feature<MultipleRandomFeatureConfig> {
   public MultipleWithChanceRandomFeature(Function<Dynamic<?>, ? extends MultipleRandomFeatureConfig> p_i51447_1_) {
      super(p_i51447_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, MultipleRandomFeatureConfig config) {
      for(ConfiguredRandomFeatureList<?> configuredrandomfeaturelist : config.features) {
         if (rand.nextFloat() < configuredrandomfeaturelist.chance) {
            return configuredrandomfeaturelist.place(worldIn, generator, rand, pos);
         }
      }

      return config.defaultFeature.place(worldIn, generator, rand, pos);
   }
}