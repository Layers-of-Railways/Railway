package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CRSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Railways.MODID);

    public static final RegistryObject<SoundEvent> CONDUCTOR_WHISTLE = registerSoundEvent();



    private static RegistryObject<SoundEvent> registerSoundEvent() {
        return SOUND_EVENTS.register("conductor_whistle", () -> new SoundEvent(new ResourceLocation(Railways.MODID, "conductor_whistle")));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
