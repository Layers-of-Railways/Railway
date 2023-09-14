package com.railwayteam.railways.util;

import com.railwayteam.railways.Railways;
import com.simibubi.create.foundation.fluid.FluidHelper;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

public class FluidUtils {
    public static boolean isFuel(Fluid fluid) {
        return isFuel(FluidHelper.convertToStill(fluid).getBucket());
    }

    /**
     * All isFuel checks for anything else should go through this.
     * @return true if it is a valid fuel item, false if it isn't
     */
    public static boolean isFuel(Item item) {
        return FuelRegistry.INSTANCE.get(item) != null;
    }
}
