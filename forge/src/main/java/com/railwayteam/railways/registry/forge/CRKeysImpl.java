package com.railwayteam.railways.registry.forge;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;

import java.util.ArrayList;
import java.util.List;

public class CRKeysImpl {
    private static final List<KeyMapping> KEYBINDS = new ArrayList<>();

    public static void registerKeyBinding(KeyMapping keyMapping) {
        KEYBINDS.add(keyMapping);
    }

    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        for (KeyMapping keyMapping : KEYBINDS) {
            event.register(keyMapping);
        }
    }
}
