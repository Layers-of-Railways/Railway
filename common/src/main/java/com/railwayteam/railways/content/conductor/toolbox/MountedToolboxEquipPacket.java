package com.railwayteam.railways.content.conductor.toolbox;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.mixin.AccessorToolboxInventory;
import com.railwayteam.railways.mixin.AccessorToolboxTileEntity;
import com.railwayteam.railways.multiloader.C2SPacket;
import com.railwayteam.railways.util.EntityUtils;
import com.simibubi.create.content.curiosities.toolbox.ToolboxHandler;
import com.simibubi.create.content.curiosities.toolbox.ToolboxInventory;
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

		MountedToolbox toolbox = conductorEntity.getToolbox();
		if (toolbox == null)
			return;

		ItemStack held = player.getInventory().getItem(hotbarSlot);
		if (!held.isEmpty()) {
			ToolboxInventory inv = ((AccessorToolboxTileEntity) toolbox).getInventory();
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
