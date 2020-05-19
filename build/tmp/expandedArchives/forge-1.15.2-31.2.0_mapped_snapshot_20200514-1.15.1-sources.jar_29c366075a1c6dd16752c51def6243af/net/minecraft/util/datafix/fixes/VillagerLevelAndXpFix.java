package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.math.MathHelper;

public class VillagerLevelAndXpFix extends DataFix {
   private static final int[] field_223004_a = new int[]{0, 10, 50, 100, 150};

   public static int func_223001_a(int p_223001_0_) {
      return field_223004_a[MathHelper.clamp(p_223001_0_ - 1, 0, field_223004_a.length - 1)];
   }

   public VillagerLevelAndXpFix(Schema p_i51508_1_, boolean p_i51508_2_) {
      super(p_i51508_1_, p_i51508_2_);
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getChoiceType(TypeReferences.ENTITY, "minecraft:villager");
      OpticFinder<?> opticfinder = DSL.namedChoice("minecraft:villager", type);
      OpticFinder<?> opticfinder1 = type.findField("Offers");
      Type<?> type1 = opticfinder1.type();
      OpticFinder<?> opticfinder2 = type1.findField("Recipes");
      ListType<?> listtype = (ListType)opticfinder2.type();
      OpticFinder<?> opticfinder3 = listtype.getElement().finder();
      return this.fixTypeEverywhereTyped("Villager level and xp rebuild", this.getInputSchema().getType(TypeReferences.ENTITY), (p_222996_5_) -> {
         return p_222996_5_.updateTyped(opticfinder, type, (p_222995_3_) -> {
            Dynamic<?> dynamic = p_222995_3_.get(DSL.remainderFinder());
            int i = dynamic.get("VillagerData").get("level").asNumber().orElse(0).intValue();
            Typed<?> typed = p_222995_3_;
            if (i == 0 || i == 1) {
               int j = p_222995_3_.getOptionalTyped(opticfinder1).flatMap((p_223002_1_) -> {
                  return p_223002_1_.getOptionalTyped(opticfinder2);
               }).map((p_222997_1_) -> {
                  return p_222997_1_.getAllTyped(opticfinder3).size();
               }).orElse(0);
               i = MathHelper.clamp(j / 2, 1, 5);
               if (i > 1) {
                  typed = func_223003_a(p_222995_3_, i);
               }
            }

            Optional<Number> optional = dynamic.get("Xp").asNumber();
            if (!optional.isPresent()) {
               typed = func_222994_b(typed, i);
            }

            return typed;
         });
      });
   }

   private static Typed<?> func_223003_a(Typed<?> p_223003_0_, int p_223003_1_) {
      return p_223003_0_.update(DSL.remainderFinder(), (p_222998_1_) -> {
         return p_222998_1_.update("VillagerData", (p_222999_1_) -> {
            return p_222999_1_.set("level", p_222999_1_.createInt(p_223003_1_));
         });
      });
   }

   private static Typed<?> func_222994_b(Typed<?> p_222994_0_, int p_222994_1_) {
      int i = func_223001_a(p_222994_1_);
      return p_222994_0_.update(DSL.remainderFinder(), (p_223000_1_) -> {
         return p_223000_1_.set("Xp", p_223000_1_.createInt(i));
      });
   }
}