package com.railwayteam.railways.util.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;

@Environment(EnvType.CLIENT)
public class ClientUtils {
    @ExpectPlatform
    public static boolean isActiveAndMatches(KeyMapping mapping, InputConstants.Key keyCode) {
        throw new AssertionError();
    }
}
