package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class ItemStackEnchantmentFix extends DataFix {
   private static final Int2ObjectMap<String> field_208047_a = DataFixUtils.make(new Int2ObjectOpenHashMap<>(), (p_208043_0_) -> {
      p_208043_0_.put(0, "minecraft:protection");
      p_208043_0_.put(1, "minecraft:fire_protection");
      p_208043_0_.put(2, "minecraft:feather_falling");
      p_208043_0_.put(3, "minecraft:blast_protection");
      p_208043_0_.put(4, "minecraft:projectile_protection");
      p_208043_0_.put(5, "minecraft:respiration");
      p_208043_0_.put(6, "minecraft:aqua_affinity");
      p_208043_0_.put(7, "minecraft:thorns");
      p_208043_0_.put(8, "minecraft:depth_strider");
      p_208043_0_.put(9, "minecraft:frost_walker");
      p_208043_0_.put(10, "minecraft:binding_curse");
      p_208043_0_.put(16, "minecraft:sharpness");
      p_208043_0_.put(17, "minecraft:smite");
      p_208043_0_.put(18, "minecraft:bane_of_arthropods");
      p_208043_0_.put(19, "minecraft:knockback");
      p_208043_0_.put(20, "minecraft:fire_aspect");
      p_208043_0_.put(21, "minecraft:looting");
      p_208043_0_.put(22, "minecraft:sweeping");
      p_208043_0_.put(32, "minecraft:efficiency");
      p_208043_0_.put(33, "minecraft:silk_touch");
      p_208043_0_.put(34, "minecraft:unbreaking");
      p_208043_0_.put(35, "minecraft:fortune");
      p_208043_0_.put(48, "minecraft:power");
      p_208043_0_.put(49, "minecraft:punch");
      p_208043_0_.put(50, "minecraft:flame");
      p_208043_0_.put(51, "minecraft:infinity");
      p_208043_0_.put(61, "minecraft:luck_of_the_sea");
      p_208043_0_.put(62, "minecraft:lure");
      p_208043_0_.put(65, "minecraft:loyalty");
      p_208043_0_.put(66, "minecraft:impaling");
      p_208043_0_.put(67, "minecraft:riptide");
      p_208043_0_.put(68, "minecraft:channeling");
      p_208043_0_.put(70, "minecraft:mending");
      p_208043_0_.put(71, "minecraft:vanishing_curse");
   });

   public ItemStackEnchantmentFix(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
      OpticFinder<?> opticfinder = type.findField("tag");
      return this.fixTypeEverywhereTyped("ItemStackEnchantmentFix", type, (p_208045_2_) -> {
         return p_208045_2_.updateTyped(opticfinder, (p_208046_1_) -> {
            return p_208046_1_.update(DSL.remainderFinder(), this::fixTag);
         });
      });
   }

   private Dynamic<?> fixTag(Dynamic<?> p_209627_1_) {
      Optional<Dynamic<?>> optional = p_209627_1_.get("ench").asStreamOpt().map((p_209626_0_) -> {
         return p_209626_0_.map((p_209628_0_) -> {
            return p_209628_0_.set("id", p_209628_0_.createString(field_208047_a.getOrDefault(p_209628_0_.get("id").asInt(0), "null")));
         });
      }).map(p_209627_1_::createList);
      if (optional.isPresent()) {
         p_209627_1_ = p_209627_1_.remove("ench").set("Enchantments", optional.get());
      }

      return p_209627_1_.update("StoredEnchantments", (p_209625_0_) -> {
         return DataFixUtils.orElse(p_209625_0_.asStreamOpt().map((p_209623_0_) -> {
            return p_209623_0_.map((p_209624_0_) -> {
               return p_209624_0_.set("id", p_209624_0_.createString(field_208047_a.getOrDefault(p_209624_0_.get("id").asInt(0), "null")));
            });
         }).map(p_209625_0_::createList), p_209625_0_);
      });
   }
}