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

package com.railwayteam.railways.base.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import com.railwayteam.railways.content.smokestack.block.variable.VariableStackPart;
import net.minecraft.util.datafix.fixes.References;

import java.util.Optional;

public class SmokestackPartFix extends DataFix {
    private final String name;
    private final String blockId;
    private final VariableStackPart defaultPart;

    public SmokestackPartFix(Schema outputSchema, String name, String blockId, VariableStackPart defaultPart) {
        super(outputSchema, false);
        this.name = name;
        this.blockId = blockId;
        this.defaultPart = defaultPart;
    }

    @Override
    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped(
            this.name,
            this.getInputSchema().getType(References.BLOCK_STATE),
            typed -> typed.update(DSL.remainderFinder(),dynamic -> {
                Optional<String> name = dynamic.get("Name").asString().result();
                if (name.isPresent() && name.get().equals(this.blockId)) {
                    Dynamic<?> properties = dynamic.get("Properties").orElseEmptyMap();
                    if (properties.get("part").result().isEmpty()) {
                        properties = properties.set("part", dynamic.createString(this.defaultPart.getSerializedName()));
                        dynamic = dynamic.set("Properties", properties);
                    }
                }
                return dynamic;
            })
        );
    }
}
