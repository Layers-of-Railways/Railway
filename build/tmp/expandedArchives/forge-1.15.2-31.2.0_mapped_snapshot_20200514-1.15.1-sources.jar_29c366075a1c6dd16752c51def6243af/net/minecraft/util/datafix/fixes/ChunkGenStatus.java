package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.datafix.TypeReferences;

public class ChunkGenStatus extends DataFix {
   public ChunkGenStatus(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
      Type<?> type1 = this.getOutputSchema().getType(TypeReferences.CHUNK);
      Type<?> type2 = type.findFieldType("Level");
      Type<?> type3 = type1.findFieldType("Level");
      Type<?> type4 = type2.findFieldType("TileTicks");
      OpticFinder<?> opticfinder = DSL.fieldFinder("Level", type2);
      OpticFinder<?> opticfinder1 = DSL.fieldFinder("TileTicks", type4);
      return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("ChunkToProtoChunkFix", type, this.getOutputSchema().getType(TypeReferences.CHUNK), (p_209732_3_) -> {
         return p_209732_3_.updateTyped(opticfinder, type3, (p_207915_2_) -> {
            Optional<? extends Stream<? extends Dynamic<?>>> optional = p_207915_2_.getOptionalTyped(opticfinder1).map(Typed::write).flatMap(Dynamic::asStreamOpt);
            Dynamic<?> dynamic = p_207915_2_.get(DSL.remainderFinder());
            boolean flag = dynamic.get("TerrainPopulated").asBoolean(false) && (!dynamic.get("LightPopulated").asNumber().isPresent() || dynamic.get("LightPopulated").asBoolean(false));
            dynamic = dynamic.set("Status", dynamic.createString(flag ? "mobs_spawned" : "empty"));
            dynamic = dynamic.set("hasLegacyStructureData", dynamic.createBoolean(true));
            Dynamic<?> dynamic1;
            if (flag) {
               Optional<ByteBuffer> optional1 = dynamic.get("Biomes").asByteBufferOpt();
               if (optional1.isPresent()) {
                  ByteBuffer bytebuffer = optional1.get();
                  int[] aint = new int[256];

                  for(int i = 0; i < aint.length; ++i) {
                     if (i < bytebuffer.capacity()) {
                        aint[i] = bytebuffer.get(i) & 255;
                     }
                  }

                  dynamic = dynamic.set("Biomes", dynamic.createIntList(Arrays.stream(aint)));
               }

               Dynamic<?> dynamic2 = dynamic;
               List<Dynamic<?>> list = IntStream.range(0, 16).mapToObj((p_211428_1_) -> {
                  return dynamic2.createList(Stream.empty());
               }).collect(Collectors.toList());
               if (optional.isPresent()) {
                  optional.get().forEach((p_211426_2_) -> {
                     int j = p_211426_2_.get("x").asInt(0);
                     int k = p_211426_2_.get("y").asInt(0);
                     int l = p_211426_2_.get("z").asInt(0);
                     short short1 = packOffsetCoordinates(j, k, l);
                     list.set(k >> 4, list.get(k >> 4).merge(dynamic2.createShort(short1)));
                  });
                  dynamic = dynamic.set("ToBeTicked", dynamic.createList(list.stream()));
               }

               dynamic1 = p_207915_2_.set(DSL.remainderFinder(), dynamic).write();
            } else {
               dynamic1 = dynamic;
            }

            return type3.readTyped(dynamic1).getSecond().orElseThrow(() -> {
               return new IllegalStateException("Could not read the new chunk");
            });
         });
      }), this.writeAndRead("Structure biome inject", this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE), this.getOutputSchema().getType(TypeReferences.STRUCTURE_FEATURE)));
   }

   private static short packOffsetCoordinates(int p_210975_0_, int p_210975_1_, int p_210975_2_) {
      return (short)(p_210975_0_ & 15 | (p_210975_1_ & 15) << 4 | (p_210975_2_ & 15) << 8);
   }
}