package com.railwayteam.railways.content.conductor.toolbox;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.registry.CRContainerTypes;
import com.simibubi.create.content.curiosities.toolbox.ToolboxContainer;
import com.simibubi.create.content.curiosities.toolbox.ToolboxTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class MountedToolboxContainer extends ToolboxContainer {
  public MountedToolboxContainer(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
    super(type, id, inv, extraData);
  }

  public MountedToolboxContainer(MenuType<?> type, int id, Inventory inv, MountedToolbox toolbox) {
    super(type, id, inv, toolbox);
    toolbox.startOpen(player);
  }

  public static MountedToolboxContainer create(int id, Inventory inv, MountedToolbox toolbox) {
    return new MountedToolboxContainer(CRContainerTypes.MOUNTED_TOOLBOX.get(), id, inv, toolbox);
  }

  @Override
  protected ToolboxTileEntity createOnClient(FriendlyByteBuf extraData) {
    int conductorId = extraData.readVarInt();
    ClientLevel world = Minecraft.getInstance().level;
    Entity entity = world.getEntity(conductorId);
    if (!(entity instanceof ConductorEntity conductor))
      return null;
    MountedToolbox toolbox = conductor.getOrCreateToolboxHolder();
    toolbox.read(extraData.readNbt(), true);
    return toolbox;
  }
}
