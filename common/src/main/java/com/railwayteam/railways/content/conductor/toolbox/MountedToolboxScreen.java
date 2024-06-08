/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
  public static AbstractSimiContainerScreen<MountedToolboxContainer> create(MountedToolboxContainer container, Inventory inv, Component title) {
    return (AbstractSimiContainerScreen) new MountedToolboxScreen(container, inv, title);
  }
}
