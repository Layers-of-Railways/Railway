package com.railwayteam.railways.content.conductor.toolbox;

import com.simibubi.create.content.equipment.toolbox.ToolboxScreen;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MountedToolboxScreen extends ToolboxScreen {
  public MountedToolboxScreen(MountedToolboxContainer container, Inventory inv, Component title) {
    super(container, inv, title);
  }

  @SuppressWarnings({"unchecked", "rawtypes"}) // this should be safe
  public static AbstractSimiContainerScreen<MountedToolboxScreen> create(MountedToolboxScreen screen, Inventory inv, Component title) {
    return (AbstractSimiContainerScreen) new MountedToolboxScreen(screen, inv, title);
  }
}
