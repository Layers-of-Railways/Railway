package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;

public class AddNewChoices extends DataFix {
   private final String name;
   private final TypeReference type;

   public AddNewChoices(Schema outputSchema, String name, TypeReference type) {
      super(outputSchema, true);
      this.name = name;
      this.type = type;
   }

   public TypeRewriteRule makeRule() {
      TaggedChoiceType<?> taggedchoicetype = this.getInputSchema().findChoiceType(this.type);
      TaggedChoiceType<?> taggedchoicetype1 = this.getOutputSchema().findChoiceType(this.type);
      return this.cap(this.name, taggedchoicetype, taggedchoicetype1);
   }

   protected final <K> TypeRewriteRule cap(String p_206290_1_, TaggedChoiceType<K> p_206290_2_, TaggedChoiceType<?> p_206290_3_) {
      if (p_206290_2_.getKeyType() != p_206290_3_.getKeyType()) {
         throw new IllegalStateException("Could not inject: key type is not the same");
      } else {
         return this.fixTypeEverywhere(p_206290_1_, p_206290_2_, p_206290_3_, (p_209687_2_) -> {
            return (p_206291_2_) -> {
               if (!((TaggedChoiceType<K>)p_206290_3_).hasType(p_206291_2_.getFirst())) {
                  throw new IllegalArgumentException(String.format("Unknown type %s in %s ", p_206291_2_.getFirst(), this.type));
               } else {
                  return (com.mojang.datafixers.util.Pair)p_206291_2_;
               }
            };
         });
      }
   }
}