package com.railwayteam.railways.multiloader;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * A pair of tags defining the same behavior with different formats across loaders.
 */
public abstract class CommonTag<T> {
	protected final TagKey<T> forge;
	protected final TagKey<T> fabric;

	public static final CommonTag<Item>
			STRING = create(Registry.ITEM_REGISTRY, "string"),
			IRON_NUGGETS = create(Registry.ITEM_REGISTRY, "nuggets/iron", "iron_nuggets"),
			ZINC_NUGGETS = create(Registry.ITEM_REGISTRY, "nuggets/zinc", "zinc_nuggets");

	protected CommonTag(TagKey<T> forge, TagKey<T> fabric) {
		this.forge = forge;
		this.fabric = fabric;
	}

	public abstract TagKey<T> resolve();

	public static <T> CommonTag<T> create(ResourceKey<? extends Registry<T>> registry, String path) {
		return create(registry, path, path);
	}

	public static <T> CommonTag<T> create(ResourceKey<? extends Registry<T>> registry, String forgePath, String fabricPath) {
		TagKey<T> forge = TagKey.create(registry, new ResourceLocation("forge", forgePath));
		TagKey<T> fabric = TagKey.create(registry, new ResourceLocation("c", fabricPath));
		return create(forge, fabric);
	}

	@ExpectPlatform
	public static <T> CommonTag<T> create(TagKey<T> forge, TagKey<T> fabric) {
		throw new AssertionError();
	}
}
