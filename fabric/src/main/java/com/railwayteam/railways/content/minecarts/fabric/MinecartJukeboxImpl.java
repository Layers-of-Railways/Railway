package com.railwayteam.railways.content.minecarts.fabric;

import com.railwayteam.railways.content.minecarts.MinecartJukebox;
import com.railwayteam.railways.registry.CRItems;
import com.simibubi.create.content.contraptions.components.structureMovement.train.capability.CapabilityMinecartController;
import com.simibubi.create.content.contraptions.components.structureMovement.train.capability.MinecartController;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Unique;

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

    @Override
    protected Item getDropItem() {
        return CRItems.ITEM_JUKEBOXCART.get();
    }

    public static MinecartJukebox create(EntityType<?> type, Level level) {
        return new MinecartJukeboxImpl(type, level);
    }

    @Unique
    public CapabilityMinecartController controllerCap = null;

    @Override
    public CapabilityMinecartController getCap() {
        return controllerCap;
    }

    @Override
    public void setCap(CapabilityMinecartController cap) {
        this.controllerCap = cap;
    }

    @Override
    public LazyOptional<MinecartController> lazyController() {
        return controllerCap.cap;
    }

    @Override
    public MinecartController getController() {
        return controllerCap.handler;
    }
}
