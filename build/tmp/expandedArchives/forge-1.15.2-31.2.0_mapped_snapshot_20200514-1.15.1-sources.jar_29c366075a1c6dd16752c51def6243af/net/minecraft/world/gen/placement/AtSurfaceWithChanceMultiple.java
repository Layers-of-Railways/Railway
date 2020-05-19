package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;

public class AtSurfaceWithChanceMultiple extends Placement<HeightWithChanceConfig> {
   public AtSurfaceWithChanceMultiple(Function<Dynamic<?>, ? extends HeightWithChanceConfig> p_i51387_1_) {
      super(p_i51387_1_);
   }

   public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generatorIn, Random random, HeightWithChanceConfig configIn, BlockPos pos) {
      return IntStream.range(0, configIn.count).filter((p_215043_2_) -> {
         return random.nextFloat() < configIn.chance;
      }).mapToObj((p_227437_3_) -> {
         int i = random.nextInt(16) + pos.getX();
         int j = random.nextInt(16) + pos.getZ();
         int k = worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING, i, j);
         return new BlockPos(i, k, j);
      });
   }
}