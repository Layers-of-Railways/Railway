package net.minecraft.world.biome;

import java.util.Collections;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

@net.minecraftforge.registries.ObjectHolder("minecraft")
public abstract class Biomes {
   public static final Biome OCEAN = register(0, "ocean", new OceanBiome());
   @net.minecraftforge.registries.ObjectHolder("minecraft:ocean")
   public static final Biome DEFAULT = OCEAN;
   public static final Biome PLAINS = register(1, "plains", new PlainsBiome());
   public static final Biome DESERT = register(2, "desert", new DesertBiome());
   public static final Biome MOUNTAINS = register(3, "mountains", new MountainsBiome());
   public static final Biome FOREST = register(4, "forest", new ForestBiome());
   public static final Biome TAIGA = register(5, "taiga", new TaigaBiome());
   public static final Biome SWAMP = register(6, "swamp", new SwampBiome());
   public static final Biome RIVER = register(7, "river", new RiverBiome());
   public static final Biome NETHER = register(8, "nether", new NetherBiome());
   public static final Biome THE_END = register(9, "the_end", new TheEndBiome());
   public static final Biome FROZEN_OCEAN = register(10, "frozen_ocean", new FrozenOceanBiome());
   public static final Biome FROZEN_RIVER = register(11, "frozen_river", new FrozenRiverBiome());
   public static final Biome SNOWY_TUNDRA = register(12, "snowy_tundra", new SnowyTundraBiome());
   public static final Biome SNOWY_MOUNTAINS = register(13, "snowy_mountains", new SnowyMountainsBiome());
   public static final Biome MUSHROOM_FIELDS = register(14, "mushroom_fields", new MushroomFieldsBiome());
   public static final Biome MUSHROOM_FIELD_SHORE = register(15, "mushroom_field_shore", new MushroomFieldShoreBiome());
   public static final Biome BEACH = register(16, "beach", new BeachBiome());
   public static final Biome DESERT_HILLS = register(17, "desert_hills", new DesertHillsBiome());
   public static final Biome WOODED_HILLS = register(18, "wooded_hills", new WoodedHillsBiome());
   public static final Biome TAIGA_HILLS = register(19, "taiga_hills", new TaigaHillsBiome());
   public static final Biome MOUNTAIN_EDGE = register(20, "mountain_edge", new MountainEdgeBiome());
   public static final Biome JUNGLE = register(21, "jungle", new JungleBiome());
   public static final Biome JUNGLE_HILLS = register(22, "jungle_hills", new JungleHillsBiome());
   public static final Biome JUNGLE_EDGE = register(23, "jungle_edge", new JungleEdgeBiome());
   public static final Biome DEEP_OCEAN = register(24, "deep_ocean", new DeepOceanBiome());
   public static final Biome STONE_SHORE = register(25, "stone_shore", new StoneShoreBiome());
   public static final Biome SNOWY_BEACH = register(26, "snowy_beach", new SnowyBeachBiome());
   public static final Biome BIRCH_FOREST = register(27, "birch_forest", new BirchForestBiome());
   public static final Biome BIRCH_FOREST_HILLS = register(28, "birch_forest_hills", new BirchForestHillsBiome());
   public static final Biome DARK_FOREST = register(29, "dark_forest", new DarkForestBiome());
   public static final Biome SNOWY_TAIGA = register(30, "snowy_taiga", new SnowyTaigaBiome());
   public static final Biome SNOWY_TAIGA_HILLS = register(31, "snowy_taiga_hills", new SnowyTaigaHillsBiome());
   public static final Biome GIANT_TREE_TAIGA = register(32, "giant_tree_taiga", new GiantTreeTaigaBiome());
   public static final Biome GIANT_TREE_TAIGA_HILLS = register(33, "giant_tree_taiga_hills", new GiantTreeTaigaHillsBiome());
   public static final Biome WOODED_MOUNTAINS = register(34, "wooded_mountains", new WoodedMountainsBiome());
   public static final Biome SAVANNA = register(35, "savanna", new SavannaBiome());
   public static final Biome SAVANNA_PLATEAU = register(36, "savanna_plateau", new SavannaPlateauBiome());
   public static final Biome BADLANDS = register(37, "badlands", new BadlandsBiome());
   public static final Biome WOODED_BADLANDS_PLATEAU = register(38, "wooded_badlands_plateau", new WoodedBadlandsPlateauBiome());
   public static final Biome BADLANDS_PLATEAU = register(39, "badlands_plateau", new BadlandsPlateauBiome());
   public static final Biome SMALL_END_ISLANDS = register(40, "small_end_islands", new SmallEndIslandsBiome());
   public static final Biome END_MIDLANDS = register(41, "end_midlands", new EndMidlandsBiome());
   public static final Biome END_HIGHLANDS = register(42, "end_highlands", new EndHighlandsBiome());
   public static final Biome END_BARRENS = register(43, "end_barrens", new EndBarrensBiome());
   public static final Biome WARM_OCEAN = register(44, "warm_ocean", new WarmOceanBiome());
   public static final Biome LUKEWARM_OCEAN = register(45, "lukewarm_ocean", new LukewarmOceanBiome());
   public static final Biome COLD_OCEAN = register(46, "cold_ocean", new ColdOceanBiome());
   public static final Biome DEEP_WARM_OCEAN = register(47, "deep_warm_ocean", new DeepWarmOceanBiome());
   public static final Biome DEEP_LUKEWARM_OCEAN = register(48, "deep_lukewarm_ocean", new DeepLukewarmOceanBiome());
   public static final Biome DEEP_COLD_OCEAN = register(49, "deep_cold_ocean", new DeepColdOceanBiome());
   public static final Biome DEEP_FROZEN_OCEAN = register(50, "deep_frozen_ocean", new DeepFrozenOceanBiome());
   public static final Biome THE_VOID = register(127, "the_void", new TheVoidBiome());
   public static final Biome SUNFLOWER_PLAINS = register(129, "sunflower_plains", new SunflowerPlainsBiome());
   public static final Biome DESERT_LAKES = register(130, "desert_lakes", new DesertLakesBiome());
   public static final Biome GRAVELLY_MOUNTAINS = register(131, "gravelly_mountains", new GravellyMountainsBiome());
   public static final Biome FLOWER_FOREST = register(132, "flower_forest", new FlowerForestBiome());
   public static final Biome TAIGA_MOUNTAINS = register(133, "taiga_mountains", new TaigaMountainsBiome());
   public static final Biome SWAMP_HILLS = register(134, "swamp_hills", new SwampHillsBiome());
   public static final Biome ICE_SPIKES = register(140, "ice_spikes", new IceSpikesBiome());
   public static final Biome MODIFIED_JUNGLE = register(149, "modified_jungle", new ModifiedJungleBiome());
   public static final Biome MODIFIED_JUNGLE_EDGE = register(151, "modified_jungle_edge", new ModifiedJungleEdgeBiome());
   public static final Biome TALL_BIRCH_FOREST = register(155, "tall_birch_forest", new TallBirchForestBiome());
   public static final Biome TALL_BIRCH_HILLS = register(156, "tall_birch_hills", new TallBirchHillsBiome());
   public static final Biome DARK_FOREST_HILLS = register(157, "dark_forest_hills", new DarkForestHillsBiome());
   public static final Biome SNOWY_TAIGA_MOUNTAINS = register(158, "snowy_taiga_mountains", new SnowyTaigaMountainsBiome());
   public static final Biome GIANT_SPRUCE_TAIGA = register(160, "giant_spruce_taiga", new GiantSpruceTaigaBiome());
   public static final Biome GIANT_SPRUCE_TAIGA_HILLS = register(161, "giant_spruce_taiga_hills", new GiantSpruceTaigaHillsBiome());
   public static final Biome MODIFIED_GRAVELLY_MOUNTAINS = register(162, "modified_gravelly_mountains", new ModifiedGravellyMountainsBiome());
   public static final Biome SHATTERED_SAVANNA = register(163, "shattered_savanna", new ShatteredSavannaBiome());
   public static final Biome SHATTERED_SAVANNA_PLATEAU = register(164, "shattered_savanna_plateau", new ShatteredSavannaPlateauBiome());
   public static final Biome ERODED_BADLANDS = register(165, "eroded_badlands", new ErodedBadlandsBiome());
   public static final Biome MODIFIED_WOODED_BADLANDS_PLATEAU = register(166, "modified_wooded_badlands_plateau", new ModifiedWoodedBadlandsPlateauBiome());
   public static final Biome MODIFIED_BADLANDS_PLATEAU = register(167, "modified_badlands_plateau", new ModifiedBadlandsPlateauBiome());
   public static final Biome BAMBOO_JUNGLE = register(168, "bamboo_jungle", new BambooJungleBiome());
   public static final Biome BAMBOO_JUNGLE_HILLS = register(169, "bamboo_jungle_hills", new BambooJungleHillsBiome());

