package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;
import net.minecraft.util.datafix.TypeReferences;

public class BiomeIdFix extends DataFix {
   public BiomeIdFix(Schema p_i225701_1_, boolean p_i225701_2_) {
      super(p_i225701_1_, p_i225701_2_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
      OpticFinder<?> opticfinder = type.findField("Level");
      return this.fixTypeEverywhereTyped("Leaves fix", type, (p_226193_1_) -> {
         return p_226193_1_.updateTyped(opticfinder, (p_226194_0_) -> {
            return p_226194_0_.update(DSL.remainderFinder(), (p_226192_0_) -> {
               Optional<IntStream> optional = p_226192_0_.get("Biomes").asIntStreamOpt();
               if (!optional.isPresent()) {
                  return p_226192_0_;
               } else {
                  int[] aint = optional.get().toArray();
                  int[] aint1 = new int[1024];

                  for(int i = 0; i < 4; ++i) {
                     for(int j = 0; j < 4; ++j) {
                        int k = (j << 2) + 2;
                        int l = (i << 2) + 2;
                        int i1 = l << 4 | k;
                        aint1[i << 2 | j] = i1 < aint.length ? aint[i1] : -1;
                     }
                  }

                  for(int j1 = 1; j1 < 64; ++j1) {
                     System.arraycopy(aint1, 0, aint1, j1 * 16, 16);
                  }

                  return p_226192_0_.set("Biomes", p_226192_0_.createIntList(Arrays.stream(aint1)));
               }
            });
         });
      });
   }
}