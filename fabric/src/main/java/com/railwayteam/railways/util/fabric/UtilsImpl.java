package com.railwayteam.railways.util.fabric;

import com.tterrag.registrate.providers.RegistrateTagsProvider;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.data.tags.TagsProvider.TagAppender;
import net.minecraft.tags.TagKey;

import java.nio.file.Path;

public class UtilsImpl {
	public static boolean isModLoaded(String id) {
		return FabricLoader.getInstance().isModLoaded(id);
	}

	public static Path configDir() {
		return FabricLoader.getInstance().getConfigDir();
	}

	public static <T> TagAppender<T> builder(RegistrateTagsProvider<T> prov, TagKey<T> tag) {
		return prov.tag(tag);
	}
}
