package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.SingleRandomFeature;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.feature.structure.OceanRuinConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinStructure;
import net.minecraft.world.gen.feature.structure.ShipwreckConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.CountConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidWithNoiseConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

public class WarmOceanBiome extends Biome {
   public WarmOceanBiome() {
      super((new Biome.Builder()).surfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.SAND_CONFIG).precipitation(Biome.RainType.RAIN).category(Biome.Category.OCEAN).depth(-1.0F).scale(0.1F).temperature(0.5F).downfall(0.5F).waterColor(4445678).waterFogColor(270131).parent((String)null));
      this.addStructure(Feature.OCEAN_RUIN.withConfiguration(new OceanRuinConfig(OceanRuinStructure.Type.WARM, 0.3F, 0.9F)));
      this.addStructure(Feature.MINESHAFT.withConfiguration(new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL)));
      this.addStructure(Feature.SHIPWRECK.withConfiguration(new ShipwreckConfig(false)));
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
      this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.SIMPLE_RANDOM_SELECTOR.withConfiguration(new SingleRandomFeature(ImmutableList.of(Feature.CORAL_TREE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG), Feature.CORAL_CLAW.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG), Feature.CORAL_MUSHROOM.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG)))).withPlacement(Placement.TOP_SOLID_HEIGHTMAP_NOISE_BIASED.configure(new TopSolidWithNoiseConfig(20, 400.0D, 0.0D, Heightmap.Type.OCEAN_FLOOR_WG))));
      DefaultBiomeFeatures.addTallSeagrassSparse(this);
      this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.SEA_PICKLE.withConfiguration(new CountConfig(20)).withPlacement(Placement.CHANCE_TOP_SOLID_HEIGHTMAP.configure(new ChanceConfig(16))));
      DefaultBiomeFeatures.addFreezeTopLayer(this);
      this.addSpawn(EntityClassification.WATER_CREATURE, new Biome.SpawnListEntry(EntityType.SQUID, 10, 4, 4));
      this.addSpawn(EntityClassification.WATER_CREATURE, new Biome.SpawnListEntry(EntityType.PUFFERFISH, 15, 1, 3));
      this.addSpawn(EntityClassification.WATER_CREATURE, new Biome.SpawnListEntry(EntityType.TROPICAL_FISH, 25, 8, 8));
      this.addSpawn(EntityClassification.WATER_CREATURE, new Biome.SpawnListEntry(EntityType.DOLPHIN, 2, 1, 2));
      this.addSpawn(EntityClassification.AMBIENT, new Biome.SpawnListEntry(EntityType.BAT, 10, 8, 8));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.SPIDER, 100, 4, 4));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ZOMBIE, 95, 4, 4));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.SKELETON, 100, 4, 4));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.CREEPER, 100, 4, 4));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.SLIME, 100, 4, 4));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ENDERMAN, 10, 1, 4));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.WITCH, 5, 1, 1));
   }
}