package com.railwayteam.railways.capabilities;

import java.util.Iterator;

public interface ISchedulable {
  // interface methods for working with a schedule on a cart
  public boolean isInSchedule       (String station);
  public boolean addToSchedule      (String station);
  public boolean removeFromSchedule (String station);
  public int getScheduleLength ();
  public Iterator<String> getIterator ();
}
