package net.minecraft.util.datafix.fixes;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.JsonOps;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.TypeReferences;

public class LevelDataGeneratorOptionsFix extends DataFix {
   static final Map<String, String> field_210553_a = Util.make(Maps.newHashMap(), (p_210550_0_) -> {
      p_210550_0_.put("0", "minecraft:ocean");
      p_210550_0_.put("1", "minecraft:plains");
      p_210550_0_.put("2", "minecraft:desert");
      p_210550_0_.put("3", "minecraft:mountains");
      p_210550_0_.put("4", "minecraft:forest");
      p_210550_0_.put("5", "minecraft:taiga");
      p_210550_0_.put("6", "minecraft:swamp");
      p_210550_0_.put("7", "minecraft:river");
      p_210550_0_.put("8", "minecraft:nether");
      p_210550_0_.put("9", "minecraft:the_end");
      p_210550_0_.put("10", "minecraft:frozen_ocean");
      p_210550_0_.put("11", "minecraft:frozen_river");
      p_210550_0_.put("12", "minecraft:snowy_tundra");
      p_210550_0_.put("13", "minecraft:snowy_mountains");
      p_210550_0_.put("14", "minecraft:mushroom_fields");
      p_210550_0_.put("15", "minecraft:mushroom_field_shore");
      p_210550_0_.put("16", "minecraft:beach");
      p_210550_0_.put("17", "minecraft:desert_hills");
      p_210550_0_.put("18", "minecraft:wooded_hills");
      p_210550_0_.put("19", "minecraft:taiga_hills");
      p_210550_0_.put("20", "minecraft:mountain_edge");
      p_210550_0_.put("21", "minecraft:jungle");
      p_210550_0_.put("22", "minecraft:jungle_hills");
      p_210550_0_.put("23", "minecraft:jungle_edge");
      p_210550_0_.put("24", "minecraft:deep_ocean");
      p_210550_0_.put("25", "minecraft:stone_shore");
      p_210550_0_.put("26", "minecraft:snowy_beach");
      p_210550_0_.put("27", "minecraft:birch_forest");
      p_210550_0_.put("28", "minecraft:birch_forest_hills");
      p_210550_0_.put("29", "minecraft:dark_forest");
      p_210550_0_.put("30", "minecraft:snowy_taiga");
      p_210550_0_.put("31", "minecraft:snowy_taiga_hills");
      p_210550_0_.put("32", "minecraft:giant_tree_taiga");
      p_210550_0_.put("33", "minecraft:giant_tree_taiga_hills");
      p_210550_0_.put("34", "minecraft:wooded_mountains");
      p_210550_0_.put("35", "minecraft:savanna");
      p_210550_0_.put("36", "minecraft:savanna_plateau");
      p_210550_0_.put("37", "minecraft:badlands");
      p_210550_0_.put("38", "minecraft:wooded_badlands_plateau");
      p_210550_0_.put("39", "minecraft:badlands_plateau");
      p_210550_0_.put("40", "minecraft:small_end_islands");
      p_210550_0_.put("41", "minecraft:end_midlands");
      p_210550_0_.put("42", "minecraft:end_highlands");
      p_210550_0_.put("43", "minecraft:end_barrens");
      p_210550_0_.put("44", "minecraft:warm_ocean");
      p_210550_0_.put("45", "minecraft:lukewarm_ocean");
      p_210550_0_.put("46", "minecraft:cold_ocean");
      p_210550_0_.put("47", "minecraft:deep_warm_ocean");
      p_210550_0_.put("48", "minecraft:deep_lukewarm_ocean");
      p_210550_0_.put("49", "minecraft:deep_cold_ocean");
      p_210550_0_.put("50", "minecraft:deep_frozen_ocean");
      p_210550_0_.put("127", "minecraft:the_void");
      p_210550_0_.put("129", "minecraft:sunflower_plains");
      p_210550_0_.put("130", "minecraft:desert_lakes");
      p_210550_0_.put("131", "minecraft:gravelly_mountains");
      p_210550_0_.put("132", "minecraft:flower_forest");
      p_210550_0_.put("133", "minecraft:taiga_mountains");
      p_210550_0_.put("134", "minecraft:swamp_hills");
      p_210550_0_.put("140", "minecraft:ice_spikes");
      p_210550_0_.put("149", "minecraft:modified_jungle");
      p_210550_0_.put("151", "minecraft:modified_jungle_edge");
      p_210550_0_.put("155", "minecraft:tall_birch_forest");
      p_210550_0_.put("156", "minecraft:tall_birch_hills");
      p_210550_0_.put("157", "minecraft:dark_forest_hills");
      p_210550_0_.put("158", "minecraft:snowy_taiga_mountains");
      p_210550_0_.put("160", "minecraft:giant_spruce_taiga");
      p_210550_0_.put("161", "minecraft:giant_spruce_taiga_hills");
      p_210550_0_.put("162", "minecraft:modified_gravelly_mountains");
      p_210550_0_.put("163", "minecraft:shattered_savanna");
      p_210550_0_.put("164", "minecraft:shattered_savanna_plateau");
      p_210550_0_.put("165", "minecraft:eroded_badlands");
      p_210550_0_.put("166", "minecraft:modified_wooded_badlands_plateau");
      p_210550_0_.put("167", "minecraft:modified_badlands_plateau");
   });

