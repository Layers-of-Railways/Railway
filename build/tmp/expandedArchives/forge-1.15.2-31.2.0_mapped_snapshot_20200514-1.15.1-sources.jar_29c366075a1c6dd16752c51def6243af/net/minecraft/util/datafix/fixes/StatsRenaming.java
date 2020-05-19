package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.util.datafix.TypeReferences;
import org.apache.commons.lang3.StringUtils;

public class StatsRenaming extends DataFix {
   private static final Set<String> field_209682_a = ImmutableSet.<String>builder().add("stat.craftItem.minecraft.spawn_egg").add("stat.useItem.minecraft.spawn_egg").add("stat.breakItem.minecraft.spawn_egg").add("stat.pickup.minecraft.spawn_egg").add("stat.drop.minecraft.spawn_egg").build();
   private static final Map<String, String> field_209683_b = ImmutableMap.<String, String>builder().put("stat.leaveGame", "minecraft:leave_game").put("stat.playOneMinute", "minecraft:play_one_minute").put("stat.timeSinceDeath", "minecraft:time_since_death").put("stat.sneakTime", "minecraft:sneak_time").put("stat.walkOneCm", "minecraft:walk_one_cm").put("stat.crouchOneCm", "minecraft:crouch_one_cm").put("stat.sprintOneCm", "minecraft:sprint_one_cm").put("stat.swimOneCm", "minecraft:swim_one_cm").put("stat.fallOneCm", "minecraft:fall_one_cm").put("stat.climbOneCm", "minecraft:climb_one_cm").put("stat.flyOneCm", "minecraft:fly_one_cm").put("stat.diveOneCm", "minecraft:dive_one_cm").put("stat.minecartOneCm", "minecraft:minecart_one_cm").put("stat.boatOneCm", "minecraft:boat_one_cm").put("stat.pigOneCm", "minecraft:pig_one_cm").put("stat.horseOneCm", "minecraft:horse_one_cm").put("stat.aviateOneCm", "minecraft:aviate_one_cm").put("stat.jump", "minecraft:jump").put("stat.drop", "minecraft:drop").put("stat.damageDealt", "minecraft:damage_dealt").put("stat.damageTaken", "minecraft:damage_taken").put("stat.deaths", "minecraft:deaths").put("stat.mobKills", "minecraft:mob_kills").put("stat.animalsBred", "minecraft:animals_bred").put("stat.playerKills", "minecraft:player_kills").put("stat.fishCaught", "minecraft:fish_caught").put("stat.talkedToVillager", "minecraft:talked_to_villager").put("stat.tradedWithVillager", "minecraft:traded_with_villager").put("stat.cakeSlicesEaten", "minecraft:eat_cake_slice").put("stat.cauldronFilled", "minecraft:fill_cauldron").put("stat.cauldronUsed", "minecraft:use_cauldron").put("stat.armorCleaned", "minecraft:clean_armor").put("stat.bannerCleaned", "minecraft:clean_banner").put("stat.brewingstandInteraction", "minecraft:interact_with_brewingstand").put("stat.beaconInteraction", "minecraft:interact_with_beacon").put("stat.dropperInspected", "minecraft:inspect_dropper").put("stat.hopperInspected", "minecraft:inspect_hopper").put("stat.dispenserInspected", "minecraft:inspect_dispenser").put("stat.noteblockPlayed", "minecraft:play_noteblock").put("stat.noteblockTuned", "minecraft:tune_noteblock").put("stat.flowerPotted", "minecraft:pot_flower").put("stat.trappedChestTriggered", "minecraft:trigger_trapped_chest").put("stat.enderchestOpened", "minecraft:open_enderchest").put("stat.itemEnchanted", "minecraft:enchant_item").put("stat.recordPlayed", "minecraft:play_record").put("stat.furnaceInteraction", "minecraft:interact_with_furnace").put("stat.craftingTableInteraction", "minecraft:interact_with_crafting_table").put("stat.chestOpened", "minecraft:open_chest").put("stat.sleepInBed", "minecraft:sleep_in_bed").put("stat.shulkerBoxOpened", "minecraft:open_shulker_box").build();
   private static final Map<String, String> field_199189_b = ImmutableMap.<String, String>builder().put("stat.craftItem", "minecraft:crafted").put("stat.useItem", "minecraft:used").put("stat.breakItem", "minecraft:broken").put("stat.pickup", "minecraft:picked_up").put("stat.drop", "minecraft:dropped").build();
   private static final Map<String, String> field_209684_d = ImmutableMap.<String, String>builder().put("stat.entityKilledBy", "minecraft:killed_by").put("stat.killEntity", "minecraft:killed").build();
   private static final Map<String, String> field_209685_e = ImmutableMap.<String, String>builder().put("Bat", "minecraft:bat").put("Blaze", "minecraft:blaze").put("CaveSpider", "minecraft:cave_spider").put("Chicken", "minecraft:chicken").put("Cow", "minecraft:cow").put("Creeper", "minecraft:creeper").put("Donkey", "minecraft:donkey").put("ElderGuardian", "minecraft:elder_guardian").put("Enderman", "minecraft:enderman").put("Endermite", "minecraft:endermite").put("EvocationIllager", "minecraft:evocation_illager").put("Ghast", "minecraft:ghast").put("Guardian", "minecraft:guardian").put("Horse", "minecraft:horse").put("Husk", "minecraft:husk").put("Llama", "minecraft:llama").put("LavaSlime", "minecraft:magma_cube").put("MushroomCow", "minecraft:mooshroom").put("Mule", "minecraft:mule").put("Ozelot", "minecraft:ocelot").put("Parrot", "minecraft:parrot").put("Pig", "minecraft:pig").put("PolarBear", "minecraft:polar_bear").put("Rabbit", "minecraft:rabbit").put("Sheep", "minecraft:sheep").put("Shulker", "minecraft:shulker").put("Silverfish", "minecraft:silverfish").put("SkeletonHorse", "minecraft:skeleton_horse").put("Skeleton", "minecraft:skeleton").put("Slime", "minecraft:slime").put("Spider", "minecraft:spider").put("Squid", "minecraft:squid").put("Stray", "minecraft:stray").put("Vex", "minecraft:vex").put("Villager", "minecraft:villager").put("VindicationIllager", "minecraft:vindication_illager").put("Witch", "minecraft:witch").put("WitherSkeleton", "minecraft:wither_skeleton").put("Wolf", "minecraft:wolf").put("ZombieHorse", "minecraft:zombie_horse").put("PigZombie", "minecraft:zombie_pigman").put("ZombieVillager", "minecraft:zombie_villager").put("Zombie", "minecraft:zombie").build();

