package com.railwayteam.railways.base.data;

import com.railwayteam.railways.multiloader.CommonTags;
import com.railwayteam.railways.registry.CRTags;
import com.railwayteam.railways.registry.CRTags.AllBlockTags;
import com.railwayteam.railways.registry.CRTags.AllItemTags;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.RegistrateItemTagsProvider;
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

	public static void generateItemTags(RegistrateItemTagsProvider tags) {
		CommonTags.DYES.values().forEach(tag -> tag.generateCommon(tags));
		CommonTags.IRON_NUGGETS.generateCommon(tags);
		CommonTags.ZINC_NUGGETS.generateCommon(tags);
		CommonTags.BRASS_NUGGETS.generateCommon(tags);
		CommonTags.COPPER_INGOTS.generateCommon(tags);
		CommonTags.BRASS_INGOTS.generateCommon(tags);
		CommonTags.IRON_INGOTS.generateCommon(tags);
		CommonTags.STRING.generateCommon(tags)
			.generateBoth(tags, tag -> tag.add(Items.STRING));
		CommonTags.IRON_PLATES.generateCommon(tags);
		CommonTags.BRASS_PLATES.generateCommon(tags);
		CommonTags.WORKBENCH.generateCommon(tags)
				.generateBoth(tags, tag -> tag.add(Items.CRAFTING_TABLE));

		for (AllItemTags tag : AllItemTags.values()) {
			if (tag.alwaysDatagen)
				tagAppender(tags, tag);
		}
	}

	public static TagAppender<Item> tagAppender(RegistrateItemTagsProvider prov, AllItemTags tag) {
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
