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

package com.railwayteam.railways.base.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.railwayteam.railways.base.datafix.CRReferences;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

import java.util.Map;
import java.util.function.Supplier;

public class V0 extends NamespacedSchema {
    public V0(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    @Override
    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
        super.registerTypes(schema, entityTypes, blockEntityTypes);

        /*
         Note from Slimeist:
         I'm adding Create's create_tracks saved data here for now. Eventually I'll PR it to Create once I figure out
         a sane way to do multi-mod datafixers. The way this is set up won't interfere with Create if it independently
         adds datafixers (though the schema would need to be updated). To minimize update work, I'm only defining
         parts of types that we actually care about fixing.

         If Create decides to register these types, it would perhaps be wise to do so in some vanilla schema, so that
         vanilla datafixers apply to Create's stuff.
        */

        schema.registerType(
            false,
            CRReferences.SAVED_DATA_CREATE_TRACKS,
            () -> DSL.optionalFields(
                "data",
                DSL.optionalFields(
                    "Trains",
                    DSL.list(CRReferences.CREATE_TRAIN.in(schema))
                )
            )
        );

        schema.registerType(
            false,
            CRReferences.CREATE_TRAIN,
            () -> DSL.optionalFields(
                "Carriages",
                DSL.list(CRReferences.CREATE_CARRIAGE.in(schema))
            )
        );

        schema.registerType(
            false,
            CRReferences.CREATE_CARRIAGE,
            () -> DSL.optionalFields(
                "Entity",
                References.ENTITY.in(schema)
            )
        );

        schema.registerType(
            false,
            CRReferences.CREATE_CONTRAPTION,
            () -> DSL.optionalFields(
                "Blocks",
                DSL.optionalFields(
                    "Palette",
                    DSL.list(References.BLOCK_STATE.in(schema))
                )
            )
        );
    }

    private static void registerContraption(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
        schema.register(
            map,
            name,
            () -> DSL.optionalFields(
                "Contraption",
                CRReferences.CREATE_CONTRAPTION.in(schema)
            )
        );
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);

        for (String contraptionType : new String[] {
            "contraption",
            "stationary_contraption",
            "gantry_contraption",
            "carriage_contraption"
        }) registerContraption(schema, map, "create:" + contraptionType);

        return map;
    }
}
