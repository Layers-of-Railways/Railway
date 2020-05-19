package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class V1125 extends NamespacedSchema {
   public V1125(int versionKey, Schema parent) {
      super(versionKey, parent);
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_registerBlockEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(p_registerBlockEntities_1_);
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:bed");
      return map;
   }

   public void registerTypes(Schema p_registerTypes_1_, Map<String, Supplier<TypeTemplate>> p_registerTypes_2_, Map<String, Supplier<TypeTemplate>> p_registerTypes_3_) {
      super.registerTypes(p_registerTypes_1_, p_registerTypes_2_, p_registerTypes_3_);
      p_registerTypes_1_.registerType(false, TypeReferences.ADVANCEMENTS, () -> {
         return DSL.optionalFields("minecraft:adventure/adventuring_time", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.BIOME.in(p_registerTypes_1_), DSL.constType(DSL.string()))), "minecraft:adventure/kill_a_mob", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.ENTITY_NAME.in(p_registerTypes_1_), DSL.constType(DSL.string()))), "minecraft:adventure/kill_all_mobs", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.ENTITY_NAME.in(p_registerTypes_1_), DSL.constType(DSL.string()))), "minecraft:husbandry/bred_all_animals", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.ENTITY_NAME.in(p_registerTypes_1_), DSL.constType(DSL.string()))));
      });
      p_registerTypes_1_.registerType(false, TypeReferences.BIOME, () -> {
         return DSL.constType(DSL.namespacedString());
      });
      p_registerTypes_1_.registerType(false, TypeReferences.ENTITY_NAME, () -> {
         return DSL.constType(DSL.namespacedString());
      });
   }
}