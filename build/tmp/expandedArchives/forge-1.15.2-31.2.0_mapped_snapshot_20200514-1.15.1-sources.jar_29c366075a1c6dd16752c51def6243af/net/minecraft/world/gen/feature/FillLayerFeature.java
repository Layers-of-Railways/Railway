package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class FillLayerFeature extends Feature<FillLayerConfig> {
   public FillLayerFeature(Function<Dynamic<?>, ? extends FillLayerConfig> p_i49877_1_) {
      super(p_i49877_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, FillLayerConfig config) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int i = 0; i < 16; ++i) {
         for(int j = 0; j < 16; ++j) {
            int k = pos.getX() + i;
            int l = pos.getZ() + j;
            int i1 = config.height;
            blockpos$mutable.setPos(k, i1, l);
            if (worldIn.getBlockState(blockpos$mutable).isAir()) {
               worldIn.setBlockState(blockpos$mutable, config.state, 2);
            }
         }
      }

      return true;
   }
}