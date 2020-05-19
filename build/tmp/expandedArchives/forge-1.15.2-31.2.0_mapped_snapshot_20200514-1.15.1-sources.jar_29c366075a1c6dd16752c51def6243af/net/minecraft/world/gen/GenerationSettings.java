package net.minecraft.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class GenerationSettings {
   protected int villageDistance = 32;
   protected final int villageSeparation = 8;
   protected int oceanMonumentSpacing = 32;
   protected int oceanMonumentSeparation = 5;
   protected int strongholdDistance = 32;
   protected int strongholdCount = 128;
   protected int strongholdSpread = 3;
   protected int biomeFeatureDistance = 32;
   protected final int field_214979_i = 8;
   protected final int field_214980_j = 16;
   protected final int field_214981_k = 8;
   protected int endCityDistance = 20;
   protected final int endCitySeparation = 11;
   protected final int field_214984_n = 16;
   protected final int field_214985_o = 8;
   protected int mansionDistance = 80;
   protected final int mansionSeparation = 20;
   protected BlockState defaultBlock = Blocks.STONE.getDefaultState();
   protected BlockState defaultFluid = Blocks.WATER.getDefaultState();

   public int getVillageDistance() {
      return this.villageDistance;
   }

   public int getVillageSeparation() {
      return 8;
   }

   public int getOceanMonumentSpacing() {
      return this.oceanMonumentSpacing;
   }

   public int getOceanMonumentSeparation() {
      return this.oceanMonumentSeparation;
   }

   public int getStrongholdDistance() {
      return this.strongholdDistance;
   }

   public int getStrongholdCount() {
      return this.strongholdCount;
   }

   public int getStrongholdSpread() {
      return this.strongholdSpread;
   }

   public int getBiomeFeatureDistance() {
      return this.biomeFeatureDistance;
   }

   public int getBiomeFeatureSeparation() {
      return 8;
   }

   public int getShipwreckDistance() {
      return 16;
   }

   public int getShipwreckSeparation() {
      return 8;
   }

   public int getOceanRuinDistance() {
      return 16;
   }

   public int getOceanRuinSeparation() {
      return 8;
   }

   public int getEndCityDistance() {
      return this.endCityDistance;
   }

   public int getEndCitySeparation() {
      return 11;
   }

   public int getMansionDistance() {
      return this.mansionDistance;
   }

   public int getMansionSeparation() {
      return 20;
   }

   public BlockState getDefaultBlock() {
      return this.defaultBlock;
   }

   public BlockState getDefaultFluid() {
      return this.defaultFluid;
   }

   public void setDefaultBlock(BlockState p_214969_1_) {
      this.defaultBlock = p_214969_1_;
   }

   public void setDefaultFluid(BlockState p_214970_1_) {
      this.defaultFluid = p_214970_1_;
   }

   public int getBedrockRoofHeight() {
      return 0;
   }

   public int getBedrockFloorHeight() {
      return 256;
   }
}