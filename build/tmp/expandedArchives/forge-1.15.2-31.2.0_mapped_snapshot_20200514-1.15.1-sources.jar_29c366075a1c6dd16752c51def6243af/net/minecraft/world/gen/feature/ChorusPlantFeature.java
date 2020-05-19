package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChorusFlowerBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class ChorusPlantFeature extends Feature<NoFeatureConfig> {
   public ChorusPlantFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactoryIn) {
      super(configFactoryIn);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
      if (worldIn.isAirBlock(pos.up()) && worldIn.getBlockState(pos).getBlock() == Blocks.END_STONE) {
         ChorusFlowerBlock.generatePlant(worldIn, pos.up(), rand, 8);
         return true;
      } else {
         return false;
      }
   }
}