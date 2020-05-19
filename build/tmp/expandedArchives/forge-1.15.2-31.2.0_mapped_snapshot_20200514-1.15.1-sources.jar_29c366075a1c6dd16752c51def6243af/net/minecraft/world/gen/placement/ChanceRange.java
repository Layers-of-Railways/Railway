package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class ChanceRange extends SimplePlacement<ChanceRangeConfig> {
   public ChanceRange(Function<Dynamic<?>, ? extends ChanceRangeConfig> p_i51358_1_) {
      super(p_i51358_1_);
   }

   public Stream<BlockPos> getPositions(Random random, ChanceRangeConfig p_212852_2_, BlockPos pos) {
      if (random.nextFloat() < p_212852_2_.chance) {
         int i = random.nextInt(16) + pos.getX();
         int j = random.nextInt(16) + pos.getZ();
         int k = random.nextInt(p_212852_2_.top - p_212852_2_.topOffset) + p_212852_2_.bottomOffset;
         return Stream.of(new BlockPos(i, k, j));
      } else {
         return Stream.empty();
      }
   }
}