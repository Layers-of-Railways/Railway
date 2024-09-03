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
