package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class TeamDisplayName extends DataFix {
   public TeamDisplayName(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   protected TypeRewriteRule makeRule() {
      Type<Pair<String, Dynamic<?>>> type = DSL.named(TypeReferences.TEAM.typeName(), DSL.remainderType());
      if (!Objects.equals(type, this.getInputSchema().getType(TypeReferences.TEAM))) {
         throw new IllegalStateException("Team type is not what was expected.");
      } else {
         return this.fixTypeEverywhere("TeamDisplayNameFix", type, (p_211876_0_) -> {
            return (p_211877_0_) -> {
               return p_211877_0_.mapSecond((p_211875_0_) -> {
                  return p_211875_0_.update("DisplayName", (p_211878_1_) -> {
                     return DataFixUtils.orElse(p_211878_1_.asString().map((p_211879_0_) -> {
                        return ITextComponent.Serializer.toJson(new StringTextComponent(p_211879_0_));
                     }).map(p_211875_0_::createString), p_211878_1_);
                  });
               });
            };
         });
      }
   }
}