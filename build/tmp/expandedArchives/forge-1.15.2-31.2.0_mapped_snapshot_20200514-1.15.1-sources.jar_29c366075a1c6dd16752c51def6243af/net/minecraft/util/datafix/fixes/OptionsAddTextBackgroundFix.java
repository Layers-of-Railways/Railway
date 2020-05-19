package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class OptionsAddTextBackgroundFix extends DataFix {
   public OptionsAddTextBackgroundFix(Schema p_i50422_1_, boolean p_i50422_2_) {
      super(p_i50422_1_, p_i50422_2_);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("OptionsAddTextBackgroundFix", this.getInputSchema().getType(TypeReferences.OPTIONS), (p_219858_1_) -> {
         return p_219858_1_.update(DSL.remainderFinder(), (p_219855_1_) -> {
            return DataFixUtils.orElse(p_219855_1_.get("chatOpacity").asString().map((p_219857_2_) -> {
               return p_219855_1_.set("textBackgroundOpacity", p_219855_1_.createDouble(this.func_219856_a(p_219857_2_)));
            }), p_219855_1_);
         });
      });
   }

   private double func_219856_a(String p_219856_1_) {
      try {
         double d0 = 0.9D * Double.parseDouble(p_219856_1_) + 0.1D;
         return d0 / 2.0D;
      } catch (NumberFormatException var4) {
         return 0.5D;
      }
   }
}