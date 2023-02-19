package com.railwayteam.railways.util.forge;

import com.tterrag.registrate.providers.RegistrateTagsProvider;
import net.minecraft.data.tags.TagsProvider.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class UtilsImpl {
	public static boolean isModLoaded(String id) {
		return ModList.get().isLoaded(id);
	}

	public static Path configDir() {
		return FMLPaths.CONFIGDIR.get();
	}

	public static <T> TagAppender<T> builder(RegistrateTagsProvider<T> prov, TagKey<T> tag) {
		return prov.tag(tag);
	}

	public static boolean isDevEnv() {
		return !FMLLoader.isProduction();
	}
}
