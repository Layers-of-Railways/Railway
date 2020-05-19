package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;

public class V1451_5 extends NamespacedSchema {
   public V1451_5(int versionKey, Schema parent) {
      super(versionKey, parent);
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_registerBlockEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(p_registerBlockEntities_1_);
      map.remove("minecraft:flower_pot");
      map.remove("minecraft:noteblock");
      return map;
   }
}