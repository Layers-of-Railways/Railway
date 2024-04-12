package com.railwayteam.railways.multiloader.fabric;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.Item;

public class PlatformAbstractionHelperImpl {
    public static int getBurnTime(Item item) {
        Integer time = FuelRegistry.INSTANCE.get(item);
        return time != null ? time : 0;
    }
}
