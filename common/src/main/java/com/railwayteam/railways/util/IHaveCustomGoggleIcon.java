package com.railwayteam.railways.util;

import com.simibubi.create.AllItems;
import net.minecraft.world.item.ItemStack;

public interface IHaveCustomGoggleIcon {
    /**
     * this method will be called when looking at a BlockEntity that implemented this
     * interface
     */
    default ItemStack setGoggleIcon(boolean isPlayerSneaking) {
        return AllItems.GOGGLES.asStack();
    }
}
