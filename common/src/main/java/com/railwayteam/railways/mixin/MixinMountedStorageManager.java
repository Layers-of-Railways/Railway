package com.railwayteam.railways.mixin;

import com.railwayteam.railways.mixin_interfaces.IFuelInventory;
import com.railwayteam.railways.util.FluidUtils;
import com.simibubi.create.content.contraptions.MountedFluidStorage;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.stream.Collectors;

@Mixin(value = MountedStorageManager.class, remap = false)
public class MixinMountedStorageManager implements IFuelInventory {
    @Unique protected CombinedTankWrapper snr$fluidFuelInventory;
    @Unique protected Map<BlockPos, MountedFluidStorage> snr$fluidFuelStorage;


    @Inject(method = "createHandlers()V", at = @At("HEAD"))
    public void createHandler(CallbackInfo ci) {
        snr$fluidFuelInventory = ((AccessorMountedStorageManager) snr$fluidFuelInventory).snr$wrapFluids(snr$fluidFuelStorage.values()
                .stream()
                .map(MountedFluidStorage::getFluidHandler)
                .collect(Collectors.toList()));
    }

    @Inject(method = "addBlock", at = @At("HEAD"))
    public void addBlock(BlockPos localPos, BlockEntity be, CallbackInfo ci) {
        if (FluidUtils.canUseAsFuelStorage(be))
            snr$fluidFuelStorage.put(localPos, new MountedFluidStorage(be));
    }

    @Override
    public CombinedTankWrapper snr$getFuelFluids() {
        return snr$fluidFuelInventory;
    }
}
