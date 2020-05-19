package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class FortressStructure extends Structure<NoFeatureConfig> {
   private static final List<Biome.SpawnListEntry> field_202381_d = Lists.newArrayList(new Biome.SpawnListEntry(EntityType.BLAZE, 10, 2, 3), new Biome.SpawnListEntry(EntityType.ZOMBIE_PIGMAN, 5, 4, 4), new Biome.SpawnListEntry(EntityType.WITHER_SKELETON, 8, 5, 5), new Biome.SpawnListEntry(EntityType.SKELETON, 2, 5, 5), new Biome.SpawnListEntry(EntityType.MAGMA_CUBE, 3, 4, 4));

   public FortressStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51476_1_) {
      super(p_i51476_1_);
   }

   /**
    * decide whether the Structure can be generated
    */
   public boolean canBeGenerated(BiomeManager biomeManagerIn, ChunkGenerator<?> generatorIn, Random randIn, int chunkX, int chunkZ, Biome biomeIn) {
      int i = chunkX >> 4;
      int j = chunkZ >> 4;
      randIn.setSeed((long)(i ^ j << 4) ^ generatorIn.getSeed());
      randIn.nextInt();
      if (randIn.nextInt(3) != 0) {
         return false;
      } else if (chunkX != (i << 4) + 4 + randIn.nextInt(8)) {
         return false;
      } else {
         return chunkZ != (j << 4) + 4 + randIn.nextInt(8) ? false : generatorIn.hasStructure(biomeIn, this);
      }
   }

   public Structure.IStartFactory getStartFactory() {
      return FortressStructure.Start::new;
   }

   public String getStructureName() {
      return "Fortress";
   }

   public int getSize() {
      return 8;
   }

   public List<Biome.SpawnListEntry> getSpawnList() {
      return field_202381_d;
   }

   public static class Start extends StructureStart {
      public Start(Structure<?> p_i225812_1_, int p_i225812_2_, int p_i225812_3_, MutableBoundingBox p_i225812_4_, int p_i225812_5_, long p_i225812_6_) {
         super(p_i225812_1_, p_i225812_2_, p_i225812_3_, p_i225812_4_, p_i225812_5_, p_i225812_6_);
      }

      public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {
         FortressPieces.Start fortresspieces$start = new FortressPieces.Start(this.rand, (chunkX << 4) + 2, (chunkZ << 4) + 2);
         this.components.add(fortresspieces$start);
         fortresspieces$start.buildComponent(fortresspieces$start, this.components, this.rand);
         List<StructurePiece> list = fortresspieces$start.pendingChildren;

         while(!list.isEmpty()) {
            int i = this.rand.nextInt(list.size());
            StructurePiece structurepiece = list.remove(i);
            structurepiece.buildComponent(fortresspieces$start, this.components, this.rand);
         }

         this.recalculateStructureSize();
         this.func_214626_a(this.rand, 48, 70);
      }
   }
}