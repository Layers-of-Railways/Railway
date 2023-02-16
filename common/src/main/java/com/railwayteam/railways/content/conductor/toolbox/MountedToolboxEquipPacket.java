package com.railwayteam.railways.content.conductor.toolbox;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.multiloader.C2SPacket;
import com.simibubi.create.content.curiosities.toolbox.ToolboxHandler;
import com.simibubi.create.content.curiosities.toolbox.ToolboxInventory;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemHandlerHelper;
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
	public void handle(ServerPlayer player, FriendlyByteBuf buf) {
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

		if (!conductorEntity.isCarryingToolbox())
			return;

		MountedToolboxHolder toolboxHolder = conductorEntity.getToolboxHolder();

		ItemStack playerStack = player.getInventory().getItem(hotbarSlot);
		if (!playerStack.isEmpty() && !ToolboxInventory.canItemsShareCompartment(playerStack,
				toolboxHolder.inventory.filters.get(slot))) {
			toolboxHolder.inventory.inLimitedMode(inventory -> {
				ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, playerStack, false);
				if (!remainder.isEmpty())
					remainder = ItemHandlerHelper.insertItemStacked(new ItemReturnInvWrapper(player.getInventory()),
							remainder, false);
				if (remainder.getCount() != playerStack.getCount())
					player.getInventory().setItem(hotbarSlot, remainder);
			});
		}

		CompoundTag compound = player.getPersistentData()
				.getCompound("CreateToolboxData");
		String key = String.valueOf(hotbarSlot);

		CompoundTag data = new CompoundTag();
		data.putInt("Slot", slot);
		data.putUUID("EntityUUID", conductorEntity.getUUID());
		data.put("Pos", NbtUtils.writeBlockPos(new BlockPos(0, 1000, 0)));
		compound.put(key, data);

		player.getPersistentData()
				.put("CreateToolboxData", compound);

		toolboxHolder.connectPlayer(slot, player, hotbarSlot);
		ToolboxHandler.syncData(player);
	}

}
