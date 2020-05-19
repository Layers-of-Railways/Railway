package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Objects;
import net.minecraft.util.datafix.TypeReferences;

public class ChunkStatusFix extends DataFix {
   public ChunkStatusFix(Schema p_i50430_1_, boolean p_i50430_2_) {
      super(p_i50430_1_, p_i50430_2_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
      Type<?> type1 = type.findFieldType("Level");
      OpticFinder<?> opticfinder = DSL.fieldFinder("Level", type1);
      return this.fixTypeEverywhereTyped("ChunkStatusFix", type, this.getOutputSchema().getType(TypeReferences.CHUNK), (p_219826_1_) -> {
         return p_219826_1_.updateTyped(opticfinder, (p_219827_0_) -> {
            Dynamic<?> dynamic = p_219827_0_.get(DSL.remainderFinder());
            String s = dynamic.get("Status").asString("empty");
            if (Objects.equals(s, "postprocessed")) {
               dynamic = dynamic.set("Status", dynamic.createString("fullchunk"));
            }

            return p_219827_0_.set(DSL.remainderFinder(), dynamic);
         });
      });
   }
}