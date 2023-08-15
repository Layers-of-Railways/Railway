package com.railwayteam.railways.config;

import com.simibubi.create.foundation.config.ConfigBase;
import com.simibubi.create.foundation.config.ui.ConfigAnnotations;

public class CCommon extends ConfigBase {

    public final ConfigBool registerMissingTracks = b(false, "registerMissingTracks", Comments.registerMissingTracks, ConfigAnnotations.RequiresRestart.BOTH.asComment());
    public final ConfigBool disableDatafixer = b(false, "disableDatafixer", Comments.disableDatafixer, ConfigAnnotations.RequiresRestart.BOTH.asComment());

    @Override
    public String getName() {
        return "common";
    }

    private static class Comments {
        static String registerMissingTracks = "Register integration tracks for mods that are not present";
        static String disableDatafixer = "Disable Steam 'n' Rails datafixers. Do not enable this config if your world contains pre-Create 0.5.1 monobogeys, because then they will be destroyed";
    }
}
