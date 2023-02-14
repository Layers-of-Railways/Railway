package com.railwayteam.railways.registry.fabric;

import io.github.fabricators_of_create.porting_lib.util.ItemGroupUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

public class CRItemsImpl {
	public static TagKey<Item> getTag(DyeColor color) {
		return color.getTag();
	}

	public static int nextTabId() {
		return ItemGroupUtil.expandArrayAndGetId();
	}
}
