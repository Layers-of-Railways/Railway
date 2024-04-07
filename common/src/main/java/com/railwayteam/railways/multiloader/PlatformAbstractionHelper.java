package com.railwayteam.railways.multiloader;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.Item;

public class PlatformAbstractionHelper {
    @ExpectPlatform
    public static int getBurnTime(Item item) {
        throw new AssertionError();
    }
}
