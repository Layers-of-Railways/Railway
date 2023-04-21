package com.railwayteam.railways.content.minecarts.forge;

import com.railwayteam.railways.content.minecarts.MinecartWorkbench;
import com.railwayteam.railways.registry.CRItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class MinecartWorkbenchImpl extends MinecartWorkbench {

    protected MinecartWorkbenchImpl(Level level, double x, double y, double z) {
        super(level, x, y, z);
    }

    public MinecartWorkbenchImpl(EntityType<?> type, Level level) {
        super(type, level);
    }

    public static MinecartWorkbench create(Level level, double x, double y, double z) {
        return new MinecartWorkbenchImpl(level, x, y, z);
    }

    @Override
    protected Item getDropItem() {
        return CRItems.ITEM_BENCHCART.get();
    }

    public static MinecartWorkbench create(EntityType<?> type, Level level) {
        return new MinecartWorkbenchImpl(type, level);
    }
}
