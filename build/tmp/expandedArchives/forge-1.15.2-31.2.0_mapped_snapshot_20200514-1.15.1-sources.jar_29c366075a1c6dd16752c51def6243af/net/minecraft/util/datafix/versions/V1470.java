package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class V1470 extends NamespacedSchema {
   public V1470(int versionKey, Schema parent) {
      super(versionKey, parent);
   }

   protected static void registerEntity(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
      schema.register(map, name, () -> {
         return V0100.equipment(schema);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_registerEntities_1_);
      registerEntity(p_registerEntities_1_, map, "minecraft:turtle");
      registerEntity(p_registerEntities_1_, map, "minecraft:cod_mob");
      registerEntity(p_registerEntities_1_, map, "minecraft:tropical_fish");
      registerEntity(p_registerEntities_1_, map, "minecraft:salmon_mob");
      registerEntity(p_registerEntities_1_, map, "minecraft:puffer_fish");
      registerEntity(p_registerEntities_1_, map, "minecraft:phantom");
      registerEntity(p_registerEntities_1_, map, "minecraft:dolphin");
      registerEntity(p_registerEntities_1_, map, "minecraft:drowned");
      p_registerEntities_1_.register(map, "minecraft:trident", (p_206561_1_) -> {
         return DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_));
      });
      return map;
   }
}