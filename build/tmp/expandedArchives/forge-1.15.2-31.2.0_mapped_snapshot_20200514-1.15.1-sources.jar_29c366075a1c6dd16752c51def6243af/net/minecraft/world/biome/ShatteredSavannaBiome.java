package net.minecraft.world.biome;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

public final class ShatteredSavannaBiome extends Biome {
   public ShatteredSavannaBiome() {
      super((new Biome.Builder()).surfaceBuilder(SurfaceBuilder.SHATTERED_SAVANNA, SurfaceBuilder.GRASS_DIRT_GRAVEL_CONFIG).precipitation(Biome.RainType.NONE).category(Biome.Category.SAVANNA).depth(0.3625F).scale(1.225F).temperature(1.1F).downfall(0.0F).waterColor(4159204).waterFogColor(329011).parent("savanna"));
      this.addStructure(Feature.MINESHAFT.withConfiguration(new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL)));
      this.addStructure(Feature.STRONGHOLD.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
      DefaultBiomeFeatures.addCarvers(this);
      DefaultBiomeFeatures.addStructures(this);
      DefaultBiomeFeatures.addLakes(this);
      DefaultBiomeFeatures.addMonsterRooms(this);
      DefaultBiomeFeatures.addStoneVariants(this);
      DefaultBiomeFeatures.addOres(this);
      DefaultBiomeFeatures.addSedimentDisks(this);
      DefaultBiomeFeatures.addShatteredSavannaTrees(this);
      DefaultBiomeFeatures.addDefaultFlowers(this);
      DefaultBiomeFeatures.addDenseGrass(this);
      DefaultBiomeFeatures.addMushrooms(this);
      DefaultBiomeFeatures.addReedsAndPumpkins(this);
      DefaultBiomeFeatures.addSprings(this);
      DefaultBiomeFeatures.addFreezeTopLayer(this);
      this.addSpawn(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.SHEEP, 12, 4, 4));
      this.addSpawn(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.PIG, 10, 4, 4));
      this.addSpawn(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.CHICKEN, 10, 4, 4));
      this.addSpawn(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.COW, 8, 4, 4));
      this.addSpawn(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.HORSE, 1, 2, 6));
      this.addSpawn(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.DONKEY, 1, 1, 1));
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