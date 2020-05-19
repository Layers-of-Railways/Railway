package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class ForceVBOOn extends DataFix {
   public ForceVBOOn(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("OptionsForceVBOFix", this.getInputSchema().getType(TypeReferences.OPTIONS), (p_207425_0_) -> {
         return p_207425_0_.update(DSL.remainderFinder(), (p_207426_0_) -> {
            return p_207426_0_.set("useVbo", p_207426_0_.createString("true"));
         });
      });
   }
}