package com.railwayteam.railways.content.conductor.toolbox.forge;

import com.simibubi.create.content.curiosities.toolbox.ItemReturnInvWrapper;
import com.simibubi.create.content.curiosities.toolbox.ToolboxInventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public class MountedToolboxEquipPacketImpl {
	public static void doEquip(ServerPlayer player, int hotbarSlot, ItemStack held, ToolboxInventory inv) {
		ItemStack remainder = ItemHandlerHelper.insertItemStacked(inv, held, false);
		if (!remainder.isEmpty())
			remainder = ItemHandlerHelper.insertItemStacked(new ItemReturnInvWrapper(player.getInventory()),
					remainder, false);
		if (remainder.getCount() != held.getCount())
			player.getInventory().setItem(hotbarSlot, remainder);
	}
}
