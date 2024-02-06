package com.railwayteam.railways.util.client.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.fabricators_of_create.porting_lib.util.KeyBindingHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;

@Environment(EnvType.CLIENT)
public class ClientUtilsImpl {
    public static boolean isActiveAndMatches(KeyMapping mapping, InputConstants.Key keyCode) {
        return KeyBindingHelper.isActiveAndMatches(mapping, keyCode);
    }
}
