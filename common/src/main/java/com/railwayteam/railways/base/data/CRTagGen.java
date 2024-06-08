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

package com.railwayteam.railways.base.data;

import com.railwayteam.railways.multiloader.CommonTags;
import com.railwayteam.railways.registry.CRTags;
import com.railwayteam.railways.registry.CRTags.AllBlockTags;
import com.railwayteam.railways.registry.CRTags.AllItemTags;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.data.tags.TagsProvider.TagAppender;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Based on {@link TagGen}
 */
public class CRTagGen {
	private static final Map<TagKey<Block>, List<ResourceLocation>> OPTIONAL_TAGS = new HashMap<>();

	@SafeVarargs
	public static void addOptionalTag(ResourceLocation id, TagKey<Block>... tags) {
		for (TagKey<Block> tag : tags) {
			OPTIONAL_TAGS.computeIfAbsent(tag, (e) -> new ArrayList<>()).add(id);
		}
	}
	public static void generateBlockTags(RegistrateTagsProvider<Block> prov) {
		prov.tag(CRTags.AllBlockTags.SEMAPHORE_POLES.tag)
				.add(AllBlocks.METAL_GIRDER.get(), AllBlocks.METAL_GIRDER_ENCASED_SHAFT.get())
				.forceAddTag(BlockTags.FENCES);

		prov.tag(CRTags.AllBlockTags.TRACK_CASING_BLACKLIST.tag);

		// VALIDATE

		for (CRTags.AllBlockTags tag : CRTags.AllBlockTags.values()) {
			if (tag.alwaysDatagen) {
				tagAppender(prov, tag);
			}
		}

		for (TagKey<Block> tag : OPTIONAL_TAGS.keySet()) {
			var appender = tagAppender(prov, tag);
			for (ResourceLocation loc : OPTIONAL_TAGS.get(tag))
				appender.addOptional(loc);
		}
	}

	public static void generateItemTags(RegistrateTagsProvider<Item> prov) {
		CommonTags.DYES.values().forEach(tag -> tag.generateCommon(prov));
		CommonTags.IRON_NUGGETS.generateCommon(prov);
		CommonTags.ZINC_NUGGETS.generateCommon(prov);
		CommonTags.BRASS_NUGGETS.generateCommon(prov);
		CommonTags.COPPER_INGOTS.generateCommon(prov);
		CommonTags.BRASS_INGOTS.generateCommon(prov);
		CommonTags.IRON_INGOTS.generateCommon(prov);
		CommonTags.STRING.generateCommon(prov)
			.generateBoth(prov, tag -> tag.add(Items.STRING));
		CommonTags.IRON_PLATES.generateCommon(prov);
		CommonTags.BRASS_PLATES.generateCommon(prov);
		CommonTags.WORKBENCH.generateCommon(prov)
				.generateBoth(prov, tag -> tag.add(Items.CRAFTING_TABLE));

		prov.tag(AllItemTags.NOT_TRAIN_FUEL.tag);

		for (AllItemTags tag : AllItemTags.values()) {
			if (tag.alwaysDatagen)
				tagAppender(prov, tag);
		}
	}

	public static TagAppender<Item> tagAppender(RegistrateTagsProvider<Item> prov, AllItemTags tag) {
		return tagAppender(prov, tag.tag);
	}

	public static TagAppender<Block> tagAppender(RegistrateTagsProvider<Block> prov, AllBlockTags tag) {
		return tagAppender(prov, tag.tag);
	}

	@ExpectPlatform
	public static <T> TagAppender<T> tagAppender(RegistrateTagsProvider<T> prov, TagKey<T> tag) {
		throw new AssertionError();
	}
}
