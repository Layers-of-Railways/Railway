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
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public abstract class PointOfInterestRename extends DataFix {
   public PointOfInterestRename(Schema p_i225703_1_, boolean p_i225703_2_) {
      super(p_i225703_1_, p_i225703_2_);
   }

   protected TypeRewriteRule makeRule() {
      Type<Pair<String, Dynamic<?>>> type = DSL.named(TypeReferences.POI_CHUNK.typeName(), DSL.remainderType());
      if (!Objects.equals(type, this.getInputSchema().getType(TypeReferences.POI_CHUNK))) {
         throw new IllegalStateException("Poi type is not what was expected.");
      } else {
         return this.fixTypeEverywhere("POI rename", type, (p_226203_1_) -> {
            return (p_226206_1_) -> {
               return p_226206_1_.mapSecond(this::func_226201_a_);
            };
         });
      }
   }

   private <T> Dynamic<T> func_226201_a_(Dynamic<T> p_226201_1_) {
      return p_226201_1_.update("Sections", (p_226209_1_) -> {
         return p_226209_1_.updateMapValues((p_226204_1_) -> {
            return p_226204_1_.mapSecond((p_226210_1_) -> {
               return p_226210_1_.update("Records", (p_226211_1_) -> {
                  return DataFixUtils.orElse(this.func_226205_b_(p_226211_1_), p_226211_1_);
               });
            });
         });
      });
   }

   private <T> Optional<Dynamic<T>> func_226205_b_(Dynamic<T> p_226205_1_) {
      return p_226205_1_.asStreamOpt().map((p_226202_2_) -> {
         return p_226205_1_.createList(p_226202_2_.map((p_226207_1_) -> {
            return p_226207_1_.update("type", (p_226208_1_) -> {
               return DataFixUtils.orElse(p_226208_1_.asString().map(this::func_225501_a_).map(p_226208_1_::createString), p_226208_1_);
            });
         }));
      });
   }

   protected abstract String func_225501_a_(String p_225501_1_);
}