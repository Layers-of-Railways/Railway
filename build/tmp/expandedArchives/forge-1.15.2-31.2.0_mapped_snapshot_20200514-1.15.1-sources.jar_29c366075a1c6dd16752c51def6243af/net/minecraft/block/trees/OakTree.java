package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class OakTree extends Tree {
   /**
    * Get a {@link net.minecraft.world.gen.feature.ConfiguredFeature} of tree
    */
   @Nullable
   protected ConfiguredFeature<TreeFeatureConfig, ?> getTreeFeature(Random randomIn, boolean p_225546_2_) {
      return randomIn.nextInt(10) == 0 ? Feature.FANCY_TREE.withConfiguration(p_225546_2_ ? DefaultBiomeFeatures.FANCY_TREE_WITH_MORE_BEEHIVES_CONFIG : DefaultBiomeFeatures.FANCY_TREE_CONFIG) : Feature.NORMAL_TREE.withConfiguration(p_225546_2_ ? DefaultBiomeFeatures.OAK_TREE_WITH_MORE_BEEHIVES_CONFIG : DefaultBiomeFeatures.OAK_TREE_CONFIG);
   }
}