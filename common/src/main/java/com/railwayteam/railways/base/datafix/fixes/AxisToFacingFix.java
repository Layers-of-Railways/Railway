/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2026 The Railways Team
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

package com.railwayteam.railways.base.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.Direction;
import net.minecraft.util.datafix.fixes.References;

import java.util.Optional;

/*
 * Converts ?[axis="z"] to ?[facing="north"]
 * and converts ?[axis="x"] to ?[facing="east"]
 * and converts ?[axis="y"] to ?[facing="up"]
 */
public abstract class AxisToFacingFix extends DataFix {
    protected final String name;

    public AxisToFacingFix(Schema outputSchema, String name) {
        super(outputSchema, false);
        this.name = name;
    }

    protected abstract boolean applyToBlockState(String blockId);

    @Override
    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped(this.name + " for block_state", this.getInputSchema().getType(References.BLOCK_STATE), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
            Optional<String> optional = dynamic.get("Name").asString().result();
            if (optional.isPresent() && applyToBlockState(optional.get())) {
                // Conversions:
                // Axis Z -> Facing North
                // Axis X -> Facing East
                // Axis Y -> Facing Up

                Dynamic<?> properties = dynamic.get("Properties").orElseEmptyMap();

                Optional<String> maybeAxis = properties.get("axis").asString().result();
                if (maybeAxis.isEmpty()) return dynamic;

                Direction.Axis axis = Direction.Axis.byName(maybeAxis.get());
                if (axis == null) return dynamic;

                switch (axis) {
                    case X -> properties = properties.set("facing", dynamic.createString("east"));
                    case Y -> properties = properties.set("facing", dynamic.createString("up"));
                    case Z -> properties = properties.set("facing", dynamic.createString("north"));
                }
                properties = properties.remove("axis");

                dynamic = dynamic.set("Properties", properties);
                return dynamic;
            }

            return dynamic;
        }));
    }
}
