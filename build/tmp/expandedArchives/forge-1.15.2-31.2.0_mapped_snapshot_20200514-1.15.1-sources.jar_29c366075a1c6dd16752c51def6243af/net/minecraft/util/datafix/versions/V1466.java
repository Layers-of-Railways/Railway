package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class V1466 extends NamespacedSchema {
   public V1466(int versionKey, Schema parent) {
      super(versionKey, parent);
   }

   public void registerTypes(Schema p_registerTypes_1_, Map<String, Supplier<TypeTemplate>> p_registerTypes_2_, Map<String, Supplier<TypeTemplate>> p_registerTypes_3_) {
      super.registerTypes(p_registerTypes_1_, p_registerTypes_2_, p_registerTypes_3_);
      p_registerTypes_1_.registerType(false, TypeReferences.CHUNK, () -> {
         return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_)), "TileEntities", DSL.list(TypeReferences.BLOCK_ENTITY.in(p_registerTypes_1_)), "TileTicks", DSL.list(DSL.fields("i", TypeReferences.BLOCK_NAME.in(p_registerTypes_1_))), "Sections", DSL.list(DSL.optionalFields("Palette", DSL.list(TypeReferences.BLOCK_STATE.in(p_registerTypes_1_)))), "Structures", DSL.optionalFields("Starts", DSL.compoundList(TypeReferences.STRUCTURE_FEATURE.in(p_registerTypes_1_)))));
      });
      p_registerTypes_1_.registerType(false, TypeReferences.STRUCTURE_FEATURE, () -> {
         return DSL.optionalFields("Children", DSL.list(DSL.optionalFields("CA", TypeReferences.BLOCK_STATE.in(p_registerTypes_1_), "CB", TypeReferences.BLOCK_STATE.in(p_registerTypes_1_), "CC", TypeReferences.BLOCK_STATE.in(p_registerTypes_1_), "CD", TypeReferences.BLOCK_STATE.in(p_registerTypes_1_))), "biome", TypeReferences.BIOME.in(p_registerTypes_1_));
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_registerBlockEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(p_registerBlockEntities_1_);
      map.put("DUMMY", DSL::remainder);
      return map;
   }
}