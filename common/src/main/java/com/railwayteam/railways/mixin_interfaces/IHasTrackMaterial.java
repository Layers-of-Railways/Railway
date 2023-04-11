package com.railwayteam.railways.mixin_interfaces;

import com.railwayteam.railways.track_api.TrackMaterial;

public interface IHasTrackMaterial { //TODO _track api
  default TrackMaterial getMaterial() {
    return TrackMaterial.ANDESITE;
  }

  default void setMaterial(TrackMaterial trackMaterial) {}
}
