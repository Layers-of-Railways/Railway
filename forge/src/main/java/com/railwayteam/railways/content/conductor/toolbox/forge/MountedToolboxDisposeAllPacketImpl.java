/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.conductor.toolbox.forge;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.content.conductor.toolbox.MountedToolbox;
import com.railwayteam.railways.mixin.AccessorToolboxBlockEntity;
import com.railwayteam.railways.util.EntityUtils;
import com.simibubi.create.content.equipment.toolbox.ToolboxHandler;
import com.simibubi.create.content.equipment.toolbox.ToolboxInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.UUID;

public class MountedToolboxDisposeAllPacketImpl {
	public static boolean doDisposal(MountedToolbox toolbox, ServerPlayer player, ConductorEntity conductor) {
		CompoundTag compound = EntityUtils.getPersistentData(player).getCompound("CreateToolboxData");
		MutableBoolean sendData = new MutableBoolean(false);
		ToolboxInventory inv = ((AccessorToolboxBlockEntity) toolbox).getInventory();
		inv.inLimitedMode(inventory -> {
			for (int i = 0; i < 36; i++) {
				String key = String.valueOf(i);
				if (compound.contains(key)) {
					CompoundTag data = compound.getCompound(key);
					if (data.hasUUID("EntityUUID")) {
						UUID uuid = data.getUUID("EntityUUID");
						if (uuid.equals(conductor.getUUID())) {
							ToolboxHandler.unequip(player, i, true);
							sendData.setTrue();
						}
					}
				}

				ItemStack itemStack = player.getInventory().getItem(i);
				ItemStack remainder = ItemHandlerHelper.insertItemStacked(inv, itemStack, false);
				if (remainder.getCount() != itemStack.getCount())
					player.getInventory().setItem(i, remainder);
			}
		});
		return sendData.booleanValue();
	}
}
