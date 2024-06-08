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

package com.railwayteam.railways.base.datafixers;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.Direction;
import net.minecraft.util.datafix.fixes.References;

import java.util.Optional;

/*
 * Converts railways:${color}_locometal_smokebox[axis="z"] to railways:${color}_locometal_smokebox[facing="north"]
 * and converts railways:${color}_locometal_smokebox[axis="x"] to railways:${color}_locometal_smokebox[facing="east"]
 *
 * This is needed due to changing them from using axis to direction properties
 */
public class LocoMetalSmokeboxFacingFix extends DataFix {
    private final String name;

    public LocoMetalSmokeboxFacingFix(Schema outputSchema, String name) {
        super(outputSchema, false);
        this.name = name;
    }

    @Override
    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped(this.name + " for block_state", this.getInputSchema().getType(References.BLOCK_STATE), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
            Optional<String> optional = dynamic.get("Name").asString().result();
            if (optional.isPresent() && optional.get().matches("railways:(.*)_locometal_smokebox")) {
                // Conversions:
                // Axis X -> Facing East
                // Axis Y -> Facing Up
                // Axis Z -> Facing North

                Dynamic<?> axis = dynamic.get("Properties").orElseEmptyMap().get("axis").orElseEmptyMap();
                Dynamic<?> properties = dynamic.get("Properties").orElseEmptyMap();

                // Returns x, y or z. As characters.
                String axisString = axis.getValue().toString().replace("\"", "");

                switch (Direction.Axis.valueOf(axisString)) {
                    case X -> properties = properties.set("facing", dynamic.createString("east"));
                    case Y -> properties = properties.set("facing", dynamic.createString("up"));
                    case Z -> properties = properties.set("facing", dynamic.createString("north"));
                }

                dynamic = dynamic.set("Properties", properties);
                return dynamic;
            }

            return dynamic;
        }));
    }
}
