package com.railwayteam.railways.util.forge;

import com.railwayteam.railways.content.fuel.tank.FuelTankBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;

public class FluidUtilsImpl {
    public static boolean canUseAsFuelStorage(BlockEntity be) {
        if (be instanceof FuelTankBlockEntity fuelTankBlockEntity)
            return fuelTankBlockEntity.isController();
        return false;
    }

    public static boolean isFuel(Item item) {
        return ForgeHooks.getBurnTime(item.getDefaultInstance(), null) > 0;
    }

    public static boolean portableFuelBlockHasState(BlockState state) {
        //fixme
        //return CRBlocksImpl.PORTABLE_FUEL_INTERFACE.has(state);
        return false;
    }
}
