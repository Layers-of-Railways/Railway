package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.util.datafix.TypeReferences;

public class RedundantChanceTags extends DataFix {
   public RedundantChanceTags(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("EntityRedundantChanceTagsFix", this.getInputSchema().getType(TypeReferences.ENTITY), (p_210996_0_) -> {
         return p_210996_0_.update(DSL.remainderFinder(), (p_206334_0_) -> {
            Dynamic<?> dynamic = p_206334_0_;
            if (Objects.equals(p_206334_0_.get("HandDropChances"), Optional.of(p_206334_0_.createList(Stream.generate(() -> {
               return dynamic.createFloat(0.0F);
            }).limit(2L))))) {
               p_206334_0_ = p_206334_0_.remove("HandDropChances");
            }

            if (Objects.equals(p_206334_0_.get("ArmorDropChances"), Optional.of(p_206334_0_.createList(Stream.generate(() -> {
               return dynamic.createFloat(0.0F);
            }).limit(4L))))) {
               p_206334_0_ = p_206334_0_.remove("ArmorDropChances");
            }

            return p_206334_0_;
         });
      });
   }
}