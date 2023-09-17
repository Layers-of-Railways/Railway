package com.railwayteam.railways.mixin_interfaces;

import com.simibubi.create.content.contraptions.MountedFluidStorage;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.minecraft.core.BlockPos;

import java.util.Map;

public interface IFuelInventory {
    void snr$setFuelFluids(CombinedTankWrapper combinedTankWrapper);
    CombinedTankWrapper snr$getFuelFluids();

    void snr$setFluidFuelStorage(Map<BlockPos, MountedFluidStorage> storageMap);
    Map<BlockPos, MountedFluidStorage> snr$getFluidFuelStorage();
}
