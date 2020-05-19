package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.TypeReferences;

public class V1022 extends Schema {
   public V1022(int versionKey, Schema parent) {
      super(versionKey, parent);
   }

   public void registerTypes(Schema p_registerTypes_1_, Map<String, Supplier<TypeTemplate>> p_registerTypes_2_, Map<String, Supplier<TypeTemplate>> p_registerTypes_3_) {
      super.registerTypes(p_registerTypes_1_, p_registerTypes_2_, p_registerTypes_3_);
      p_registerTypes_1_.registerType(false, TypeReferences.RECIPE, () -> {
         return DSL.constType(DSL.namespacedString());
      });
      p_registerTypes_1_.registerType(false, TypeReferences.PLAYER, () -> {
         return DSL.optionalFields("RootVehicle", DSL.optionalFields("Entity", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_)), "Inventory", DSL.list(TypeReferences.ITEM_STACK.in(p_registerTypes_1_)), "EnderItems", DSL.list(TypeReferences.ITEM_STACK.in(p_registerTypes_1_)), DSL.optionalFields("ShoulderEntityLeft", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_), "ShoulderEntityRight", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_), "recipeBook", DSL.optionalFields("recipes", DSL.list(TypeReferences.RECIPE.in(p_registerTypes_1_)), "toBeDisplayed", DSL.list(TypeReferences.RECIPE.in(p_registerTypes_1_)))));
      });
      p_registerTypes_1_.registerType(false, TypeReferences.HOTBAR, () -> {
         return DSL.compoundList(DSL.list(TypeReferences.ITEM_STACK.in(p_registerTypes_1_)));
      });
   }
}