package com.railwayteam.railways.util.client.forge;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientUtilsImpl {
    public static boolean isActiveAndMatches(KeyMapping mapping, InputConstants.Key keyCode) {
        return mapping.isActiveAndMatches(keyCode);
    }
}
