package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class CustomNameStringToComponentEntity extends DataFix {
   public CustomNameStringToComponentEntity(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   public TypeRewriteRule makeRule() {
      OpticFinder<String> opticfinder = DSL.fieldFinder("id", DSL.namespacedString());
      return this.fixTypeEverywhereTyped("EntityCustomNameToComponentFix", this.getInputSchema().getType(TypeReferences.ENTITY), (p_207792_1_) -> {
         return p_207792_1_.update(DSL.remainderFinder(), (p_207791_2_) -> {
            Optional<String> optional = p_207792_1_.getOptional(opticfinder);
            return optional.isPresent() && Objects.equals(optional.get(), "minecraft:commandblock_minecart") ? p_207791_2_ : fixTagCustomName(p_207791_2_);
         });
      });
   }

   public static Dynamic<?> fixTagCustomName(Dynamic<?> p_209740_0_) {
      String s = p_209740_0_.get("CustomName").asString("");
      return s.isEmpty() ? p_209740_0_.remove("CustomName") : p_209740_0_.set("CustomName", p_209740_0_.createString(ITextComponent.Serializer.toJson(new StringTextComponent(s))));
   }
}