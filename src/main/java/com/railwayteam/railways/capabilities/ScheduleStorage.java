package com.railwayteam.railways.capabilities;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;

public class ScheduleStorage implements Capability.IStorage<ISchedulable> {
  public static final ScheduleStorage scheduleStorage = new ScheduleStorage();

  @Nullable
  @Override
  public INBT writeNBT (Capability<ISchedulable> cap, ISchedulable instance, Direction side) {
    ListNBT ret = new ListNBT();
    Iterator<String> listIterator = instance.getIterator();
    while (listIterator.hasNext()) {
      StringNBT next = StringNBT.valueOf(listIterator.next());
      ret.add(next);
    }
    return ret;
  }

  @Override
  public void readNBT (Capability<ISchedulable> cap, ISchedulable instance, Direction side, INBT nbt) {
    if (instance == null) return;

    ListNBT read = (ListNBT)nbt;
    ArrayList<String> out = new ArrayList<String>();
    for (INBT snbt : read) {
      out.add(snbt.toString());
    }

  }
}
