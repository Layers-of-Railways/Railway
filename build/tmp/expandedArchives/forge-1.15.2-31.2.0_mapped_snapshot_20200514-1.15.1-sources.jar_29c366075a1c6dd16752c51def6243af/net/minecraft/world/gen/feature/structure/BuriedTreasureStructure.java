package net.minecraft.world.gen.feature.structure;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class BuriedTreasureStructure extends Structure<BuriedTreasureConfig> {
   public BuriedTreasureStructure(Function<Dynamic<?>, ? extends BuriedTreasureConfig> p_i49910_1_) {
      super(p_i49910_1_);
   }

   /**
    * decide whether the Structure can be generated
    */
   public boolean canBeGenerated(BiomeManager biomeManagerIn, ChunkGenerator<?> generatorIn, Random randIn, int chunkX, int chunkZ, Biome biomeIn) {
      if (generatorIn.hasStructure(biomeIn, this)) {
         ((SharedSeedRandom)randIn).setLargeFeatureSeedWithSalt(generatorIn.getSeed(), chunkX, chunkZ, 10387320);
         BuriedTreasureConfig buriedtreasureconfig = (BuriedTreasureConfig)generatorIn.getStructureConfig(biomeIn, this);
         return randIn.nextFloat() < buriedtreasureconfig.probability;
      } else {
         return false;
      }
   }

   public Structure.IStartFactory getStartFactory() {
      return BuriedTreasureStructure.Start::new;
   }

   public String getStructureName() {
      return "Buried_Treasure";
   }

   public int getSize() {
      return 1;
   }

   public static class Start extends StructureStart {
      public Start(Structure<?> p_i225799_1_, int p_i225799_2_, int p_i225799_3_, MutableBoundingBox p_i225799_4_, int p_i225799_5_, long p_i225799_6_) {
         super(p_i225799_1_, p_i225799_2_, p_i225799_3_, p_i225799_4_, p_i225799_5_, p_i225799_6_);
      }

      public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {
         int i = chunkX * 16;
         int j = chunkZ * 16;
         BlockPos blockpos = new BlockPos(i + 9, 90, j + 9);
         this.components.add(new BuriedTreasure.Piece(blockpos));
         this.recalculateStructureSize();
      }

      public BlockPos getPos() {
         return new BlockPos((this.getChunkPosX() << 4) + 9, 0, (this.getChunkPosZ() << 4) + 9);
      }
   }
}