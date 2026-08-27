/*
 * Steam 'n' Rails
 * Copyright (c) 2026 The Railways Team
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

package com.railwayteam.railways.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.foundation.utility.CreateCodecs;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

// todo: remove once https://github.com/Fabricators-of-Create/Create/issues/1918 is resolved
@Mixin(CreateCodecs.class)
@SuppressWarnings("UnstableApiUsage")
public abstract class CreateCodecsMixin {
    /**
     * Create fabric doesn't load fluid stack NBT properly at all, since the setTag can't modify the final FluidVariant's final tag
     */
    @WrapOperation(
        method = "lambda$static$19",
        at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/world/level/material/Fluid;J)Lio/github/fabricators_of_create/porting_lib/fluids/FluidStack;"
        ),
        require = 0 // things will be broken without this mixin, but that's not our fault
    )
    @SuppressWarnings({"NameDoesntMatchTargetClass", "OptionalUsedAsFieldOrParameterType"})
    private static FluidStack setTagOnVariant(Fluid type, long amount, Operation<FluidStack> original, Fluid $fluid, Long $amount, Optional<CompoundTag> tag) {
        FluidStack stack = original.call(type, amount);
        if (tag.isPresent() && !stack.isEmpty()) {
            var variant = stack.getType();
            return new FluidStack(FluidVariant.of(variant.getFluid(), tag.get()), stack.getAmount());
        }
        return stack;
    }
}
