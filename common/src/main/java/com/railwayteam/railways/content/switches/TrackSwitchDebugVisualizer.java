package com.railwayteam.railways.content.switches;

import com.railwayteam.railways.Railways;
import com.simibubi.create.Create;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.logistics.trains.BezierConnection;
import com.simibubi.create.content.logistics.trains.TrackEdge;
import com.simibubi.create.content.logistics.trains.TrackNodeLocation;
import com.simibubi.create.foundation.utility.Color;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
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
    for (TrackNodeLocation to : sw.getExits()) {
      CreateClient.OUTLINER.showLine(to,
          from.getLocation().add(offset),
          to.getLocation().add(offset))
        .colored(new Color(255, 192, 203))
        .lineWidth(width);
    }
  }
}
