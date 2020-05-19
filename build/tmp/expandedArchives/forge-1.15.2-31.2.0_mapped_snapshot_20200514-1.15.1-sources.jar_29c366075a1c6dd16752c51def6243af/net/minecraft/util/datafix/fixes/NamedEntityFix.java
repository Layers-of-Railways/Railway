package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;

public abstract class NamedEntityFix extends DataFix {
   private final String name;
   private final String entityName;
   private final TypeReference type;

   public NamedEntityFix(Schema outputSchema, boolean changesType, String name, TypeReference type, String entityName) {
      super(outputSchema, changesType);
      this.name = name;
      this.type = type;
      this.entityName = entityName;
   }

   public TypeRewriteRule makeRule() {
      OpticFinder<?> opticfinder = DSL.namedChoice(this.entityName, this.getInputSchema().getChoiceType(this.type, this.entityName));
      return this.fixTypeEverywhereTyped(this.name, this.getInputSchema().getType(this.type), this.getOutputSchema().getType(this.type), (p_206371_2_) -> {
         return p_206371_2_.updateTyped(opticfinder, this.getOutputSchema().getChoiceType(this.type, this.entityName), this::fix);
      });
   }

   protected abstract Typed<?> fix(Typed<?> p_207419_1_);
}