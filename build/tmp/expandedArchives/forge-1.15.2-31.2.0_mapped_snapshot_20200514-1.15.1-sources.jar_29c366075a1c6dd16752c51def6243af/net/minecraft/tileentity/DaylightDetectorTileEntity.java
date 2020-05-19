package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DaylightDetectorBlock;

public class DaylightDetectorTileEntity extends TileEntity implements ITickableTileEntity {
   public DaylightDetectorTileEntity() {
      super(TileEntityType.DAYLIGHT_DETECTOR);
   }

   public void tick() {
      if (this.world != null && !this.world.isRemote && this.world.getGameTime() % 20L == 0L) {
         BlockState blockstate = this.getBlockState();
         Block block = blockstate.getBlock();
         if (block instanceof DaylightDetectorBlock) {
            DaylightDetectorBlock.updatePower(blockstate, this.world, this.pos);
         }
      }

   }
}