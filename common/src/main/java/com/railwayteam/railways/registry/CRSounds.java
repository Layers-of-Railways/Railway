package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;

public class CRSounds {
    public static final RegistryEntry<SoundEvent> CONDUCTOR_WHISTLE = Railways.registrate()
        .simple(
            "conductor_whistle",
            Registries.SOUND_EVENT,
            () -> SoundEvent.createVariableRangeEvent(Railways.asResource("conductor_whistle"))
        );

    public static final RegistryEntry<SoundEvent> HANDCAR_COGS = Railways.registrate()
        .simple(
            "handcar_cogs",
            Registries.SOUND_EVENT,
            () -> SoundEvent.createVariableRangeEvent(Railways.asResource("handcar_cogs"))
        );

    public static void register() {
    }
}
