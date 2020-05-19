package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class ItemFilledMapMetadata extends DataFix {
   public ItemFilledMapMetadata(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
      OpticFinder<Pair<String, String>> opticfinder = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
      OpticFinder<?> opticfinder1 = type.findField("tag");
      return this.fixTypeEverywhereTyped("ItemInstanceMapIdFix", type, (p_206360_2_) -> {
         Optional<Pair<String, String>> optional = p_206360_2_.getOptional(opticfinder);
         if (optional.isPresent() && Objects.equals(optional.get().getSecond(), "minecraft:filled_map")) {
            Dynamic<?> dynamic = p_206360_2_.get(DSL.remainderFinder());
            Typed<?> typed = p_206360_2_.getOrCreateTyped(opticfinder1);
            Dynamic<?> dynamic1 = typed.get(DSL.remainderFinder());
            dynamic1 = dynamic1.set("map", dynamic1.createInt(dynamic.get("Damage").asInt(0)));
            return p_206360_2_.set(opticfinder1, typed.set(DSL.remainderFinder(), dynamic1));
         } else {
            return p_206360_2_;
         }
      });
   }
}