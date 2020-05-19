package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class V1800 extends NamespacedSchema {
   public V1800(int p_i50419_1_, Schema p_i50419_2_) {
      super(p_i50419_1_, p_i50419_2_);
   }

   protected static void func_219873_a(Schema p_219873_0_, Map<String, Supplier<TypeTemplate>> p_219873_1_, String p_219873_2_) {
      p_219873_0_.register(p_219873_1_, p_219873_2_, () -> {
         return V0100.equipment(p_219873_0_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_registerEntities_1_);
      func_219873_a(p_registerEntities_1_, map, "minecraft:panda");
      p_registerEntities_1_.register(map, "minecraft:pillager", (p_219875_1_) -> {
         return DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), V0100.equipment(p_registerEntities_1_));
      });
      return map;
   }
}