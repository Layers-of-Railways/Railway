/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import com.simibubi.create.content.trains.track.TrackMaterial;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

public class RailwaysSequencedAssemblyRecipeBuilder extends SequencedAssemblyRecipeBuilder {
    public RailwaysSequencedAssemblyRecipeBuilder(ResourceLocation id) {
        super(id);
    }

    public RailwaysSequencedAssemblyRecipeBuilder conditionalMaterial(TrackMaterial trackMaterial) {
        return this;
    }

    @Override
    public void build(RecipeOutput consumer) {
        var recipe = build();
        consumer.accept(getRecipeId(), recipe.value(), recipe.advancement());
    }
}
