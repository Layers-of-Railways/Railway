package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Objects;
import net.minecraft.util.datafix.TypeReferences;

public class MinecartEntityTypes extends DataFix {
   private static final List<String> MINECART_TYPE_LIST = Lists.newArrayList("MinecartRideable", "MinecartChest", "MinecartFurnace");

   public MinecartEntityTypes(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   public TypeRewriteRule makeRule() {
      TaggedChoiceType<String> taggedchoicetype = (TaggedChoiceType<String>)this.getInputSchema().findChoiceType(TypeReferences.ENTITY);
      TaggedChoiceType<String> taggedchoicetype1 = (TaggedChoiceType<String>)this.getOutputSchema().findChoiceType(TypeReferences.ENTITY);
      return this.fixTypeEverywhere("EntityMinecartIdentifiersFix", taggedchoicetype, taggedchoicetype1, (p_209746_2_) -> {
         return (p_206328_3_) -> {
            if (!Objects.equals(p_206328_3_.getFirst(), "Minecart")) {
               return p_206328_3_;
            } else {
               Typed<? extends Pair<String, ?>> typed = taggedchoicetype.point(p_209746_2_, "Minecart", p_206328_3_.getSecond()).orElseThrow(IllegalStateException::new);
               Dynamic<?> dynamic = typed.getOrCreate(DSL.remainderFinder());
               int i = dynamic.get("Type").asInt(0);
               String s;
               if (i > 0 && i < MINECART_TYPE_LIST.size()) {
                  s = MINECART_TYPE_LIST.get(i);
               } else {
                  s = "MinecartRideable";
               }

               return Pair.of(s, taggedchoicetype1.types().get(s).read(typed.write()).getSecond().orElseThrow(() -> {
                  return new IllegalStateException("Could not read the new minecart.");
               }));
            }
         };
      });
   }
}