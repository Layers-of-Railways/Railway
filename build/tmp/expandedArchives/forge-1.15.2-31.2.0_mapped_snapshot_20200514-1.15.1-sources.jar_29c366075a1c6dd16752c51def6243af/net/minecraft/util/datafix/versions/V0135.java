package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.TypeReferences;

public class V0135 extends Schema {
   public V0135(int versionKey, Schema parent) {
      super(versionKey, parent);
   }

   public void registerTypes(Schema p_registerTypes_1_, Map<String, Supplier<TypeTemplate>> p_registerTypes_2_, Map<String, Supplier<TypeTemplate>> p_registerTypes_3_) {
      super.registerTypes(p_registerTypes_1_, p_registerTypes_2_, p_registerTypes_3_);
      p_registerTypes_1_.registerType(false, TypeReferences.PLAYER, () -> {
         return DSL.optionalFields("RootVehicle", DSL.optionalFields("Entity", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_)), "Inventory", DSL.list(TypeReferences.ITEM_STACK.in(p_registerTypes_1_)), "EnderItems", DSL.list(TypeReferences.ITEM_STACK.in(p_registerTypes_1_)));
      });
      p_registerTypes_1_.registerType(true, TypeReferences.ENTITY_TYPE, () -> {
         return DSL.optionalFields("Passengers", DSL.list(TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_)), TypeReferences.ENTITY.in(p_registerTypes_1_));
      });
   }
}