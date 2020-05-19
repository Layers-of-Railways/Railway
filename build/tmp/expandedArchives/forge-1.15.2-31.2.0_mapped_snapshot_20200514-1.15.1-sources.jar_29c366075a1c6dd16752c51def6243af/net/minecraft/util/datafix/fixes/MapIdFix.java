package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class MapIdFix extends DataFix {
   public MapIdFix(Schema p_i50424_1_, boolean p_i50424_2_) {
      super(p_i50424_1_, p_i50424_2_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(TypeReferences.SAVED_DATA);
      OpticFinder<?> opticfinder = type.findField("data");
      return this.fixTypeEverywhereTyped("Map id fix", type, (p_219839_1_) -> {
         Optional<? extends Typed<?>> optional = p_219839_1_.getOptionalTyped(opticfinder);
         return optional.isPresent() ? p_219839_1_ : p_219839_1_.update(DSL.remainderFinder(), (p_219838_0_) -> {
            return p_219838_0_.emptyMap().merge(p_219838_0_.createString("data"), p_219838_0_);
         });
      });
   }
}