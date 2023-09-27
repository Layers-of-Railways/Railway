package com.railwayteam.railways.util.fabric;

import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.content.fuel.tank.FuelTankBlockEntity;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FluidUtilsImpl {
    public static boolean canUseAsFuelStorage(BlockEntity be) {
        if (be instanceof FuelTankBlockEntity fuelTankBlockEntity)
            return fuelTankBlockEntity.isController();
        return false;
    }

    public static boolean isFuel(Item item) {
        // If realistic fuel tanks is enabled check if the fluid/item is valid fuel
        if (CRConfigs.server().realism.realisticFuelTanks.get())
            return FuelRegistry.INSTANCE.get(item) != null;
        // else just return true
        return true;
    }
}
