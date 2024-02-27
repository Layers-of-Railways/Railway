package com.railwayteam.railways.mixin_interfaces;

import com.simibubi.create.content.contraptions.MountedFluidStorage;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.minecraft.core.BlockPos;

import java.util.Map;

public interface IFuelInventory {
    void railways$setFuelFluids(CombinedTankWrapper combinedTankWrapper);
    CombinedTankWrapper railways$getFuelFluids();

    void railways$setFluidFuelStorage(Map<BlockPos, MountedFluidStorage> storageMap);
    Map<BlockPos, MountedFluidStorage> railways$getFluidFuelStorage();
}
