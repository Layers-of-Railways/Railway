package com.railwayteam.railways.content.switches;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CREdgePointTypes;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.trains.*;
import com.simibubi.create.content.logistics.trains.management.edgePoint.EdgePointType;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SignalPropagator;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SingleTileEdgePoint;
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
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static java.util.stream.Collectors.toSet;

public class TrackSwitch extends SingleTileEdgePoint {
  private @Nullable TrackNodeLocation switchPoint = null;
  private final Set<TrackNodeLocation> exits = new HashSet<>();

  public TrackSwitch() {
  }

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
    exits.addAll(newExits);
  }

  TrackNodeLocation getSwitchPoint() {
    return switchPoint;
  }

  Collection<TrackNodeLocation> getExits() {
    return exits.stream().toList();
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
    switchPoint = TrackNodeLocation.read(nbt.getCompound("SwitchPoint"), dimensions);
    exits.clear();
    nbt.getList("Exits", Tag.TAG_COMPOUND)
      .stream()
      .map(t -> TrackNodeLocation.read((CompoundTag) t, dimensions))
      .forEach(exits::add);
  }

  @Override
  public void read(FriendlyByteBuf buffer, DimensionPalette dimensions) {
    super.read(buffer, dimensions);
    switchPoint = TrackNodeLocation.receive(buffer, dimensions);
    exits.clear();
    exits.addAll(buffer.readList(buf -> TrackNodeLocation.receive(buf, dimensions)));
  }
}
