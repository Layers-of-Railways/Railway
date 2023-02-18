package com.railwayteam.railways.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Contract;

public class ItemUtils {
	@ExpectPlatform
	@Contract // shut
	public static boolean blocksEndermanView(ItemStack stack, Player wearer, EnderMan enderman) {
		throw new AssertionError();
	}

	// despite seeming useless at first glance, this is needed. The 2 impls are different.
	@ExpectPlatform
	public static TagKey<Item> getTag(DyeColor color) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static int nextTabId() {
		throw new AssertionError();
	}
}
