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
import com.railwayteam.railways.multiloader.C2SPacket;
import com.simibubi.create.content.equipment.toolbox.ToolboxHandler;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class MountedToolboxDisposeAllPacket implements C2SPacket {

	private final int toolboxCarrierId;

	public MountedToolboxDisposeAllPacket(ConductorEntity toolboxCarrier) {
		this.toolboxCarrierId = toolboxCarrier.getId();
	}

	public MountedToolboxDisposeAllPacket(FriendlyByteBuf buffer) {
		toolboxCarrierId = buffer.readInt();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(toolboxCarrierId);
	}

	@Override
	public void handle(ServerPlayer player) {
		Level world = player.level;
		if (world.getEntity(toolboxCarrierId) instanceof ConductorEntity conductorEntity) {

			double maxRange = ToolboxHandler.getMaxRange(player);
			if (player.distanceToSqr(conductorEntity) > maxRange
					* maxRange)
				return;

			MountedToolbox toolbox = conductorEntity.getToolbox();
			if (toolbox == null)
				return;
			boolean sendData = doDisposal(toolbox, player, conductorEntity);

			if (sendData)
				ToolboxHandler.syncData(player);
		}
	}

	@ExpectPlatform
	public static boolean doDisposal(MountedToolbox toolbox, ServerPlayer player, ConductorEntity conductor) {
		throw new AssertionError();
	}
}
