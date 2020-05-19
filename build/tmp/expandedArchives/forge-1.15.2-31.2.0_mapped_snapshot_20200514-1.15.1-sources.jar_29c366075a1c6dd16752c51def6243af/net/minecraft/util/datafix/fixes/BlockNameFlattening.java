package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class BlockNameFlattening extends DataFix {
   public BlockNameFlattening(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(TypeReferences.BLOCK_NAME);
      Type<?> type1 = this.getOutputSchema().getType(TypeReferences.BLOCK_NAME);
      Type<Pair<String, Either<Integer, String>>> type2 = DSL.named(TypeReferences.BLOCK_NAME.typeName(), DSL.or(DSL.intType(), DSL.namespacedString()));
      Type<Pair<String, String>> type3 = DSL.named(TypeReferences.BLOCK_NAME.typeName(), DSL.namespacedString());
      if (Objects.equals(type, type2) && Objects.equals(type1, type3)) {
         return this.fixTypeEverywhere("BlockNameFlatteningFix", type2, type3, (p_209702_0_) -> {
            return (p_206303_0_) -> {
               return p_206303_0_.mapSecond((p_206304_0_) -> {
                  return p_206304_0_.map(BlockStateFlatteningMap::updateId, (p_206305_0_) -> {
                     return BlockStateFlatteningMap.updateName(NamespacedSchema.ensureNamespaced(p_206305_0_));
                  });
               });
            };
         });
      } else {
         throw new IllegalStateException("Expected and actual types don't match.");
      }
   }
}