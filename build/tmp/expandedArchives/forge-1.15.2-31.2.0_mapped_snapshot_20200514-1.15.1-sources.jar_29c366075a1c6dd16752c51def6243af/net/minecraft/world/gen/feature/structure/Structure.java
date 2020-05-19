package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IStructureReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Structure<C extends IFeatureConfig> extends Feature<C> {
   private static final Logger LOGGER = LogManager.getLogger();

   public Structure(Function<Dynamic<?>, ? extends C> configFactoryIn) {
      super(configFactoryIn);
   }

   public ConfiguredFeature<C, ? extends Structure<C>> withConfiguration(C p_225566_1_) {
      return new ConfiguredFeature<>(this, p_225566_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, C config) {
      if (!worldIn.getWorldInfo().isMapFeaturesEnabled()) {
         return false;
      } else {
         int i = pos.getX() >> 4;
         int j = pos.getZ() >> 4;
         int k = i << 4;
         int l = j << 4;
         boolean flag = false;

         for(Long olong : worldIn.getChunk(i, j).getStructureReferences(this.getStructureName())) {
            ChunkPos chunkpos = new ChunkPos(olong);
            StructureStart structurestart = worldIn.getChunk(chunkpos.x, chunkpos.z).getStructureStart(this.getStructureName());
            if (structurestart != null && structurestart != StructureStart.DUMMY) {
               structurestart.generateStructure(worldIn, generator, rand, new MutableBoundingBox(k, l, k + 15, l + 15), new ChunkPos(i, j));
               flag = true;
            }
         }

         return flag;
      }
   }

   protected StructureStart getStart(IWorld worldIn, BlockPos pos, boolean p_202364_3_) {
      label35:
      for(StructureStart structurestart : this.getStarts(worldIn, pos.getX() >> 4, pos.getZ() >> 4)) {
         if (structurestart.isValid() && structurestart.getBoundingBox().isVecInside(pos)) {
            if (!p_202364_3_) {
               return structurestart;
            }

            Iterator iterator = structurestart.getComponents().iterator();

            while(true) {
               if (!iterator.hasNext()) {
                  continue label35;
               }

               StructurePiece structurepiece = (StructurePiece)iterator.next();
               if (structurepiece.getBoundingBox().isVecInside(pos)) {
                  break;
               }
            }

            return structurestart;
         }
      }

      return StructureStart.DUMMY;
   }

   public boolean isPositionInStructure(IWorld worldIn, BlockPos pos) {
      return this.getStart(worldIn, pos, false).isValid();
   }

   public boolean isPositionInsideStructure(IWorld worldIn, BlockPos pos) {
      return this.getStart(worldIn, pos, true).isValid();
   }

   @Nullable
   public BlockPos findNearest(World worldIn, ChunkGenerator<? extends GenerationSettings> chunkGenerator, BlockPos pos, int radius, boolean skipExistingChunks) {
      if (!chunkGenerator.getBiomeProvider().hasStructure(this)) {
         return null;
      } else {
         int i = pos.getX() >> 4;
         int j = pos.getZ() >> 4;
         int k = 0;

         for(SharedSeedRandom sharedseedrandom = new SharedSeedRandom(); k <= radius; ++k) {
            for(int l = -k; l <= k; ++l) {
               boolean flag = l == -k || l == k;

               for(int i1 = -k; i1 <= k; ++i1) {
                  boolean flag1 = i1 == -k || i1 == k;
                  if (flag || flag1) {
                     ChunkPos chunkpos = this.getStartPositionForPosition(chunkGenerator, sharedseedrandom, i, j, l, i1);
                     StructureStart structurestart = worldIn.getChunk(chunkpos.x, chunkpos.z, ChunkStatus.STRUCTURE_STARTS).getStructureStart(this.getStructureName());
                     if (structurestart != null && structurestart.isValid()) {
                        if (skipExistingChunks && structurestart.isRefCountBelowMax()) {
                           structurestart.incrementRefCount();
                           return structurestart.getPos();
                        }

                        if (!skipExistingChunks) {
                           return structurestart.getPos();
                        }
                     }

                     if (k == 0) {
                        break;
                     }
                  }
               }

               if (k == 0) {
                  break;
               }
            }
         }

         return null;
      }
   }

   private List<StructureStart> getStarts(IWorld worldIn, int x, int z) {
      List<StructureStart> list = Lists.newArrayList();
      IChunk ichunk = worldIn.getChunk(x, z, ChunkStatus.STRUCTURE_REFERENCES);
      LongIterator longiterator = ichunk.getStructureReferences(this.getStructureName()).iterator();

      while(longiterator.hasNext()) {
         long i = longiterator.nextLong();
         IStructureReader istructurereader = worldIn.getChunk(ChunkPos.getX(i), ChunkPos.getZ(i), ChunkStatus.STRUCTURE_STARTS);
         StructureStart structurestart = istructurereader.getStructureStart(this.getStructureName());
         if (structurestart != null) {
            list.add(structurestart);
         }
      }

      return list;
   }

   protected ChunkPos getStartPositionForPosition(ChunkGenerator<?> chunkGenerator, Random random, int x, int z, int spacingOffsetsX, int spacingOffsetsZ) {
      return new ChunkPos(x + spacingOffsetsX, z + spacingOffsetsZ);
   }

   /**
    * decide whether the Structure can be generated
    */
   public abstract boolean canBeGenerated(BiomeManager biomeManagerIn, ChunkGenerator<?> generatorIn, Random randIn, int chunkX, int chunkZ, Biome biomeIn);

   public abstract Structure.IStartFactory getStartFactory();

   public abstract String getStructureName();

   public abstract int getSize();

   public interface IStartFactory {
      StructureStart create(Structure<?> p_create_1_, int p_create_2_, int p_create_3_, MutableBoundingBox p_create_4_, int p_create_5_, long p_create_6_);
   }
}