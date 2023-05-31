package com.railwayteam.railways.mixin;

import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.equipment.toolbox.ToolboxInventory;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.WeakHashMap;

@Mixin(value = ToolboxBlockEntity.class, remap = false)
public interface AccessorToolboxBlockEntity {
  @Accessor
  ToolboxInventory getInventory();

  @Accessor
  Map<Integer, WeakHashMap<Player, Integer>> getConnectedPlayers();
}
