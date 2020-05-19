package net.minecraft.stats;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class Stats {
   public static final StatType<Block> BLOCK_MINED = registerType("mined", Registry.BLOCK);
   public static final StatType<Item> ITEM_CRAFTED = registerType("crafted", Registry.ITEM);
   public static final StatType<Item> ITEM_USED = registerType("used", Registry.ITEM);
   public static final StatType<Item> ITEM_BROKEN = registerType("broken", Registry.ITEM);
   public static final StatType<Item> ITEM_PICKED_UP = registerType("picked_up", Registry.ITEM);
   public static final StatType<Item> ITEM_DROPPED = registerType("dropped", Registry.ITEM);
   public static final StatType<EntityType<?>> ENTITY_KILLED = registerType("killed", Registry.ENTITY_TYPE);
   public static final StatType<EntityType<?>> ENTITY_KILLED_BY = registerType("killed_by", Registry.ENTITY_TYPE);
   public static final StatType<ResourceLocation> CUSTOM = registerType("custom", Registry.CUSTOM_STAT);
   public static final ResourceLocation LEAVE_GAME = registerCustom("leave_game", IStatFormatter.DEFAULT);
   public static final ResourceLocation PLAY_ONE_MINUTE = registerCustom("play_one_minute", IStatFormatter.TIME);
   public static final ResourceLocation TIME_SINCE_DEATH = registerCustom("time_since_death", IStatFormatter.TIME);
   public static final ResourceLocation TIME_SINCE_REST = registerCustom("time_since_rest", IStatFormatter.TIME);
   public static final ResourceLocation SNEAK_TIME = registerCustom("sneak_time", IStatFormatter.TIME);
   public static final ResourceLocation WALK_ONE_CM = registerCustom("walk_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation CROUCH_ONE_CM = registerCustom("crouch_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation SPRINT_ONE_CM = registerCustom("sprint_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation WALK_ON_WATER_ONE_CM = registerCustom("walk_on_water_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation FALL_ONE_CM = registerCustom("fall_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation CLIMB_ONE_CM = registerCustom("climb_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation FLY_ONE_CM = registerCustom("fly_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation WALK_UNDER_WATER_ONE_CM = registerCustom("walk_under_water_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation MINECART_ONE_CM = registerCustom("minecart_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation BOAT_ONE_CM = registerCustom("boat_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation PIG_ONE_CM = registerCustom("pig_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation HORSE_ONE_CM = registerCustom("horse_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation AVIATE_ONE_CM = registerCustom("aviate_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation SWIM_ONE_CM = registerCustom("swim_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation JUMP = registerCustom("jump", IStatFormatter.DEFAULT);
   public static final ResourceLocation DROP = registerCustom("drop", IStatFormatter.DEFAULT);
   public static final ResourceLocation DAMAGE_DEALT = registerCustom("damage_dealt", IStatFormatter.DIVIDE_BY_TEN);
   public static final ResourceLocation DAMAGE_DEALT_ABSORBED = registerCustom("damage_dealt_absorbed", IStatFormatter.DIVIDE_BY_TEN);
   public static final ResourceLocation DAMAGE_DEALT_RESISTED = registerCustom("damage_dealt_resisted", IStatFormatter.DIVIDE_BY_TEN);
   public static final ResourceLocation DAMAGE_TAKEN = registerCustom("damage_taken", IStatFormatter.DIVIDE_BY_TEN);
   public static final ResourceLocation DAMAGE_BLOCKED_BY_SHIELD = registerCustom("damage_blocked_by_shield", IStatFormatter.DIVIDE_BY_TEN);
   public static final ResourceLocation DAMAGE_ABSORBED = registerCustom("damage_absorbed", IStatFormatter.DIVIDE_BY_TEN);
   public static final ResourceLocation DAMAGE_RESISTED = registerCustom("damage_resisted", IStatFormatter.DIVIDE_BY_TEN);
   public static final ResourceLocation DEATHS = registerCustom("deaths", IStatFormatter.DEFAULT);
   public static final ResourceLocation MOB_KILLS = registerCustom("mob_kills", IStatFormatter.DEFAULT);
   public static final ResourceLocation ANIMALS_BRED = registerCustom("animals_bred", IStatFormatter.DEFAULT);
   public static final ResourceLocation PLAYER_KILLS = registerCustom("player_kills", IStatFormatter.DEFAULT);
   public static final ResourceLocation FISH_CAUGHT = registerCustom("fish_caught", IStatFormatter.DEFAULT);
   public static final ResourceLocation TALKED_TO_VILLAGER = registerCustom("talked_to_villager", IStatFormatter.DEFAULT);
   public static final ResourceLocation TRADED_WITH_VILLAGER = registerCustom("traded_with_villager", IStatFormatter.DEFAULT);
   public static final ResourceLocation EAT_CAKE_SLICE = registerCustom("eat_cake_slice", IStatFormatter.DEFAULT);
   public static final ResourceLocation FILL_CAULDRON = registerCustom("fill_cauldron", IStatFormatter.DEFAULT);
   public static final ResourceLocation USE_CAULDRON = registerCustom("use_cauldron", IStatFormatter.DEFAULT);
   public static final ResourceLocation CLEAN_ARMOR = registerCustom("clean_armor", IStatFormatter.DEFAULT);
   public static final ResourceLocation CLEAN_BANNER = registerCustom("clean_banner", IStatFormatter.DEFAULT);
   public static final ResourceLocation CLEAN_SHULKER_BOX = registerCustom("clean_shulker_box", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_BREWINGSTAND = registerCustom("interact_with_brewingstand", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_BEACON = registerCustom("interact_with_beacon", IStatFormatter.DEFAULT);
   public static final ResourceLocation INSPECT_DROPPER = registerCustom("inspect_dropper", IStatFormatter.DEFAULT);
   public static final ResourceLocation INSPECT_HOPPER = registerCustom("inspect_hopper", IStatFormatter.DEFAULT);
   public static final ResourceLocation INSPECT_DISPENSER = registerCustom("inspect_dispenser", IStatFormatter.DEFAULT);
   public static final ResourceLocation PLAY_NOTEBLOCK = registerCustom("play_noteblock", IStatFormatter.DEFAULT);
   public static final ResourceLocation TUNE_NOTEBLOCK = registerCustom("tune_noteblock", IStatFormatter.DEFAULT);
   public static final ResourceLocation POT_FLOWER = registerCustom("pot_flower", IStatFormatter.DEFAULT);
   public static final ResourceLocation TRIGGER_TRAPPED_CHEST = registerCustom("trigger_trapped_chest", IStatFormatter.DEFAULT);
   public static final ResourceLocation OPEN_ENDERCHEST = registerCustom("open_enderchest", IStatFormatter.DEFAULT);
   public static final ResourceLocation ENCHANT_ITEM = registerCustom("enchant_item", IStatFormatter.DEFAULT);
   public static final ResourceLocation PLAY_RECORD = registerCustom("play_record", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_FURNACE = registerCustom("interact_with_furnace", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_CRAFTING_TABLE = registerCustom("interact_with_crafting_table", IStatFormatter.DEFAULT);
   public static final ResourceLocation OPEN_CHEST = registerCustom("open_chest", IStatFormatter.DEFAULT);
   public static final ResourceLocation SLEEP_IN_BED = registerCustom("sleep_in_bed", IStatFormatter.DEFAULT);
   public static final ResourceLocation OPEN_SHULKER_BOX = registerCustom("open_shulker_box", IStatFormatter.DEFAULT);
   public static final ResourceLocation OPEN_BARREL = registerCustom("open_barrel", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_BLAST_FURNACE = registerCustom("interact_with_blast_furnace", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_SMOKER = registerCustom("interact_with_smoker", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_LECTERN = registerCustom("interact_with_lectern", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_CAMPFIRE = registerCustom("interact_with_campfire", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_CARTOGRAPHY_TABLE = registerCustom("interact_with_cartography_table", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_LOOM = registerCustom("interact_with_loom", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_STONECUTTER = registerCustom("interact_with_stonecutter", IStatFormatter.DEFAULT);
   public static final ResourceLocation BELL_RING = registerCustom("bell_ring", IStatFormatter.DEFAULT);
   public static final ResourceLocation RAID_TRIGGER = registerCustom("raid_trigger", IStatFormatter.DEFAULT);
   public static final ResourceLocation RAID_WIN = registerCustom("raid_win", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_ANVIL = registerCustom("interact_with_anvil", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_GRINDSTONE = registerCustom("interact_with_grindstone", IStatFormatter.DEFAULT);

   private static ResourceLocation registerCustom(String key, IStatFormatter formatter) {
      ResourceLocation resourcelocation = new ResourceLocation(key);
      Registry.register(Registry.CUSTOM_STAT, key, resourcelocation);
      CUSTOM.get(resourcelocation, formatter);
      return resourcelocation;
   }

   private static <T> StatType<T> registerType(String key, Registry<T> registry) {
      return Registry.register(Registry.STATS, key, new StatType<>(registry));
   }
}