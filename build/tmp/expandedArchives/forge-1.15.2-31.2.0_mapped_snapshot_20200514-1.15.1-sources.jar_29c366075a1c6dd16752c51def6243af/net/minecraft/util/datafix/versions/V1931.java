package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;

public class V1931 extends NamespacedSchema {
   public V1931(int p_i50411_1_, Schema p_i50411_2_) {
      super(p_i50411_1_, p_i50411_2_);
   }

   protected static void func_219888_a(Schema p_219888_0_, Map<String, Supplier<TypeTemplate>> p_219888_1_, String p_219888_2_) {
      p_219888_0_.register(p_219888_1_, p_219888_2_, () -> {
         return V0100.equipment(p_219888_0_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_registerEntities_1_);
      func_219888_a(p_registerEntities_1_, map, "minecraft:fox");
      return map;
   }
}