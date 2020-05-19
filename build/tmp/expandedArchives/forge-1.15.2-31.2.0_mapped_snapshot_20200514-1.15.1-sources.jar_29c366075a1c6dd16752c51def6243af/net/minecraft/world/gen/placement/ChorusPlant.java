package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;

public class ChorusPlant extends Placement<NoPlacementConfig> {
   public ChorusPlant(Function<Dynamic<?>, ? extends NoPlacementConfig> p_i51391_1_) {
      super(p_i51391_1_);
   }

   public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generatorIn, Random random, NoPlacementConfig configIn, BlockPos pos) {
      int i = random.nextInt(5);
      return IntStream.range(0, i).mapToObj((p_227435_3_) -> {
         int j = random.nextInt(16) + pos.getX();
         int k = random.nextInt(16) + pos.getZ();
         int l = worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING, j, k);
         if (l > 0) {
            int i1 = l - 1;
            return new BlockPos(j, i1, k);
         } else {
            return null;
         }
      }).filter(Objects::nonNull);
   }
}