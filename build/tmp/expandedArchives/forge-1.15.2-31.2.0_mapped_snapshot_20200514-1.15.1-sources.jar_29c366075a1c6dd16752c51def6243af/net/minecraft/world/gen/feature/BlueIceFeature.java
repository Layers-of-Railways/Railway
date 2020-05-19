package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class BlueIceFeature extends Feature<NoFeatureConfig> {
   public BlueIceFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49912_1_) {
      super(p_i49912_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
      if (pos.getY() > worldIn.getSeaLevel() - 1) {
         return false;
      } else if (worldIn.getBlockState(pos).getBlock() != Blocks.WATER && worldIn.getBlockState(pos.down()).getBlock() != Blocks.WATER) {
         return false;
      } else {
         boolean flag = false;

         for(Direction direction : Direction.values()) {
            if (direction != Direction.DOWN && worldIn.getBlockState(pos.offset(direction)).getBlock() == Blocks.PACKED_ICE) {
               flag = true;
               break;
            }
         }

         if (!flag) {
            return false;
         } else {
            worldIn.setBlockState(pos, Blocks.BLUE_ICE.getDefaultState(), 2);

            for(int i = 0; i < 200; ++i) {
               int j = rand.nextInt(5) - rand.nextInt(6);
               int k = 3;
               if (j < 2) {
                  k += j / 2;
               }

               if (k >= 1) {
                  BlockPos blockpos = pos.add(rand.nextInt(k) - rand.nextInt(k), j, rand.nextInt(k) - rand.nextInt(k));
                  BlockState blockstate = worldIn.getBlockState(blockpos);
                  Block block = blockstate.getBlock();
                  if (blockstate.getMaterial() == Material.AIR || block == Blocks.WATER || block == Blocks.PACKED_ICE || block == Blocks.ICE) {
                     for(Direction direction1 : Direction.values()) {
                        Block block1 = worldIn.getBlockState(blockpos.offset(direction1)).getBlock();
                        if (block1 == Blocks.BLUE_ICE) {
                           worldIn.setBlockState(blockpos, Blocks.BLUE_ICE.getDefaultState(), 2);
                           break;
                        }
                     }
                  }
               }
            }

            return true;
         }
      }
   }
}