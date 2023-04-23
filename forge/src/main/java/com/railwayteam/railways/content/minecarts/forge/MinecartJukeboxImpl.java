package com.railwayteam.railways.content.minecarts.forge;

import com.railwayteam.railways.content.minecarts.MinecartJukebox;
import com.railwayteam.railways.registry.CRItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class MinecartJukeboxImpl extends MinecartJukebox {
    protected MinecartJukeboxImpl(Level level, double x, double y, double z) {
        super(level, x, y, z);
    }

    public MinecartJukeboxImpl(EntityType<?> type, Level level) {
        super(type, level);
    }

    public static MinecartJukebox create(Level level, double x, double y, double z) {
        return new MinecartJukeboxImpl(level, x, y, z);
    }

    public static MinecartJukebox create(EntityType<?> type, Level level) {
        return new MinecartJukeboxImpl(type, level);
    }

    @Override
    public int getComparatorLevel() {
        return getComparatorOutput();

    @Override
    protected Item getDropItem() {
        return CRItems.ITEM_JUKEBOXCART.get();
    }
}
