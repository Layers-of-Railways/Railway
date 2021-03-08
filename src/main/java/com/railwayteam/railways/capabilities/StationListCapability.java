package com.railwayteam.railways.capabilities;

import com.railwayteam.railways.Railways;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

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
      if (nbt.getType() == CompoundNBT.TYPE) {
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
