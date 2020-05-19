package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.TypeReferences;

public class V0106 extends Schema {
   public V0106(int versionKey, Schema parent) {
      super(versionKey, parent);
   }

   public void registerTypes(Schema p_registerTypes_1_, Map<String, Supplier<TypeTemplate>> p_registerTypes_2_, Map<String, Supplier<TypeTemplate>> p_registerTypes_3_) {
      super.registerTypes(p_registerTypes_1_, p_registerTypes_2_, p_registerTypes_3_);
      p_registerTypes_1_.registerType(true, TypeReferences.UNTAGGED_SPAWNER, () -> {
         return DSL.optionalFields("SpawnPotentials", DSL.list(DSL.fields("Entity", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_))), "SpawnData", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_));
      });
   }
}