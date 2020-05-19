package net.minecraft.world.gen.feature.structure;

import com.mojang.datafixers.Dynamic;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class OceanRuinStructure extends ScatteredStructure<OceanRuinConfig> {
   public OceanRuinStructure(Function<Dynamic<?>, ? extends OceanRuinConfig> p_i51348_1_) {
      super(p_i51348_1_);
   }

   public String getStructureName() {
      return "Ocean_Ruin";
   }

   public int getSize() {
      return 3;
   }

   protected int getBiomeFeatureDistance(ChunkGenerator<?> chunkGenerator) {
      return chunkGenerator.getSettings().getOceanRuinDistance();
   }

   protected int getBiomeFeatureSeparation(ChunkGenerator<?> chunkGenerator) {
      return chunkGenerator.getSettings().getOceanRuinSeparation();
   }

   public Structure.IStartFactory getStartFactory() {
      return OceanRuinStructure.Start::new;
   }

   protected int getSeedModifier() {
      return 14357621;
   }

   public static class Start extends StructureStart {
      public Start(Structure<?> p_i225875_1_, int p_i225875_2_, int p_i225875_3_, MutableBoundingBox p_i225875_4_, int p_i225875_5_, long p_i225875_6_) {
         super(p_i225875_1_, p_i225875_2_, p_i225875_3_, p_i225875_4_, p_i225875_5_, p_i225875_6_);
      }

      public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {
         OceanRuinConfig oceanruinconfig = (OceanRuinConfig)generator.getStructureConfig(biomeIn, Feature.OCEAN_RUIN);
         int i = chunkX * 16;
         int j = chunkZ * 16;
         BlockPos blockpos = new BlockPos(i, 90, j);
         Rotation rotation = Rotation.values()[this.rand.nextInt(Rotation.values().length)];
         OceanRuinPieces.func_204041_a(templateManagerIn, blockpos, rotation, this.components, this.rand, oceanruinconfig);
         this.recalculateStructureSize();
      }
   }

   public static enum Type {
      WARM("warm"),
      COLD("cold");

      private static final Map<String, OceanRuinStructure.Type> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(OceanRuinStructure.Type::getName, (p_215134_0_) -> {
         return p_215134_0_;
      }));
      private final String name;

      private Type(String nameIn) {
         this.name = nameIn;
      }

      public String getName() {
         return this.name;
      }

      public static OceanRuinStructure.Type getType(String nameIn) {
         return BY_NAME.get(nameIn);
      }
   }
}