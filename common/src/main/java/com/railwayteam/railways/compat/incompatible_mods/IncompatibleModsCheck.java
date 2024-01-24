package com.railwayteam.railways.compat.incompatible_mods;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.compat.incompatible_mods.optifine.OptifineWarningScreen;
import com.railwayteam.railways.config.CRConfigs;
import net.minecraft.client.Minecraft;

public class IncompatibleModsCheck {
    public static boolean optifinePresent = false;

    public static void run() {
        try {
            Class.forName("net.optifine.Config");
            optifinePresent = true;
        } catch (ClassNotFoundException e) {
            optifinePresent = false;
        }
    }

    public static void warnings(Minecraft mc) {
        if (IncompatibleModsCheck.optifinePresent) {
            if (!CRConfigs.client().disableOptifineWarning.get()) {
                Railways.LOGGER.error("Optifine Has been detected, Disabled Warning Status: false");
                mc.setScreen(new OptifineWarningScreen());
            } else {
                Railways.LOGGER.error("Optifine Has been detected, Disabled Warning Status: true");
            }
        }
    }
}
