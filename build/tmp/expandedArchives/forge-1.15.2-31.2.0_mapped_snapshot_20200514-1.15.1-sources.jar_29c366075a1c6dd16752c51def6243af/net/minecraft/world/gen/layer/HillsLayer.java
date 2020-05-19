package net.minecraft.world.gen.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset1Transformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum HillsLayer implements IAreaTransformer2, IDimOffset1Transformer {
   INSTANCE;

   private static final Logger LOGGER = LogManager.getLogger();
   private static final int BIRCH_FOREST = Registry.BIOME.getId(Biomes.BIRCH_FOREST);
   private static final int BIRCH_FOREST_HILLS = Registry.BIOME.getId(Biomes.BIRCH_FOREST_HILLS);
   private static final int DESERT = Registry.BIOME.getId(Biomes.DESERT);
   private static final int DESERT_HILLS = Registry.BIOME.getId(Biomes.DESERT_HILLS);
   private static final int MOUNTAINS = Registry.BIOME.getId(Biomes.MOUNTAINS);
   private static final int WOODED_MOUNTAINS = Registry.BIOME.getId(Biomes.WOODED_MOUNTAINS);
   private static final int FOREST = Registry.BIOME.getId(Biomes.FOREST);
   private static final int WOODED_HILLS = Registry.BIOME.getId(Biomes.WOODED_HILLS);
   private static final int SNOWY_TUNDRA = Registry.BIOME.getId(Biomes.SNOWY_TUNDRA);
   private static final int SNOWY_MOUNTAINS = Registry.BIOME.getId(Biomes.SNOWY_MOUNTAINS);
   private static final int JUNGLE = Registry.BIOME.getId(Biomes.JUNGLE);
   private static final int JUNGLE_HILLS = Registry.BIOME.getId(Biomes.JUNGLE_HILLS);
   private static final int BAMBOO_JUNGLE = Registry.BIOME.getId(Biomes.BAMBOO_JUNGLE);
   private static final int BAMBOO_JUNGLE_HILLS = Registry.BIOME.getId(Biomes.BAMBOO_JUNGLE_HILLS);
   private static final int BADLANDS = Registry.BIOME.getId(Biomes.BADLANDS);
   private static final int WOODED_BADLANDS_PLATEAU = Registry.BIOME.getId(Biomes.WOODED_BADLANDS_PLATEAU);
   private static final int PLAINS = Registry.BIOME.getId(Biomes.PLAINS);
   private static final int GIANT_TREE_TAIGA = Registry.BIOME.getId(Biomes.GIANT_TREE_TAIGA);
   private static final int GIANT_TREE_TAIGA_HILLS = Registry.BIOME.getId(Biomes.GIANT_TREE_TAIGA_HILLS);
   private static final int DARK_FOREST = Registry.BIOME.getId(Biomes.DARK_FOREST);
   private static final int SAVANNA = Registry.BIOME.getId(Biomes.SAVANNA);
   private static final int SAVANA_PLATEAU = Registry.BIOME.getId(Biomes.SAVANNA_PLATEAU);
   private static final int TAIGA = Registry.BIOME.getId(Biomes.TAIGA);
   private static final int SNOWY_TAIGA = Registry.BIOME.getId(Biomes.SNOWY_TAIGA);
   private static final int SNOWY_TAIGA_HILLS = Registry.BIOME.getId(Biomes.SNOWY_TAIGA_HILLS);
   private static final int TAIGA_HILLS = Registry.BIOME.getId(Biomes.TAIGA_HILLS);

   public int apply(INoiseRandom p_215723_1_, IArea p_215723_2_, IArea p_215723_3_, int p_215723_4_, int p_215723_5_) {
      int i = p_215723_2_.getValue(this.func_215721_a(p_215723_4_ + 1), this.func_215722_b(p_215723_5_ + 1));
      int j = p_215723_3_.getValue(this.func_215721_a(p_215723_4_ + 1), this.func_215722_b(p_215723_5_ + 1));
      if (i > 255) {
         LOGGER.debug("old! {}", (int)i);
      }

      int k = (j - 2) % 29;
      if (!LayerUtil.isShallowOcean(i) && j >= 2 && k == 1) {
         Biome biome = Registry.BIOME.getByValue(i);
         if (biome == null || !biome.isMutation()) {
            Biome biome2 = Biome.getMutationForBiome(biome);
            return biome2 == null ? i : Registry.BIOME.getId(biome2);
         }
      }

      if (p_215723_1_.random(3) == 0 || k == 0) {
         int l = i;
         Biome biome = Registry.BIOME.getByValue(i);
         Biome hill = biome == null ? null : biome.getHill(p_215723_1_);
         if (hill != null) l = Registry.BIOME.getId(hill);
         else if (i == DESERT) {
            l = DESERT_HILLS;
         } else if (i == FOREST) {
            l = WOODED_HILLS;
         } else if (i == BIRCH_FOREST) {
            l = BIRCH_FOREST_HILLS;
         } else if (i == DARK_FOREST) {
            l = PLAINS;
         } else if (i == TAIGA) {
            l = TAIGA_HILLS;
         } else if (i == GIANT_TREE_TAIGA) {
            l = GIANT_TREE_TAIGA_HILLS;
         } else if (i == SNOWY_TAIGA) {
            l = SNOWY_TAIGA_HILLS;
         } else if (i == PLAINS) {
            l = p_215723_1_.random(3) == 0 ? WOODED_HILLS : FOREST;
         } else if (i == SNOWY_TUNDRA) {
            l = SNOWY_MOUNTAINS;
         } else if (i == JUNGLE) {
            l = JUNGLE_HILLS;
         } else if (i == BAMBOO_JUNGLE) {
            l = BAMBOO_JUNGLE_HILLS;
         } else if (i == LayerUtil.OCEAN) {
            l = LayerUtil.DEEP_OCEAN;
         } else if (i == LayerUtil.LUKEWARM_OCEAN) {
            l = LayerUtil.DEEP_LUKEWARM_OCEAN;
         } else if (i == LayerUtil.COLD_OCEAN) {
            l = LayerUtil.DEEP_COLD_OCEAN;
         } else if (i == LayerUtil.FROZEN_OCEAN) {
            l = LayerUtil.DEEP_FROZEN_OCEAN;
         } else if (i == MOUNTAINS) {
            l = WOODED_MOUNTAINS;
         } else if (i == SAVANNA) {
            l = SAVANA_PLATEAU;
         } else if (LayerUtil.areBiomesSimilar(i, WOODED_BADLANDS_PLATEAU)) {
            l = BADLANDS;
         } else if ((i == LayerUtil.DEEP_OCEAN || i == LayerUtil.DEEP_LUKEWARM_OCEAN || i == LayerUtil.DEEP_COLD_OCEAN || i == LayerUtil.DEEP_FROZEN_OCEAN) && p_215723_1_.random(3) == 0) {
            l = p_215723_1_.random(2) == 0 ? PLAINS : FOREST;
         }

         if (k == 0 && l != i) {
            Biome biome1 = Biome.getMutationForBiome(Registry.BIOME.getByValue(l));
            l = biome1 == null ? i : Registry.BIOME.getId(biome1);
         }

         if (l != i) {
            int i1 = 0;
            if (LayerUtil.areBiomesSimilar(p_215723_2_.getValue(this.func_215721_a(p_215723_4_ + 1), this.func_215722_b(p_215723_5_ + 0)), i)) {
               ++i1;
            }

            if (LayerUtil.areBiomesSimilar(p_215723_2_.getValue(this.func_215721_a(p_215723_4_ + 2), this.func_215722_b(p_215723_5_ + 1)), i)) {
               ++i1;
            }

            if (LayerUtil.areBiomesSimilar(p_215723_2_.getValue(this.func_215721_a(p_215723_4_ + 0), this.func_215722_b(p_215723_5_ + 1)), i)) {
               ++i1;
            }

            if (LayerUtil.areBiomesSimilar(p_215723_2_.getValue(this.func_215721_a(p_215723_4_ + 1), this.func_215722_b(p_215723_5_ + 2)), i)) {
               ++i1;
            }

            if (i1 >= 3) {
               return l;
            }
         }
      }

      return i;
   }
}