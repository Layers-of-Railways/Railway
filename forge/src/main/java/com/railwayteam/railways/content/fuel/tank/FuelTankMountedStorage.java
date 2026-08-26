/*
 * Steam 'n' Rails
 * Copyright (c) 2025 The Railways Team
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

package com.railwayteam.railways.content.fuel.tank;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.railwayteam.railways.content.fuel.LiquidFuelTrainHandler;
import com.railwayteam.railways.content.fuel.tank.FuelTankMountedStorage.Handler;
import com.railwayteam.railways.registry.forge.CRMountedStorageTypesImpl;
import com.simibubi.create.api.contraption.storage.SyncedMountedStorage;
import com.simibubi.create.api.contraption.storage.fluid.WrapperMountedFluidStorage;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.foundation.utility.CreateCodecs;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class FuelTankMountedStorage extends WrapperMountedFluidStorage<Handler> implements SyncedMountedStorage {
	public static final Codec<FuelTankMountedStorage> CODEC = RecordCodecBuilder.create(i -> i.group(
		ExtraCodecs.NON_NEGATIVE_INT.fieldOf("capacity").forGetter(FuelTankMountedStorage::getCapacity),
		CreateCodecs.FLUID_STACK_CODEC.fieldOf("fluid").forGetter(FuelTankMountedStorage::getFluid)
	).apply(i, FuelTankMountedStorage::new));

	private boolean dirty;

	protected FuelTankMountedStorage(int capacity, FluidStack stack) {
		super(CRMountedStorageTypesImpl.FUEL_TANK.get(), new FuelTankMountedStorage.Handler(capacity, stack));
		this.wrapped.onChange = () -> this.dirty = true;
	}

	@Override
	public void unmount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
		if (be instanceof FuelTankBlockEntity tank && tank.isController()) {
			FluidTank inventory = tank.getTankInventory();
			// capacity shouldn't change, leave it
			inventory.setFluid(this.wrapped.getFluid());
		}
	}

	public FluidStack getFluid() {
		return Objects.requireNonNull(this.wrapped.getFluid());
	}

	public int getCapacity() {
		return this.wrapped.getCapacity();
	}

	@Override
	public boolean isDirty() {
		return this.dirty;
	}

	@Override
	public void markClean() {
		this.dirty = false;
	}

	@Override
	public void afterSync(Contraption contraption, BlockPos localPos) {
		BlockEntity be = contraption.getBlockEntityClientSide(localPos);
		if (!(be instanceof FuelTankBlockEntity tank))
			return;

		FluidTank inv = tank.getTankInventory();
		inv.setFluid(this.getFluid());
		float fillLevel = inv.getFluidAmount() / (float) inv.getCapacity();
		if (tank.getFluidLevel() == null) {
			tank.setFluidLevel(LerpedFloat.linear().startWithValue(fillLevel));
		}
		tank.getFluidLevel().chase(fillLevel, 0.5, LerpedFloat.Chaser.EXP);
	}

	public static FuelTankMountedStorage fromTank(FuelTankBlockEntity tank) {
		// tank has update callbacks, make an isolated copy
		FluidTank inventory = tank.getTankInventory();
		return new FuelTankMountedStorage(inventory.getCapacity(), inventory.getFluid().copy());
	}

	public static FuelTankMountedStorage fromLegacy(CompoundTag nbt) {
		int capacity = nbt.getInt("Capacity");
		FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
		return new FuelTankMountedStorage(capacity, fluid);
	}

	public static final class Handler extends FluidTank {
		private Runnable onChange = () -> {};

		public Handler(int capacity, FluidStack stack) {
			super(capacity, fluid -> LiquidFuelTrainHandler.isFuelForTanks(fluid.getFluid()));
			Objects.requireNonNull(stack);
			this.setFluid(stack);
		}

		@Override
		protected void onContentsChanged() {
			this.onChange.run();
		}
	}
}