package net.minecraft.util;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class CachedBlockInfo {
   private final IWorldReader world;
   private final BlockPos pos;
   private final boolean forceLoad;
   private BlockState state;
   private TileEntity tileEntity;
   private boolean tileEntityInitialized;

   public CachedBlockInfo(IWorldReader worldIn, BlockPos posIn, boolean forceLoadIn) {
      this.world = worldIn;
      this.pos = posIn.toImmutable();
      this.forceLoad = forceLoadIn;
   }

   /**
    * Gets the block state as currently held, or (if it has not gotten it from the world) loads it from the world.
    *  This will only look up the state from the world if {@link #forceLoad} is true or the block position is loaded.
    */
   public BlockState getBlockState() {
      if (this.state == null && (this.forceLoad || this.world.isBlockLoaded(this.pos))) {
         this.state = this.world.getBlockState(this.pos);
      }

      return this.state;
   }

   /**
    * Gets the tile entity as currently held, or (if it has not gotten it from the world) loads it from the world.
    */
   @Nullable
   public TileEntity getTileEntity() {
      if (this.tileEntity == null && !this.tileEntityInitialized) {
         this.tileEntity = this.world.getTileEntity(this.pos);
         this.tileEntityInitialized = true;
      }

      return this.tileEntity;
   }

   public IWorldReader getWorld() {
      return this.world;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   /**
    * Creates a new {@link Predicate} that will match when the given {@link IBlockState} predicate matches.
    */
   public static Predicate<CachedBlockInfo> hasState(Predicate<BlockState> predicatesIn) {
      return (p_201002_1_) -> {
         return p_201002_1_ != null && predicatesIn.test(p_201002_1_.getBlockState());
      };
   }
}