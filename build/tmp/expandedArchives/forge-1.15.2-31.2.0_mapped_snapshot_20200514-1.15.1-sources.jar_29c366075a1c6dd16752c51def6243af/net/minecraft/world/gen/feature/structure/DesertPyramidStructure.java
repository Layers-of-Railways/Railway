package net.minecraft.world.gen.feature.structure;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class DesertPyramidStructure extends ScatteredStructure<NoFeatureConfig> {
   public DesertPyramidStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49888_1_) {
      super(p_i49888_1_);
   }

   public String getStructureName() {
      return "Desert_Pyramid";
   }

   public int getSize() {
      return 3;
   }

   public Structure.IStartFactory getStartFactory() {
      return DesertPyramidStructure.Start::new;
   }

   protected int getSeedModifier() {
      return 14357617;
   }

   public static class Start extends StructureStart {
      public Start(Structure<?> p_i225801_1_, int p_i225801_2_, int p_i225801_3_, MutableBoundingBox p_i225801_4_, int p_i225801_5_, long p_i225801_6_) {
         super(p_i225801_1_, p_i225801_2_, p_i225801_3_, p_i225801_4_, p_i225801_5_, p_i225801_6_);
      }

      public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {
         DesertPyramidPiece desertpyramidpiece = new DesertPyramidPiece(this.rand, chunkX * 16, chunkZ * 16);
         this.components.add(desertpyramidpiece);
         this.recalculateStructureSize();
      }
   }
}