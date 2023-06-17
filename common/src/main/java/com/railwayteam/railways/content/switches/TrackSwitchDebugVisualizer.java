package com.railwayteam.railways.content.switches;

import com.simibubi.create.CreateClient;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class TrackSwitchDebugVisualizer {
  public static void visualizeSwitchExits(TrackSwitch sw) {
    Minecraft mc = Minecraft.getInstance();
    Entity camera = mc.cameraEntity;
    if (camera == null) {
      return;
    }

    Vec3 offset = new Vec3(0, 0.4, 0);
    float width = 1 / 16f;

    TrackNodeLocation from = sw.getSwitchPoint();
    TrackNodeLocation activeExit = sw.getSwitchTarget();
    for (TrackNodeLocation to : sw.getExits()) {
      boolean active = to == activeExit;
      CreateClient.OUTLINER.showLine(to,
          from.getLocation().add(offset),
          to.getLocation().add(offset))
        .colored(active ? new Color(0, 203, 150) : new Color(255, 50, 150))
        .lineWidth(width);
    }
  }
}
