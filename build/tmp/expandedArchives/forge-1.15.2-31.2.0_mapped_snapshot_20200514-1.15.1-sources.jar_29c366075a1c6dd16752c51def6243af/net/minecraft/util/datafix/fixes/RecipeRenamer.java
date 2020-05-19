package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.TypeReferences;

public class RecipeRenamer extends DataFix {
   private final String field_230073_a_;
   private final Function<String, String> field_230074_b_;

   public RecipeRenamer(Schema p_i230047_1_, boolean p_i230047_2_, String p_i230047_3_, Function<String, String> p_i230047_4_) {
      super(p_i230047_1_, p_i230047_2_);
      this.field_230073_a_ = p_i230047_3_;
      this.field_230074_b_ = p_i230047_4_;
   }

   protected TypeRewriteRule makeRule() {
      Type<Pair<String, String>> type = DSL.named(TypeReferences.RECIPE.typeName(), DSL.namespacedString());
      if (!Objects.equals(type, this.getInputSchema().getType(TypeReferences.RECIPE))) {
         throw new IllegalStateException("Recipe type is not what was expected.");
      } else {
         return this.fixTypeEverywhere(this.field_230073_a_, type, (p_230075_1_) -> {
            return (p_230076_1_) -> {
               return p_230076_1_.mapSecond(this.field_230074_b_);
            };
         });
      }
   }
}