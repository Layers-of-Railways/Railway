package com.railwayteam.railways.capabilities;

import com.railwayteam.railways.Railways;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StationListProvider implements ICapabilitySerializable<INBT> {
  private final Direction SIDE_AGNOSTIC = null;

  private StationListCapability stationList = new StationListCapability();

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
    if (CapabilitySetup.CAPABILITY_STATION_LIST == cap) {
      return (LazyOptional<T>)LazyOptional.of(()-> stationList);
    }
    return LazyOptional.empty();
  }

  @Override
  public INBT serializeNBT () {
    INBT nbt = CapabilitySetup.CAPABILITY_STATION_LIST.writeNBT(stationList, SIDE_AGNOSTIC);
    return nbt;
  }

  @Override
  public void deserializeNBT (INBT nbt) {
    if (nbt.getType() != CompoundNBT.TYPE) {
      LogManager.getLogger(Railways.MODID).debug("wrong NBT Type on deserialize!");
      return;
    }
    CapabilitySetup.CAPABILITY_STATION_LIST.readNBT(stationList, SIDE_AGNOSTIC, nbt);
  }
}
