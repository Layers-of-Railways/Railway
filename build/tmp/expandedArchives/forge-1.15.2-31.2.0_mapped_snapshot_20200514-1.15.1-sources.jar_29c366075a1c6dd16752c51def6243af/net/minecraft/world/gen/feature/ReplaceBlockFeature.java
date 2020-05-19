package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class ReplaceBlockFeature extends Feature<ReplaceBlockConfig> {
   public ReplaceBlockFeature(Function<Dynamic<?>, ? extends ReplaceBlockConfig> p_i51444_1_) {
      super(p_i51444_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, ReplaceBlockConfig config) {
      if (worldIn.getBlockState(pos).getBlock() == config.target.getBlock()) {
         worldIn.setBlockState(pos, config.state, 2);
      }

      return true;
   }
}