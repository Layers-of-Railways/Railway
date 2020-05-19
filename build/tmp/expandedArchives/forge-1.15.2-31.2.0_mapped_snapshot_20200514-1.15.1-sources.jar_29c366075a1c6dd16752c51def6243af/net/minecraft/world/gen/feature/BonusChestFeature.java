package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.storage.loot.LootTables;

public class BonusChestFeature extends Feature<NoFeatureConfig> {
   public BonusChestFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49911_1_) {
      super(p_i49911_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
      ChunkPos chunkpos = new ChunkPos(pos);
      List<Integer> list = IntStream.rangeClosed(chunkpos.getXStart(), chunkpos.getXEnd()).boxed().collect(Collectors.toList());
      Collections.shuffle(list, rand);
      List<Integer> list1 = IntStream.rangeClosed(chunkpos.getZStart(), chunkpos.getZEnd()).boxed().collect(Collectors.toList());
      Collections.shuffle(list1, rand);
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(Integer integer : list) {
         for(Integer integer1 : list1) {
            blockpos$mutable.setPos(integer, 0, integer1);
            BlockPos blockpos = worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable);
            if (worldIn.isAirBlock(blockpos) || worldIn.getBlockState(blockpos).getCollisionShape(worldIn, blockpos).isEmpty()) {
               worldIn.setBlockState(blockpos, Blocks.CHEST.getDefaultState(), 2);
               LockableLootTileEntity.setLootTable(worldIn, rand, blockpos, LootTables.CHESTS_SPAWN_BONUS_CHEST);
               BlockState blockstate = Blocks.TORCH.getDefaultState();

               for(Direction direction : Direction.Plane.HORIZONTAL) {
                  BlockPos blockpos1 = blockpos.offset(direction);
                  if (blockstate.isValidPosition(worldIn, blockpos1)) {
                     worldIn.setBlockState(blockpos1, blockstate, 2);
                  }
               }

               return true;
            }
         }
      }

      return false;
   }
}