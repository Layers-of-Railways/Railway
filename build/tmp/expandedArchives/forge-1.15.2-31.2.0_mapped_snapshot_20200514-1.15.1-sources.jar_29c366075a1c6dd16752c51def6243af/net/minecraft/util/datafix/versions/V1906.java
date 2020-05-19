package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class V1906 extends NamespacedSchema {
   public V1906(int p_i50416_1_, Schema p_i50416_2_) {
      super(p_i50416_1_, p_i50416_2_);
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_registerBlockEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(p_registerBlockEntities_1_);
      func_219880_a(p_registerBlockEntities_1_, map, "minecraft:barrel");
      func_219880_a(p_registerBlockEntities_1_, map, "minecraft:smoker");
      func_219880_a(p_registerBlockEntities_1_, map, "minecraft:blast_furnace");
      p_registerBlockEntities_1_.register(map, "minecraft:lectern", (p_219882_1_) -> {
         return DSL.optionalFields("Book", TypeReferences.ITEM_STACK.in(p_registerBlockEntities_1_));
      });
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:bell");
      return map;
   }

   protected static void func_219880_a(Schema p_219880_0_, Map<String, Supplier<TypeTemplate>> p_219880_1_, String p_219880_2_) {
      p_219880_0_.register(p_219880_1_, p_219880_2_, () -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_219880_0_)));
      });
   }
}