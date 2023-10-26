package com.railwayteam.railways.base.datafixers;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.fixes.References;

import java.util.Optional;

public class CompatCherryTrackFix extends DataFix {
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
                if (optional.get().equals("railways:track_biomesoplenty_cherry") || optional.get().equals("railways:track_blue_skies_cherry")) {
                    dynamic = dynamic.set("Name", dynamic.createString("railways:track_cherry"));

                    dynamic = fixProperties(dynamic);

                    return dynamic;
                }
                if (optional.get().equals("railways:track_biomesoplenty_cherry_wide") || optional.get().equals("railways:track_blue_skies_cherry_wide")) {
                    dynamic = dynamic.set("Name", dynamic.createString("railways:track_cherry_wide"));

                    dynamic = fixProperties(dynamic);

                    return dynamic;
                }
                if (optional.get().equals("railways:track_biomesoplenty_cherry_narrow") || optional.get().equals("railways:track_blue_skies_cherry_narrow")) {
                    dynamic = dynamic.set("Name", dynamic.createString("railways:track_cherry_narrow"));

                    dynamic = fixProperties(dynamic);

                    return dynamic;
                }
            }

            return dynamic;
        }));
    }

    private static Dynamic<?> fixProperties(Dynamic<?> dynamic) {
        Dynamic<?> properties = dynamic.get("Properties").orElseEmptyMap();
        Dynamic<?> connections = properties.get("Connections").orElseEmptyMap();
        dynamic = dynamic.set("Properties", properties).set("Connections", connections).set("Material", dynamic.createString("railways:cherry"));
        return dynamic;
    }
}
