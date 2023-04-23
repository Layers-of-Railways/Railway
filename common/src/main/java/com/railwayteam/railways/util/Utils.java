package com.railwayteam.railways.util;

import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.data.tags.TagsProvider.TagAppender;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class Utils {
	@ExpectPlatform
	public static boolean isModLoaded(String id, @Nullable String fabricId) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static Path configDir() {
		throw new AssertionError();
	}

	@ExpectPlatform
	@Contract // shut
	public static boolean isDevEnv() {
		throw new AssertionError();
	}
}
