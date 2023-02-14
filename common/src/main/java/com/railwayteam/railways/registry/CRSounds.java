package com.railwayteam.railways.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class CRSounds {
    public static final Supplier<SoundEvent> CONDUCTOR_WHISTLE = registerSoundEvent("conductor_whistle");

    @ExpectPlatform
    public static Supplier<SoundEvent> registerSoundEvent(String name) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void register() {
        throw new AssertionError();
    }
}
