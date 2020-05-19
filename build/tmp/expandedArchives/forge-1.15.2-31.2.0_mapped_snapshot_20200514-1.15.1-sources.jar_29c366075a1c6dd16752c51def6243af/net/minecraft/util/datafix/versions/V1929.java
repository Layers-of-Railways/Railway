package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class V1929 extends NamespacedSchema {
   public V1929(int p_i50412_1_, Schema p_i50412_2_) {
      super(p_i50412_1_, p_i50412_2_);
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_registerEntities_1_);
      p_registerEntities_1_.register(map, "minecraft:wandering_trader", (p_219890_1_) -> {
         return DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "buyB", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "sell", TypeReferences.ITEM_STACK.in(p_registerEntities_1_)))), V0100.equipment(p_registerEntities_1_));
      });
      p_registerEntities_1_.register(map, "minecraft:trader_llama", (p_219891_1_) -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "DecorItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_));
      });
      return map;
   }
}