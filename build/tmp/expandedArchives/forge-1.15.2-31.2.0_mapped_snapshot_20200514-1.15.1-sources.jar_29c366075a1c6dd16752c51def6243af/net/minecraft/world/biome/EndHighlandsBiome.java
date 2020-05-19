package net.minecraft.world.biome;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.dimension.EndDimension;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.EndGatewayConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EndHighlandsBiome extends Biome {
   public EndHighlandsBiome() {
      super((new Biome.Builder()).surfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.END_STONE_CONFIG).precipitation(Biome.RainType.NONE).category(Biome.Category.THEEND).depth(0.1F).scale(0.2F).temperature(0.5F).downfall(0.5F).waterColor(4159204).waterFogColor(329011).parent((String)null));
      this.addStructure(Feature.END_CITY.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
      this.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, Feature.END_GATEWAY.withConfiguration(EndGatewayConfig.func_214702_a(EndDimension.SPAWN, true)).withPlacement(Placement.END_GATEWAY.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
      DefaultBiomeFeatures.addEndCity(this);
      this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.CHORUS_PLANT.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.CHORUS_PLANT.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ENDERMAN, 10, 4, 4));
   }

   @OnlyIn(Dist.CLIENT)
   public int getSkyColor() {
      return 0;
   }
}