package com.railwayteam.railways.multiloader;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class EntityUtils {
	@ExpectPlatform
	public static CompoundTag getPersistentData(Entity entity) {
		throw new AssertionError();
	}

	/**
	 * Gives a player an item. Plays the pickup sound, and drops whatever can't be picked up.
	 */
	@ExpectPlatform
	public static void givePlayerItem(Player player, ItemStack stack) {
		throw new AssertionError();
	}
}
