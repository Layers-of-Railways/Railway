package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;

public class V1904 extends NamespacedSchema {
   public V1904(int p_i50417_1_, Schema p_i50417_2_) {
      super(p_i50417_1_, p_i50417_2_);
   }

   protected static void func_219876_a(Schema p_219876_0_, Map<String, Supplier<TypeTemplate>> p_219876_1_, String p_219876_2_) {
      p_219876_0_.register(p_219876_1_, p_219876_2_, () -> {
         return V0100.equipment(p_219876_0_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_registerEntities_1_);
      func_219876_a(p_registerEntities_1_, map, "minecraft:cat");
      return map;
   }
}