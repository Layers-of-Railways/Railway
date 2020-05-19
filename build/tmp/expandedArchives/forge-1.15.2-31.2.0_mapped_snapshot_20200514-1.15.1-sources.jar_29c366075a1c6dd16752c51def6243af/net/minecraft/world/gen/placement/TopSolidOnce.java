package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;

public class TopSolidOnce extends Placement<NoPlacementConfig> {
   public TopSolidOnce(Function<Dynamic<?>, ? extends NoPlacementConfig> p_i51361_1_) {
      super(p_i51361_1_);
   }

   public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generatorIn, Random random, NoPlacementConfig configIn, BlockPos pos) {
      int i = random.nextInt(16) + pos.getX();
      int j = random.nextInt(16) + pos.getZ();
      int k = worldIn.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, i, j);
      return Stream.of(new BlockPos(i, k, j));
   }
}