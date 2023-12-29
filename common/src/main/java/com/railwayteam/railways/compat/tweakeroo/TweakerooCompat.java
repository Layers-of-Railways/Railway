package com.railwayteam.railways.compat.tweakeroo;

import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TweakerooCompat {
    public static boolean inFreecam() {
        return FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue();
    }
}
