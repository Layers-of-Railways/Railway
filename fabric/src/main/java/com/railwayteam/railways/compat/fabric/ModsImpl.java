package com.railwayteam.railways.compat.fabric;

import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

public class ModsImpl {
    public static boolean isModLoaded(String id, @Nullable String fabricId) {
        return FabricLoader.getInstance().isModLoaded(fabricId != null ? fabricId : id);
    }
}
