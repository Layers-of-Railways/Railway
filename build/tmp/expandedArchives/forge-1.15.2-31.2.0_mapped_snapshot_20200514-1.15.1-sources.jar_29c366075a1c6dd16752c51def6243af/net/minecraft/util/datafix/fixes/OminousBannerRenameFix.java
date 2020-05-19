package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class OminousBannerRenameFix extends DataFix {
   public OminousBannerRenameFix(Schema p_i50433_1_, boolean p_i50433_2_) {
      super(p_i50433_1_, p_i50433_2_);
   }

   private Dynamic<?> func_219818_a(Dynamic<?> p_219818_1_) {
      Optional<? extends Dynamic<?>> optional = p_219818_1_.get("display").get();
      if (optional.isPresent()) {
         Dynamic<?> dynamic = optional.get();
         Optional<String> optional1 = dynamic.get("Name").asString();
         if (optional1.isPresent()) {
            String s = optional1.get();
            s = s.replace("\"translate\":\"block.minecraft.illager_banner\"", "\"translate\":\"block.minecraft.ominous_banner\"");
            dynamic = dynamic.set("Name", dynamic.createString(s));
         }

         return p_219818_1_.set("display", dynamic);
      } else {
         return p_219818_1_;
      }
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
      OpticFinder<Pair<String, String>> opticfinder = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
      OpticFinder<?> opticfinder1 = type.findField("tag");
      return this.fixTypeEverywhereTyped("OminousBannerRenameFix", type, (p_219819_3_) -> {
         Optional<Pair<String, String>> optional = p_219819_3_.getOptional(opticfinder);
         if (optional.isPresent() && Objects.equals(optional.get().getSecond(), "minecraft:white_banner")) {
            Optional<? extends Typed<?>> optional1 = p_219819_3_.getOptionalTyped(opticfinder1);
            if (optional1.isPresent()) {
               Typed<?> typed = optional1.get();
               Dynamic<?> dynamic = typed.get(DSL.remainderFinder());
               return p_219819_3_.set(opticfinder1, typed.set(DSL.remainderFinder(), this.func_219818_a(dynamic)));
            }
         }

         return p_219819_3_;
      });
   }
}