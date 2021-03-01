package com.railwayteam.railways.capabilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Schedulable implements ISchedulable {
  private List<String> stations = new ArrayList<String>();

  @Override
  public boolean isInSchedule (String station) {
    return stations.contains(station);
  }

  @Override
  public boolean addToSchedule (String station) {
    if (!isInSchedule(station)) {
      stations.add(station);
      return true;
    }
    return false;
  }

  @Override
  public boolean removeFromSchedule (String station) {
    if (!isInSchedule(station)) return false;
    stations.remove(station);
    return true;
  }

  @Override
  public int getScheduleLength () {
    return stations.size();
  }

  @Override
  public Iterator<String> getIterator () {
    return stations.listIterator();
  }
}
