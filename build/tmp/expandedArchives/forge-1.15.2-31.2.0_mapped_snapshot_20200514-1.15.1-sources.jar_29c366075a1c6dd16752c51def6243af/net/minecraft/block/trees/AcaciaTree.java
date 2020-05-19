package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class AcaciaTree extends Tree {
   /**
    * Get a {@link net.minecraft.world.gen.feature.ConfiguredFeature} of tree
    */
   @Nullable
   protected ConfiguredFeature<TreeFeatureConfig, ?> getTreeFeature(Random randomIn, boolean p_225546_2_) {
      return Feature.ACACIA_TREE.withConfiguration(DefaultBiomeFeatures.ACACIA_TREE_CONFIG);
   }
}