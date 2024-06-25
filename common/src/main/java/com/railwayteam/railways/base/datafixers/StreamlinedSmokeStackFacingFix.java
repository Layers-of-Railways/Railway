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
import net.minecraft.util.datafix.fixes.References;

import java.util.Optional;

/*
 * Converts railways:smokestack_streamlined[axis="z"] to railways:smokestack_streamlined[facing="north"]
 * and converts railways:smokestack_streamlined[axis="x"] to railways:smokestack_streamlined[facing="east"]
 *
 * This is needed due to changing them from using axis to facing since streamlined smokestack's had a texture change
 */
public class StreamlinedSmokeStackFacingFix extends DataFix {
    private final String name;

    public StreamlinedSmokeStackFacingFix(Schema outputSchema, String name) {
        super(outputSchema, false);
        this.name = name;
    }

    @Override
    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped(this.name + " for block_state", this.getInputSchema().getType(References.BLOCK_STATE), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
            Optional<String> optional = dynamic.get("Name").asString().result();
            if (optional.isPresent() && optional.get().equals("railways:smokestack_streamlined")) {
                // Conversions:
                // Axis Z -> Facing North
                // Axis X -> Facing East

                Dynamic<?> axis = dynamic.get("Properties").orElseEmptyMap().get("axis").orElseEmptyMap();
                Dynamic<?> properties = dynamic.get("Properties").orElseEmptyMap();

                // Returns either "z" or "x", Yes the quotations are included, therefore, when checking
                // we need to make sure to escape it properly
                String axisString = axis.getValue().toString();

                if (axisString.equals("\"z\"")) {
                    properties = properties.set("facing", dynamic.createString("north"));
                } else if (axisString.equals("\"x\"")) {
                    properties = properties.set("facing", dynamic.createString("east"));
                }

                dynamic = dynamic.set("Properties", properties);
                return dynamic;
            }

            return dynamic;
        }));
    }
}
