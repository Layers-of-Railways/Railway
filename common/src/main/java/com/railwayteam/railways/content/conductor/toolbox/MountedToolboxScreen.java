package com.railwayteam.railways.content.conductor.toolbox;

import com.simibubi.create.content.curiosities.toolbox.ToolboxScreen;
import com.simibubi.create.foundation.gui.container.AbstractSimiContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MountedToolboxScreen extends ToolboxScreen {
  public MountedToolboxScreen(MountedToolboxContainer container, Inventory inv, Component title) {
    super(container, inv, title);
  }

  @SuppressWarnings({"unchecked", "rawtypes"}) // this should be safe
  public static AbstractSimiContainerScreen<MountedToolboxContainer> create(MountedToolboxContainer container, Inventory inv, Component title) {
    return (AbstractSimiContainerScreen) new MountedToolboxScreen(container, inv, title);
  }
}
