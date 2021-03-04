package com.railwayteam.railways.capabilities;

import com.railwayteam.railways.Railways;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.logging.log4j.LogManager;

public class StationListCapability {
  private String entry;

  public String getEntry () { return entry; }
  public void   setEntry (String station) { entry = station; }

  public StationListCapability () {
    this("");
  }

  public StationListCapability (String station) {
    entry = station;
  }

  public static class StationListNBTStorage implements Capability.IStorage<StationListCapability> {
    @Override
    public INBT writeNBT (Capability<StationListCapability> cap, StationListCapability instance, Direction side) {
      StringNBT snbt = StringNBT.valueOf(instance.entry);
      return snbt;
    }

    @Override
    public void readNBT (Capability<StationListCapability> cap, StationListCapability instance, Direction side, INBT nbt) {
      String candidate = "";
      if (nbt.getType() == StringNBT.TYPE) {
        candidate = ((StringNBT)nbt).getString();
        LogManager.getLogger(Railways.MODID).debug("Parsed NBT, found: " + candidate);
      }
      instance.setEntry(candidate);
    }
  }

  public static StationListCapability createADefaultInstance () {
    return new StationListCapability();
  }
}
