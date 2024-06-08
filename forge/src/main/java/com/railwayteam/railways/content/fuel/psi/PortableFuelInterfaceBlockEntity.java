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

package com.railwayteam.railways.content.fuel.psi;

import com.railwayteam.railways.mixin.AccessorCarriageContraption;
import com.railwayteam.railways.mixin_interfaces.IContraptionFuel;
import com.railwayteam.railways.mixin_interfaces.IFuelInventory;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceBlockEntity;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class PortableFuelInterfaceBlockEntity extends PortableStorageInterfaceBlockEntity {

    protected LazyOptional<IFluidHandler> capability;

    public PortableFuelInterfaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        capability = createEmptyHandler();
    }

    @Override
    public void startTransferringTo(Contraption contraption, float distance) {
        CombinedTankWrapper ctw = ((IContraptionFuel) contraption).railways$getSharedFuelTanks();
        if (contraption instanceof CarriageContraption carriageContraption) {
            MountedStorageManager storageProxy = ((AccessorCarriageContraption) carriageContraption).railways$getStorageProxy();
            ctw = ((IFuelInventory) storageProxy).railways$getFuelFluids();
        }
        CombinedTankWrapper finalCtw = ctw;

        LazyOptional<IFluidHandler> oldcap = capability;
        capability = LazyOptional.of(() -> new InterfaceFluidHandler(finalCtw));
        oldcap.invalidate();
        super.startTransferringTo(contraption, distance);
    }

    @Override
    protected void invalidateCapability() {
        capability.invalidate();
    }

    @Override
    protected void stopTransferring() {
        LazyOptional<IFluidHandler> oldcap = capability;
        capability = createEmptyHandler();
        oldcap.invalidate();
        super.stopTransferring();
    }

    private LazyOptional<IFluidHandler> createEmptyHandler() {
        return LazyOptional.of(() -> new InterfaceFluidHandler(new FluidTank(0)));
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (isFluidHandlerCap(cap))
            return capability.cast();
        return super.getCapability(cap, side);
    }

    public class InterfaceFluidHandler implements IFluidHandler {

        private IFluidHandler wrapped;

        public InterfaceFluidHandler(IFluidHandler wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public int getTanks() {
            return wrapped.getTanks();
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            return wrapped.getFluidInTank(tank);
        }

        @Override
        public int getTankCapacity(int tank) {
            return wrapped.getTankCapacity(tank);
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return wrapped.isFluidValid(tank, stack);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (!isConnected() || wrapped == null) return 0;
            int fill = wrapped.fill(resource, action);
            if (fill > 0 && action.execute())
                keepAlive();
            return fill;
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            if (!canTransfer())
                return FluidStack.EMPTY;
            FluidStack drain = wrapped.drain(resource, action);
            if (!drain.isEmpty() && action.execute())
                keepAlive();
            return drain;
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            if (!canTransfer())
                return FluidStack.EMPTY;
            FluidStack drain = wrapped.drain(maxDrain, action);
            if (!drain.isEmpty() && action.execute())
                keepAlive();
            return drain;
        }

        public void keepAlive() {
            onContentTransferred();
        }

        boolean isConnected() {
            int timeUnit = getTransferTimeout();
            return transferTimer >= ANIMATION && transferTimer <= timeUnit + ANIMATION;
        }
    }

}
