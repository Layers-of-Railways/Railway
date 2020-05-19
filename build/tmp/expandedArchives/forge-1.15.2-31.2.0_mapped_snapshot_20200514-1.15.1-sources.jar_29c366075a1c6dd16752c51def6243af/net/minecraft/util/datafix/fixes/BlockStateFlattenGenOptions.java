package net.minecraft.util.datafix.fixes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.util.datafix.TypeReferences;
import org.apache.commons.lang3.math.NumberUtils;

public class BlockStateFlattenGenOptions extends DataFix {
   private static final Splitter field_199181_a = Splitter.on(';').limit(5);
   private static final Splitter field_199182_b = Splitter.on(',');
   private static final Splitter field_199183_c = Splitter.on('x').limit(2);
   private static final Splitter field_199184_d = Splitter.on('*').limit(2);
   private static final Splitter field_199185_e = Splitter.on(':').limit(3);

   public BlockStateFlattenGenOptions(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("LevelFlatGeneratorInfoFix", this.getInputSchema().getType(TypeReferences.LEVEL), (p_207414_1_) -> {
         return p_207414_1_.update(DSL.remainderFinder(), this::fix);
      });
   }

   private Dynamic<?> fix(Dynamic<?> p_209636_1_) {
      return p_209636_1_.get("generatorName").asString("").equalsIgnoreCase("flat") ? p_209636_1_.update("generatorOptions", (p_209634_1_) -> {
         return DataFixUtils.orElse(p_209634_1_.asString().map(this::fixString).map(s -> p_209634_1_.createString(s)), p_209634_1_);
      }) : p_209636_1_;
   }

   @VisibleForTesting
   String fixString(String p_199180_1_) {
      if (p_199180_1_.isEmpty()) {
         return "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
      } else {
         Iterator<String> iterator = field_199181_a.split(p_199180_1_).iterator();
         String s = iterator.next();
         int i;
         String s1;
         if (iterator.hasNext()) {
            i = NumberUtils.toInt(s, 0);
            s1 = iterator.next();
         } else {
            i = 0;
            s1 = s;
         }

         if (i >= 0 && i <= 3) {
            StringBuilder stringbuilder = new StringBuilder();
            Splitter splitter = i < 3 ? field_199183_c : field_199184_d;
            stringbuilder.append(StreamSupport.stream(field_199182_b.split(s1).spliterator(), false).map((p_206368_2_) -> {
               List<String> list = splitter.splitToList(p_206368_2_);
               int j;
               String s2;
               if (list.size() == 2) {
                  j = NumberUtils.toInt(list.get(0));
                  s2 = list.get(1);
               } else {
                  j = 1;
                  s2 = list.get(0);
               }

               List<String> list1 = field_199185_e.splitToList(s2);
               int k = list1.get(0).equals("minecraft") ? 1 : 0;
               String s3 = list1.get(k);
               int l = i == 3 ? BlockStateFlatternEntities.getBlockId("minecraft:" + s3) : NumberUtils.toInt(s3, 0);
               int i1 = k + 1;
               int j1 = list1.size() > i1 ? NumberUtils.toInt(list1.get(i1), 0) : 0;
               return (j == 1 ? "" : j + "*") + BlockStateFlatteningMap.getFixedNBTForID(l << 4 | j1).get("Name").asString("");
            }).collect(Collectors.joining(",")));

            while(iterator.hasNext()) {
               stringbuilder.append(';').append(iterator.next());
            }

            return stringbuilder.toString();
         } else {
            return "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
         }
      }
   }
}