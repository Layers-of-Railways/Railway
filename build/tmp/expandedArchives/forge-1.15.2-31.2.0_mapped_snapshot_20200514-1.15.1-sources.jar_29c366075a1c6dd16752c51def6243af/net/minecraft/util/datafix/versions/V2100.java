package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class V2100 extends NamespacedSchema {
   public V2100(int p_i225705_1_, Schema p_i225705_2_) {
      super(p_i225705_1_, p_i225705_2_);
   }

   protected static void func_226217_a_(Schema p_226217_0_, Map<String, Supplier<TypeTemplate>> p_226217_1_, String p_226217_2_) {
      p_226217_0_.register(p_226217_1_, p_226217_2_, () -> {
         return V0100.equipment(p_226217_0_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_registerEntities_1_);
      func_226217_a_(p_registerEntities_1_, map, "minecraft:bee");
      func_226217_a_(p_registerEntities_1_, map, "minecraft:bee_stinger");
      return map;
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_registerBlockEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(p_registerBlockEntities_1_);
      p_registerBlockEntities_1_.register(map, "minecraft:beehive", () -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerBlockEntities_1_)), "Bees", DSL.list(DSL.optionalFields("EntityData", TypeReferences.ENTITY_TYPE.in(p_registerBlockEntities_1_))));
      });
      return map;
   }
}