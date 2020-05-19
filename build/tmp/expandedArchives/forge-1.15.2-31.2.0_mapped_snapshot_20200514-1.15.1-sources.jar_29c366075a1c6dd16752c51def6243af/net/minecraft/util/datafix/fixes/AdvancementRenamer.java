package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.function.Function;
import net.minecraft.util.datafix.TypeReferences;

public class AdvancementRenamer extends DataFix {
   private final String field_230066_a_;
   private final Function<String, String> field_230067_b_;

   public AdvancementRenamer(Schema p_i230046_1_, boolean p_i230046_2_, String p_i230046_3_, Function<String, String> p_i230046_4_) {
      super(p_i230046_1_, p_i230046_2_);
      this.field_230066_a_ = p_i230046_3_;
      this.field_230067_b_ = p_i230046_4_;
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped(this.field_230066_a_, this.getInputSchema().getType(TypeReferences.ADVANCEMENTS), (p_230071_1_) -> {
         return p_230071_1_.update(DSL.remainderFinder(), (p_230068_1_) -> {
            return p_230068_1_.updateMapValues((p_230069_2_) -> {
               String s = p_230069_2_.getFirst().asString("");
               return p_230069_2_.mapFirst((p_230070_3_) -> {
                  return p_230068_1_.createString(this.field_230067_b_.apply(s));
               });
            });
         });
      });
   }
}