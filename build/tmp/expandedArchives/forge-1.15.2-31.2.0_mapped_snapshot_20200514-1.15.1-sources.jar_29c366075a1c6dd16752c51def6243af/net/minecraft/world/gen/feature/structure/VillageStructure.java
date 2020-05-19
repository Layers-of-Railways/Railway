package net.minecraft.world.gen.feature.structure;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class VillageStructure extends Structure<VillageConfig> {
   public VillageStructure(Function<Dynamic<?>, ? extends VillageConfig> p_i51419_1_) {
      super(p_i51419_1_);
   }

   protected ChunkPos getStartPositionForPosition(ChunkGenerator<?> chunkGenerator, Random random, int x, int z, int spacingOffsetsX, int spacingOffsetsZ) {
      int i = chunkGenerator.getSettings().getVillageDistance();
      int j = chunkGenerator.getSettings().getVillageSeparation();
      int k = x + i * spacingOffsetsX;
      int l = z + i * spacingOffsetsZ;
      int i1 = k < 0 ? k - i + 1 : k;
      int j1 = l < 0 ? l - i + 1 : l;
      int k1 = i1 / i;
      int l1 = j1 / i;
      ((SharedSeedRandom)random).setLargeFeatureSeedWithSalt(chunkGenerator.getSeed(), k1, l1, 10387312);
      k1 = k1 * i;
      l1 = l1 * i;
      k1 = k1 + random.nextInt(i - j);
      l1 = l1 + random.nextInt(i - j);
      return new ChunkPos(k1, l1);
   }

   /**
    * decide whether the Structure can be generated
    */
   public boolean canBeGenerated(BiomeManager biomeManagerIn, ChunkGenerator<?> generatorIn, Random randIn, int chunkX, int chunkZ, Biome biomeIn) {
      ChunkPos chunkpos = this.getStartPositionForPosition(generatorIn, randIn, chunkX, chunkZ, 0, 0);
      return chunkX == chunkpos.x && chunkZ == chunkpos.z ? generatorIn.hasStructure(biomeIn, this) : false;
   }

   public Structure.IStartFactory getStartFactory() {
      return VillageStructure.Start::new;
   }

   public String getStructureName() {
      return "Village";
   }

   public int getSize() {
      return 8;
   }

   public static class Start extends MarginedStructureStart {
      public Start(Structure<?> p_i225821_1_, int p_i225821_2_, int p_i225821_3_, MutableBoundingBox p_i225821_4_, int p_i225821_5_, long p_i225821_6_) {
         super(p_i225821_1_, p_i225821_2_, p_i225821_3_, p_i225821_4_, p_i225821_5_, p_i225821_6_);
      }

      public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {
         VillageConfig villageconfig = (VillageConfig)generator.getStructureConfig(biomeIn, Feature.VILLAGE);
         BlockPos blockpos = new BlockPos(chunkX * 16, 0, chunkZ * 16);
         VillagePieces.addPieces(generator, templateManagerIn, blockpos, this.components, this.rand, villageconfig);
         this.recalculateStructureSize();
      }
   }
}