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

  public void setStation(String value) {
    station = value;
    setChanged();
  }
  public String getStation() { return station; }

  @Override
  public CompoundNBT save(CompoundNBT compound) {
    compound.putString("station", station);
    return super.save(compound);
  }

  @Override
  public void load(BlockState state, CompoundNBT compound) {
    station = compound.getString("station");
    super.deserializeNBT(compound);
  }
}
