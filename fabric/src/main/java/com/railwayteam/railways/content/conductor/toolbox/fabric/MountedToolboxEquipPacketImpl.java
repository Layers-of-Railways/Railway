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

package com.railwayteam.railways.content.conductor.toolbox.fabric;

import com.simibubi.create.content.equipment.toolbox.ToolboxInventory;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class MountedToolboxEquipPacketImpl {
	public static void doEquip(ServerPlayer player, int hotbarSlot, ItemStack held, ToolboxInventory inv) {
		try (Transaction t = TransferUtil.getTransaction()) {
			ItemVariant stack = ItemVariant.of(held);
			long count = held.getCount();
			long inserted = inv.insert(stack, count, t);
			if (inserted != count)
				inserted += TransferUtil.insertToMainInv(player, stack, count - inserted);
			long remainder = count - inserted;
			if (remainder != count) {
				t.commit();
				ItemStack newStack = player.getSlot(hotbarSlot).get().copy();
				newStack.setCount((int) remainder);
				player.getInventory().setItem(hotbarSlot, newStack);
			}
		}
	}
}
