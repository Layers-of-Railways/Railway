package com.railwayteam.railways.content.roller_extensions.forge;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.foundation.item.ItemHelper;
import net.minecraft.world.item.ItemStack;

public class TrackReplacePaverImpl {
    public static ItemStack extract(ItemStack filter, MovementContext context, int amt) {
        return ItemHelper.extract(context.contraption.getSharedInventory(),
                stack -> FilterItem.test(context.world, stack, filter), amt, false);
    }
}
