package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class EntityRenaming1510 extends TypedEntityRenameHelper {
   public static final Map<String, String> ENTITY_RENAME_MAP = ImmutableMap.<String, String>builder().put("minecraft:commandblock_minecart", "minecraft:command_block_minecart").put("minecraft:ender_crystal", "minecraft:end_crystal").put("minecraft:snowman", "minecraft:snow_golem").put("minecraft:evocation_illager", "minecraft:evoker").put("minecraft:evocation_fangs", "minecraft:evoker_fangs").put("minecraft:illusion_illager", "minecraft:illusioner").put("minecraft:vindication_illager", "minecraft:vindicator").put("minecraft:villager_golem", "minecraft:iron_golem").put("minecraft:xp_orb", "minecraft:experience_orb").put("minecraft:xp_bottle", "minecraft:experience_bottle").put("minecraft:eye_of_ender_signal", "minecraft:eye_of_ender").put("minecraft:fireworks_rocket", "minecraft:firework_rocket").build();
   public static final Map<String, String> BLOCK_RENAME_MAP = ImmutableMap.<String, String>builder().put("minecraft:portal", "minecraft:nether_portal").put("minecraft:oak_bark", "minecraft:oak_wood").put("minecraft:spruce_bark", "minecraft:spruce_wood").put("minecraft:birch_bark", "minecraft:birch_wood").put("minecraft:jungle_bark", "minecraft:jungle_wood").put("minecraft:acacia_bark", "minecraft:acacia_wood").put("minecraft:dark_oak_bark", "minecraft:dark_oak_wood").put("minecraft:stripped_oak_bark", "minecraft:stripped_oak_wood").put("minecraft:stripped_spruce_bark", "minecraft:stripped_spruce_wood").put("minecraft:stripped_birch_bark", "minecraft:stripped_birch_wood").put("minecraft:stripped_jungle_bark", "minecraft:stripped_jungle_wood").put("minecraft:stripped_acacia_bark", "minecraft:stripped_acacia_wood").put("minecraft:stripped_dark_oak_bark", "minecraft:stripped_dark_oak_wood").put("minecraft:mob_spawner", "minecraft:spawner").build();
   public static final Map<String, String> ITEM_RENAME_MAP = ImmutableMap.<String, String>builder().putAll(BLOCK_RENAME_MAP).put("minecraft:clownfish", "minecraft:tropical_fish").put("minecraft:chorus_fruit_popped", "minecraft:popped_chorus_fruit").put("minecraft:evocation_illager_spawn_egg", "minecraft:evoker_spawn_egg").put("minecraft:vindication_illager_spawn_egg", "minecraft:vindicator_spawn_egg").build();

   public EntityRenaming1510(Schema outputSchema, boolean changesType) {
      super("EntityTheRenameningBlock", outputSchema, changesType);
   }

   protected String rename(String name) {
      if (name.startsWith("minecraft:bred_")) {
         name = "minecraft:" + name.substring("minecraft:bred_".length());
      }

      return ENTITY_RENAME_MAP.getOrDefault(name, name);
   }
}