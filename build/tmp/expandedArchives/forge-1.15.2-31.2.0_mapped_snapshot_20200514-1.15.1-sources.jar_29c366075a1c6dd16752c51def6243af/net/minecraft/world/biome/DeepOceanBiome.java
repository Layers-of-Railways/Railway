package net.minecraft.world.biome;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.SeaGrassConfig;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.feature.structure.OceanRuinConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinStructure;
import net.minecraft.world.gen.feature.structure.ShipwreckConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

public final class DeepOceanBiome extends Biome {
   public DeepOceanBiome() {
      super((new Biome.Builder()).surfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_DIRT_GRAVEL_CONFIG).precipitation(Biome.RainType.RAIN).category(Biome.Category.OCEAN).depth(-1.8F).scale(0.1F).temperature(0.5F).downfall(0.5F).waterColor(4159204).waterFogColor(329011).parent((String)null));
      this.addStructure(Feature.OCEAN_MONUMENT.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
      this.addStructure(Feature.MINESHAFT.withConfiguration(new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL)));
      this.addStructure(Feature.SHIPWRECK.withConfiguration(new ShipwreckConfig(false)));
      this.addStructure(Feature.OCEAN_RUIN.withConfiguration(new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.9F)));
      DefaultBiomeFeatures.addOceanCarvers(this);
      DefaultBiomeFeatures.addStructures(this);
      DefaultBiomeFeatures.addLakes(this);
      DefaultBiomeFeatures.addMonsterRooms(this);
      DefaultBiomeFeatures.addStoneVariants(this);
      DefaultBiomeFeatures.addOres(this);
      DefaultBiomeFeatures.addSedimentDisks(this);
      DefaultBiomeFeatures.addScatteredOakTrees(this);
      DefaultBiomeFeatures.addDefaultFlowers(this);
      DefaultBiomeFeatures.addSparseGrass(this);
      DefaultBiomeFeatures.addMushrooms(this);
      DefaultBiomeFeatures.addReedsAndPumpkins(this);
      DefaultBiomeFeatures.addSprings(this);
      this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.SEAGRASS.withConfiguration(new SeaGrassConfig(48, 0.8D)).withPlacement(Placement.TOP_SOLID_HEIGHTMAP.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
      DefaultBiomeFeatures.addSeagrass(this);
      DefaultBiomeFeatures.addExtraKelp(this);
      DefaultBiomeFeatures.addFreezeTopLayer(this);
      this.addSpawn(EntityClassification.WATER_CREATURE, new Biome.SpawnListEntry(EntityType.SQUID, 1, 1, 4));
      this.addSpawn(EntityClassification.WATER_CREATURE, new Biome.SpawnListEntry(EntityType.COD, 10, 3, 6));
      this.addSpawn(EntityClassification.WATER_CREATURE, new Biome.SpawnListEntry(EntityType.DOLPHIN, 1, 1, 2));
      this.addSpawn(EntityClassification.AMBIENT, new Biome.SpawnListEntry(EntityType.BAT, 10, 8, 8));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.SPIDER, 100, 4, 4));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ZOMBIE, 95, 4, 4));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.DROWNED, 5, 1, 1));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.SKELETON, 100, 4, 4));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.CREEPER, 100, 4, 4));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.SLIME, 100, 4, 4));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ENDERMAN, 10, 1, 4));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.WITCH, 5, 1, 1));
   }
}