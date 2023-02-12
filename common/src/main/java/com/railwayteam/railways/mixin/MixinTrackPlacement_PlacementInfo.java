package com.railwayteam.railways.mixin;

import com.railwayteam.railways.Railways;
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
      Railways.LOGGER.error("TrackPlacement$Placement info material is null. this is bad");
      trackMaterial = TrackMaterial.ANDESITE;
      throw new RuntimeException("TrackPlacement$Placement info material is null. this is bad");
    }
    return trackMaterial;
  }

  @Override
  public void setMaterial(TrackMaterial material) {
    trackMaterial = material;
  }
}
