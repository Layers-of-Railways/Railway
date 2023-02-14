package com.railwayteam.railways.registry.fabric;

import com.railwayteam.railways.Railways;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class CRSoundsImpl {
	public static Supplier<SoundEvent> registerSoundEvent(String name) {
		ResourceLocation id = Railways.asResource(name);
		SoundEvent sound = new SoundEvent(id);
		Registry.register(Registry.SOUND_EVENT, id, sound);
		return () -> sound;
	}

	public static void register() {
		// nothing to do here
	}
}
