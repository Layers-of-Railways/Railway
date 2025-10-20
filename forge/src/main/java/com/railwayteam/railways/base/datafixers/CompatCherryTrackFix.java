/*
 * Steam 'n' Rails
 * Copyright (c) 2024 The Railways Team
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

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import com.railwayteam.railways.mixin.MixinTrackMaterial;
import net.minecraft.util.datafix.fixes.References;

import java.util.List;
import java.util.Optional;

/**
 * Also see railways$updateCherryCompatTracks inside {@link MixinTrackMaterial}
 */
public class CompatCherryTrackFix extends DataFix {
    /*
     * List of modded cherry tracks that need to be fixed
     */
    public static final ImmutableList<String> standardCherryOld = ImmutableList.of(
            "railways:track_biomesoplenty_cherry",
            "railways:track_byg_cherry",
            "railways:track_blue_skies_cherry"
    );
    public static final ImmutableList<String> wideCherryOld = ImmutableList.of(
            "railways:track_biomesoplenty_cherry_wide",
            "railways:track_byg_cherry_wide",
            "railways:track_blue_skies_cherry_wide"
    );
    public static final ImmutableList<String> narrowCherryOld = ImmutableList.of(
            "railways:track_biomesoplenty_cherry_narrow",
            "railways:track_byg_cherry_narrow",
            "railways:track_blue_skies_cherry_narrow"
    );

    private final String name;

    public CompatCherryTrackFix(Schema outputSchema, String name) {
        super(outputSchema, false);
        this.name = name;
    }

    @Override
    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped(this.name + " for block_state", this.getInputSchema().getType(References.BLOCK_STATE), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
            Optional<String> optional = dynamic.get("Name").asString().result();

            if (optional.isPresent()) {
                if (standardCherryOld.contains(optional.get())) {
                    return fixCherryData(dynamic, "railways:track_cherry");
                } else if (wideCherryOld.contains(optional.get())) {
                    return fixCherryData(dynamic, "railways:track_cherry_wide");
                } else if (narrowCherryOld.contains(optional.get())) {
                    return fixCherryData(dynamic, "railways:track_cherry_narrow");
                }
            }

            return dynamic;
        }));
    }

    private static Dynamic<?> fixCherryData(Dynamic<?> dynamic, String name) {
        dynamic = dynamic.set("Name", dynamic.createString(name));
        return dynamic.set("Properties", dynamic.get("Properties").orElseEmptyMap());
    }
}
