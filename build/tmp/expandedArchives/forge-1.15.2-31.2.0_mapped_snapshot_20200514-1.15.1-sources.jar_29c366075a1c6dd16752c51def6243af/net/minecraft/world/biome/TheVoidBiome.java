package net.minecraft.world.biome;

import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

public final class TheVoidBiome extends Biome {
   public TheVoidBiome() {
      super((new Biome.Builder()).surfaceBuilder(SurfaceBuilder.NOPE, SurfaceBuilder.STONE_STONE_GRAVEL_CONFIG).precipitation(Biome.RainType.NONE).category(Biome.Category.NONE).depth(0.1F).scale(0.2F).temperature(0.5F).downfall(0.5F).waterColor(4159204).waterFogColor(329011).parent((String)null));
      this.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, Feature.VOID_START_PLATFORM.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
   }
}