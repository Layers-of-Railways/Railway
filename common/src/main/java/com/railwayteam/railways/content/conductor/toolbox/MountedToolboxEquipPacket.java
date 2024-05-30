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

package com.railwayteam.railways.content.conductor.toolbox;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.mixin.AccessorToolboxBlockEntity;
import com.railwayteam.railways.mixin.AccessorToolboxInventory;
import com.railwayteam.railways.multiloader.C2SPacket;
import com.railwayteam.railways.util.EntityUtils;
import com.simibubi.create.content.equipment.toolbox.ToolboxHandler;
import com.simibubi.create.content.equipment.toolbox.ToolboxInventory;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MountedToolboxEquipPacket implements C2SPacket {

	private Integer toolboxCarrierId = null;
	private final int slot;
	private final int hotbarSlot;

	public MountedToolboxEquipPacket(ConductorEntity toolboxCarrier, int slot, int hotbarSlot) {
		this.toolboxCarrierId = toolboxCarrier.getId();
		this.slot = slot;
		this.hotbarSlot = hotbarSlot;
	}

	public MountedToolboxEquipPacket(FriendlyByteBuf buffer) {
		if (buffer.readBoolean())
			toolboxCarrierId = buffer.readInt();
		slot = buffer.readVarInt();
		hotbarSlot = buffer.readVarInt();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeBoolean(toolboxCarrierId != null);
		if (toolboxCarrierId != null)
			buffer.writeInt(toolboxCarrierId);
		buffer.writeVarInt(slot);
		buffer.writeVarInt(hotbarSlot);
	}

	@Override
	public void handle(ServerPlayer player) {
		Level world = player.level;

		if (toolboxCarrierId == null) {
			ToolboxHandler.unequip(player, hotbarSlot, false);
			ToolboxHandler.syncData(player);
			return;
		}

		Entity entity = world.getEntity(toolboxCarrierId);

		double maxRange = ToolboxHandler.getMaxRange(player);
		if (player.distanceToSqr(entity) > maxRange
				* maxRange)
			return;
		if (!(entity instanceof ConductorEntity conductorEntity))
			return;

		ToolboxHandler.unequip(player, hotbarSlot, false);

		if (slot < 0 || slot >= 8) {
			ToolboxHandler.syncData(player);
			return;
		}

		MountedToolbox toolbox = conductorEntity.getToolbox();
		if (toolbox == null)
			return;

		ItemStack held = player.getInventory().getItem(hotbarSlot);
		if (!held.isEmpty()) {
			ToolboxInventory inv = ((AccessorToolboxBlockEntity) toolbox).getInventory();
			AccessorToolboxInventory invAccess = (AccessorToolboxInventory) inv;

			ItemStack filterStack = invAccess.getFilters().get(slot);
			if (!ToolboxInventory.canItemsShareCompartment(held, filterStack)) {
				inv.inLimitedMode($ -> doEquip(player, hotbarSlot, held, inv));
			}
		}

		CompoundTag playerData = EntityUtils.getPersistentData(player);
		CompoundTag compound = playerData.getCompound("CreateToolboxData");
		String key = String.valueOf(hotbarSlot);

		CompoundTag data = new CompoundTag();
		data.putInt("Slot", slot);
		data.putUUID("EntityUUID", conductorEntity.getUUID());
		data.put("Pos", NbtUtils.writeBlockPos(new BlockPos(0, 1000, 0)));
		compound.put(key, data);

		playerData.put("CreateToolboxData", compound);

		toolbox.connectPlayer(slot, player, hotbarSlot);
		ToolboxHandler.syncData(player);
	}

	@ExpectPlatform
	public static void doEquip(ServerPlayer player, int hotbarSlot, ItemStack held, ToolboxInventory inv) {
		throw new AssertionError();
	}
}