   public LevelDataGeneratorOptionsFix(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getOutputSchema().getType(TypeReferences.LEVEL);
      return this.fixTypeEverywhereTyped("LevelDataGeneratorOptionsFix", this.getInputSchema().getType(TypeReferences.LEVEL), type, (p_210545_1_) -> {
         Dynamic<?> dynamic = p_210545_1_.write();
         Optional<String> optional = dynamic.get("generatorOptions").asString();
         Dynamic<?> dynamic1;
         if ("flat".equalsIgnoreCase(dynamic.get("generatorName").asString(""))) {
            String s = optional.orElse("");
            dynamic1 = dynamic.set("generatorOptions", convert(s, dynamic.getOps()));
         } else if ("buffet".equalsIgnoreCase(dynamic.get("generatorName").asString("")) && optional.isPresent()) {
            Dynamic<JsonElement> dynamic2 = new Dynamic<>(JsonOps.INSTANCE, JSONUtils.fromJson(optional.get(), true));
            dynamic1 = dynamic.set("generatorOptions", dynamic2.convert(dynamic.getOps()));
         } else {
            dynamic1 = dynamic;
         }

         return type.readTyped(dynamic1).getSecond().orElseThrow(() -> {
            return new IllegalStateException("Could not read new level type.");
         });
      });
   }

   private static <T> Dynamic<T> convert(String p_210549_0_, DynamicOps<T> p_210549_1_) {
      Iterator<String> iterator = Splitter.on(';').split(p_210549_0_).iterator();
      String s = "minecraft:plains";
      Map<String, Map<String, String>> map = Maps.newHashMap();
      List<Pair<Integer, String>> list;
      if (!p_210549_0_.isEmpty() && iterator.hasNext()) {
         list = getLayersInfoFromString(iterator.next());
         if (!list.isEmpty()) {
            if (iterator.hasNext()) {
               s = field_210553_a.getOrDefault(iterator.next(), "minecraft:plains");
            }

            if (iterator.hasNext()) {
               String[] astring = iterator.next().toLowerCase(Locale.ROOT).split(",");

               for(String s1 : astring) {
                  String[] astring1 = s1.split("\\(", 2);
                  if (!astring1[0].isEmpty()) {
                     map.put(astring1[0], Maps.newHashMap());
                     if (astring1.length > 1 && astring1[1].endsWith(")") && astring1[1].length() > 1) {
                        String[] astring2 = astring1[1].substring(0, astring1[1].length() - 1).split(" ");

                        for(String s2 : astring2) {
                           String[] astring3 = s2.split("=", 2);
                           if (astring3.length == 2) {
                              map.get(astring1[0]).put(astring3[0], astring3[1]);
                           }
                        }
                     }
                  }
               }
            } else {
               map.put("village", Maps.newHashMap());
            }
         }
      } else {
         list = Lists.newArrayList();
         list.add(Pair.of(1, "minecraft:bedrock"));
         list.add(Pair.of(2, "minecraft:dirt"));
         list.add(Pair.of(1, "minecraft:grass_block"));
         map.put("village", Maps.newHashMap());
      }

      T t = p_210549_1_.createList(list.stream().map((p_210547_1_) -> {
         return p_210549_1_.createMap(ImmutableMap.of(p_210549_1_.createString("height"), p_210549_1_.createInt(p_210547_1_.getFirst()), p_210549_1_.createString("block"), p_210549_1_.createString(p_210547_1_.getSecond())));
      }));
      T t1 = p_210549_1_.createMap(map.entrySet().stream().map((p_210544_1_) -> {
         return Pair.of(p_210549_1_.createString(p_210544_1_.getKey().toLowerCase(Locale.ROOT)), p_210549_1_.createMap(p_210544_1_.getValue().entrySet().stream().map((p_210551_1_) -> {
            return Pair.of(p_210549_1_.createString(p_210551_1_.getKey()), p_210549_1_.createString(p_210551_1_.getValue()));
         }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))));
      }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
      return new Dynamic<>(p_210549_1_, p_210549_1_.createMap(ImmutableMap.of(p_210549_1_.createString("layers"), t, p_210549_1_.createString("biome"), p_210549_1_.createString(s), p_210549_1_.createString("structures"), t1)));
   }

   @Nullable
   private static Pair<Integer, String> getLayerInfoFromString(String p_210548_0_) {
      String[] astring = p_210548_0_.split("\\*", 2);
      int i;
      if (astring.length == 2) {
         try {
            i = Integer.parseInt(astring[0]);
         } catch (NumberFormatException var4) {
            return null;
         }
      } else {
         i = 1;
      }

      String s = astring[astring.length - 1];
      return Pair.of(i, s);
   }

   private static List<Pair<Integer, String>> getLayersInfoFromString(String p_210552_0_) {
      List<Pair<Integer, String>> list = Lists.newArrayList();
      String[] astring = p_210552_0_.split(",");

      for(String s : astring) {
         Pair<Integer, String> pair = getLayerInfoFromString(s);
         if (pair == null) {
            return Collections.emptyList();
         }

         list.add(pair);
      }

      return list;
   }
}