package com.railwayteam.railways.fabric.mixin;

import com.railwayteam.railways.content.fuel.tank.FuelTankBlockEntity;
import com.railwayteam.railways.mixin.AccessorMountedStorageManager;
import com.railwayteam.railways.mixin_interfaces.IFuelInventory;
import com.simibubi.create.content.contraptions.MountedFluidStorage;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTank;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = MountedStorageManager.class, remap = false)
public class MountedStorageManagerMixin {
    @Inject(method = "read", at = @At("RETURN"))
    public void read(CompoundTag nbt, Map<BlockPos, BlockEntity> presentBlockEntities, boolean clientPacket, CallbackInfo ci) {
        ((IFuelInventory) this).snr$setFuelFluids(((AccessorMountedStorageManager) ((IFuelInventory) this).snr$getFuelFluids()).snr$wrapFluids(((IFuelInventory) this).snr$getFluidFuelStorage().values()
                .stream()
                .map(MountedFluidStorage::getFluidHandler)
                .map(tank -> (Storage<FluidVariant>) tank)
                .toList()));
    }

    @Inject(method = "bindTanks", at = @At("RETURN"))
    public void bindTanks(Map<BlockPos, BlockEntity> presentBlockEntities, CallbackInfo ci) {
        ((IFuelInventory) this).snr$getFluidFuelStorage().forEach((pos, mfs) -> {
            BlockEntity blockEntity = presentBlockEntities.get(pos);
            if (!(blockEntity instanceof FuelTankBlockEntity tank))
                return;
            FluidTank tankInventory = tank.getTankInventory();
            if (tankInventory != null)
                tankInventory.setFluid(mfs.getFluidHandler().getFluid());
            tank.getFluidLevel()
                    .startWithValue(tank.getFillState());
            mfs.assignBlockEntity(tank);
        });
    }

    @Inject(method = "clear", at = @At("RETURN"))
    private void clear(CallbackInfo ci) {
        TransferUtil.clearStorage(((IFuelInventory) this).snr$getFuelFluids());
    }

    @Inject(method = "updateContainedFluid", at = @At("RETURN"))
    private void updateContainedFluid(BlockPos localPos, FluidStack containedFluid, CallbackInfo ci) {
        MountedFluidStorage mountedFuelFluidStorage = ((IFuelInventory) this).snr$getFluidFuelStorage().get(localPos);
        if (mountedFuelFluidStorage != null)
            mountedFuelFluidStorage.updateFluid(containedFluid);
    }
}
