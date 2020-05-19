package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.util.datafix.TypeReferences;

public class EntityArmorAndHeld extends DataFix {
   public EntityArmorAndHeld(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   public TypeRewriteRule makeRule() {
      return this.cap(this.getInputSchema().getTypeRaw(TypeReferences.ITEM_STACK));
   }

   private <IS> TypeRewriteRule cap(Type<IS> p_206323_1_) {
      Type<Pair<Either<List<IS>, Unit>, Dynamic<?>>> type = DSL.and(DSL.optional(DSL.field("Equipment", DSL.list(p_206323_1_))), DSL.remainderType());
      Type<Pair<Either<List<IS>, Unit>, Pair<Either<List<IS>, Unit>, Dynamic<?>>>> type1 = DSL.and(DSL.optional(DSL.field("ArmorItems", DSL.list(p_206323_1_))), DSL.optional(DSL.field("HandItems", DSL.list(p_206323_1_))), DSL.remainderType());
      OpticFinder<Pair<Either<List<IS>, Unit>, Dynamic<?>>> opticfinder = DSL.typeFinder(type);
      OpticFinder<List<IS>> opticfinder1 = DSL.fieldFinder("Equipment", DSL.list(p_206323_1_));
      return this.fixTypeEverywhereTyped("EntityEquipmentToArmorAndHandFix", this.getInputSchema().getType(TypeReferences.ENTITY), this.getOutputSchema().getType(TypeReferences.ENTITY), (p_207448_4_) -> {
         Either<List<IS>, Unit> either = Either.right(DSL.unit());
         Either<List<IS>, Unit> either1 = Either.right(DSL.unit());
         Dynamic<?> dynamic = p_207448_4_.getOrCreate(DSL.remainderFinder());
         Optional<List<IS>> optional = p_207448_4_.getOptional(opticfinder1);
         if (optional.isPresent()) {
            List<IS> list = optional.get();
            IS is = p_206323_1_.read(dynamic.emptyMap()).getSecond().orElseThrow(() -> {
               return new IllegalStateException("Could not parse newly created empty itemstack.");
            });
            if (!list.isEmpty()) {
               either = Either.left(Lists.newArrayList(list.get(0), is));
            }

            if (list.size() > 1) {
               List<IS> list1 = Lists.newArrayList(is, is, is, is);

               for(int i = 1; i < Math.min(list.size(), 5); ++i) {
                  list1.set(i - 1, list.get(i));
               }

               either1 = Either.left(list1);
            }
         }

         Dynamic<?> dynamic2 = dynamic;
         Optional<? extends Stream<? extends Dynamic<?>>> optional1 = dynamic.get("DropChances").asStreamOpt();
         if (optional1.isPresent()) {
            Iterator<? extends Dynamic<?>> iterator = Stream.concat(optional1.get(), Stream.generate(() -> {
               return dynamic2.createInt(0);
            })).iterator();
            float f = iterator.next().asFloat(0.0F);
            if (!dynamic.get("HandDropChances").get().isPresent()) {
               Dynamic<?> dynamic1 = dynamic.emptyMap().merge(dynamic.createFloat(f)).merge(dynamic.createFloat(0.0F));
               dynamic = dynamic.set("HandDropChances", dynamic1);
            }

            if (!dynamic.get("ArmorDropChances").get().isPresent()) {
               Dynamic<?> dynamic3 = dynamic.emptyMap().merge(dynamic.createFloat(iterator.next().asFloat(0.0F))).merge(dynamic.createFloat(iterator.next().asFloat(0.0F))).merge(dynamic.createFloat(iterator.next().asFloat(0.0F))).merge(dynamic.createFloat(iterator.next().asFloat(0.0F)));
               dynamic = dynamic.set("ArmorDropChances", dynamic3);
            }

            dynamic = dynamic.remove("DropChances");
         }

         return p_207448_4_.set(opticfinder, type1, Pair.of(either, Pair.of(either1, dynamic)));
      });
   }
}