package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;

public class TwiceSurfaceWithNoise extends Placement<NoiseDependant> {
   public TwiceSurfaceWithNoise(Function<Dynamic<?>, ? extends NoiseDependant> p_i51364_1_) {
      super(p_i51364_1_);
   }

   public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generatorIn, Random random, NoiseDependant configIn, BlockPos pos) {
      double d0 = Biome.INFO_NOISE.noiseAt((double)pos.getX() / 200.0D, (double)pos.getZ() / 200.0D, false);
      int i = d0 < configIn.noiseLevel ? configIn.belowNoise : configIn.aboveNoise;
      return IntStream.range(0, i).mapToObj((p_227450_3_) -> {
         int j = random.nextInt(16) + pos.getX();
         int k = random.nextInt(16) + pos.getZ();
         int l = worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING, j, k) * 2;
         return l <= 0 ? null : new BlockPos(j, random.nextInt(l), k);
      }).filter(Objects::nonNull);
   }
}