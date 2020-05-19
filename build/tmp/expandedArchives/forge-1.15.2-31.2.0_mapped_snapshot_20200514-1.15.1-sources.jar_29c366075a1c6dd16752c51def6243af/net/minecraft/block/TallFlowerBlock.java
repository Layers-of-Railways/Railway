package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class TallFlowerBlock extends DoublePlantBlock implements IGrowable {
   public TallFlowerBlock(Block.Properties properties) {
      super(properties);
   }

   public boolean isReplaceable(BlockState state, BlockItemUseContext useContext) {
      return false;
   }

   /**
    * Whether this IGrowable can grow
    */
   public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
      return true;
   }

   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
      return true;
   }

   public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
      spawnAsEntity(worldIn, pos, new ItemStack(this));
   }
}