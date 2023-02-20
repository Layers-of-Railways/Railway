package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.custom_tracks.TrackMaterial;
import com.railwayteam.railways.mixin_interfaces.IHasTrackMaterial;
import com.simibubi.create.content.logistics.trains.track.TrackPlacement;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = TrackPlacement.PlacementInfo.class, remap = false)
public abstract class MixinTrackPlacement_PlacementInfo implements IHasTrackMaterial {
  private TrackMaterial trackMaterial;

  @Override
  public TrackMaterial getMaterial() {
    if (trackMaterial == null) {
      return IHasTrackMaterial.super.getMaterial();
    }
    return trackMaterial;
  }

  @Override
  public void setMaterial(TrackMaterial material) {
    trackMaterial = material;
  }
}
