package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Map;
import java.util.Objects;
import net.minecraft.util.datafix.TypeReferences;

public class ChunkStatusFix2 extends DataFix {
   private static final Map<String, String> field_219825_a = ImmutableMap.<String, String>builder().put("structure_references", "empty").put("biomes", "empty").put("base", "surface").put("carved", "carvers").put("liquid_carved", "liquid_carvers").put("decorated", "features").put("lighted", "light").put("mobs_spawned", "spawn").put("finalized", "heightmaps").put("fullchunk", "full").build();

   public ChunkStatusFix2(Schema p_i50429_1_, boolean p_i50429_2_) {
      super(p_i50429_1_, p_i50429_2_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
      Type<?> type1 = type.findFieldType("Level");
      OpticFinder<?> opticfinder = DSL.fieldFinder("Level", type1);
      return this.fixTypeEverywhereTyped("ChunkStatusFix2", type, this.getOutputSchema().getType(TypeReferences.CHUNK), (p_219823_1_) -> {
         return p_219823_1_.updateTyped(opticfinder, (p_219824_0_) -> {
            Dynamic<?> dynamic = p_219824_0_.get(DSL.remainderFinder());
            String s = dynamic.get("Status").asString("empty");
            String s1 = field_219825_a.getOrDefault(s, "empty");
            return Objects.equals(s, s1) ? p_219824_0_ : p_219824_0_.set(DSL.remainderFinder(), dynamic.set("Status", dynamic.createString(s1)));
         });
      });
   }
}