package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.datafix.TypeReferences;

public class SwimStatsRename extends DataFix {
   public SwimStatsRename(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getOutputSchema().getType(TypeReferences.STATS);
      Type<?> type1 = this.getInputSchema().getType(TypeReferences.STATS);
      OpticFinder<?> opticfinder = type1.findField("stats");
      OpticFinder<?> opticfinder1 = opticfinder.type().findField("minecraft:custom");
      OpticFinder<String> opticfinder2 = DSL.namespacedString().finder();
      return this.fixTypeEverywhereTyped("SwimStatsRenameFix", type1, type, (p_211690_3_) -> {
         return p_211690_3_.updateTyped(opticfinder, (p_211692_2_) -> {
            return p_211692_2_.updateTyped(opticfinder1, (p_211691_1_) -> {
               return p_211691_1_.update(opticfinder2, (p_211693_0_) -> {
                  if (p_211693_0_.equals("minecraft:swim_one_cm")) {
                     return "minecraft:walk_on_water_one_cm";
                  } else {
                     return p_211693_0_.equals("minecraft:dive_one_cm") ? "minecraft:walk_under_water_one_cm" : p_211693_0_;
                  }
               });
            });
         });
      });
   }
}