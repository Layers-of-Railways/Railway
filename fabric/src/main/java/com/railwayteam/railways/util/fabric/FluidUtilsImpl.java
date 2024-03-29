package com.railwayteam.railways.util.fabric;

import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.content.fuel.LiquidFuelManager;
import com.railwayteam.railways.content.fuel.LiquidFuelType;
import com.railwayteam.railways.content.fuel.tank.FuelTankBlockEntity;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;

public class FluidUtilsImpl {
    public static boolean canUseAsFuelStorage(BlockEntity be) {
        if (be instanceof FuelTankBlockEntity fuelTankBlockEntity)
            return fuelTankBlockEntity.isController();
        return false;
    }

    public static boolean isFuel(Fluid fluid) {
        // If realistic fuel tanks is enabled check if the fluid/item is valid fuel
        if (CRConfigs.server().realism.realisticFuelTanks.get()) {
            LiquidFuelType fuelType = LiquidFuelManager.getTypeForFluid(fluid);
            if (fuelType != null) {
                return true;
            } else {
                return FuelRegistry.INSTANCE.get(fluid.getBucket()) != null;
            }
        }
        // else just return true
        return true;
    }
}
