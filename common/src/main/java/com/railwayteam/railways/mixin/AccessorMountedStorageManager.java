package com.railwayteam.railways.mixin;

import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Collection;

@Mixin(MountedStorageManager.class)
public interface AccessorMountedStorageManager {
    @Invoker("wrapFluids")
    CombinedTankWrapper wrapFluids(Collection<? extends Storage<FluidVariant>> list);
}
