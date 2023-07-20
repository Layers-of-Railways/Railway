package com.railwayteam.railways.util.fabric;

import com.railwayteam.railways.registry.CRItems;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ItemUtilsImpl {
	public static boolean blocksEndermanView(ItemStack stack, Player wearer, EnderMan enderman) {
		return stack.is(Items.CARVED_PUMPKIN) || stack.is(CRItems.CONDUCTOR_CAPS);
	}
}
