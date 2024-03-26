package com.railwayteam.railways.util.forge;

import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.content.fuel.LiquidFuelManager;
import com.railwayteam.railways.content.fuel.LiquidFuelType;
import com.railwayteam.railways.content.fuel.tank.FuelTankBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeHooks;

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
                return ForgeHooks.getBurnTime(fluid.getBucket().getDefaultInstance(), null) > 0;
            }
        }
        // else just return true
        return true;
    }
}
