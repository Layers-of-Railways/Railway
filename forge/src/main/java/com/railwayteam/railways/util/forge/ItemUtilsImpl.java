package com.railwayteam.railways.util.forge;

import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ItemUtilsImpl {
	public static boolean blocksEndermanView(ItemStack stack, Player wearer, EnderMan enderman) {
		return stack.isEnderMask(wearer, enderman);
	}

	// FIXME figure out how to fix this *sigh*
	public static int nextTabId() {
		return CreativeModeTab.getGroupCountSafe();
	}
}
