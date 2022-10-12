package com.railwayteam.railways.mixin;

import com.simibubi.create.content.logistics.trains.BezierConnection;
import com.simibubi.create.content.logistics.trains.track.TrackPlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TrackPlacement.PlacementInfo.class)
public interface AccessorTrackPlacement_PlacementInfo {
  @Accessor("curve")
  void setCurve(BezierConnection bc);

  @Accessor("curve")
  BezierConnection getCurve();
}
