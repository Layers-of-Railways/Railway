package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class V1451_1 extends NamespacedSchema {
   public V1451_1(int versionKey, Schema parent) {
      super(versionKey, parent);
   }

   public void registerTypes(Schema p_registerTypes_1_, Map<String, Supplier<TypeTemplate>> p_registerTypes_2_, Map<String, Supplier<TypeTemplate>> p_registerTypes_3_) {
      super.registerTypes(p_registerTypes_1_, p_registerTypes_2_, p_registerTypes_3_);
      p_registerTypes_1_.registerType(false, TypeReferences.CHUNK, () -> {
         return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_)), "TileEntities", DSL.list(TypeReferences.BLOCK_ENTITY.in(p_registerTypes_1_)), "TileTicks", DSL.list(DSL.fields("i", TypeReferences.BLOCK_NAME.in(p_registerTypes_1_))), "Sections", DSL.list(DSL.optionalFields("Palette", DSL.list(TypeReferences.BLOCK_STATE.in(p_registerTypes_1_))))));
      });
   }
}