package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.TypeReferences;

public class VillagerTrades extends NamedEntityFix {
   public VillagerTrades(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType, "Villager trade fix", TypeReferences.ENTITY, "minecraft:villager");
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      OpticFinder<?> opticfinder = p_207419_1_.getType().findField("Offers");
      OpticFinder<?> opticfinder1 = opticfinder.type().findField("Recipes");
      Type<?> type = opticfinder1.type();
      if (!(type instanceof ListType)) {
         throw new IllegalStateException("Recipes are expected to be a list.");
      } else {
         ListType<?> listtype = (ListType)type;
         Type<?> type1 = listtype.getElement();
         OpticFinder<?> opticfinder2 = DSL.typeFinder(type1);
         OpticFinder<?> opticfinder3 = type1.findField("buy");
         OpticFinder<?> opticfinder4 = type1.findField("buyB");
         OpticFinder<?> opticfinder5 = type1.findField("sell");
         OpticFinder<Pair<String, String>> opticfinder6 = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
         Function<Typed<?>, Typed<?>> function = (p_209284_2_) -> {
            return this.updateItemStack(opticfinder6, p_209284_2_);
         };
         return p_207419_1_.updateTyped(opticfinder, (p_209285_6_) -> {
            return p_209285_6_.updateTyped(opticfinder1, (p_209287_5_) -> {
               return p_209287_5_.updateTyped(opticfinder2, (p_209286_4_) -> {
                  return p_209286_4_.updateTyped(opticfinder3, function).updateTyped(opticfinder4, function).updateTyped(opticfinder5, function);
               });
            });
         });
      }
   }

   private Typed<?> updateItemStack(OpticFinder<Pair<String, String>> p_210482_1_, Typed<?> p_210482_2_) {
      return p_210482_2_.update(p_210482_1_, (p_209288_0_) -> {
         return p_209288_0_.mapSecond((p_209289_0_) -> {
            return Objects.equals(p_209289_0_, "minecraft:carved_pumpkin") ? "minecraft:pumpkin" : p_209289_0_;
         });
      });
   }
}