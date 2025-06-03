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

package com.railwayteam.railways.util;

import com.google.gson.JsonObject;
import com.railwayteam.railways.multiloader.fluid.FluidUnits;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public class FluidUtils {
    @ExpectPlatform
    public static boolean canUseAsFuelStorage(BlockEntity be) {
        throw new AssertionError();
    }

    /**
     * @param o Either a FluidStack (forge & fabric) or FluidVariant (fabric)
     * @return The fluid
     * @throws IllegalArgumentException If any object that isn't an instance of FluidStack or FluidVariant is passed.
     */
    @ExpectPlatform
    public static Fluid getFluid(Object o) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void addFluidOutput(ProcessingRecipeBuilder<ProcessingRecipe<?>> b, Fluid fluid, long amount, @Nullable CompoundTag nbt) {
        throw new AssertionError();
    }

    public static void mangleFluidAmount(JsonObject json) {
        if (!json.has("amount")) return;

        long amount = json.remove("amount").getAsLong();
        json.addProperty("railways:amount", amount);
        json.addProperty("railways:amount:unit", FluidUnits.bucket());
    }

    public static void demangleFluidAmount(JsonObject json) {
        if (!json.has("railways:amount")) return;
        if (!json.has("railways:amount:unit")) return;

        long amount = json.remove("railways:amount").getAsLong();
        long unit = json.remove("railways:amount:unit").getAsLong();

        if (unit != FluidUnits.bucket()) {
            amount = (long) Math.floor(amount * (double) FluidUnits.bucket() / unit);
        }

        json.addProperty("amount", amount);
    }
}
