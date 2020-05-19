package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class BlockWithContextFeature extends Feature<BlockWithContextConfig> {
   public BlockWithContextFeature(Function<Dynamic<?>, ? extends BlockWithContextConfig> p_i51438_1_) {
      super(p_i51438_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, BlockWithContextConfig config) {
      if (config.placeOn.contains(worldIn.getBlockState(pos.down())) && config.placeIn.contains(worldIn.getBlockState(pos)) && config.placeUnder.contains(worldIn.getBlockState(pos.up()))) {
         worldIn.setBlockState(pos, config.toPlace, 2);
         return true;
      } else {
         return false;
      }
   }
}