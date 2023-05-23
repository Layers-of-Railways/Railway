package com.railwayteam.railways.content.switches;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CREdgePointTypes;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.trains.*;
import com.simibubi.create.content.logistics.trains.management.edgePoint.EdgePointType;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SignalPropagator;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SingleTileEdgePoint;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static java.util.stream.Collectors.toSet;

public class TrackSwitch extends SingleTileEdgePoint {
  private TrackNodeLocation switchPoint;
  private final List<TrackNodeLocation> exits = new ArrayList<>();

  public TrackSwitch() {
  }

  private @Nullable TrackNodeLocation straightExit;
  private @Nullable TrackNodeLocation leftExit;
  private @Nullable TrackNodeLocation rightExit;

  @Override
  public EdgePointType<?> getType() {
    return CREdgePointTypes.SWITCH;
  }

  @Override
  public boolean canCoexistWith(EdgePointType<?> otherType, boolean front) {
    return otherType == EdgePointType.SIGNAL;
  }

  @Override
  public void tileAdded(BlockEntity tile, boolean front) {
    super.tileAdded(tile, front);

    if (tile instanceof TrackSwitchTileEntity te) {
      te.calculateExits(this);
    }

    notifyTrains(tile.getLevel());
  }

  @Override
  public void onRemoved(TrackGraph graph) {
    exits.clear();
    sortExits();
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

  void updateExits(TrackNodeLocation switchPoint, Collection<TrackNodeLocation> newExits) {
    this.switchPoint = switchPoint;

    Vec3 forward = edgeLocation.getFirst().getLocation().vectorTo(edgeLocation.getSecond().getLocation());
    exits.clear();
    exits.addAll(newExits.stream()
      // Exit should be in the same direction switch is facing
      .filter(e -> forward.dot(switchPoint.getLocation().vectorTo(e.getLocation())) > 0)
      .toList());
    sortExits();
  }

  private void sortExits() {
    Vec3 forward = edgeLocation.getFirst().getLocation().vectorTo(edgeLocation.getSecond().getLocation()).normalize();
    exits.sort(Comparator.comparing(e -> {
      // Relative exit directions -- 0 is straight on, negative for left, positive for right
      Vec3 exitDir = switchPoint.getLocation().vectorTo(e.getLocation());
      return forward.x * exitDir.z - forward.z * exitDir.x;
    }));

    if (exits.size() == 1) {
      leftExit = null;
      straightExit = exits.get(0);
      rightExit = null;
    } else if (exits.size() == 2) {
      Vec3 firstExitDir = switchPoint.getLocation().vectorTo(exits.get(0).getLocation()).normalize();
      Vec3 secondExitDir = switchPoint.getLocation().vectorTo(exits.get(1).getLocation()).normalize();

      double firstExitRelativeDir = forward.x * firstExitDir.z - forward.z * firstExitDir.x;
      double secondExitRelativeDir = forward.x * secondExitDir.z - forward.z * secondExitDir.x;

      // Determine which exit is left/right/straight based on relative exit directions
      // 0.2 *should* be straight enough, maybe
      if (firstExitRelativeDir < 0 && secondExitRelativeDir <= 0.2) {
        //    / /         /
        // --+-'   or  --+---  = left, straight
        leftExit = exits.get(0);
        straightExit = exits.get(1);
        rightExit = null;
      } else if (firstExitRelativeDir >= 0.2 && secondExitRelativeDir > 0) {
        // --+-.       --+---
        //    \ \  or     \    = right, straight
        leftExit = null;
        straightExit = exits.get(0);
        rightExit = exits.get(1);
      } else {
        //    /
        // --<   = left, right
        //    \
        leftExit = exits.get(0);
        straightExit = null;
        rightExit = exits.get(1);
      }
    } else if (exits.size() == 3) {
      leftExit = exits.get(0);
      straightExit = exits.get(1);
      rightExit = exits.get(2);
    } else {
      leftExit = null;
      straightExit = null;
      rightExit = null;
    }
  }

  @Nullable TrackNodeLocation getSwitchPoint() {
    return switchPoint;
  }

  Collection<TrackNodeLocation> getExits() {
    return exits.stream().toList();
  }

  public boolean hasStraightExit() {
    return straightExit != null;
  }

  public boolean hasLeftExit() {
    return leftExit != null;
  }

  public boolean hasRightExit() {
    return rightExit != null;
  }

  @Override
  public void write(CompoundTag nbt, DimensionPalette dimensions) {
    super.write(nbt, dimensions);
    nbt.put("SwitchPoint", switchPoint.write(dimensions));
    nbt.put("Exits", NBTHelper.writeCompoundList(exits, e -> e.write(dimensions)));
  }

  @Override
  public void write(FriendlyByteBuf buffer, DimensionPalette dimensions) {
    super.write(buffer, dimensions);
    switchPoint.send(buffer, dimensions);
    buffer.writeCollection(exits, (buf, e) -> e.send(buf, dimensions));
  }

  @Override
  public void read(CompoundTag nbt, boolean migration, DimensionPalette dimensions) {
    super.read(nbt, migration, dimensions);
    updateExits(
      TrackNodeLocation.read(nbt.getCompound("SwitchPoint"), dimensions),
      nbt.getList("Exits", Tag.TAG_COMPOUND)
        .stream()
        .map(t -> TrackNodeLocation.read((CompoundTag) t, dimensions))
        .toList()
    );
  }

  @Override
  public void read(FriendlyByteBuf buffer, DimensionPalette dimensions) {
    super.read(buffer, dimensions);
    updateExits(
      TrackNodeLocation.receive(buffer, dimensions),
      buffer.readList(buf -> TrackNodeLocation.receive(buf, dimensions))
    );
  }
}
