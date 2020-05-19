package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.util.datafix.TypeReferences;

public class ObjectiveRenderType extends DataFix {
   public ObjectiveRenderType(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   private static ScoreCriteria.RenderType getRenderType(String p_211858_0_) {
      return p_211858_0_.equals("health") ? ScoreCriteria.RenderType.HEARTS : ScoreCriteria.RenderType.INTEGER;
   }

   protected TypeRewriteRule makeRule() {
      Type<Pair<String, Dynamic<?>>> type = DSL.named(TypeReferences.OBJECTIVE.typeName(), DSL.remainderType());
      if (!Objects.equals(type, this.getInputSchema().getType(TypeReferences.OBJECTIVE))) {
         throw new IllegalStateException("Objective type is not what was expected.");
      } else {
         return this.fixTypeEverywhere("ObjectiveRenderTypeFix", type, (p_211859_0_) -> {
            return (p_211860_0_) -> {
               return p_211860_0_.mapSecond((p_211857_0_) -> {
                  Optional<String> optional = p_211857_0_.get("RenderType").asString();
                  if (!optional.isPresent()) {
                     String s = p_211857_0_.get("CriteriaName").asString("");
                     ScoreCriteria.RenderType scorecriteria$rendertype = getRenderType(s);
                     return p_211857_0_.set("RenderType", p_211857_0_.createString(scorecriteria$rendertype.getId()));
                  } else {
                     return p_211857_0_;
                  }
               });
            };
         });
      }
   }
}