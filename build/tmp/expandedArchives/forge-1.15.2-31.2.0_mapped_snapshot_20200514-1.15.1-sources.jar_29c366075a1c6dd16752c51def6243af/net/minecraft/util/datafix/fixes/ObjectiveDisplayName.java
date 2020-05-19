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

public class ObjectiveDisplayName extends DataFix {
   public ObjectiveDisplayName(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   protected TypeRewriteRule makeRule() {
      Type<Pair<String, Dynamic<?>>> type = DSL.named(TypeReferences.OBJECTIVE.typeName(), DSL.remainderType());
      if (!Objects.equals(type, this.getInputSchema().getType(TypeReferences.OBJECTIVE))) {
         throw new IllegalStateException("Objective type is not what was expected.");
      } else {
         return this.fixTypeEverywhere("ObjectiveDisplayNameFix", type, (p_211862_0_) -> {
            return (p_211863_0_) -> {
               return p_211863_0_.mapSecond((p_211861_0_) -> {
                  return p_211861_0_.update("DisplayName", (p_211864_1_) -> {
                     return DataFixUtils.orElse(p_211864_1_.asString().map((p_211865_0_) -> {
                        return ITextComponent.Serializer.toJson(new StringTextComponent(p_211865_0_));
                     }).map(p_211861_0_::createString), p_211864_1_);
                  });
               });
            };
         });
      }
   }
}