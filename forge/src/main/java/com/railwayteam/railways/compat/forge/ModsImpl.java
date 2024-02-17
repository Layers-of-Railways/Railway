package com.railwayteam.railways.compat.forge;

import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModsImpl {
    public static boolean isModLoaded(String id, @Nullable String fabricId) {
        List<ModInfo> mods = LoadingModList.get().getMods();
        for (ModInfo mod : mods) {
            if (mod.getModId().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
