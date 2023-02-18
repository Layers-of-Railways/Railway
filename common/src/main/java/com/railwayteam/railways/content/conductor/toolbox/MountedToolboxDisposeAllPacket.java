package com.railwayteam.railways.content.conductor.toolbox;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.multiloader.C2SPacket;
import com.simibubi.create.content.curiosities.toolbox.ToolboxHandler;
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
	public void handle(ServerPlayer player, FriendlyByteBuf buf) {
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
