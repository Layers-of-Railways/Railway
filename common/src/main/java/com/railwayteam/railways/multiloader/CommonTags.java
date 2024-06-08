/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.multiloader;

import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class CommonTags {
	public static final List<CommonTag<Item>> ALL_ITEMS = new ArrayList<>();
	public static final List<CommonTag<Block>> ALL_BLOCKS = new ArrayList<>();

	public static final CommonTag<Item>
			STRING = item("string"),
			IRON_NUGGETS = item("nuggets/iron_nuggets", "iron_nuggets", "nuggets/iron"),
			ZINC_NUGGETS = item("nuggets/zinc_nuggets", "zinc_nuggets", "nuggets/zinc"),
			BRASS_NUGGETS = item("nuggets/brass_nuggets", "brass_nuggets", "nuggets/brass"),
			IRON_PLATES = item("plates/iron_plates", "iron_plates", "plates/iron"),
			BRASS_PLATES = item("plates/brass_plates", "brass_plates", "plates/brass"),
			COPPER_INGOTS = item("ingots/copper_ingots", "copper_ingots", "ingots/copper"),
			BRASS_INGOTS = item("ingots/brass_ingots", "brass_ingots", "ingots/brass"),
			IRON_INGOTS = item("ingots/iron_ingots", "iron_ingots", "ingots/iron"),
			WORKBENCH = item("workbench");

	public static final Map<DyeColor, CommonTag<Item>> DYES = Util.make(new EnumMap<>(DyeColor.class), dyes -> {
		for (DyeColor color : DyeColor.values()) {
			String name = color.getName();
			String common = "dyes/" + name + "_dyes";
			String fabric = name + "_dyes";
			String forge = "dyes/" + name;
			dyes.put(color, item(common, fabric, forge));
		}
	});

	public static final CommonTag<Block>
			RELOCATION_NOT_SUPPORTED = block("relocation_not_supported");

	public static CommonTag<Block> block(String common, String fabric, String forge) {
		CommonTag<Block> tag = CommonTag.conventional(Registries.BLOCK, common, fabric, forge);
		ALL_BLOCKS.add(tag);
		return tag;
	}

	public static CommonTag<Block> block(String path) {
		return block(path, path, path);
	}

	public static CommonTag<Item> item(String common, String fabric, String forge) {
		CommonTag<Item> tag = CommonTag.conventional(Registries.ITEM, common, fabric, forge);
		ALL_ITEMS.add(tag);
		return tag;
	}

	public static CommonTag<Item> item(String path) {
		return item(path, path, path);
	}
}
