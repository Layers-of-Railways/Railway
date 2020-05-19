package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class EndIslandFeature extends Feature<NoFeatureConfig> {
   public EndIslandFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49880_1_) {
      super(p_i49880_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
      float f = (float)(rand.nextInt(3) + 4);

      for(int i = 0; f > 0.5F; --i) {
         for(int j = MathHelper.floor(-f); j <= MathHelper.ceil(f); ++j) {
            for(int k = MathHelper.floor(-f); k <= MathHelper.ceil(f); ++k) {
               if ((float)(j * j + k * k) <= (f + 1.0F) * (f + 1.0F)) {
                  this.setBlockState(worldIn, pos.add(j, i, k), Blocks.END_STONE.getDefaultState());
               }
            }
         }

         f = (float)((double)f - ((double)rand.nextInt(2) + 0.5D));
      }

      return true;
   }
}