package com.railwayteam.railways.compat.incompatible_mods.fabric;

import com.railwayteam.railways.compat.incompatible_mods.IncompatibleModsCheck;
import com.railwayteam.railways.compat.incompatible_mods.optifine.OptifineWarningScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class IncompatibleModsCheckImpl {
    public static void showOptifineScreen() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {

        });
    }
}
