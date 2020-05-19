package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class StrongholdStructure extends Structure<NoFeatureConfig> {
   private boolean ranBiomeCheck;
   private ChunkPos[] structureCoords;
   private final List<StructureStart> structureStarts = Lists.newArrayList();
   private long seed;

   public StrongholdStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51428_1_) {
      super(p_i51428_1_);
   }

   /**
    * decide whether the Structure can be generated
    */
   public boolean canBeGenerated(BiomeManager biomeManagerIn, ChunkGenerator<?> generatorIn, Random randIn, int chunkX, int chunkZ, Biome biomeIn) {
      if (this.seed != generatorIn.getSeed()) {
         this.resetData();
      }

      if (!this.ranBiomeCheck) {
         this.reinitializeData(generatorIn);
         this.ranBiomeCheck = true;
      }

      for(ChunkPos chunkpos : this.structureCoords) {
         if (chunkX == chunkpos.x && chunkZ == chunkpos.z) {
            return true;
         }
      }

      return false;
   }

   /**
    * Resets the current available data on the stronghold structure, since biome checks and existing structure
    * coordinates are needed to properly generate strongholds.
    */
   private void resetData() {
      this.ranBiomeCheck = false;
      this.structureCoords = null;
      this.structureStarts.clear();
   }

   public Structure.IStartFactory getStartFactory() {
      return StrongholdStructure.Start::new;
   }

   public String getStructureName() {
      return "Stronghold";
   }

   public int getSize() {
      return 8;
   }

   @Nullable
   public BlockPos findNearest(World worldIn, ChunkGenerator<? extends GenerationSettings> chunkGenerator, BlockPos pos, int radius, boolean skipExistingChunks) {
      if (!chunkGenerator.getBiomeProvider().hasStructure(this)) {
         return null;
      } else {
         if (this.seed != worldIn.getSeed()) {
            this.resetData();
         }

         if (!this.ranBiomeCheck) {
            this.reinitializeData(chunkGenerator);
            this.ranBiomeCheck = true;
         }

         BlockPos blockpos = null;
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
         double d0 = Double.MAX_VALUE;

         for(ChunkPos chunkpos : this.structureCoords) {
            blockpos$mutable.setPos((chunkpos.x << 4) + 8, 32, (chunkpos.z << 4) + 8);
            double d1 = blockpos$mutable.distanceSq(pos);
            if (blockpos == null) {
               blockpos = new BlockPos(blockpos$mutable);
               d0 = d1;
            } else if (d1 < d0) {
               blockpos = new BlockPos(blockpos$mutable);
               d0 = d1;
            }
         }

         return blockpos;
      }
   }

   /**
    * Re-initializes the stronghold information needed to generate strongholds. Due to the requirement to rely on seeds
    * and other settings provided by the chunk generator, each time the structure is used on a different seed, this can
    * be called multiple times during the game lifecycle.
    */
   private void reinitializeData(ChunkGenerator<?> generator) {
      this.seed = generator.getSeed();
      List<Biome> list = Lists.newArrayList();

      for(Biome biome : Registry.BIOME) {
         if (biome != null && generator.hasStructure(biome, this)) {
            list.add(biome);
         }
      }

      int i2 = generator.getSettings().getStrongholdDistance();
      int j2 = generator.getSettings().getStrongholdCount();
      int i = generator.getSettings().getStrongholdSpread();
      this.structureCoords = new ChunkPos[j2];
      int j = 0;

      for(StructureStart structurestart : this.structureStarts) {
         if (j < this.structureCoords.length) {
            this.structureCoords[j++] = new ChunkPos(structurestart.getChunkPosX(), structurestart.getChunkPosZ());
         }
      }

      Random random = new Random();
      random.setSeed(generator.getSeed());
      double d1 = random.nextDouble() * Math.PI * 2.0D;
      int k = j;
      if (j < this.structureCoords.length) {
         int l = 0;
         int i1 = 0;

         for(int j1 = 0; j1 < this.structureCoords.length; ++j1) {
            double d0 = (double)(4 * i2 + i2 * i1 * 6) + (random.nextDouble() - 0.5D) * (double)i2 * 2.5D;
            int k1 = (int)Math.round(Math.cos(d1) * d0);
            int l1 = (int)Math.round(Math.sin(d1) * d0);
            BlockPos blockpos = generator.getBiomeProvider().func_225531_a_((k1 << 4) + 8, generator.getSeaLevel(), (l1 << 4) + 8, 112, list, random);
            if (blockpos != null) {
               k1 = blockpos.getX() >> 4;
               l1 = blockpos.getZ() >> 4;
            }

            if (j1 >= k) {
               this.structureCoords[j1] = new ChunkPos(k1, l1);
            }

            d1 += (Math.PI * 2D) / (double)i;
            ++l;
            if (l == i) {
               ++i1;
               l = 0;
               i = i + 2 * i / (i1 + 1);
               i = Math.min(i, this.structureCoords.length - j1);
               d1 += random.nextDouble() * Math.PI * 2.0D;
            }
         }
      }

   }

   public static class Start extends StructureStart {
      public Start(Structure<?> p_i225818_1_, int p_i225818_2_, int p_i225818_3_, MutableBoundingBox p_i225818_4_, int p_i225818_5_, long p_i225818_6_) {
         super(p_i225818_1_, p_i225818_2_, p_i225818_3_, p_i225818_4_, p_i225818_5_, p_i225818_6_);
      }

      public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {
         int i = 0;
         long j = generator.getSeed();

         while(true) {
            this.components.clear();
            this.bounds = MutableBoundingBox.getNewBoundingBox();
            this.rand.setLargeFeatureSeed(j + (long)(i++), chunkX, chunkZ);
            StrongholdPieces.prepareStructurePieces();
            StrongholdPieces.Stairs2 strongholdpieces$stairs2 = new StrongholdPieces.Stairs2(this.rand, (chunkX << 4) + 2, (chunkZ << 4) + 2);
            this.components.add(strongholdpieces$stairs2);
            strongholdpieces$stairs2.buildComponent(strongholdpieces$stairs2, this.components, this.rand);
            List<StructurePiece> list = strongholdpieces$stairs2.pendingChildren;

            while(!list.isEmpty()) {
               int k = this.rand.nextInt(list.size());
               StructurePiece structurepiece = list.remove(k);
               structurepiece.buildComponent(strongholdpieces$stairs2, this.components, this.rand);
            }

            this.recalculateStructureSize();
            this.func_214628_a(generator.getSeaLevel(), this.rand, 10);
            if (!this.components.isEmpty() && strongholdpieces$stairs2.strongholdPortalRoom != null) {
               break;
            }
         }

         ((StrongholdStructure)this.getStructure()).structureStarts.add(this);
      }
   }
}