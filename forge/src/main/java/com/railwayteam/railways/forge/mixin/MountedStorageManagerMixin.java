/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.forge.mixin;

import com.railwayteam.railways.content.fuel.tank.FuelTankBlockEntity;
import com.railwayteam.railways.mixin.AccessorMountedFluidStorage;
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
import java.util.stream.Collectors;

@Mixin(value = MountedStorageManager.class, remap = false)
public abstract class MountedStorageManagerMixin {
    @Shadow protected abstract CombinedTankWrapper wrapFluids(Collection<IFluidHandler> list);

    @Inject(method = "createHandlers", at = @At("TAIL"))
    private void createHandler(CallbackInfo ci) {
        CombinedTankWrapper combinedTankWrapper = wrapFluids(((IFuelInventory) this).railways$getFluidFuelStorage().values()
                .stream()
                .map(MountedFluidStorage::getFluidHandler)
                .collect(Collectors.toList()));

        ((IFuelInventory) this).railways$setFuelFluids(combinedTankWrapper);
    }

    @Inject(method = "read", at = @At("TAIL"))
    public void read(CompoundTag nbt, Map<BlockPos, BlockEntity> presentBlockEntities, boolean clientPacket, CallbackInfo ci) {
        CombinedTankWrapper ctw = wrapFluids(((IFuelInventory) this).railways$getFluidFuelStorage().values()
                .stream()
                .map(MountedFluidStorage::getFluidHandler)
                .toList());

        ((IFuelInventory) this).railways$setFuelFluids(ctw);
    }

    @Inject(method = "bindTanks", at = @At("TAIL"))
    public void bindTanks(Map<BlockPos, BlockEntity> presentBlockEntities, CallbackInfo ci) {
        ((IFuelInventory) this).railways$getFluidFuelStorage().forEach((pos, mfs) -> {
            BlockEntity blockEntity = presentBlockEntities.get(pos);
            if (!(blockEntity instanceof FuelTankBlockEntity tank))
                return;
            IFluidTank tankInventory = tank.getTankInventory();
            if (tankInventory instanceof FluidTank)
                ((FluidTank) tankInventory).setFluid(((AccessorMountedFluidStorage) mfs).railways$getTank().getFluid());
            tank.getFluidLevel()
                    .startWithValue(tank.getFillState());
            mfs.assignBlockEntity(tank);
        });
    }

    @Inject(method = "clear", at = @At("TAIL"))
    private void clear(CallbackInfo ci) {
        CombinedTankWrapper fuelFluidInventory = ((IFuelInventory) this).railways$getFuelFluids();

        for (int i = 0; i < fuelFluidInventory.getTanks(); i++)
            fuelFluidInventory.drain(fuelFluidInventory.getFluidInTank(i), IFluidHandler.FluidAction.EXECUTE);
    }

    @Inject(method = "updateContainedFluid", at = @At("TAIL"))
    private void updateContainedFluid(BlockPos localPos, FluidStack containedFluid, CallbackInfo ci) {
        MountedFluidStorage mountedFuelFluidStorage = ((IFuelInventory) this).railways$getFluidFuelStorage().get(localPos);
        if (mountedFuelFluidStorage != null)
            mountedFuelFluidStorage.updateFluid(containedFluid);
    }
}
