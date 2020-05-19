package net.minecraft.world.biome;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraft.world.level.ColorResolver;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BiomeColors {
   public static final ColorResolver GRASS_COLOR = Biome::getGrassColor;
   public static final ColorResolver FOLIAGE_COLOR = (p_228362_0_, p_228362_1_, p_228362_3_) -> {
      return p_228362_0_.getFoliageColor();
   };
   public static final ColorResolver WATER_COLOR = (p_228360_0_, p_228360_1_, p_228360_3_) -> {
      return p_228360_0_.getWaterColor();
   };

   private static int func_228359_a_(ILightReader worldIn, BlockPos blockPosIn, ColorResolver colorResolverIn) {
      return worldIn.getBlockColor(blockPosIn, colorResolverIn);
   }

   public static int getGrassColor(ILightReader worldIn, BlockPos blockPosIn) {
      return func_228359_a_(worldIn, blockPosIn, GRASS_COLOR);
   }

   public static int getFoliageColor(ILightReader worldIn, BlockPos blockPosIn) {
      return func_228359_a_(worldIn, blockPosIn, FOLIAGE_COLOR);
   }

   public static int getWaterColor(ILightReader worldIn, BlockPos blockPosIn) {
      return func_228359_a_(worldIn, blockPosIn, WATER_COLOR);
   }
}