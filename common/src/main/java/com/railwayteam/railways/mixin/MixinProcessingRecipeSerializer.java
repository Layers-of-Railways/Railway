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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.util.FluidUtils;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProcessingRecipeSerializer.class)
public class MixinProcessingRecipeSerializer<T extends ProcessingRecipe<?>> {
    @Inject(method = "writeToJson", at = @At("RETURN"), remap = false)
    private void multiloaderMangleFluidIngredient(JsonObject json, T recipe, CallbackInfo ci) {
        if (!recipe.getId().getNamespace().equals(Railways.MOD_ID)) return;

        for (JsonElement ingredient : json.getAsJsonArray("ingredients")) {
            if (FluidIngredient.isFluidIngredient(ingredient)) {
                JsonObject ingredientObject = ingredient.getAsJsonObject();
                FluidUtils.mangleFluidAmount(ingredientObject);
            }
        }

        for (JsonElement result : json.getAsJsonArray("results")) {
            if (!result.isJsonObject()) continue;
            JsonObject resultObject = result.getAsJsonObject();
            if (!resultObject.has("fluid")) continue;
            FluidUtils.mangleFluidAmount(resultObject);
        }
    }

    @Inject(method = "readFromJson", at = @At("HEAD"), remap = false)
    private void multiloaderDemangleFluidIngredient(ResourceLocation recipeId, JsonObject json, CallbackInfoReturnable<T> cir) {
        if (!recipeId.getNamespace().equals(Railways.MOD_ID)) return;
        if (!json.has("ingredients")) return;

        for (JsonElement ingredient : json.getAsJsonArray("ingredients")) {
            if (FluidIngredient.isFluidIngredient(ingredient)) {
                JsonObject ingredientObject = ingredient.getAsJsonObject();
                FluidUtils.demangleFluidAmount(ingredientObject);
            }
        }

        for (JsonElement result : json.getAsJsonArray("results")) {
            if (!result.isJsonObject()) continue;
            JsonObject resultObject = result.getAsJsonObject();
            if (!resultObject.has("fluid")) continue;
            FluidUtils.demangleFluidAmount(resultObject);
        }
    }
}
