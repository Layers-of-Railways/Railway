/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.palettes.boiler;

import com.google.gson.JsonObject;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.loaders.ObjModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CustomObjModelBuilder<T extends ModelBuilder<T>> extends ObjModelBuilder<T> {
    protected CustomObjModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
        super(parent, existingFileHelper);
    }

    public static <T extends ModelBuilder<T>> CustomObjModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper) {
        return new CustomObjModelBuilder<>(parent, existingFileHelper);
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        json = super.toJson(json);
        json.addProperty("porting_lib:loader", "porting_lib:obj");
        return json;
    }
}
