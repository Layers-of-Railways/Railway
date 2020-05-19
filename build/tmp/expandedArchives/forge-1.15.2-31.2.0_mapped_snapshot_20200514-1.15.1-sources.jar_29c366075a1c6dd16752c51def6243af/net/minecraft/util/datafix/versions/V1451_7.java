package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class V1451_7 extends NamespacedSchema {
   public V1451_7(int versionKey, Schema parent) {
      super(versionKey, parent);
   }

   public void registerTypes(Schema p_registerTypes_1_, Map<String, Supplier<TypeTemplate>> p_registerTypes_2_, Map<String, Supplier<TypeTemplate>> p_registerTypes_3_) {
      super.registerTypes(p_registerTypes_1_, p_registerTypes_2_, p_registerTypes_3_);
      p_registerTypes_1_.registerType(false, TypeReferences.STRUCTURE_FEATURE, () -> {
         return DSL.optionalFields("Children", DSL.list(DSL.optionalFields("CA", TypeReferences.BLOCK_STATE.in(p_registerTypes_1_), "CB", TypeReferences.BLOCK_STATE.in(p_registerTypes_1_), "CC", TypeReferences.BLOCK_STATE.in(p_registerTypes_1_), "CD", TypeReferences.BLOCK_STATE.in(p_registerTypes_1_))));
      });
   }
}