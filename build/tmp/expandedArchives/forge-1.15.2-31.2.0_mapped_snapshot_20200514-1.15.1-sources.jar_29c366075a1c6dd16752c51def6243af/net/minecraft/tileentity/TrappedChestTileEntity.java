package net.minecraft.tileentity;

public class TrappedChestTileEntity extends ChestTileEntity {
   public TrappedChestTileEntity() {
      super(TileEntityType.TRAPPED_CHEST);
   }

   protected void onOpenOrClose() {
      super.onOpenOrClose();
      this.world.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockState().getBlock());
   }
}