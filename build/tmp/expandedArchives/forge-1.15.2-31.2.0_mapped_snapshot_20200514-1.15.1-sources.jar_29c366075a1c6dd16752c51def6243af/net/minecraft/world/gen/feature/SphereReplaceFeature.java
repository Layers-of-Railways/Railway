package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class SphereReplaceFeature extends Feature<SphereReplaceConfig> {
   public SphereReplaceFeature(Function<Dynamic<?>, ? extends SphereReplaceConfig> p_i49885_1_) {
      super(p_i49885_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, SphereReplaceConfig config) {
      if (!worldIn.getFluidState(pos).isTagged(FluidTags.WATER)) {
         return false;
      } else {
         int i = 0;
         int j = rand.nextInt(config.radius - 2) + 2;

         for(int k = pos.getX() - j; k <= pos.getX() + j; ++k) {
            for(int l = pos.getZ() - j; l <= pos.getZ() + j; ++l) {
               int i1 = k - pos.getX();
               int j1 = l - pos.getZ();
               if (i1 * i1 + j1 * j1 <= j * j) {
                  for(int k1 = pos.getY() - config.ySize; k1 <= pos.getY() + config.ySize; ++k1) {
                     BlockPos blockpos = new BlockPos(k, k1, l);
                     BlockState blockstate = worldIn.getBlockState(blockpos);

                     for(BlockState blockstate1 : config.targets) {
                        if (blockstate1.getBlock() == blockstate.getBlock()) {
                           worldIn.setBlockState(blockpos, config.state, 2);
                           ++i;
                           break;
                        }
                     }
                  }
               }
            }
         }

         return i > 0;
      }
   }
}