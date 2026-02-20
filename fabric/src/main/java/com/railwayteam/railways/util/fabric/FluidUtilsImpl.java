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

package com.railwayteam.railways.util.fabric;

import com.railwayteam.railways.annotation.multiloader.ImplClass;
import com.railwayteam.railways.content.fuel.tank.FuelTankBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

@ImplClass
public class FluidUtilsImpl {
    public static boolean canUseAsFuelStorage(BlockEntity be) {
        if (be instanceof FuelTankBlockEntity fuelTankBlockEntity)
            return fuelTankBlockEntity.isController();
        return false;
    }

    public static Fluid getFluid(Object o) {
        Fluid fluid;

        if (o instanceof FluidVariant fluidVariant) {
            fluid = fluidVariant.getFluid();
        } else if (o instanceof FluidStack fluidStack) {
            fluid = fluidStack.getFluid();
        } else {
            throw new IllegalArgumentException("FluidUtils#getFluid expected to get a FluidVariant or FluidStack but got " + o.getClass().getName());
        }

        return fluid;
    }

    public static void addFluidOutput(ProcessingRecipeBuilder<ProcessingRecipe<?>> b, Fluid fluid, long amount, @Nullable CompoundTag nbt) {
        b.withFluidOutputs(new FluidStack(fluid, amount, nbt));
    }
}
