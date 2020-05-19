package net.minecraft.block;

import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.tileentity.TrappedChestTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;

public class TrappedChestBlock extends ChestBlock {
   public TrappedChestBlock(Block.Properties properties) {
      super(properties, () -> {
         return TileEntityType.TRAPPED_CHEST;
      });
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new TrappedChestTileEntity();
   }

   protected Stat<ResourceLocation> getOpenStat() {
      return Stats.CUSTOM.get(Stats.TRIGGER_TRAPPED_CHEST);
   }

   /**
    * Can this block provide power. Only wire currently seems to have this change based on its state.
    * @deprecated call via {@link IBlockState#canProvidePower()} whenever possible. Implementing/overriding is fine.
    */
   public boolean canProvidePower(BlockState state) {
      return true;
   }

   /**
    * @deprecated call via {@link IBlockState#getWeakPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
    * Implementing/overriding is fine.
    */
   public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
      return MathHelper.clamp(ChestTileEntity.getPlayersUsing(blockAccess, pos), 0, 15);
   }

   /**
    * @deprecated call via {@link IBlockState#getStrongPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
    * Implementing/overriding is fine.
    */
   public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
      return side == Direction.UP ? blockState.getWeakPower(blockAccess, pos, side) : 0;
   }
}