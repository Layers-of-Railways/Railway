package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public abstract class SimplePlacement<DC extends IPlacementConfig> extends Placement<DC> {
   public SimplePlacement(Function<Dynamic<?>, ? extends DC> p_i51362_1_) {
      super(p_i51362_1_);
   }

   public final Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generatorIn, Random random, DC configIn, BlockPos pos) {
      return this.getPositions(random, configIn, pos);
   }

   protected abstract Stream<BlockPos> getPositions(Random random, DC p_212852_2_, BlockPos pos);
}