package com.railwayteam.railways.base.datafixers;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.fixes.References;

import java.util.Optional;

public class UpsideDownMonoBogeyFix extends DataFix {

    private final String name;

    public UpsideDownMonoBogeyFix(Schema outputSchema, String name) {
        super(outputSchema, false);
        this.name = name;
    }

    @Override
    public TypeRewriteRule makeRule() {
        /*Type<Pair<String, String>> type2;
        Type<?> type = this.getInputSchema().getType(References.BLOCK_NAME);
        if (!Objects.equals(type, type2 = DSL.named(References.BLOCK_NAME.typeName(), NamespacedSchema.namespacedString()))) {
            throw new IllegalStateException("block type is not what was expected.");
        }*/
        //TypeRewriteRule typeRewriteRule = this.fixTypeEverywhere(this.name + " for block", type2, dynamicOps -> pair -> pair.mapSecond(this::fixBlock));
        return this.fixTypeEverywhereTyped(this.name + " for block_state", this.getInputSchema().getType(References.BLOCK_STATE), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
            Optional<String> optional = dynamic.get("Name").asString().result();
            if (optional.isPresent() && optional.get().equals("railways:mono_bogey_upside_down")) {
                dynamic = dynamic.set("Name", dynamic.createString("railways:mono_bogey"));

                Dynamic<?> properties = dynamic.get("Properties").orElseEmptyMap()
                    .set("upside_down", dynamic.createString("true"));

                dynamic = dynamic.set("Properties", properties);
                return dynamic;
            }

            return dynamic;
        }));
        //return TypeRewriteRule.seq(typeRewriteRule, typeRewriteRule2);
    }

    private String fixBlock(String original) {
        if (original.equals("railways:mono_bogey_upside_down")) {
            return "railways:mono_bogey";
        }
        return original;
    }
}
