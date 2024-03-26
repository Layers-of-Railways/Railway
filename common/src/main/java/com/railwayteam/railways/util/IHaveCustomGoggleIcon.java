package com.railwayteam.railways.util;

import com.simibubi.create.AllItems;
import net.minecraft.world.item.ItemStack;

// TODO - Remove when https://github.com/Creators-of-Create/Create/pull/5900 is merged
// Has been merged, awaiting release
@Deprecated
public interface IHaveCustomGoggleIcon {
    /**
     * this method will be called when looking at a BlockEntity that implemented this
     * interface
     */
    default ItemStack railways$setGoggleIcon(boolean isPlayerSneaking) {
        return AllItems.GOGGLES.asStack();
    }
}
