package com.railwayteam.railways.content.minecarts.fabric;

import com.railwayteam.railways.content.minecarts.MinecartWorkbench;
import com.railwayteam.railways.registry.CRItems;
import com.simibubi.create.content.contraptions.minecart.capability.CapabilityMinecartController;
import com.simibubi.create.content.contraptions.minecart.capability.MinecartController;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Unique;

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

    @Unique //FIXME this should not be needed
    public CapabilityMinecartController create$controllerCap = null;

    @Override
    public CapabilityMinecartController getCap() {
        if (create$controllerCap == null) {
            CapabilityMinecartController.attach(this);
        }
        return create$controllerCap;
    }

    @Override
    public void setCap(CapabilityMinecartController cap) {
        this.create$controllerCap = cap;
    }

    @Override
    public LazyOptional<MinecartController> lazyController() {
        if (create$controllerCap == null) {
            CapabilityMinecartController.attach(this);
        }
        return create$controllerCap.cap;
    }

    @Override
    public MinecartController getController() {
        if (create$controllerCap == null) {
            CapabilityMinecartController.attach(this);
        }
        return create$controllerCap.handler;
    }
}
