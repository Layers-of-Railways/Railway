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
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class PotionWater extends DataFix {
   public PotionWater(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
      OpticFinder<Pair<String, String>> opticfinder = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
      OpticFinder<?> opticfinder1 = type.findField("tag");
      return this.fixTypeEverywhereTyped("ItemWaterPotionFix", type, (p_206363_2_) -> {
         Optional<Pair<String, String>> optional = p_206363_2_.getOptional(opticfinder);
         if (optional.isPresent()) {
            String s = optional.get().getSecond();
            if ("minecraft:potion".equals(s) || "minecraft:splash_potion".equals(s) || "minecraft:lingering_potion".equals(s) || "minecraft:tipped_arrow".equals(s)) {
               Typed<?> typed = p_206363_2_.getOrCreateTyped(opticfinder1);
               Dynamic<?> dynamic = typed.get(DSL.remainderFinder());
               if (!dynamic.get("Potion").asString().isPresent()) {
                  dynamic = dynamic.set("Potion", dynamic.createString("minecraft:water"));
               }

               return p_206363_2_.set(opticfinder1, typed.set(DSL.remainderFinder(), dynamic));
            }
         }

         return p_206363_2_;
      });
   }
}