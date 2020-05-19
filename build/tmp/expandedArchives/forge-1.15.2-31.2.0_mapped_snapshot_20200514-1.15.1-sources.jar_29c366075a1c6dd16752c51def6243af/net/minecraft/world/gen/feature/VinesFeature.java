package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class VinesFeature extends Feature<NoFeatureConfig> {
   private static final Direction[] DIRECTIONS = Direction.values();

   public VinesFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51418_1_) {
      super(p_i51418_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(pos);

      for(int i = pos.getY(); i < worldIn.getWorld().getDimension().getHeight(); ++i) {
         blockpos$mutable.setPos(pos);
         blockpos$mutable.move(rand.nextInt(4) - rand.nextInt(4), 0, rand.nextInt(4) - rand.nextInt(4));
         blockpos$mutable.setY(i);
         if (worldIn.isAirBlock(blockpos$mutable)) {
            for(Direction direction : DIRECTIONS) {
               if (direction != Direction.DOWN && VineBlock.canAttachTo(worldIn, blockpos$mutable, direction)) {
                  worldIn.setBlockState(blockpos$mutable, Blocks.VINE.getDefaultState().with(VineBlock.getPropertyFor(direction), Boolean.valueOf(true)), 2);
                  break;
               }
            }
         }
      }

      return true;
   }
}