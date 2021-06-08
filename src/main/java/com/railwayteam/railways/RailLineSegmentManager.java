package com.railwayteam.railways;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;

public class RailLineSegmentManager {
  private static RailLineSegmentManager singleton;
  private static Graph segmentGraph;

  private RailLineSegmentManager () {
    segmentGraph = new Graph();
  }

  public static RailLineSegmentManager getInstance() {
    if (singleton == null) singleton = new RailLineSegmentManager();
    return singleton;
  }

  public static int addTrack (BlockPos position) {
    // this is just to test!
    segmentGraph.addNode(
      position.getX()
    );
    return 0;
  }

  public static boolean containsTrack (BlockPos position) {
    return segmentGraph.containsNode(position.getX());
  }

  private class GraphNode {
    int id;
    GraphNode (int id) {
      this.id = id;
    }

    @Override
    public boolean equals (Object other) {
      if (other instanceof GraphNode) {
        return this.id == ((GraphNode)other).id;
      }
      return false;
    }

    @Override
    public int hashCode() { // probably naive...
      return id;
    }
  }

  private class Graph {
    private HashMap<GraphNode, ArrayList<GraphNode>> adjacentNodes;

    Graph () {
      adjacentNodes = new HashMap<>();
    }

    void addNode (int id) {
      adjacentNodes.putIfAbsent(new GraphNode(id), new ArrayList<>());
    }

    void removeNode (int id) {
      GraphNode n = new GraphNode(id);
      adjacentNodes.values().stream().forEach(v -> v.remove(n));
      adjacentNodes.remove(n);
    }

    boolean containsNode (int id) {
      return adjacentNodes.containsKey(new GraphNode(id));
    }

    void addLink (int idA, int idB) {
      GraphNode a = new GraphNode(idA);
      GraphNode b = new GraphNode(idB);
      ArrayList<GraphNode> ala = adjacentNodes.get(a);
      ArrayList<GraphNode> alb = adjacentNodes.get(b);
      if (ala == null || alb == null) return; // error, can't link nonsense nodes
      adjacentNodes.get(a).add(b);
      adjacentNodes.get(b).add(a);
    }

    void removeLink (int idA, int idB) {
      GraphNode a = new GraphNode(idA);
      GraphNode b = new GraphNode(idB);
      ArrayList<GraphNode> ala = adjacentNodes.get(a);
      ArrayList<GraphNode> alb = adjacentNodes.get(b);
      if (ala == null || alb == null) return; // error, can't unlink nonsense nodes
      ala.remove(b);
      alb.remove(a);
    }

    ArrayList<Integer> getLinkedNodeIDs (int id) {
      ArrayList<Integer> ret = new ArrayList<>();
      adjacentNodes.get(new GraphNode(id)).stream().forEach(gn -> ret.add(gn.id));
      return ret;
    }
  }
}
