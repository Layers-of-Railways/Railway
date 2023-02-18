package com.railwayteam.railways.multiloader;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public class EntityUtils {
	@ExpectPlatform
	public static CompoundTag getPersistentData(Entity entity) {
		throw new AssertionError();
	}
}
