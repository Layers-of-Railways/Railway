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

package com.railwayteam.railways.mixin;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.sugar.Local;
import com.railwayteam.railways.mixin_interfaces.IFuelInventory;
import com.railwayteam.railways.util.AbstractionUtils;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorage;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageWrapper;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.Predicate;

@Mixin(MountedStorageManager.class)
public abstract class MixinMountedStorageManager implements IFuelInventory {
	@Shadow
	private static <K, V> ImmutableMap<K, V> subMap(Map<K, V> map, Predicate<V> predicate) {
		throw new AssertionError();
	}

	@Unique
	private MountedFluidStorageWrapper railways$fluidFuels;
	
	@Inject(method = "initialize", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/api/contraption/storage/fluid/MountedFluidStorageWrapper;<init>(Lcom/google/common/collect/ImmutableMap;)V"), remap = false)
	private void railways$initFluidFuels(CallbackInfo ci, @Local(ordinal = 1) ImmutableMap<BlockPos, MountedFluidStorage> fluids) {
		ImmutableMap<BlockPos, MountedFluidStorage> fuelMap = subMap(fluids, (s) -> 
				AbstractionUtils.isInstanceOfFuelTankMountedStorageType(s.type)
		);
		this.railways$fluidFuels = fuelMap.isEmpty() ? null : new MountedFluidStorageWrapper(fuelMap);
	}

	@Override
	public MountedFluidStorageWrapper railways$getFluidFuels() {
		return railways$fluidFuels;
	}
}
