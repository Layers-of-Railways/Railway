package com.railwayteam.railways.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.nbt.CompoundNBT;

public class StationSensorRailTileEntity extends TileEntity {
  public static final String NAME = "station_sensor";

  private String station = "";

  public StationSensorRailTileEntity (TileEntityType<? extends StationSensorRailTileEntity> typeIn) {
    super(typeIn);
  }

  public void setStation (String value) {
    station = value;
    markDirty();
  }
  public String getStation () { return station; }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    compound.putString("station", station);
    return super.write(compound);
  }

//  @Override
//  public void read(CompoundNBT compound) {
//    station = compound.getString("station");
//    super.read(compound);
//  }


  @Override
  public void read(BlockState state, CompoundNBT nbt) {
    station = nbt.getString("station");
    super.read(state, nbt);
  }
}
