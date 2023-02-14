package com.railwayteam.railways.registry.forge;

import com.railwayteam.railways.Railways;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CRSoundsImpl {
	private static final DeferredRegister<SoundEvent> register = DeferredRegister.create(Registry.SOUND_EVENT_REGISTRY, Railways.MODID);

	public static Supplier<SoundEvent> registerSoundEvent(String name) {
		return register.register(name, () -> new SoundEvent(new ResourceLocation(Railways.MODID, name)));
	}

	public static void register() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		register.register(bus);
	}
}
