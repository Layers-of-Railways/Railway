package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class PointOfInterestReorganizationFix extends DataFix {
   public PointOfInterestReorganizationFix(Schema p_i50421_1_, boolean p_i50421_2_) {
      super(p_i50421_1_, p_i50421_2_);
   }

   protected TypeRewriteRule makeRule() {
      Type<Pair<String, Dynamic<?>>> type = DSL.named(TypeReferences.POI_CHUNK.typeName(), DSL.remainderType());
      if (!Objects.equals(type, this.getInputSchema().getType(TypeReferences.POI_CHUNK))) {
         throw new IllegalStateException("Poi type is not what was expected.");
      } else {
         return this.fixTypeEverywhere("POI reorganization", type, (p_219871_0_) -> {
            return (p_219872_0_) -> {
               return p_219872_0_.mapSecond(PointOfInterestReorganizationFix::func_219870_a);
            };
         });
      }
   }

   private static <T> Dynamic<T> func_219870_a(Dynamic<T> p_219870_0_) {
      Map<Dynamic<T>, Dynamic<T>> map = Maps.newHashMap();

      for(int i = 0; i < 16; ++i) {
         String s = String.valueOf(i);
         Optional<Dynamic<T>> optional = p_219870_0_.get(s).get();
         if (optional.isPresent()) {
            Dynamic<T> dynamic = optional.get();
            Dynamic<T> dynamic1 = p_219870_0_.createMap(ImmutableMap.of(p_219870_0_.createString("Records"), dynamic));
            map.put(p_219870_0_.createInt(i), dynamic1);
            p_219870_0_ = p_219870_0_.remove(s);
         }
      }

      return p_219870_0_.set("Sections", p_219870_0_.createMap(map));
   }
}