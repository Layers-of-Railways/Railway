package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class OptionsLowerCaseLanguage extends DataFix {
   public OptionsLowerCaseLanguage(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("OptionsLowerCaseLanguageFix", this.getInputSchema().getType(TypeReferences.OPTIONS), (p_206281_0_) -> {
         return p_206281_0_.update(DSL.remainderFinder(), (p_207428_0_) -> {
            Optional<String> optional = p_207428_0_.get("lang").asString();
            return optional.isPresent() ? p_207428_0_.set("lang", p_207428_0_.createString(optional.get().toLowerCase(Locale.ROOT))) : p_207428_0_;
         });
      });
   }
}