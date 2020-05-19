package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class RidingToPassengers extends DataFix {
   public RidingToPassengers(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   public TypeRewriteRule makeRule() {
      Schema schema = this.getInputSchema();
      Schema schema1 = this.getOutputSchema();
      Type<?> type = schema.getTypeRaw(TypeReferences.ENTITY_TYPE);
      Type<?> type1 = schema1.getTypeRaw(TypeReferences.ENTITY_TYPE);
      Type<?> type2 = schema.getTypeRaw(TypeReferences.ENTITY);
      return this.cap(schema, schema1, type, type1, type2);
   }

   private <OldEntityTree, NewEntityTree, Entity> TypeRewriteRule cap(Schema p_206340_1_, Schema p_206340_2_, Type<OldEntityTree> p_206340_3_, Type<NewEntityTree> p_206340_4_, Type<Entity> p_206340_5_) {
      Type<Pair<String, Pair<Either<OldEntityTree, Unit>, Entity>>> type = DSL.named(TypeReferences.ENTITY_TYPE.typeName(), DSL.and(DSL.optional(DSL.field("Riding", p_206340_3_)), p_206340_5_));
      Type<Pair<String, Pair<Either<List<NewEntityTree>, Unit>, Entity>>> type1 = DSL.named(TypeReferences.ENTITY_TYPE.typeName(), DSL.and(DSL.optional(DSL.field("Passengers", DSL.list(p_206340_4_))), p_206340_5_));
      Type<?> type2 = p_206340_1_.getType(TypeReferences.ENTITY_TYPE);
      Type<?> type3 = p_206340_2_.getType(TypeReferences.ENTITY_TYPE);
      if (!Objects.equals(type2, type)) {
         throw new IllegalStateException("Old entity type is not what was expected.");
      } else if (!type3.equals(type1, true, true)) {
         throw new IllegalStateException("New entity type is not what was expected.");
      } else {
         OpticFinder<Pair<String, Pair<Either<OldEntityTree, Unit>, Entity>>> opticfinder = DSL.typeFinder(type);
         OpticFinder<Pair<String, Pair<Either<List<NewEntityTree>, Unit>, Entity>>> opticfinder1 = DSL.typeFinder(type1);
         OpticFinder<NewEntityTree> opticfinder2 = DSL.typeFinder(p_206340_4_);
         Type<?> type4 = p_206340_1_.getType(TypeReferences.PLAYER);
         Type<?> type5 = p_206340_2_.getType(TypeReferences.PLAYER);
         return TypeRewriteRule.seq(this.fixTypeEverywhere("EntityRidingToPassengerFix", type, type1, (p_209760_5_) -> {
            return (p_208042_6_) -> {
               Optional<Pair<String, Pair<Either<List<NewEntityTree>, Unit>, Entity>>> optional = Optional.empty();
               Pair<String, Pair<Either<OldEntityTree, Unit>, Entity>> pair = p_208042_6_;

               while(true) {
                  Either<List<NewEntityTree>, Unit> either = DataFixUtils.orElse(optional.map((p_208037_4_) -> {
                     Typed<NewEntityTree> typed = p_206340_4_.pointTyped(p_209760_5_).orElseThrow(() -> {
                        return new IllegalStateException("Could not create new entity tree");
                     });
                     NewEntityTree newentitytree = typed.set(opticfinder1, p_208037_4_).getOptional(opticfinder2).orElseThrow(() -> {
                        return new IllegalStateException("Should always have an entity tree here");
                     });
                     return Either.left(ImmutableList.of(newentitytree));
                  }), Either.right(DSL.unit()));
                  optional = Optional.of(Pair.of(TypeReferences.ENTITY_TYPE.typeName(), Pair.of(either, pair.getSecond().getSecond())));
                  Optional<OldEntityTree> optional1 = pair.getSecond().getFirst().left();
                  if (!optional1.isPresent()) {
                     return optional.orElseThrow(() -> {
                        return new IllegalStateException("Should always have an entity tree here");
                     });
                  }

                  pair = (new Typed<>(p_206340_3_, p_209760_5_, optional1.get())).getOptional(opticfinder).orElseThrow(() -> {
                     return new IllegalStateException("Should always have an entity here");
                  });
               }
            };
         }), this.writeAndRead("player RootVehicle injecter", type4, type5));
      }
   }
}