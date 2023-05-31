package com.railwayteam.railways.base.data;

import com.railwayteam.railways.multiloader.CommonTags;
import com.railwayteam.railways.registry.CRTags.AllBlockTags;
import com.railwayteam.railways.registry.CRTags.AllItemTags;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.RegistrateItemTagsProvider;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.data.tags.TagsProvider.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

/**
 * Based on {@link TagGen}
 */
public class CRTagGen {
	public static void generateBlockTags(RegistrateTagsProvider<Block> tags) {
		tagAppender(tags, AllBlockTags.TRACKS)
			.add(AllBlocks.TRACK.get());
	}

	public static void generateItemTags(RegistrateItemTagsProvider tags) {
		CommonTags.DYES.values().forEach(tag -> tag.generateCommon(tags));
		CommonTags.IRON_NUGGETS.generateCommon(tags);
		CommonTags.ZINC_NUGGETS.generateCommon(tags);
		CommonTags.BRASS_NUGGETS.generateCommon(tags);
		CommonTags.COPPER_INGOTS.generateCommon(tags);
		CommonTags.STRING.generateCommon(tags)
			.generateBoth(tags, tag -> tag.add(Items.STRING));
		CommonTags.IRON_PLATES.generateCommon(tags);
//			.generateBoth(tags, tag -> tag.add(AllItems.IRON_SHEET.get()));

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
