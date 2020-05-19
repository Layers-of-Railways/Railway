package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class PotionItems extends DataFix {
   private static final String[] POTION_IDS = DataFixUtils.make(new String[128], (p_209316_0_) -> {
      p_209316_0_[0] = "minecraft:water";
      p_209316_0_[1] = "minecraft:regeneration";
      p_209316_0_[2] = "minecraft:swiftness";
      p_209316_0_[3] = "minecraft:fire_resistance";
      p_209316_0_[4] = "minecraft:poison";
      p_209316_0_[5] = "minecraft:healing";
      p_209316_0_[6] = "minecraft:night_vision";
      p_209316_0_[7] = null;
      p_209316_0_[8] = "minecraft:weakness";
      p_209316_0_[9] = "minecraft:strength";
      p_209316_0_[10] = "minecraft:slowness";
      p_209316_0_[11] = "minecraft:leaping";
      p_209316_0_[12] = "minecraft:harming";
      p_209316_0_[13] = "minecraft:water_breathing";
      p_209316_0_[14] = "minecraft:invisibility";
      p_209316_0_[15] = null;
      p_209316_0_[16] = "minecraft:awkward";
      p_209316_0_[17] = "minecraft:regeneration";
      p_209316_0_[18] = "minecraft:swiftness";
      p_209316_0_[19] = "minecraft:fire_resistance";
      p_209316_0_[20] = "minecraft:poison";
      p_209316_0_[21] = "minecraft:healing";
      p_209316_0_[22] = "minecraft:night_vision";
      p_209316_0_[23] = null;
      p_209316_0_[24] = "minecraft:weakness";
      p_209316_0_[25] = "minecraft:strength";
      p_209316_0_[26] = "minecraft:slowness";
      p_209316_0_[27] = "minecraft:leaping";
      p_209316_0_[28] = "minecraft:harming";
      p_209316_0_[29] = "minecraft:water_breathing";
      p_209316_0_[30] = "minecraft:invisibility";
      p_209316_0_[31] = null;
      p_209316_0_[32] = "minecraft:thick";
      p_209316_0_[33] = "minecraft:strong_regeneration";
      p_209316_0_[34] = "minecraft:strong_swiftness";
      p_209316_0_[35] = "minecraft:fire_resistance";
      p_209316_0_[36] = "minecraft:strong_poison";
      p_209316_0_[37] = "minecraft:strong_healing";
      p_209316_0_[38] = "minecraft:night_vision";
      p_209316_0_[39] = null;
      p_209316_0_[40] = "minecraft:weakness";
      p_209316_0_[41] = "minecraft:strong_strength";
      p_209316_0_[42] = "minecraft:slowness";
      p_209316_0_[43] = "minecraft:strong_leaping";
      p_209316_0_[44] = "minecraft:strong_harming";
      p_209316_0_[45] = "minecraft:water_breathing";
      p_209316_0_[46] = "minecraft:invisibility";
      p_209316_0_[47] = null;
      p_209316_0_[48] = null;
      p_209316_0_[49] = "minecraft:strong_regeneration";
      p_209316_0_[50] = "minecraft:strong_swiftness";
      p_209316_0_[51] = "minecraft:fire_resistance";
      p_209316_0_[52] = "minecraft:strong_poison";
      p_209316_0_[53] = "minecraft:strong_healing";
      p_209316_0_[54] = "minecraft:night_vision";
      p_209316_0_[55] = null;
      p_209316_0_[56] = "minecraft:weakness";
      p_209316_0_[57] = "minecraft:strong_strength";
      p_209316_0_[58] = "minecraft:slowness";
      p_209316_0_[59] = "minecraft:strong_leaping";
      p_209316_0_[60] = "minecraft:strong_harming";
      p_209316_0_[61] = "minecraft:water_breathing";
      p_209316_0_[62] = "minecraft:invisibility";
      p_209316_0_[63] = null;
      p_209316_0_[64] = "minecraft:mundane";
      p_209316_0_[65] = "minecraft:long_regeneration";
      p_209316_0_[66] = "minecraft:long_swiftness";
      p_209316_0_[67] = "minecraft:long_fire_resistance";
      p_209316_0_[68] = "minecraft:long_poison";
      p_209316_0_[69] = "minecraft:healing";
      p_209316_0_[70] = "minecraft:long_night_vision";
      p_209316_0_[71] = null;
      p_209316_0_[72] = "minecraft:long_weakness";
      p_209316_0_[73] = "minecraft:long_strength";
      p_209316_0_[74] = "minecraft:long_slowness";
      p_209316_0_[75] = "minecraft:long_leaping";
      p_209316_0_[76] = "minecraft:harming";
      p_209316_0_[77] = "minecraft:long_water_breathing";
      p_209316_0_[78] = "minecraft:long_invisibility";
      p_209316_0_[79] = null;
      p_209316_0_[80] = "minecraft:awkward";
      p_209316_0_[81] = "minecraft:long_regeneration";
      p_209316_0_[82] = "minecraft:long_swiftness";
      p_209316_0_[83] = "minecraft:long_fire_resistance";
      p_209316_0_[84] = "minecraft:long_poison";
      p_209316_0_[85] = "minecraft:healing";
      p_209316_0_[86] = "minecraft:long_night_vision";
      p_209316_0_[87] = null;
      p_209316_0_[88] = "minecraft:long_weakness";
      p_209316_0_[89] = "minecraft:long_strength";
      p_209316_0_[90] = "minecraft:long_slowness";
      p_209316_0_[91] = "minecraft:long_leaping";
      p_209316_0_[92] = "minecraft:harming";
      p_209316_0_[93] = "minecraft:long_water_breathing";
      p_209316_0_[94] = "minecraft:long_invisibility";
      p_209316_0_[95] = null;
      p_209316_0_[96] = "minecraft:thick";
      p_209316_0_[97] = "minecraft:regeneration";
      p_209316_0_[98] = "minecraft:swiftness";
      p_209316_0_[99] = "minecraft:long_fire_resistance";
      p_209316_0_[100] = "minecraft:poison";
      p_209316_0_[101] = "minecraft:strong_healing";
      p_209316_0_[102] = "minecraft:long_night_vision";
      p_209316_0_[103] = null;
      p_209316_0_[104] = "minecraft:long_weakness";
      p_209316_0_[105] = "minecraft:strength";
      p_209316_0_[106] = "minecraft:long_slowness";
      p_209316_0_[107] = "minecraft:leaping";
      p_209316_0_[108] = "minecraft:strong_harming";
      p_209316_0_[109] = "minecraft:long_water_breathing";
      p_209316_0_[110] = "minecraft:long_invisibility";
      p_209316_0_[111] = null;
      p_209316_0_[112] = null;
      p_209316_0_[113] = "minecraft:regeneration";
      p_209316_0_[114] = "minecraft:swiftness";
      p_209316_0_[115] = "minecraft:long_fire_resistance";
      p_209316_0_[116] = "minecraft:poison";
      p_209316_0_[117] = "minecraft:strong_healing";
      p_209316_0_[118] = "minecraft:long_night_vision";
      p_209316_0_[119] = null;
      p_209316_0_[120] = "minecraft:long_weakness";
      p_209316_0_[121] = "minecraft:strength";
      p_209316_0_[122] = "minecraft:long_slowness";
      p_209316_0_[123] = "minecraft:leaping";
      p_209316_0_[124] = "minecraft:strong_harming";
      p_209316_0_[125] = "minecraft:long_water_breathing";
      p_209316_0_[126] = "minecraft:long_invisibility";
      p_209316_0_[127] = null;
   });

   public PotionItems(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
      OpticFinder<Pair<String, String>> opticfinder = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
      OpticFinder<?> opticfinder1 = type.findField("tag");
      return this.fixTypeEverywhereTyped("ItemPotionFix", type, (p_206351_2_) -> {
         Optional<Pair<String, String>> optional = p_206351_2_.getOptional(opticfinder);
         if (optional.isPresent() && Objects.equals(optional.get().getSecond(), "minecraft:potion")) {
            Dynamic<?> dynamic = p_206351_2_.get(DSL.remainderFinder());
            Optional<? extends Typed<?>> optional1 = p_206351_2_.getOptionalTyped(opticfinder1);
            short short1 = dynamic.get("Damage").asShort((short)0);
            if (optional1.isPresent()) {
               Typed<?> typed = p_206351_2_;
               Dynamic<?> dynamic1 = optional1.get().get(DSL.remainderFinder());
               Optional<String> optional2 = dynamic1.get("Potion").asString();
               if (!optional2.isPresent()) {
                  String s = POTION_IDS[short1 & 127];
                  Typed<?> typed1 = optional1.get().set(DSL.remainderFinder(), dynamic1.set("Potion", dynamic1.createString(s == null ? "minecraft:water" : s)));
                  typed = p_206351_2_.set(opticfinder1, typed1);
                  if ((short1 & 16384) == 16384) {
                     typed = typed.set(opticfinder, Pair.of(TypeReferences.ITEM_NAME.typeName(), "minecraft:splash_potion"));
                  }
               }

               if (short1 != 0) {
                  dynamic = dynamic.set("Damage", dynamic.createShort((short)0));
               }

               return typed.set(DSL.remainderFinder(), dynamic);
            }
         }

         return p_206351_2_;
      });
   }
}