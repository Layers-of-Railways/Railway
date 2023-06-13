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
