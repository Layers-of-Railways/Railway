package net.minecraft.world.biome;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.SeaGrassConfig;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class SwampBiome extends Biome {
   protected SwampBiome() {
      super((new Biome.Builder()).surfaceBuilder(SurfaceBuilder.SWAMP, SurfaceBuilder.GRASS_DIRT_GRAVEL_CONFIG).precipitation(Biome.RainType.RAIN).category(Biome.Category.SWAMP).depth(-0.2F).scale(0.1F).temperature(0.8F).downfall(0.9F).waterColor(6388580).waterFogColor(2302743).parent((String)null));
      this.addStructure(Feature.SWAMP_HUT.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
      this.addStructure(Feature.MINESHAFT.withConfiguration(new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL)));
      DefaultBiomeFeatures.addCarvers(this);
      DefaultBiomeFeatures.addStructures(this);
      DefaultBiomeFeatures.addLakes(this);
      DefaultBiomeFeatures.addMonsterRooms(this);
      DefaultBiomeFeatures.addStoneVariants(this);
      DefaultBiomeFeatures.addOres(this);
      DefaultBiomeFeatures.addSwampClayDisks(this);
      DefaultBiomeFeatures.addSwampVegetation(this);
      DefaultBiomeFeatures.addMushrooms(this);
      DefaultBiomeFeatures.addExtraReedsAndPumpkins(this);
      DefaultBiomeFeatures.addSprings(this);
      this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.SEAGRASS.withConfiguration(new SeaGrassConfig(64, 0.6D)).withPlacement(Placement.TOP_SOLID_HEIGHTMAP.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
      DefaultBiomeFeatures.addFossils(this);
      DefaultBiomeFeatures.addFreezeTopLayer(this);
      this.addSpawn(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.SHEEP, 12, 4, 4));
      this.addSpawn(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.PIG, 10, 4, 4));
      this.addSpawn(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.CHICKEN, 10, 4, 4));
      this.addSpawn(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.COW, 8, 4, 4));
      this.addSpawn(EntityClassification.AMBIENT, new Biome.SpawnListEntry(EntityType.BAT, 10, 8, 8));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.SPIDER, 100, 4, 4));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ZOMBIE, 95, 4, 4));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.SKELETON, 100, 4, 4));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.CREEPER, 100, 4, 4));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.SLIME, 100, 4, 4));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ENDERMAN, 10, 1, 4));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.WITCH, 5, 1, 1));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.SLIME, 1, 1, 1));
   }

   @OnlyIn(Dist.CLIENT)
   public int getGrassColor(double posX, double posZ) {
      double d0 = INFO_NOISE.noiseAt(posX * 0.0225D, posZ * 0.0225D, false);
      return d0 < -0.1D ? 5011004 : 6975545;
   }

   @OnlyIn(Dist.CLIENT)
   public int getFoliageColor() {
      return 6975545;
   }
}