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

package com.railwayteam.railways.registry;

import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.base.datafixerapi.DataFixesInternals;
import com.railwayteam.railways.base.datafixers.LocoMetalSmokeboxFacingFix;
import com.railwayteam.railways.base.datafixers.StreamlinedSmokeStackFacingFix;
import com.railwayteam.railways.base.datafixers.UpsideDownMonoBogeyFix;
import com.railwayteam.railways.config.CRConfigs;
import net.minecraft.Util;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

import java.util.function.BiFunction;

import static com.railwayteam.railways.base.datafixerapi.DataFixesInternals.BASE_SCHEMA;

public class CRDataFixers {
    private static final BiFunction<Integer, Schema, Schema> SAME = Schema::new;
    private static final BiFunction<Integer, Schema, Schema> SAME_NAMESPACED = NamespacedSchema::new;

    public static void register() {
        Railways.LOGGER.info("Registering data fixers");

        if (CRConfigs.getDisableDatafixer()) {
            Railways.LOGGER.warn("Skipping Datafixer Registration due to it being disabled in the config.");
            return;
        }

        DataFixesInternals api = DataFixesInternals.get();

        DataFixerBuilder builder = new DataFixerBuilder(Railways.DATA_FIXER_VERSION);
        addFixers(builder);
        api.registerFixer(Railways.DATA_FIXER_VERSION, builder.buildOptimized(Util.bootstrapExecutor()));
    }

    private static void addFixers(DataFixerBuilder builder) {
        builder.addSchema(0, BASE_SCHEMA);

        // Register a schema, and then the fixes to get *to* that schema

        // For v1, need to upgrade railways:mono_bogey_upside_down to railways:mono_bogey[upside_down=true]
        Schema schemaV1 = builder.addSchema(1, SAME_NAMESPACED);
        builder.addFixer(new UpsideDownMonoBogeyFix(schemaV1, "Merge railways:mono_bogey_upside_down into railways:mono_bogey[upside_down=true]"));

        // For v2,
        // we need to change the streamlined smokestack's AXIS property to a HORIZONTAL_FACING property
        // and need to change the locometal smokebox's AXIS property to a FACING property
        Schema schemaV2 = builder.addSchema(2, SAME_NAMESPACED);
        builder.addFixer(new StreamlinedSmokeStackFacingFix(schemaV2, "Convert railways:smokestack_streamlined[axis=\"*\"] to railways:smokestack_streamlined[facing=\"*\"]"));
        builder.addFixer(new LocoMetalSmokeboxFacingFix(schemaV2, "Convert railways:${*}_locometal_smokebox[axis=\"*\"] to railways:${*}_locometal_smokebox[facing=\"*\"]"));
    }
}
