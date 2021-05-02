package com.railwayteam.railways.blocks;

import com.railwayteam.railways.Railways;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;

public class SignalTileEntity extends TileEntity {
  public static final String NAME = "signal";

  private BlockPos target;

  public SignalTileEntity(TileEntityType<? extends SignalTileEntity> type) {
    super(type);
    Railways.LOGGER.debug("TE created");
  }

  public BlockPos getTarget () { return target; }
  public void     setTarget (BlockPos in) {
    target = in;
    Railways.LOGGER.debug("target set to: " + in.toShortString());
    markDirty();
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    compound.putIntArray("target", new int[]{target.getX(), target.getY(), target.getZ()});
    return super.write(compound);
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    int[] data = nbt.getIntArray("target");
    target = new BlockPos(data[0],data[1],data[2]);
    super.deserializeNBT(nbt);
  }
}
