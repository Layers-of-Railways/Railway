package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public interface IWorldWriter {
   /**
    * Sets a block state into this world.Flags are as follows:
    * 1 will cause a block update.
    * 2 will send the change to clients.
    * 4 will prevent the block from being re-rendered.
    * 8 will force any re-renders to run on the main thread instead
    * 16 will prevent neighbor reactions (e.g. fences connecting, observers pulsing).
    * 32 will prevent neighbor reactions from spawning drops.
    * 64 will signify the block is being moved.
    * Flags can be OR-ed
    */
   boolean setBlockState(BlockPos pos, BlockState newState, int flags);

   boolean removeBlock(BlockPos pos, boolean isMoving);

   /**
    * Sets a block to air, but also plays the sound and particles and can spawn drops
    */
   default boolean destroyBlock(BlockPos pos, boolean dropBlock) {
      return this.destroyBlock(pos, dropBlock, (Entity)null);
   }

   boolean destroyBlock(BlockPos p_225521_1_, boolean p_225521_2_, @Nullable Entity p_225521_3_);

   default boolean addEntity(Entity entityIn) {
      return false;
   }
}