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

public class TopSolidWithChance extends Placement<ChanceConfig> {
   public TopSolidWithChance(Function<Dynamic<?>, ? extends ChanceConfig> p_i51392_1_) {
      super(p_i51392_1_);
   }

   public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generatorIn, Random random, ChanceConfig configIn, BlockPos pos) {
      if (random.nextFloat() < 1.0F / (float)configIn.chance) {
         int i = random.nextInt(16) + pos.getX();
         int j = random.nextInt(16) + pos.getZ();
         int k = worldIn.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, i, j);
         return Stream.of(new BlockPos(i, k, j));
      } else {
         return Stream.empty();
      }
   }
}