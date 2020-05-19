package net.minecraft.world.storage.loot;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import net.minecraft.util.ResourceLocation;

public class LootTables {
   private static final Set<ResourceLocation> LOOT_TABLES = Sets.newHashSet();
   private static final Set<ResourceLocation> READ_ONLY_LOOT_TABLES = Collections.unmodifiableSet(LOOT_TABLES);
   public static final ResourceLocation EMPTY = new ResourceLocation("empty");
   public static final ResourceLocation CHESTS_SPAWN_BONUS_CHEST = register("chests/spawn_bonus_chest");
   public static final ResourceLocation CHESTS_END_CITY_TREASURE = register("chests/end_city_treasure");
   public static final ResourceLocation CHESTS_SIMPLE_DUNGEON = register("chests/simple_dungeon");
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_WEAPONSMITH = register("chests/village/village_weaponsmith");
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_TOOLSMITH = register("chests/village/village_toolsmith");
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_ARMORER = register("chests/village/village_armorer");
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_CARTOGRAPHER = register("chests/village/village_cartographer");
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_MASON = register("chests/village/village_mason");
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_SHEPHERD = register("chests/village/village_shepherd");
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_BUTCHER = register("chests/village/village_butcher");
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_FLETCHER = register("chests/village/village_fletcher");
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_FISHER = register("chests/village/village_fisher");
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_TANNERY = register("chests/village/village_tannery");
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_TEMPLE = register("chests/village/village_temple");
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_DESERT_HOUSE = register("chests/village/village_desert_house");
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_PLAINS_HOUSE = register("chests/village/village_plains_house");
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_TAIGA_HOUSE = register("chests/village/village_taiga_house");
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_SNOWY_HOUSE = register("chests/village/village_snowy_house");
   public static final ResourceLocation CHESTS_VILLAGE_VILLAGE_SAVANNA_HOUSE = register("chests/village/village_savanna_house");
   public static final ResourceLocation CHESTS_ABANDONED_MINESHAFT = register("chests/abandoned_mineshaft");
   public static final ResourceLocation CHESTS_NETHER_BRIDGE = register("chests/nether_bridge");
   public static final ResourceLocation CHESTS_STRONGHOLD_LIBRARY = register("chests/stronghold_library");
   public static final ResourceLocation CHESTS_STRONGHOLD_CROSSING = register("chests/stronghold_crossing");
   public static final ResourceLocation CHESTS_STRONGHOLD_CORRIDOR = register("chests/stronghold_corridor");
   public static final ResourceLocation CHESTS_DESERT_PYRAMID = register("chests/desert_pyramid");
   public static final ResourceLocation CHESTS_JUNGLE_TEMPLE = register("chests/jungle_temple");
   public static final ResourceLocation CHESTS_JUNGLE_TEMPLE_DISPENSER = register("chests/jungle_temple_dispenser");
   public static final ResourceLocation CHESTS_IGLOO_CHEST = register("chests/igloo_chest");
   public static final ResourceLocation CHESTS_WOODLAND_MANSION = register("chests/woodland_mansion");
   public static final ResourceLocation CHESTS_UNDERWATER_RUIN_SMALL = register("chests/underwater_ruin_small");
   public static final ResourceLocation CHESTS_UNDERWATER_RUIN_BIG = register("chests/underwater_ruin_big");
   public static final ResourceLocation CHESTS_BURIED_TREASURE = register("chests/buried_treasure");
   public static final ResourceLocation CHESTS_SHIPWRECK_MAP = register("chests/shipwreck_map");
   public static final ResourceLocation CHESTS_SHIPWRECK_SUPPLY = register("chests/shipwreck_supply");
   public static final ResourceLocation CHESTS_SHIPWRECK_TREASURE = register("chests/shipwreck_treasure");
   public static final ResourceLocation CHESTS_PILLAGER_OUTPOST = register("chests/pillager_outpost");
   public static final ResourceLocation ENTITIES_SHEEP_WHITE = register("entities/sheep/white");
   public static final ResourceLocation ENTITIES_SHEEP_ORANGE = register("entities/sheep/orange");
   public static final ResourceLocation ENTITIES_SHEEP_MAGENTA = register("entities/sheep/magenta");
   public static final ResourceLocation ENTITIES_SHEEP_LIGHT_BLUE = register("entities/sheep/light_blue");
   public static final ResourceLocation ENTITIES_SHEEP_YELLOW = register("entities/sheep/yellow");
   public static final ResourceLocation ENTITIES_SHEEP_LIME = register("entities/sheep/lime");
   public static final ResourceLocation ENTITIES_SHEEP_PINK = register("entities/sheep/pink");
   public static final ResourceLocation ENTITIES_SHEEP_GRAY = register("entities/sheep/gray");
   public static final ResourceLocation ENTITIES_SHEEP_LIGHT_GRAY = register("entities/sheep/light_gray");
   public static final ResourceLocation ENTITIES_SHEEP_CYAN = register("entities/sheep/cyan");
   public static final ResourceLocation ENTITIES_SHEEP_PURPLE = register("entities/sheep/purple");
   public static final ResourceLocation ENTITIES_SHEEP_BLUE = register("entities/sheep/blue");
   public static final ResourceLocation ENTITIES_SHEEP_BROWN = register("entities/sheep/brown");
   public static final ResourceLocation ENTITIES_SHEEP_GREEN = register("entities/sheep/green");
   public static final ResourceLocation ENTITIES_SHEEP_RED = register("entities/sheep/red");
   public static final ResourceLocation ENTITIES_SHEEP_BLACK = register("entities/sheep/black");
   public static final ResourceLocation GAMEPLAY_FISHING = register("gameplay/fishing");
   public static final ResourceLocation GAMEPLAY_FISHING_JUNK = register("gameplay/fishing/junk");
   public static final ResourceLocation GAMEPLAY_FISHING_TREASURE = register("gameplay/fishing/treasure");
   public static final ResourceLocation GAMEPLAY_FISHING_FISH = register("gameplay/fishing/fish");
   public static final ResourceLocation GAMEPLAY_CAT_MORNING_GIFT = register("gameplay/cat_morning_gift");
   public static final ResourceLocation GAMEPLAY_HERO_OF_THE_VILLAGE_ARMORER_GIFT = register("gameplay/hero_of_the_village/armorer_gift");
   public static final ResourceLocation GAMEPLAY_HERO_OF_THE_VILLAGE_BUTCHER_GIFT = register("gameplay/hero_of_the_village/butcher_gift");
   public static final ResourceLocation GAMEPLAY_HERO_OF_THE_VILLAGE_CARTOGRAPHER_GIFT = register("gameplay/hero_of_the_village/cartographer_gift");
   public static final ResourceLocation GAMEPLAY_HERO_OF_THE_VILLAGE_CLERIC_GIFT = register("gameplay/hero_of_the_village/cleric_gift");
   public static final ResourceLocation GAMEPLAY_HERO_OF_THE_VILLAGE_FARMER_GIFT = register("gameplay/hero_of_the_village/farmer_gift");
   public static final ResourceLocation GAMEPLAY_HERO_OF_THE_VILLAGE_FISHERMAN_GIFT = register("gameplay/hero_of_the_village/fisherman_gift");
   public static final ResourceLocation GAMEPLAY_HERO_OF_THE_VILLAGE_FLETCHER_GIFT = register("gameplay/hero_of_the_village/fletcher_gift");
   public static final ResourceLocation GAMEPLAY_HERO_OF_THE_VILLAGE_LEATHERWORKER_GIFT = register("gameplay/hero_of_the_village/leatherworker_gift");
   public static final ResourceLocation GAMEPLAY_HERO_OF_THE_VILLAGE_LIBRARIAN_GIFT = register("gameplay/hero_of_the_village/librarian_gift");
   public static final ResourceLocation GAMEPLAY_HERO_OF_THE_VILLAGE_MASON_GIFT = register("gameplay/hero_of_the_village/mason_gift");
   public static final ResourceLocation GAMEPLAY_HERO_OF_THE_VILLAGE_SHEPHERD_GIFT = register("gameplay/hero_of_the_village/shepherd_gift");
   public static final ResourceLocation GAMEPLAY_HERO_OF_THE_VILLAGE_TOOLSMITH_GIFT = register("gameplay/hero_of_the_village/toolsmith_gift");
   public static final ResourceLocation GAMEPLAY_HERO_OF_THE_VILLAGE_WEAPONSMITH_GIFT = register("gameplay/hero_of_the_village/weaponsmith_gift");

   private static ResourceLocation register(String id) {
      return register(new ResourceLocation(id));
   }

   private static ResourceLocation register(ResourceLocation id) {
      if (LOOT_TABLES.add(id)) {
         return id;
      } else {
         throw new IllegalArgumentException(id + " is already a registered built-in loot table");
      }
   }

   public static Set<ResourceLocation> func_215796_a() {
      return READ_ONLY_LOOT_TABLES;
   }
}