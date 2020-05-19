package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class V1928 extends NamespacedSchema {
   public V1928(int p_i50413_1_, Schema p_i50413_2_) {
      super(p_i50413_1_, p_i50413_2_);
   }

   protected static TypeTemplate func_219884_a(Schema p_219884_0_) {
      return DSL.optionalFields("ArmorItems", DSL.list(TypeReferences.ITEM_STACK.in(p_219884_0_)), "HandItems", DSL.list(TypeReferences.ITEM_STACK.in(p_219884_0_)));
   }

   protected static void func_219883_a(Schema p_219883_0_, Map<String, Supplier<TypeTemplate>> p_219883_1_, String p_219883_2_) {
      p_219883_0_.register(p_219883_1_, p_219883_2_, () -> {
         return func_219884_a(p_219883_0_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_registerEntities_1_);
      map.remove("minecraft:illager_beast");
      func_219883_a(p_registerEntities_1_, map, "minecraft:ravager");
      return map;
   }
}