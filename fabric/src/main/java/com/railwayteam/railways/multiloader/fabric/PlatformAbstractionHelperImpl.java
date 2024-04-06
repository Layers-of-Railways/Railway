package com.railwayteam.railways.multiloader.fabric;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.Item;

public class PlatformAbstractionHelperImpl {
    public static int getBurnTime(Item item) {
        return FuelRegistry.INSTANCE.get(item);
    }
}