   public StatsRenaming(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getOutputSchema().getType(TypeReferences.STATS);
      return this.fixTypeEverywhereTyped("StatsCounterFix", this.getInputSchema().getType(TypeReferences.STATS), type, (p_209680_2_) -> {
         Dynamic<?> dynamic = p_209680_2_.get(DSL.remainderFinder());
         Map<Dynamic<?>, Dynamic<?>> map = Maps.newHashMap();
         Optional<? extends Map<? extends Dynamic<?>, ? extends Dynamic<?>>> optional = dynamic.getMapValues();
         if (optional.isPresent()) {
            Iterator iterator = optional.get().entrySet().iterator();

            while(true) {
               Entry<? extends Dynamic<?>, ? extends Dynamic<?>> entry;
               String s1;
               String s2;
               while(true) {
                  if (!iterator.hasNext()) {
                     return type.readTyped(dynamic.emptyMap().set("stats", dynamic.createMap(map))).getSecond().orElseThrow(() -> {
                        return new IllegalStateException("Could not parse new stats object.");
                     });
                  }

                  entry = (Entry)iterator.next();
                  if (entry.getValue().asNumber().isPresent()) {
                     String s = entry.getKey().asString("");
                     if (!field_209682_a.contains(s)) {
                        if (field_209683_b.containsKey(s)) {
                           s1 = "minecraft:custom";
                           s2 = field_209683_b.get(s);
                           break;
                        }

                        int i = StringUtils.ordinalIndexOf(s, ".", 2);
                        if (i >= 0) {
                           String s3 = s.substring(0, i);
                           if ("stat.mineBlock".equals(s3)) {
                              s1 = "minecraft:mined";
                              s2 = this.upgradeBlock(s.substring(i + 1).replace('.', ':'));
                              break;
                           }

                           if (field_199189_b.containsKey(s3)) {
                              s1 = field_199189_b.get(s3);
                              String s6 = s.substring(i + 1).replace('.', ':');
                              String s5 = this.upgradeItem(s6);
                              s2 = s5 == null ? s6 : s5;
                              break;
                           }

                           if (field_209684_d.containsKey(s3)) {
                              s1 = field_209684_d.get(s3);
                              String s4 = s.substring(i + 1).replace('.', ':');
                              s2 = field_209685_e.getOrDefault(s4, s4);
                              break;
                           }
                        }
                     }
                  }
               }

               Dynamic<?> dynamic1 = dynamic.createString(s1);
               Dynamic<?> dynamic2 = map.computeIfAbsent(dynamic1, (p_209679_1_) -> {
                  return dynamic.emptyMap();
               });
               map.put(dynamic1, dynamic2.set(s2, entry.getValue()));
            }
         } else {
            return type.readTyped(dynamic.emptyMap().set("stats", dynamic.createMap(map))).getSecond().orElseThrow(() -> {
               return new IllegalStateException("Could not parse new stats object.");
            });
         }
      });
   }

   @Nullable
   protected String upgradeItem(String p_209681_1_) {
      return ItemStackDataFlattening.updateItem(p_209681_1_, 0);
   }

   protected String upgradeBlock(String p_206287_1_) {
      return BlockStateFlatteningMap.updateName(p_206287_1_);
   }
}