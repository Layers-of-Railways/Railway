package com.railwayteam.railways.mixin_interfaces;

import com.railwayteam.railways.content.custom_tracks.TrackMaterial;

public interface IHasTrackMaterial {
  default TrackMaterial getMaterial() {
    return TrackMaterial.ANDESITE;
  }

  default void setMaterial(TrackMaterial trackMaterial) {}
}
