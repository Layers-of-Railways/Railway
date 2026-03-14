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

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.base.datafix.fixes.CompatCherryTrackFix;
import com.railwayteam.railways.base.datafix.fixes.DiagonalHazardStripesFacingFix;
import com.railwayteam.railways.base.datafix.fixes.LocoMetalSmokeboxFacingFix;
import com.railwayteam.railways.base.datafix.fixes.SmokestackPartFix;
import com.railwayteam.railways.base.datafix.fixes.StreamlinedSmokeStackFacingFix;
import com.railwayteam.railways.base.datafix.fixes.UpsideDownMonoBogeyFix;
import com.railwayteam.railways.base.datafix.schemas.V0;
import com.railwayteam.railways.base.datafixerapi.DataFixesInternals;
import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.content.smokestack.block.variable.VariableStackPart;
import net.minecraft.SharedConstants;
import net.minecraft.util.datafix.fixes.AddNewChoices;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;

import static com.railwayteam.railways.base.datafixerapi.DataFixesInternals.baseSchema;

public class CRDataFixers {
    //private static final BiFunction<Integer, Schema, Schema> SAME = Schema::new;
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

        ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Railways Datafixer Bootstrap").setDaemon(true).setPriority(1).build());
        api.registerFixer(Railways.DATA_FIXER_VERSION, builder.buildOptimized(SharedConstants.DATA_FIX_TYPES_TO_OPTIMIZE, executor));
    }

    private static void addFixers(DataFixerBuilder builder) {
        Schema schemaV0 = builder.addSchema(0, baseSchema(V0::new));
        builder.addFixer(new AddNewChoices(schemaV0, "Added Create's Contraptions", References.ENTITY));

        // Register a schema, and then the fixes to get *to* that schema

        // For v1, need to upgrade railways:mono_bogey_upside_down to railways:mono_bogey[upside_down=true]
        Schema schemaV1 = builder.addSchema(1, SAME_NAMESPACED);
        builder.addFixer(new UpsideDownMonoBogeyFix(schemaV1, "Merge railways:mono_bogey_upside_down into railways:mono_bogey[upside_down=true]"));

        // For v2,
        // need to upgrade BOP & Blueskies cherry compat tracks to railways:track_cherry[_narrow||_wide]
        // and need to change the streamlined smokestack's AXIS property to a HORIZONTAL_FACING property
        // and need to change the locometal smokebox's AXIS property to a FACING property
        Schema schemaV2 = builder.addSchema(2, SAME_NAMESPACED);
        builder.addFixer(new CompatCherryTrackFix(schemaV2, "Convert Compat Cherry Tracks to Default Cherry Tracks"));
        builder.addFixer(new StreamlinedSmokeStackFacingFix(schemaV2, "Convert railways:smokestack_streamlined[axis=\"*\"] to railways:smokestack_streamlined[facing=\"*\"]"));
        builder.addFixer(new LocoMetalSmokeboxFacingFix(schemaV2, "Convert railways:${*}_locometal_smokebox[axis=\"*\"] to railways:${*}_locometal_smokebox[facing=\"*\"]"));

        // For v10,
        // need to change diagonal hazard stripes from HORIZONTAL_AXIS to HORIZONTAL_FACING
        Schema schemaV10 = builder.addSchema(10, SAME_NAMESPACED);
        builder.addFixer(new DiagonalHazardStripesFacingFix(schemaV10, "Convert railways:*_diagonal_hazard_stripes{black,white}[axis=\"*\"] to railways:*_diagonal_hazard_stripes{black,white}[facing=\"*\"]"));

        // For v11,
        // need to convert simple stacks into variable stacks
        Schema schemaV11 = builder.addSchema(11, SAME_NAMESPACED);
        //noinspection unchecked
        Pair<String, VariableStackPart>[] variableStacks = new Pair[] {
            Pair.of("long", VariableStackPart.SINGLE),
            Pair.of("coalburner", VariableStackPart.DOUBLE),
            Pair.of("oilburner", VariableStackPart.DOUBLE),
            Pair.of("streamlined", VariableStackPart.SINGLE),
            Pair.of("woodburner", VariableStackPart.DOUBLE)
        };
        for (var pair : variableStacks) {
            String blockId = "railways:smokestack_" + pair.getFirst();
            builder.addFixer(new SmokestackPartFix(schemaV11, "Fix part BlockState for "+blockId, blockId, pair.getSecond()));
        }
    }
}
