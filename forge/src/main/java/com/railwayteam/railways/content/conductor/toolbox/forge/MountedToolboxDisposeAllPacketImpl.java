package com.railwayteam.railways.content.conductor.toolbox.forge;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.content.conductor.toolbox.MountedToolbox;
import com.railwayteam.railways.mixin.AccessorToolboxTileEntity;
import com.railwayteam.railways.util.EntityUtils;
import com.simibubi.create.content.curiosities.toolbox.ToolboxHandler;
import com.simibubi.create.content.curiosities.toolbox.ToolboxInventory;
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
		ToolboxInventory inv = ((AccessorToolboxTileEntity) toolbox).getInventory();
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