   private static Biome register(int id, String key, Biome p_222369_2_) {
      Registry.register(Registry.BIOME, id, key, p_222369_2_);
      if (p_222369_2_.isMutation()) {
         Biome.MUTATION_TO_BASE_ID_MAP.put(p_222369_2_, Registry.BIOME.getId(Registry.BIOME.getOrDefault(new ResourceLocation(p_222369_2_.parent))));
      }

      return p_222369_2_;
   }

   static {
      Collections.addAll(Biome.BIOMES, OCEAN, PLAINS, DESERT, MOUNTAINS, FOREST, TAIGA, SWAMP, RIVER, FROZEN_RIVER, SNOWY_TUNDRA, SNOWY_MOUNTAINS, MUSHROOM_FIELDS, MUSHROOM_FIELD_SHORE, BEACH, DESERT_HILLS, WOODED_HILLS, TAIGA_HILLS, JUNGLE, JUNGLE_HILLS, JUNGLE_EDGE, DEEP_OCEAN, STONE_SHORE, SNOWY_BEACH, BIRCH_FOREST, BIRCH_FOREST_HILLS, DARK_FOREST, SNOWY_TAIGA, SNOWY_TAIGA_HILLS, GIANT_TREE_TAIGA, GIANT_TREE_TAIGA_HILLS, WOODED_MOUNTAINS, SAVANNA, SAVANNA_PLATEAU, BADLANDS, WOODED_BADLANDS_PLATEAU, BADLANDS_PLATEAU);
   }
}