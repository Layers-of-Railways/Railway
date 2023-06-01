package com.railwayteam.railways.content.conductor.toolbox;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.registry.CRContainerTypes;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.equipment.toolbox.ToolboxMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class MountedToolboxContainer extends ToolboxMenu {
  private ConductorEntity conductor;

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
  protected void init(Inventory inv, ToolboxBlockEntity contentHolderIn) {
    super.init(inv, contentHolderIn);
    this.conductor = ((MountedToolbox) contentHolderIn).parent;
  }

  @Override
  protected ToolboxBlockEntity createOnClient(FriendlyByteBuf extraData) {
    int conductorId = extraData.readVarInt();
    ClientLevel world = Minecraft.getInstance().level;
    Entity entity = world.getEntity(conductorId);
    if (!(entity instanceof ConductorEntity conductor)) {
      Railways.LOGGER.error("Conductor with ID not found: " + conductorId);
      return null;
    }
    MountedToolbox toolbox = conductor.getOrCreateToolboxHolder();
    toolbox.read(extraData.readNbt(), true);
    return toolbox;
  }

  @Override
  public boolean stillValid(Player player) {
    return player.distanceToSqr(conductor) < 8 * 8;
  }
}
