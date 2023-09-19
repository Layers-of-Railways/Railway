package com.railwayteam.railways.forge.mixin;

import com.railwayteam.railways.content.fuel.tank.FuelTankBlockEntity;
import com.railwayteam.railways.mixin_interfaces.IFuelInventory;
import com.simibubi.create.content.contraptions.MountedFluidStorage;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Map;

@Mixin(value = MountedStorageManager.class, remap = false)
public abstract class MountedStorageManagerMixin {
    @Shadow protected abstract CombinedTankWrapper wrapFluids(Collection<IFluidHandler> list);

    @Inject(method = "read", at = @At("RETURN"))
    public void read(CompoundTag nbt, Map<BlockPos, BlockEntity> presentBlockEntities, boolean clientPacket, CallbackInfo ci) {
        CombinedTankWrapper combinedTankWrapper = wrapFluids(((IFuelInventory) this).snr$getFluidFuelStorage().values()
                .stream()
                .map(MountedFluidStorage::getFluidHandler)
                .toList());

        ((IFuelInventory) this).snr$setFuelFluids(combinedTankWrapper);
    }

    @Inject(method = "bindTanks", at = @At("RETURN"))
    public void bindTanks(Map<BlockPos, BlockEntity> presentBlockEntities, CallbackInfo ci) {
        ((IFuelInventory) this).snr$getFluidFuelStorage().forEach((pos, mfs) -> {
            BlockEntity blockEntity = presentBlockEntities.get(pos);
            if (!(blockEntity instanceof FuelTankBlockEntity tank))
                return;
            IFluidTank tankInventory = tank.getTankInventory();
            if (tankInventory instanceof FluidTank)
                ((FluidTank) tankInventory).setFluid(((MountedFluidStorageAccessor) mfs).getTank().getFluid());
            tank.getFluidLevel()
                    .startWithValue(tank.getFillState());
            mfs.assignBlockEntity(tank);
        });
    }

    @Inject(method = "clear", at = @At("RETURN"))
    private void clear(CallbackInfo ci) {
        CombinedTankWrapper fuelFluidInventory = ((IFuelInventory) this).snr$getFuelFluids();

        for (int i = 0; i < fuelFluidInventory.getTanks(); i++)
            fuelFluidInventory.drain(fuelFluidInventory.getFluidInTank(i), IFluidHandler.FluidAction.EXECUTE);
    }

    @Inject(method = "updateContainedFluid", at = @At("RETURN"))
    private void updateContainedFluid(BlockPos localPos, FluidStack containedFluid, CallbackInfo ci) {
        MountedFluidStorage mountedFuelFluidStorage = ((IFuelInventory) this).snr$getFluidFuelStorage().get(localPos);
        if (mountedFuelFluidStorage != null)
            mountedFuelFluidStorage.updateFluid(containedFluid);
    }
}
