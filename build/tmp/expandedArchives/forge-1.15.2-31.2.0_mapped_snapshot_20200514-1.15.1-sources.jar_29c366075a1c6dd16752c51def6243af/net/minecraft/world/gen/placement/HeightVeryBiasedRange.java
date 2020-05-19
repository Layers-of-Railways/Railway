package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class HeightVeryBiasedRange extends SimplePlacement<CountRangeConfig> {
   public HeightVeryBiasedRange(Function<Dynamic<?>, ? extends CountRangeConfig> p_i51379_1_) {
      super(p_i51379_1_);
   }

   public Stream<BlockPos> getPositions(Random random, CountRangeConfig p_212852_2_, BlockPos pos) {
      return IntStream.range(0, p_212852_2_.count).mapToObj((p_227443_3_) -> {
         int i = random.nextInt(16) + pos.getX();
         int j = random.nextInt(16) + pos.getZ();
         int k = random.nextInt(random.nextInt(random.nextInt(p_212852_2_.maximum - p_212852_2_.topOffset) + p_212852_2_.bottomOffset) + p_212852_2_.bottomOffset);
         return new BlockPos(i, k, j);
      });
   }
}