package com.railwayteam.railways.mixin;

import com.simibubi.create.content.curiosities.toolbox.ToolboxInventory;
import com.simibubi.create.content.curiosities.toolbox.ToolboxTileEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.WeakHashMap;

@Mixin(value = ToolboxTileEntity.class, remap = false)
public interface AccessorToolboxTileEntity {
  @Accessor
  ToolboxInventory getInventory();

  @Accessor
  Map<Integer, WeakHashMap<Player, Integer>> getConnectedPlayers();
}
