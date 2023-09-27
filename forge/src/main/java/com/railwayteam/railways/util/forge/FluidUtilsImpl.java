package com.railwayteam.railways.util.forge;

import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.content.fuel.tank.FuelTankBlockEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.ForgeHooks;

public class FluidUtilsImpl {
    public static boolean canUseAsFuelStorage(BlockEntity be) {
        if (be instanceof FuelTankBlockEntity fuelTankBlockEntity)
            return fuelTankBlockEntity.isController();
        return false;
    }

    public static boolean isFuel(Item item) {
        // If realistic fuel tanks is enabled check if the fluid/item is valid fuel
        if (CRConfigs.server().realism.realisticFuelTanks.get())
            return ForgeHooks.getBurnTime(item.getDefaultInstance(), null) > 0;
        // else just return true
        return true;
    }
}
