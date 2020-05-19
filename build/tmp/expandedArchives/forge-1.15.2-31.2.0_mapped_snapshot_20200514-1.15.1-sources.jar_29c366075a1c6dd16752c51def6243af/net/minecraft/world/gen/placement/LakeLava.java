package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class LakeLava extends Placement<ChanceConfig> {
   public LakeLava(Function<Dynamic<?>, ? extends ChanceConfig> p_i51368_1_) {
      super(p_i51368_1_);
   }

   public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generatorIn, Random random, ChanceConfig configIn, BlockPos pos) {
      if (random.nextInt(configIn.chance / 10) == 0) {
         int i = random.nextInt(16) + pos.getX();
         int j = random.nextInt(16) + pos.getZ();
         int k = random.nextInt(random.nextInt(generatorIn.getMaxHeight() - 8) + 8);
         if (k < worldIn.getSeaLevel() || random.nextInt(configIn.chance / 8) == 0) {
            return Stream.of(new BlockPos(i, k, j));
         }
      }

      return Stream.empty();
   }
}