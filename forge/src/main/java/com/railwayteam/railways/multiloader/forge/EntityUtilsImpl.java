package com.railwayteam.railways.multiloader.forge;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public class EntityUtilsImpl {
	public static CompoundTag getPersistentData(Entity entity) {
		return entity.getPersistentData();
	}

	public static void givePlayerItem(Player player, ItemStack stack) {
		ItemHandlerHelper.giveItemToPlayer(player, stack);
	}
}
