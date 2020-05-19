package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.datafix.TypeReferences;

public class StructureReferenceFix extends DataFix {
   public StructureReferenceFix(Schema p_i225704_1_, boolean p_i225704_2_) {
      super(p_i225704_1_, p_i225704_2_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE);
      return this.fixTypeEverywhereTyped("Structure Reference Fix", type, (p_226213_0_) -> {
         return p_226213_0_.update(DSL.remainderFinder(), StructureReferenceFix::func_226212_a_);
      });
   }

   private static <T> Dynamic<T> func_226212_a_(Dynamic<T> p_226212_0_) {
      return p_226212_0_.update("references", (p_226215_0_) -> {
         return p_226215_0_.createInt(p_226215_0_.asNumber().map(Number::intValue).filter((p_226214_0_) -> {
            return p_226214_0_ > 0;
         }).orElse(1));
      });
   }
}