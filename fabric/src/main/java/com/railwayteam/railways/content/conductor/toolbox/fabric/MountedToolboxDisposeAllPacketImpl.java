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

package com.railwayteam.railways.content.conductor.toolbox.fabric;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.content.conductor.toolbox.MountedToolbox;
import com.railwayteam.railways.mixin.AccessorToolboxBlockEntity;
import com.railwayteam.railways.util.EntityUtils;
import com.simibubi.create.content.equipment.toolbox.ToolboxHandler;
import com.simibubi.create.content.equipment.toolbox.ToolboxInventory;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.UUID;

public class MountedToolboxDisposeAllPacketImpl {
	public static boolean doDisposal(MountedToolbox toolbox, ServerPlayer player, ConductorEntity conductor) {
		CompoundTag compound = EntityUtils.getPersistentData(player).getCompound("CreateToolboxData");
		MutableBoolean sendData = new MutableBoolean(false);
		ToolboxInventory inv = ((AccessorToolboxBlockEntity) toolbox).getInventory();
		inv.inLimitedMode(inventory -> {
			try (Transaction t = TransferUtil.getTransaction()) {
				PlayerInventoryStorage playerInv = PlayerInventoryStorage.of(player);
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

					SingleSlotStorage<ItemVariant> slot = playerInv.getSlot(i);
					if (slot.isResourceBlank())
						continue;
					long amount = slot.getAmount();
					ItemVariant resource = slot.getResource();

					long inserted = inventory.insert(resource, amount, t);
					if (inserted == 0)
						continue;
					slot.extract(resource, inserted, t);
				}
				t.commit();
			}
		});
		return sendData.booleanValue();
	}
}
