package net.minecraft.world.gen.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum EdgeBiomeLayer implements ICastleTransformer {
   INSTANCE;

   private static final int DESERT = Registry.BIOME.getId(Biomes.DESERT);
   private static final int MOUNTAINS = Registry.BIOME.getId(Biomes.MOUNTAINS);
   private static final int WOODED_MOUNTAINS = Registry.BIOME.getId(Biomes.WOODED_MOUNTAINS);
   private static final int SNOWY_TUNDRA = Registry.BIOME.getId(Biomes.SNOWY_TUNDRA);
   private static final int JUNGLE = Registry.BIOME.getId(Biomes.JUNGLE);
   private static final int BAMBOO_JUNGLE = Registry.BIOME.getId(Biomes.BAMBOO_JUNGLE);
   private static final int JUNGLE_EDGE = Registry.BIOME.getId(Biomes.JUNGLE_EDGE);
   private static final int BADLANDS = Registry.BIOME.getId(Biomes.BADLANDS);
   private static final int BADLANDS_PLATEAU = Registry.BIOME.getId(Biomes.BADLANDS_PLATEAU);
   private static final int WOODED_BADLANDS_PLATEAU = Registry.BIOME.getId(Biomes.WOODED_BADLANDS_PLATEAU);
   private static final int PLAINS = Registry.BIOME.getId(Biomes.PLAINS);
   private static final int GIANT_TREE_TAIGA = Registry.BIOME.getId(Biomes.GIANT_TREE_TAIGA);
   private static final int MOUNTAIN_EDGE = Registry.BIOME.getId(Biomes.MOUNTAIN_EDGE);
   private static final int SWAMP = Registry.BIOME.getId(Biomes.SWAMP);
   private static final int TAIGA = Registry.BIOME.getId(Biomes.TAIGA);
   private static final int SNOWY_TAIGA = Registry.BIOME.getId(Biomes.SNOWY_TAIGA);

   public int apply(INoiseRandom context, int north, int west, int south, int east, int center) {
      int[] aint = new int[1];
      if (!this.func_202751_a(aint, north, west, south, east, center, MOUNTAINS, MOUNTAIN_EDGE) && !this.replaceBiomeEdge(aint, north, west, south, east, center, WOODED_BADLANDS_PLATEAU, BADLANDS) && !this.replaceBiomeEdge(aint, north, west, south, east, center, BADLANDS_PLATEAU, BADLANDS) && !this.replaceBiomeEdge(aint, north, west, south, east, center, GIANT_TREE_TAIGA, TAIGA)) {
         if (center != DESERT || north != SNOWY_TUNDRA && west != SNOWY_TUNDRA && east != SNOWY_TUNDRA && south != SNOWY_TUNDRA) {
            if (center == SWAMP) {
               if (north == DESERT || west == DESERT || east == DESERT || south == DESERT || north == SNOWY_TAIGA || west == SNOWY_TAIGA || east == SNOWY_TAIGA || south == SNOWY_TAIGA || north == SNOWY_TUNDRA || west == SNOWY_TUNDRA || east == SNOWY_TUNDRA || south == SNOWY_TUNDRA) {
                  return PLAINS;
               }

               if (north == JUNGLE || south == JUNGLE || west == JUNGLE || east == JUNGLE || north == BAMBOO_JUNGLE || south == BAMBOO_JUNGLE || west == BAMBOO_JUNGLE || east == BAMBOO_JUNGLE) {
                  return JUNGLE_EDGE;
               }
            }

            return center;
         } else {
            return WOODED_MOUNTAINS;
         }
      } else {
         return aint[0];
      }
   }

   private boolean func_202751_a(int[] p_202751_1_, int p_202751_2_, int p_202751_3_, int p_202751_4_, int p_202751_5_, int p_202751_6_, int p_202751_7_, int p_202751_8_) {
      if (!LayerUtil.areBiomesSimilar(p_202751_6_, p_202751_7_)) {
         return false;
      } else {
         if (this.canBiomesBeNeighbors(p_202751_2_, p_202751_7_) && this.canBiomesBeNeighbors(p_202751_3_, p_202751_7_) && this.canBiomesBeNeighbors(p_202751_5_, p_202751_7_) && this.canBiomesBeNeighbors(p_202751_4_, p_202751_7_)) {
            p_202751_1_[0] = p_202751_6_;
         } else {
            p_202751_1_[0] = p_202751_8_;
         }

         return true;
      }
   }

   /**
    * Creates a border around a biome.
    */
   private boolean replaceBiomeEdge(int[] p_151635_1_, int p_151635_2_, int p_151635_3_, int p_151635_4_, int p_151635_5_, int p_151635_6_, int p_151635_7_, int p_151635_8_) {
      if (p_151635_6_ != p_151635_7_) {
         return false;
      } else {
         if (LayerUtil.areBiomesSimilar(p_151635_2_, p_151635_7_) && LayerUtil.areBiomesSimilar(p_151635_3_, p_151635_7_) && LayerUtil.areBiomesSimilar(p_151635_5_, p_151635_7_) && LayerUtil.areBiomesSimilar(p_151635_4_, p_151635_7_)) {
            p_151635_1_[0] = p_151635_6_;
         } else {
            p_151635_1_[0] = p_151635_8_;
         }

         return true;
      }
   }

   /**
    * Returns if two biomes can logically be neighbors. If one is hot and the other cold, for example, it returns false.
    */
   private boolean canBiomesBeNeighbors(int p_151634_1_, int p_151634_2_) {
      if (LayerUtil.areBiomesSimilar(p_151634_1_, p_151634_2_)) {
         return true;
      } else {
         Biome biome = Registry.BIOME.getByValue(p_151634_1_);
         Biome biome1 = Registry.BIOME.getByValue(p_151634_2_);
         if (biome != null && biome1 != null) {
            Biome.TempCategory biome$tempcategory = biome.getTempCategory();
            Biome.TempCategory biome$tempcategory1 = biome1.getTempCategory();
            return biome$tempcategory == biome$tempcategory1 || biome$tempcategory == Biome.TempCategory.MEDIUM || biome$tempcategory1 == Biome.TempCategory.MEDIUM;
         } else {
            return false;
         }
      }
   }
}