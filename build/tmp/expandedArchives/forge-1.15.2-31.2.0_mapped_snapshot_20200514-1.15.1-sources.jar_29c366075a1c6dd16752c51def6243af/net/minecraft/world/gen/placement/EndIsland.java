package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class EndIsland extends SimplePlacement<NoPlacementConfig> {
   public EndIsland(Function<Dynamic<?>, ? extends NoPlacementConfig> p_i51372_1_) {
      super(p_i51372_1_);
   }

   public Stream<BlockPos> getPositions(Random random, NoPlacementConfig p_212852_2_, BlockPos pos) {
      Stream<BlockPos> stream = Stream.empty();
      if (random.nextInt(14) == 0) {
         stream = Stream.concat(stream, Stream.of(pos.add(random.nextInt(16), 55 + random.nextInt(16), random.nextInt(16))));
         if (random.nextInt(4) == 0) {
            stream = Stream.concat(stream, Stream.of(pos.add(random.nextInt(16), 55 + random.nextInt(16), random.nextInt(16))));
         }

         return stream;
      } else {
         return Stream.empty();
      }
   }
}