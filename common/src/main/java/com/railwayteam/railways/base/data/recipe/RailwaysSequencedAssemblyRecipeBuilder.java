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

package com.railwayteam.railways.base.data.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.railwayteam.railways.Railways;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import com.simibubi.create.content.trains.track.TrackMaterial;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.DefaultResourceConditions;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Consumer;

/**
 * Extends Create's Fabric recipe builder with Forge conditional recipe support as well.
 */
public class RailwaysSequencedAssemblyRecipeBuilder extends SequencedAssemblyRecipeBuilder {
    public RailwaysSequencedAssemblyRecipeBuilder(ResourceLocation id) {
        super(id);
    }

    /**
     * If the material is from another mod, add a recipe condition for the mod.
     * @param trackMaterial the material
     * @return this
     */
    public RailwaysSequencedAssemblyRecipeBuilder conditionalMaterial(TrackMaterial trackMaterial) {
        String namespace = trackMaterial.id.getNamespace();
        if (!Railways.MOD_ID.equals(namespace)) {
            recipeConditions.add(DefaultResourceConditions.allModsLoaded(namespace));
        }
        return this;
    }

    @Override
    public void build(Consumer<FinishedRecipe> consumer) {
        consumer.accept(new RailwaysDataGenResult(build(), recipeConditions));
    }

    public static class RailwaysDataGenResult extends DataGenResult {
        private final List<ConditionJsonProvider> recipeConditions;
        public RailwaysDataGenResult(SequencedAssemblyRecipe recipe, List<ConditionJsonProvider> recipeConditions) {
            super(recipe, recipeConditions);
            this.recipeConditions = recipeConditions;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            super.serializeRecipeData(json);
            if (recipeConditions.isEmpty())
                return;

            JsonArray conds = new JsonArray();
            recipeConditions.forEach(c -> conds.add(toForgeJson(c)));
            json.add("conditions", conds);
        }

        /**
         * Generates forge:mod_loaded conditions from the fabric:all_mods_loaded condition.
         * If there are multiple mods, these are wrapped in a forge:and condition.
         * This conversion assumes that Fabric and Forge versions share the same modid.
         * @param provider the fabric condition
         * @return the generated JSON
         */
        private JsonObject toForgeJson(ConditionJsonProvider provider) {
            if (provider.getConditionId().getPath().equals("all_mods_loaded")) {
                JsonObject original = provider.toJson();
                JsonArray mods = original.getAsJsonArray("values");
                if (mods.size() > 1) {
                    JsonObject condition = new JsonObject();
                    condition.addProperty("type", "forge:and");

                    JsonArray values = new JsonArray();
                    mods.forEach(e -> values.add(conditionForgeModLoaded(e.getAsString())));

                    condition.add("values", values);
                    return condition;
                } else {
                    return conditionForgeModLoaded(mods.get(0).getAsString());
                }

            } else {
                throw new UnsupportedOperationException("This provider only supports the fabric:all_mods_loaded recipe condition.");
            }
        }

        private JsonObject conditionForgeModLoaded(String mod) {
            JsonObject condition = new JsonObject();
            condition.addProperty("type", "forge:mod_loaded");
            condition.addProperty("modid", mod);
            return condition;
        }
    }
}
