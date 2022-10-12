package com.railwayteam.railways.content.custom_tracks;

import com.railwayteam.railways.mixin_interfaces.IHasTrackMaterial;
import com.simibubi.create.content.logistics.trains.track.TrackBlock;

public class CustomTrackBlock extends TrackBlock implements IHasTrackMaterial {

  protected final TrackMaterial material;

  public CustomTrackBlock(Properties properties, TrackMaterial material) {
    super(properties);
    this.material = material;
  }

  @Override
  public TrackMaterial getMaterial() {
    return this.material;
  }
}
