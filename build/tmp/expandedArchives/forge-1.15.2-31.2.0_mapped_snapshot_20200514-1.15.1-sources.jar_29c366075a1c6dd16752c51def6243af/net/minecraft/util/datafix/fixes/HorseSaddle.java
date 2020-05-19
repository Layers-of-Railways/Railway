package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class HorseSaddle extends NamedEntityFix {
   public HorseSaddle(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType, "EntityHorseSaddleFix", TypeReferences.ENTITY, "EntityHorse");
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      OpticFinder<Pair<String, String>> opticfinder = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
      Type<?> type = this.getInputSchema().getTypeRaw(TypeReferences.ITEM_STACK);
      OpticFinder<?> opticfinder1 = DSL.fieldFinder("SaddleItem", type);
      Optional<? extends Typed<?>> optional = p_207419_1_.getOptionalTyped(opticfinder1);
      Dynamic<?> dynamic = p_207419_1_.get(DSL.remainderFinder());
      if (!optional.isPresent() && dynamic.get("Saddle").asBoolean(false)) {
         Typed<?> typed = type.pointTyped(p_207419_1_.getOps()).orElseThrow(IllegalStateException::new);
         typed = typed.set(opticfinder, Pair.of(TypeReferences.ITEM_NAME.typeName(), "minecraft:saddle"));
         Dynamic<?> dynamic1 = dynamic.emptyMap();
         dynamic1 = dynamic1.set("Count", dynamic1.createByte((byte)1));
         dynamic1 = dynamic1.set("Damage", dynamic1.createShort((short)0));
         typed = typed.set(DSL.remainderFinder(), dynamic1);
         dynamic.remove("Saddle");
         return p_207419_1_.set(opticfinder1, typed).set(DSL.remainderFinder(), dynamic);
      } else {
         return p_207419_1_;
      }
   }
}