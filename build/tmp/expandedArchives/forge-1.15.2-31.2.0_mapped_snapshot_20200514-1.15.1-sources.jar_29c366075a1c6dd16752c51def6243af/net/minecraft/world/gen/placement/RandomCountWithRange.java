package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class RandomCountWithRange extends SimplePlacement<CountRangeConfig> {
   public RandomCountWithRange(Function<Dynamic<?>, ? extends CountRangeConfig> p_i51353_1_) {
      super(p_i51353_1_);
   }

   public Stream<BlockPos> getPositions(Random random, CountRangeConfig p_212852_2_, BlockPos pos) {
      int i = random.nextInt(Math.max(p_212852_2_.count, 1));
      return IntStream.range(0, i).mapToObj((p_227455_3_) -> {
         int j = random.nextInt(16) + pos.getX();
         int k = random.nextInt(16) + pos.getZ();
         int l = random.nextInt(p_212852_2_.maximum - p_212852_2_.topOffset) + p_212852_2_.bottomOffset;
         return new BlockPos(j, l, k);
      });
   }
}