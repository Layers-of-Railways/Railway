package net.minecraft.world.gen.feature.structure;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class ShipwreckStructure extends ScatteredStructure<ShipwreckConfig> {
   public ShipwreckStructure(Function<Dynamic<?>, ? extends ShipwreckConfig> p_i51440_1_) {
      super(p_i51440_1_);
   }

   public String getStructureName() {
      return "Shipwreck";
   }

   public int getSize() {
      return 3;
   }

   public Structure.IStartFactory getStartFactory() {
      return ShipwreckStructure.Start::new;
   }

   protected int getSeedModifier() {
      return 165745295;
   }

   protected int getBiomeFeatureDistance(ChunkGenerator<?> chunkGenerator) {
      return chunkGenerator.getSettings().getShipwreckDistance();
   }

   protected int getBiomeFeatureSeparation(ChunkGenerator<?> chunkGenerator) {
      return chunkGenerator.getSettings().getShipwreckSeparation();
   }

   public static class Start extends StructureStart {
      public Start(Structure<?> p_i225817_1_, int p_i225817_2_, int p_i225817_3_, MutableBoundingBox p_i225817_4_, int p_i225817_5_, long p_i225817_6_) {
         super(p_i225817_1_, p_i225817_2_, p_i225817_3_, p_i225817_4_, p_i225817_5_, p_i225817_6_);
      }

      public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {
         ShipwreckConfig shipwreckconfig = (ShipwreckConfig)generator.getStructureConfig(biomeIn, Feature.SHIPWRECK);
         Rotation rotation = Rotation.values()[this.rand.nextInt(Rotation.values().length)];
         BlockPos blockpos = new BlockPos(chunkX * 16, 90, chunkZ * 16);
         ShipwreckPieces.func_204760_a(templateManagerIn, blockpos, rotation, this.components, this.rand, shipwreckconfig);
         this.recalculateStructureSize();
      }
   }
}