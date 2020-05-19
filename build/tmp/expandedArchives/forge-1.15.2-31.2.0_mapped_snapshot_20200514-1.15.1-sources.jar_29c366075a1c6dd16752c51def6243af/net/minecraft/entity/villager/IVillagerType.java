package net.minecraft.entity.villager;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public interface IVillagerType {
   IVillagerType DESERT = register("desert");
   IVillagerType JUNGLE = register("jungle");
   IVillagerType PLAINS = register("plains");
   IVillagerType SAVANNA = register("savanna");
   IVillagerType SNOW = register("snow");
   IVillagerType SWAMP = register("swamp");
   IVillagerType TAIGA = register("taiga");
   Map<Biome, IVillagerType> BY_BIOME = Util.make(Maps.newHashMap(), (p_221172_0_) -> {
      p_221172_0_.put(Biomes.BADLANDS, DESERT);
      p_221172_0_.put(Biomes.BADLANDS_PLATEAU, DESERT);
      p_221172_0_.put(Biomes.DESERT, DESERT);
      p_221172_0_.put(Biomes.DESERT_HILLS, DESERT);
      p_221172_0_.put(Biomes.DESERT_LAKES, DESERT);
      p_221172_0_.put(Biomes.ERODED_BADLANDS, DESERT);
      p_221172_0_.put(Biomes.MODIFIED_BADLANDS_PLATEAU, DESERT);
      p_221172_0_.put(Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, DESERT);
      p_221172_0_.put(Biomes.WOODED_BADLANDS_PLATEAU, DESERT);
      p_221172_0_.put(Biomes.BAMBOO_JUNGLE, JUNGLE);
      p_221172_0_.put(Biomes.BAMBOO_JUNGLE_HILLS, JUNGLE);
      p_221172_0_.put(Biomes.JUNGLE, JUNGLE);
      p_221172_0_.put(Biomes.JUNGLE_EDGE, JUNGLE);
      p_221172_0_.put(Biomes.JUNGLE_HILLS, JUNGLE);
      p_221172_0_.put(Biomes.MODIFIED_JUNGLE, JUNGLE);
      p_221172_0_.put(Biomes.MODIFIED_JUNGLE_EDGE, JUNGLE);
      p_221172_0_.put(Biomes.SAVANNA_PLATEAU, SAVANNA);
      p_221172_0_.put(Biomes.SAVANNA, SAVANNA);
      p_221172_0_.put(Biomes.SHATTERED_SAVANNA, SAVANNA);
      p_221172_0_.put(Biomes.SHATTERED_SAVANNA_PLATEAU, SAVANNA);
      p_221172_0_.put(Biomes.DEEP_FROZEN_OCEAN, SNOW);
      p_221172_0_.put(Biomes.FROZEN_OCEAN, SNOW);
      p_221172_0_.put(Biomes.FROZEN_RIVER, SNOW);
      p_221172_0_.put(Biomes.ICE_SPIKES, SNOW);
      p_221172_0_.put(Biomes.SNOWY_BEACH, SNOW);
      p_221172_0_.put(Biomes.SNOWY_MOUNTAINS, SNOW);
      p_221172_0_.put(Biomes.SNOWY_TAIGA, SNOW);
      p_221172_0_.put(Biomes.SNOWY_TAIGA_HILLS, SNOW);
      p_221172_0_.put(Biomes.SNOWY_TAIGA_MOUNTAINS, SNOW);
      p_221172_0_.put(Biomes.SNOWY_TUNDRA, SNOW);
      p_221172_0_.put(Biomes.SWAMP, SWAMP);
      p_221172_0_.put(Biomes.SWAMP_HILLS, SWAMP);
      p_221172_0_.put(Biomes.GIANT_SPRUCE_TAIGA, TAIGA);
      p_221172_0_.put(Biomes.GIANT_SPRUCE_TAIGA_HILLS, TAIGA);
      p_221172_0_.put(Biomes.GIANT_TREE_TAIGA, TAIGA);
      p_221172_0_.put(Biomes.GIANT_TREE_TAIGA_HILLS, TAIGA);
      p_221172_0_.put(Biomes.GRAVELLY_MOUNTAINS, TAIGA);
      p_221172_0_.put(Biomes.MODIFIED_GRAVELLY_MOUNTAINS, TAIGA);
      p_221172_0_.put(Biomes.MOUNTAIN_EDGE, TAIGA);
      p_221172_0_.put(Biomes.MOUNTAINS, TAIGA);
      p_221172_0_.put(Biomes.TAIGA, TAIGA);
      p_221172_0_.put(Biomes.TAIGA_HILLS, TAIGA);
      p_221172_0_.put(Biomes.TAIGA_MOUNTAINS, TAIGA);
      p_221172_0_.put(Biomes.WOODED_MOUNTAINS, TAIGA);
   });

   static IVillagerType register(final String key) {
      return Registry.register(Registry.VILLAGER_TYPE, new ResourceLocation(key), new IVillagerType() {
         public String toString() {
            return key;
         }
      });
   }

   /**
    * Gets the type of villager for the given biome.
    */
   static IVillagerType byBiome(Biome biomeIn) {
      return BY_BIOME.getOrDefault(biomeIn, PLAINS);
   }
}