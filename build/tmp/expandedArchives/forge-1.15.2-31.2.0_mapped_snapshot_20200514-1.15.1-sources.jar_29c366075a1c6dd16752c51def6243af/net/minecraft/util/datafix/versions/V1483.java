package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;

public class V1483 extends NamespacedSchema {
   public V1483(int versionKey, Schema parent) {
      super(versionKey, parent);
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_registerEntities_1_);
      map.put("minecraft:pufferfish", map.remove("minecraft:puffer_fish"));
      return map;
   }
}