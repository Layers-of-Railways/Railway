package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class V1920 extends NamespacedSchema {
   public V1920(int p_i50414_1_, Schema p_i50414_2_) {
      super(p_i50414_1_, p_i50414_2_);
   }

   protected static void func_219886_a(Schema p_219886_0_, Map<String, Supplier<TypeTemplate>> p_219886_1_, String p_219886_2_) {
      p_219886_0_.register(p_219886_1_, p_219886_2_, () -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_219886_0_)));
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_registerBlockEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(p_registerBlockEntities_1_);
      func_219886_a(p_registerBlockEntities_1_, map, "minecraft:campfire");
      return map;
   }
}