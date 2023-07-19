package com.railwayteam.railways.base.data.fabric;

import com.tterrag.registrate.providers.RegistrateTagsProvider;
import net.minecraft.data.tags.TagsProvider.TagAppender;
import net.minecraft.tags.TagKey;

public class CRTagGenImpl {
	public static <T> TagAppender<T> tagAppender(RegistrateTagsProvider<T> prov, TagKey<T> tag) {
		// fixme should work ig
		return prov.addTag(tag);
	}
}
