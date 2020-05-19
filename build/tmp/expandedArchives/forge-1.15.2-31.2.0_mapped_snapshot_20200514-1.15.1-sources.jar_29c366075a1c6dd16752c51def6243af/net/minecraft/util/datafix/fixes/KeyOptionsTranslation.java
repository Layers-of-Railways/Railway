package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.stream.Collectors;
import net.minecraft.util.datafix.TypeReferences;

public class KeyOptionsTranslation extends DataFix {
   public KeyOptionsTranslation(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("OptionsKeyTranslationFix", this.getInputSchema().getType(TypeReferences.OPTIONS), (p_209667_0_) -> {
         return p_209667_0_.update(DSL.remainderFinder(), (p_209668_0_) -> {
            return p_209668_0_.getMapValues().map((p_209669_1_) -> {
               return p_209668_0_.createMap(p_209669_1_.entrySet().stream().map((p_209666_1_) -> {
                  if (p_209666_1_.getKey().asString("").startsWith("key_")) {
                     String s = p_209666_1_.getValue().asString("");
                     if (!s.startsWith("key.mouse") && !s.startsWith("scancode.")) {
                        return Pair.of(p_209666_1_.getKey(), p_209668_0_.createString("key.keyboard." + s.substring("key.".length())));
                     }
                  }

                  return Pair.of(p_209666_1_.getKey(), p_209666_1_.getValue());
               }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
            }).orElse((com.mojang.datafixers.Dynamic)p_209668_0_);
         });
      });
   }
}