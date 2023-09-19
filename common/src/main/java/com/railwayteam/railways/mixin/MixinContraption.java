package com.railwayteam.railways.mixin;

import com.railwayteam.railways.mixin_interfaces.IContraptionFuel;
import com.railwayteam.railways.mixin_interfaces.IFuelInventory;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Contraption.class)
public abstract class MixinContraption implements IContraptionFuel {

    @Shadow protected MountedStorageManager storage;

    @Override
    public CombinedTankWrapper snr$getSharedFuelTanks() {
        return ((IFuelInventory) storage).snr$getFuelFluids();
    }
}
