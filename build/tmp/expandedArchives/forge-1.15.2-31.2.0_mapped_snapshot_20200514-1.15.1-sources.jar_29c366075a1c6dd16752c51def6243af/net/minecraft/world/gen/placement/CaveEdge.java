package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class CaveEdge extends Placement<CaveEdgeConfig> {
   public CaveEdge(Function<Dynamic<?>, ? extends CaveEdgeConfig> p_i51396_1_) {
      super(p_i51396_1_);
   }

   public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generatorIn, Random random, CaveEdgeConfig configIn, BlockPos pos) {
      IChunk ichunk = worldIn.getChunk(pos);
      ChunkPos chunkpos = ichunk.getPos();
      BitSet bitset = ichunk.getCarvingMask(configIn.step);
      return IntStream.range(0, bitset.length()).filter((p_215067_3_) -> {
         return bitset.get(p_215067_3_) && random.nextFloat() < configIn.probability;
      }).mapToObj((p_215068_1_) -> {
         int i = p_215068_1_ & 15;
         int j = p_215068_1_ >> 4 & 15;
         int k = p_215068_1_ >> 8;
         return new BlockPos(chunkpos.getXStart() + i, k, chunkpos.getZStart() + j);
      });
   }
}