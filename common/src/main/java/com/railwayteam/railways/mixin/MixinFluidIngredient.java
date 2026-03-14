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

package com.railwayteam.railways.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.railwayteam.railways.util.FluidUtils;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// mods like KubeJS bypass the ProcessingRecipeSerializer implementation, so this is a backup demangler
@Mixin(FluidIngredient.class)
public class MixinFluidIngredient {
    @WrapOperation(method = "deserialize", at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonElement;getAsJsonObject()Lcom/google/gson/JsonObject;", ordinal = 0), remap = false)
    private static JsonObject demangle(JsonElement instance, Operation<JsonObject> original) {
        JsonObject jo = original.call(instance);
        FluidUtils.demangleFluidAmount(jo);
        return jo;
    }
}
