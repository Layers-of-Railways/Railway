package com.railwayteam.railways.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import java.util.ArrayList;
import java.util.Iterator;

public class StationListCapability {
  public static final String NBTKEY = "station:";
  private String entry;
  private ArrayList<String> stations;

  public boolean contains (String station) { return stations.contains(station); }
  public boolean isEmpty  ()               { return (stations.isEmpty()); }
  public void    add      (String station) { if (!contains(station) && !station.isEmpty()) stations.add(station); }
  public void    remove   (String station) { stations.remove(station); }
  public void    clear    ()               { stations.clear(); }
  public Iterator<String> iterate ()       { return stations.iterator(); }
  public int     length   ()               { return stations.size(); }
  public ArrayList<String> copy ()         {
    ArrayList<String> ret = new ArrayList<String>();
    for (String station : stations) ret.add(station);
    return ret;
  }

  public StationListCapability () {
    this("");
  }

  public StationListCapability (String station) {
    entry = station;
    stations = new ArrayList<String>();
    if (!station.isEmpty()) stations.add(entry);
  }

  public static class StationListNBTStorage implements Capability.IStorage<StationListCapability> {
    @Override
    public INBT writeNBT (Capability<StationListCapability> cap, StationListCapability instance, Direction side) {
      CompoundNBT nbt = new CompoundNBT();
      for (String station : instance.stations) {
        nbt.putString(NBTKEY+station, station);
      }
      return nbt;
    }

    @Override
    public void readNBT (Capability<StationListCapability> cap, StationListCapability instance, Direction side, INBT nbt) {
      ArrayList<String> proc = new ArrayList<String>();
    //  LogManager.getLogger(Railways.MODID).debug("reading NBT type " + nbt.getType().toString());
      if (nbt.getReader() == CompoundNBT.READER) {
        for (String key : ((CompoundNBT)nbt).keySet()) {
        //  LogManager.getLogger(Railways.MODID).debug("reading string in packet: " + key);
          if (!key.startsWith(NBTKEY)) continue;
          proc.add( ((CompoundNBT)nbt).getString(key) );
        }
      }
      instance.stations = proc;
    }
  }

  public static StationListCapability createADefaultInstance () {
    return new StationListCapability();
  }
}
