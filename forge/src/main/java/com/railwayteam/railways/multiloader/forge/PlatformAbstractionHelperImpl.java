package com.railwayteam.railways.multiloader.forge;

import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeHooks;

public class PlatformAbstractionHelperImpl {
    public static int getBurnTime(Item item) {
        return ForgeHooks.getBurnTime(item.getDefaultInstance(), null);
    }
}
