package com.railwayteam.railways.content.switches;

import com.railwayteam.railways.Railways;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.trains.GraphLocation;
import com.simibubi.create.content.logistics.trains.TrackEdge;
import com.simibubi.create.content.logistics.trains.TrackGraph;
import com.simibubi.create.content.logistics.trains.TrackNode;
import com.simibubi.create.content.logistics.trains.management.edgePoint.EdgePointType;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SignalPropagator;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SingleTileEdgePoint;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class TrackSwitch extends SingleTileEdgePoint {

  public TrackSwitch() {

  }


  @Override
  public boolean canCoexistWith(EdgePointType<?> otherType, boolean front) {
    return otherType == EdgePointType.SIGNAL;
  }

  enum ExitEdge {
    LEFT, STRAIGHT, RIGHT
  }

  static Vec3 exitDirection(TrackEdge edge, TrackNode startNode) {
    return edge.getDirection(
      edge.node1.getLocation().equals(startNode.getLocation()));
  }

  public @Nullable Map<ExitEdge, TrackEdge> exitsFrom(TrackEdge edge, boolean front, TrackGraph graph) {
    // TODO: Handle 3-way junctions where the turns are not at the
    // exact same node position, as in:
    //         ,---
    // -----.-'----
    //       `-----

    TrackNode startNode = edge.node2;
    Vec3 forward = edge.getDirection(true);

    Map<ExitEdge, TrackEdge> exitMap = new HashMap<>();

    Set<TrackEdge> exits = graph.getConnectionsFrom(startNode).values()
      .stream()
      .filter(e -> e != edge)
      // Edges with reversed nodes, i.e. (a, b) and (b, a)
      .filter(e -> !e.node1.getLocation().equals(edge.node2.getLocation())
        || !e.node2.getLocation().equals(edge.node1.getLocation()))
      .collect(toSet());

    // Look for any straight exit at all
    exits.stream()
      .filter(e -> exitDirection(e, startNode).equals(forward))
      .forEach(e -> {
        exitMap.put(ExitEdge.STRAIGHT, e);
      });
    if (exitMap.containsKey(ExitEdge.STRAIGHT)) {
      exits.remove(exitMap.get(ExitEdge.STRAIGHT));
    }

    exits
      .forEach(e -> {
        Vec3 direction = e.getDirection(e.node1.getLocation().equals(startNode.getLocation()));

        if (forward.dot(direction) <= 0) {
          // Opposite direction
          return;
        }

        double side = forward.x * -direction.z + forward.z * direction.x;
        if (side > 0) {
          // Left turn
          exitMap.put(ExitEdge.LEFT, e);
        } else if (side <= 0) {
          // Right turn
          exitMap.put(exitMap.containsKey(ExitEdge.STRAIGHT)
            ? ExitEdge.RIGHT : ExitEdge.STRAIGHT, e);
        }
      });

    exits.removeAll(exitMap.values());

    // If there are more than three exits, mark as invalid
    if (!exits.isEmpty()) { return null; }
    return exitMap;
  }

  @Override
  public void tileAdded(BlockEntity tile, boolean front) {
    super.tileAdded(tile, front);

    if (tile instanceof TrackSwitchTileEntity te) {
      GraphLocation loc = te.edgePoint.determineGraphLocation();
      TrackGraph graph = loc.graph;
      TrackEdge edge = graph
        .getConnectionsFrom(graph.locateNode(loc.edge.getFirst()))
        .get(graph.locateNode(loc.edge.getSecond()));

      Map<ExitEdge, TrackEdge> exits = exitsFrom(edge, front, graph);
      if (exits == null) {
        te.setExits(false, false);
      } else {
        te.setExits(exits.containsKey(ExitEdge.LEFT), exits.containsKey(ExitEdge.RIGHT));
      }
    }

    notifyTrains(tile.getLevel());
  }

  @Override
  public void onRemoved(TrackGraph graph) {
    removeFromAllGraphs();
  }

  private void notifyTrains(Level level) {
    TrackGraph graph = Create.RAILWAYS.sided(level).getGraph(level, edgeLocation.getFirst());
    if (graph == null)
      return;
    TrackEdge edge = graph.getConnection(edgeLocation.map(graph::locateNode));
    if (edge == null)
      return;
    SignalPropagator.notifyTrains(graph, edge);
  }
}
