package com.railwayteam.railways.multiloader.forge;

import com.railwayteam.railways.multiloader.CommonTag;
import net.minecraft.tags.TagKey;

public class CommonTagImpl<T> extends CommonTag<T> {
	protected CommonTagImpl(TagKey<T> forge, TagKey<T> fabric) {
		super(forge, fabric);
	}

	@Override
	public TagKey<T> resolve() {
		return forge;
	}

	public static <T> CommonTag<T> create(TagKey<T> forge, TagKey<T> fabric) {
		return new CommonTagImpl<>(forge, fabric);
	}
}
