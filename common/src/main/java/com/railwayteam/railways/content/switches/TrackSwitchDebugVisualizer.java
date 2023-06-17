package com.railwayteam.railways.content.switches;

import com.railwayteam.railways.registry.CREdgePointTypes;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.Objects;

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

  public static void visualizePotentialLocations() {
    Minecraft mc = Minecraft.getInstance();
    Entity camera = mc.cameraEntity;
    if (camera == null)
      return;

    if (mc.player == null)
      return;

    if (!(mc.player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof TrackSwitchBlockItem)
            && !(mc.player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof TrackSwitchBlockItem)) {
      return;
    }

    int range = 64;
    int rangeSqr = range*range;

    for (TrackGraph graph : CreateClient.RAILWAYS.trackNetworks.values()) {
      for (TrackNodeLocation tnl : graph.getNodes()) {
        if (!Objects.equals(tnl.dimension, mc.level.dimension()))
          continue;
        if (tnl.getLocation().distanceToSqr(camera.position()) > rangeSqr)
          continue;

        for (TrackSwitch sw : graph.getPoints(CREdgePointTypes.SWITCH)) {
          TrackSwitchDebugVisualizer.visualizeSwitchExits(sw);
        }

        TrackNode node = graph.locateNode(tnl);
        Map<TrackNode, TrackEdge> connections = graph.getConnectionsFrom(node);
        int connectionCount = connections.size();
        if (connectionCount > 2 && connectionCount <= 4) {
          Vec3 basePos = node.getLocation().getLocation();
          Vec3 averageOffset = Vec3.ZERO;
          Vec3[] offsets = new Vec3[connectionCount];
          int i = 0;
          for (Map.Entry<TrackNode, TrackEdge> entry : connections.entrySet()) {
            Vec3 offset = entry.getKey().getLocation().getLocation()
                    .subtract(basePos)
                    .normalize();
            averageOffset = averageOffset.add(offset);
            offsets[i] = offset;
            i++;
          }
          Vec3 farthestOffset = Vec3.ZERO;
          double farthestDistance = 0;

          for (Vec3 offset : offsets) {
            double distance = averageOffset.distanceToSqr(offset);
            if (distance > farthestDistance) {
              farthestDistance = distance;
              farthestOffset = offset;
            }
          }

          Direction offsetDirection = Direction.getNearest(farthestOffset.x, farthestOffset.y, farthestOffset.z);
          CreateClient.OUTLINER.showAABB(node, AABB.ofSize(tnl.getLocation()
                          .add(offsetDirection.getStepX()/2., offsetDirection.getStepY()/2., offsetDirection.getStepZ()/2.)
                          .add(0, 8 / 16f, 0),
                          1, 1, 1))
            .colored(graph.color)
            //.disableLineNormals()
            .lineWidth(1 / 16f);
        }
      }
    }
  }
}
