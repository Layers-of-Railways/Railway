package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class V1451_2 extends NamespacedSchema {
   public V1451_2(int versionKey, Schema parent) {
      super(versionKey, parent);
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_registerBlockEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(p_registerBlockEntities_1_);
      p_registerBlockEntities_1_.register(map, "minecraft:piston", (p_206510_1_) -> {
         return DSL.optionalFields("blockState", TypeReferences.BLOCK_STATE.in(p_registerBlockEntities_1_));
      });
      return map;
   }
}