package com.railwayteam.railways.content.roller_extensions.forge;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.foundation.item.ItemHelper;
import net.minecraft.world.item.ItemStack;

public class TrackReplacePaverImpl {
    public static ItemStack extract(FilterItemStack filter, MovementContext context, int amt) {
        return ItemHelper.extract(context.contraption.getSharedInventory(),
                stack -> filter.test(context.world, stack), amt, false);
    }
}
