package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.datafix.TypeReferences;

public class ChunkLightRemoveFix extends DataFix {
   public ChunkLightRemoveFix(Schema p_i50431_1_, boolean p_i50431_2_) {
      super(p_i50431_1_, p_i50431_2_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
      Type<?> type1 = type.findFieldType("Level");
      OpticFinder<?> opticfinder = DSL.fieldFinder("Level", type1);
      return this.fixTypeEverywhereTyped("ChunkLightRemoveFix", type, this.getOutputSchema().getType(TypeReferences.CHUNK), (p_219821_1_) -> {
         return p_219821_1_.updateTyped(opticfinder, (p_219822_0_) -> {
            return p_219822_0_.update(DSL.remainderFinder(), (p_219820_0_) -> {
               return p_219820_0_.remove("isLightOn");
            });
         });
      });
   }
}