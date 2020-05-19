package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import net.minecraft.util.datafix.TypeReferences;

public class HorseSplit extends EntityRename {
   public HorseSplit(Schema outputSchema, boolean changesType) {
      super("EntityHorseSplitFix", outputSchema, changesType);
   }

   protected Pair<String, Typed<?>> fix(String p_209149_1_, Typed<?> p_209149_2_) {
      Dynamic<?> dynamic = p_209149_2_.get(DSL.remainderFinder());
      if (Objects.equals("EntityHorse", p_209149_1_)) {
         int i = dynamic.get("Type").asInt(0);
         String s;
         switch(i) {
         case 0:
         default:
            s = "Horse";
            break;
         case 1:
            s = "Donkey";
            break;
         case 2:
            s = "Mule";
            break;
         case 3:
            s = "ZombieHorse";
            break;
         case 4:
            s = "SkeletonHorse";
         }

         dynamic.remove("Type");
         Type<?> type = (Type)this.getOutputSchema().findChoiceType(TypeReferences.ENTITY).types().get(s);
         return Pair.of(s, type.readTyped(p_209149_2_.write()).getSecond().orElseThrow(() -> {
            return new IllegalStateException("Could not parse the new horse");
         }));
      } else {
         return Pair.of(p_209149_1_, p_209149_2_);
      }
   }
}