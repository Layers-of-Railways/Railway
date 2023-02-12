package com.railwayteam.railways.mixin_interfaces;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.simibubi.create.content.curiosities.toolbox.ToolboxHandler;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.utility.WorldAttached;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface IMountedToolboxHandler {
  WorldAttached<WeakHashMap<UUID, ConductorEntity>> conductors =
      new WorldAttached<>(w -> new WeakHashMap<>());

  static void onLoad(ConductorEntity ce) {
    conductors.get(ce.getLevel())
        .put(ce.getUUID(), ce);
  }

  static void onUnload(ConductorEntity ce) {
    conductors.get(ce.getLevel())
        .remove(ce.getUUID());
  }

  // List<ConductorEntity | ToolboxTileEntity>
  static List<Object> getNearest(LevelAccessor world, Player player, int maxAmount) {
    Vec3 location = player.position();
    double maxRange = getMaxRange(player);
    return Stream.concat(conductors.get(world)
        .keySet()
        .stream()
        .map(conductors.get(world)::get)
        .filter(ce -> ce != null && ce.isAlive() && !ce.isRemoved())
        .filter(p -> distance(location, p.position()) < maxRange * maxRange)
        .sorted(Comparator.comparingDouble(p -> distance(location, p.position())))
        .limit(maxAmount)
        .filter((ce) -> {
          if (!ce.isCarryingToolbox())
            return false;
          return ce.getToolboxHolder().isFullyInitialized();
        }), ToolboxHandler.getNearest(world, player, maxAmount).stream())
        .limit(maxAmount)
        .collect(Collectors.toList());
  }

  static boolean withinRange(Player player, ConductorEntity ce) {
    if (player.level != ce.getLevel())
      return false;
    double maxRange = getMaxRange(player);
    return distance(player.position(), ce.position()) < maxRange * maxRange;
  }

  static double distance(Vec3 location, Vec3 p) {
    return location.distanceToSqr(p);
  }

  static double getMaxRange(Player player) {
    return AllConfigs.SERVER.curiosities.toolboxRange.get()
        .doubleValue();
  }
}
