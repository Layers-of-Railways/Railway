package com.railwayteam.railways.content.conductor.toolbox;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.multiloader.C2SPacket;
import com.simibubi.create.content.curiosities.toolbox.ToolboxHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemHandlerHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.MutableBoolean;

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

			if (!conductorEntity.isCarryingToolbox())
				return;

			MountedToolboxHolder toolboxHolder = conductorEntity.getToolboxHolder();

			CompoundTag compound = player.getPersistentData()
					.getCompound("CreateToolboxData");
			MutableBoolean sendData = new MutableBoolean(false);

			toolboxHolder.inventory.inLimitedMode(inventory -> {
				for (int i = 0; i < 36; i++) {
					String key = String.valueOf(i);
					if (compound.contains(key) && compound.getCompound(key).hasUUID("EntityUUID") && compound.getCompound(key).getUUID("EntityUUID")
							.equals(conductorEntity.getUUID())) {
						ToolboxHandler.unequip(player, i, true);
						sendData.setTrue();
					}

					ItemStack itemStack = player.getInventory().getItem(i);
					ItemStack remainder = ItemHandlerHelper.insertItemStacked(toolboxHolder.inventory, itemStack, false);
					if (remainder.getCount() != itemStack.getCount())
						player.getInventory().setItem(i, remainder);
				}
			});

			if (sendData.booleanValue())
				ToolboxHandler.syncData(player);
		}
	}

}
