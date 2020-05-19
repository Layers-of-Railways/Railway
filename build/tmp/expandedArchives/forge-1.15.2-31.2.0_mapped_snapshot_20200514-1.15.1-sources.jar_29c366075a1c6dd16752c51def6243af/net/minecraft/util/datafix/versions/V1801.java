package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;

public class V1801 extends NamespacedSchema {
   public V1801(int p_i50418_1_, Schema p_i50418_2_) {
      super(p_i50418_1_, p_i50418_2_);
   }

   protected static void func_219878_a(Schema p_219878_0_, Map<String, Supplier<TypeTemplate>> p_219878_1_, String p_219878_2_) {
      p_219878_0_.register(p_219878_1_, p_219878_2_, () -> {
         return V0100.equipment(p_219878_0_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_registerEntities_1_);
      func_219878_a(p_registerEntities_1_, map, "minecraft:illager_beast");
      return map;
   }
}