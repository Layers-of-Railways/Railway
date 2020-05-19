package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;

public class V1510 extends NamespacedSchema {
   public V1510(int versionKey, Schema schema) {
      super(versionKey, schema);
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_registerEntities_1_);
      map.put("minecraft:command_block_minecart", map.remove("minecraft:commandblock_minecart"));
      map.put("minecraft:end_crystal", map.remove("minecraft:ender_crystal"));
      map.put("minecraft:snow_golem", map.remove("minecraft:snowman"));
      map.put("minecraft:evoker", map.remove("minecraft:evocation_illager"));
      map.put("minecraft:evoker_fangs", map.remove("minecraft:evocation_fangs"));
      map.put("minecraft:illusioner", map.remove("minecraft:illusion_illager"));
      map.put("minecraft:vindicator", map.remove("minecraft:vindication_illager"));
      map.put("minecraft:iron_golem", map.remove("minecraft:villager_golem"));
      map.put("minecraft:experience_orb", map.remove("minecraft:xp_orb"));
      map.put("minecraft:experience_bottle", map.remove("minecraft:xp_bottle"));
      map.put("minecraft:eye_of_ender", map.remove("minecraft:eye_of_ender_signal"));
      map.put("minecraft:firework_rocket", map.remove("minecraft:fireworks_rocket"));
      return map;
   }
}