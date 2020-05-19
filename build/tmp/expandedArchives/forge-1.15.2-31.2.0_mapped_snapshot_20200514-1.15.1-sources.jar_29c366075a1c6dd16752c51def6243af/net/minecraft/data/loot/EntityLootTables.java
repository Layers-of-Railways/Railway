package net.minecraft.data.loot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.advancements.criterion.DamageSourcePredicate;
import net.minecraft.advancements.criterion.EntityFlagsPredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.storage.loot.ConstantRange;
import net.minecraft.world.storage.loot.EmptyLootEntry;
import net.minecraft.world.storage.loot.ItemLootEntry;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.TableLootEntry;
import net.minecraft.world.storage.loot.TagLootEntry;
import net.minecraft.world.storage.loot.conditions.DamageSourceProperties;
import net.minecraft.world.storage.loot.conditions.EntityHasProperty;
import net.minecraft.world.storage.loot.conditions.KilledByPlayer;
import net.minecraft.world.storage.loot.conditions.RandomChance;
import net.minecraft.world.storage.loot.conditions.RandomChanceWithLooting;
import net.minecraft.world.storage.loot.functions.LootingEnchantBonus;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraft.world.storage.loot.functions.SetNBT;
import net.minecraft.world.storage.loot.functions.Smelt;

public class EntityLootTables implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
   protected static final EntityPredicate.Builder ON_FIRE = EntityPredicate.Builder.create().flags(EntityFlagsPredicate.Builder.create().onFire(true).build());
   private static final Set<EntityType<?>> NO_DROPS = ImmutableSet.of(EntityType.PLAYER, EntityType.ARMOR_STAND, EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.VILLAGER);
   private final Map<ResourceLocation, LootTable.Builder> lootTables = Maps.newHashMap();

   private static LootTable.Builder sheepLootTableBuilderWithDrop(IItemProvider p_218583_0_) {
      return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(p_218583_0_))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(TableLootEntry.builder(EntityType.SHEEP.getLootTable())));
   }

   protected void addTables() {
      this.registerLootTable(EntityType.ARMOR_STAND, LootTable.builder());
      this.registerLootTable(EntityType.BAT, LootTable.builder());
      this.registerLootTable(EntityType.BEE, LootTable.builder());
      this.registerLootTable(EntityType.BLAZE, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.BLAZE_ROD).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))).acceptCondition(KilledByPlayer.builder())));
      this.registerLootTable(EntityType.CAT, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.STRING).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))))));
      this.registerLootTable(EntityType.CAVE_SPIDER, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.STRING).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.SPIDER_EYE).acceptFunction(SetCount.builder(RandomValueRange.of(-1.0F, 1.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))).acceptCondition(KilledByPlayer.builder())));
      this.registerLootTable(EntityType.CHICKEN, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.FEATHER).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.CHICKEN).acceptFunction(Smelt.func_215953_b().acceptCondition(EntityHasProperty.builder(LootContext.EntityTarget.THIS, ON_FIRE))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))));
      this.registerLootTable(EntityType.COD, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.COD).acceptFunction(Smelt.func_215953_b().acceptCondition(EntityHasProperty.builder(LootContext.EntityTarget.THIS, ON_FIRE))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.BONE_MEAL)).acceptCondition(RandomChance.builder(0.05F))));
      this.registerLootTable(EntityType.COW, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.LEATHER).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.BEEF).acceptFunction(SetCount.builder(RandomValueRange.of(1.0F, 3.0F))).acceptFunction(Smelt.func_215953_b().acceptCondition(EntityHasProperty.builder(LootContext.EntityTarget.THIS, ON_FIRE))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))));
      this.registerLootTable(EntityType.CREEPER, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.GUNPOWDER).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))).addLootPool(LootPool.builder().addEntry(TagLootEntry.func_216176_b(ItemTags.MUSIC_DISCS)).acceptCondition(EntityHasProperty.builder(LootContext.EntityTarget.KILLER, EntityPredicate.Builder.create().type(EntityTypeTags.SKELETONS)))));
      this.registerLootTable(EntityType.DOLPHIN, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.COD).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(Smelt.func_215953_b().acceptCondition(EntityHasProperty.builder(LootContext.EntityTarget.THIS, ON_FIRE))))));
      this.registerLootTable(EntityType.DONKEY, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.LEATHER).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))));
      this.registerLootTable(EntityType.DROWNED, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.ROTTEN_FLESH).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.GOLD_INGOT)).acceptCondition(KilledByPlayer.builder()).acceptCondition(RandomChanceWithLooting.builder(0.05F, 0.01F))));
      this.registerLootTable(EntityType.ELDER_GUARDIAN, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.PRISMARINE_SHARD).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.COD).weight(3).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(Smelt.func_215953_b().acceptCondition(EntityHasProperty.builder(LootContext.EntityTarget.THIS, ON_FIRE)))).addEntry(ItemLootEntry.builder(Items.PRISMARINE_CRYSTALS).weight(2).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))).addEntry(EmptyLootEntry.func_216167_a())).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Blocks.WET_SPONGE)).acceptCondition(KilledByPlayer.builder())).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(TableLootEntry.builder(LootTables.GAMEPLAY_FISHING_FISH)).acceptCondition(KilledByPlayer.builder()).acceptCondition(RandomChanceWithLooting.builder(0.025F, 0.01F))));
      this.registerLootTable(EntityType.ENDER_DRAGON, LootTable.builder());
      this.registerLootTable(EntityType.ENDERMAN, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.ENDER_PEARL).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))));
      this.registerLootTable(EntityType.ENDERMITE, LootTable.builder());
      this.registerLootTable(EntityType.EVOKER, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.TOTEM_OF_UNDYING))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.EMERALD).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))).acceptCondition(KilledByPlayer.builder())));
      this.registerLootTable(EntityType.FOX, LootTable.builder());
      this.registerLootTable(EntityType.GHAST, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.GHAST_TEAR).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.GUNPOWDER).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))));
      this.registerLootTable(EntityType.GIANT, LootTable.builder());
      this.registerLootTable(EntityType.GUARDIAN, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.PRISMARINE_SHARD).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.COD).weight(2).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(Smelt.func_215953_b().acceptCondition(EntityHasProperty.builder(LootContext.EntityTarget.THIS, ON_FIRE)))).addEntry(ItemLootEntry.builder(Items.PRISMARINE_CRYSTALS).weight(2).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))).addEntry(EmptyLootEntry.func_216167_a())).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(TableLootEntry.builder(LootTables.GAMEPLAY_FISHING_FISH)).acceptCondition(KilledByPlayer.builder()).acceptCondition(RandomChanceWithLooting.builder(0.025F, 0.01F))));
      this.registerLootTable(EntityType.HORSE, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.LEATHER).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))));
      this.registerLootTable(EntityType.HUSK, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.ROTTEN_FLESH).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.IRON_INGOT)).addEntry(ItemLootEntry.builder(Items.CARROT)).addEntry(ItemLootEntry.builder(Items.POTATO)).acceptCondition(KilledByPlayer.builder()).acceptCondition(RandomChanceWithLooting.builder(0.025F, 0.01F))));
      this.registerLootTable(EntityType.RAVAGER, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.SADDLE).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
      this.registerLootTable(EntityType.ILLUSIONER, LootTable.builder());
      this.registerLootTable(EntityType.IRON_GOLEM, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Blocks.POPPY).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.IRON_INGOT).acceptFunction(SetCount.builder(RandomValueRange.of(3.0F, 5.0F))))));
      this.registerLootTable(EntityType.LLAMA, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.LEATHER).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))));
      this.registerLootTable(EntityType.MAGMA_CUBE, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.MAGMA_CREAM).acceptFunction(SetCount.builder(RandomValueRange.of(-2.0F, 1.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))));
      this.registerLootTable(EntityType.MULE, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.LEATHER).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))));
      this.registerLootTable(EntityType.MOOSHROOM, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.LEATHER).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.BEEF).acceptFunction(SetCount.builder(RandomValueRange.of(1.0F, 3.0F))).acceptFunction(Smelt.func_215953_b().acceptCondition(EntityHasProperty.builder(LootContext.EntityTarget.THIS, ON_FIRE))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))));
      this.registerLootTable(EntityType.OCELOT, LootTable.builder());
      this.registerLootTable(EntityType.PANDA, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Blocks.BAMBOO).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
      this.registerLootTable(EntityType.PARROT, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.FEATHER).acceptFunction(SetCount.builder(RandomValueRange.of(1.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))));
      this.registerLootTable(EntityType.PHANTOM, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.PHANTOM_MEMBRANE).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))).acceptCondition(KilledByPlayer.builder())));
      this.registerLootTable(EntityType.PIG, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.PORKCHOP).acceptFunction(SetCount.builder(RandomValueRange.of(1.0F, 3.0F))).acceptFunction(Smelt.func_215953_b().acceptCondition(EntityHasProperty.builder(LootContext.EntityTarget.THIS, ON_FIRE))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))));
      this.registerLootTable(EntityType.PILLAGER, LootTable.builder());
      this.registerLootTable(EntityType.PLAYER, LootTable.builder());
      this.registerLootTable(EntityType.POLAR_BEAR, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.COD).weight(3).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))).addEntry(ItemLootEntry.builder(Items.SALMON).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))));
      this.registerLootTable(EntityType.PUFFERFISH, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.PUFFERFISH).acceptFunction(SetCount.builder(ConstantRange.of(1))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.BONE_MEAL)).acceptCondition(RandomChance.builder(0.05F))));
      this.registerLootTable(EntityType.RABBIT, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.RABBIT_HIDE).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.RABBIT).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(Smelt.func_215953_b().acceptCondition(EntityHasProperty.builder(LootContext.EntityTarget.THIS, ON_FIRE))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.RABBIT_FOOT)).acceptCondition(KilledByPlayer.builder()).acceptCondition(RandomChanceWithLooting.builder(0.1F, 0.03F))));
      this.registerLootTable(EntityType.SALMON, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.SALMON).acceptFunction(Smelt.func_215953_b().acceptCondition(EntityHasProperty.builder(LootContext.EntityTarget.THIS, ON_FIRE))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.BONE_MEAL)).acceptCondition(RandomChance.builder(0.05F))));
      this.registerLootTable(EntityType.SHEEP, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.MUTTON).acceptFunction(SetCount.builder(RandomValueRange.of(1.0F, 2.0F))).acceptFunction(Smelt.func_215953_b().acceptCondition(EntityHasProperty.builder(LootContext.EntityTarget.THIS, ON_FIRE))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))));
      this.registerLootTable(LootTables.ENTITIES_SHEEP_BLACK, sheepLootTableBuilderWithDrop(Blocks.BLACK_WOOL));
      this.registerLootTable(LootTables.ENTITIES_SHEEP_BLUE, sheepLootTableBuilderWithDrop(Blocks.BLUE_WOOL));
      this.registerLootTable(LootTables.ENTITIES_SHEEP_BROWN, sheepLootTableBuilderWithDrop(Blocks.BROWN_WOOL));
      this.registerLootTable(LootTables.ENTITIES_SHEEP_CYAN, sheepLootTableBuilderWithDrop(Blocks.CYAN_WOOL));
      this.registerLootTable(LootTables.ENTITIES_SHEEP_GRAY, sheepLootTableBuilderWithDrop(Blocks.GRAY_WOOL));
      this.registerLootTable(LootTables.ENTITIES_SHEEP_GREEN, sheepLootTableBuilderWithDrop(Blocks.GREEN_WOOL));
      this.registerLootTable(LootTables.ENTITIES_SHEEP_LIGHT_BLUE, sheepLootTableBuilderWithDrop(Blocks.LIGHT_BLUE_WOOL));
      this.registerLootTable(LootTables.ENTITIES_SHEEP_LIGHT_GRAY, sheepLootTableBuilderWithDrop(Blocks.LIGHT_GRAY_WOOL));
      this.registerLootTable(LootTables.ENTITIES_SHEEP_LIME, sheepLootTableBuilderWithDrop(Blocks.LIME_WOOL));
      this.registerLootTable(LootTables.ENTITIES_SHEEP_MAGENTA, sheepLootTableBuilderWithDrop(Blocks.MAGENTA_WOOL));
      this.registerLootTable(LootTables.ENTITIES_SHEEP_ORANGE, sheepLootTableBuilderWithDrop(Blocks.ORANGE_WOOL));
      this.registerLootTable(LootTables.ENTITIES_SHEEP_PINK, sheepLootTableBuilderWithDrop(Blocks.PINK_WOOL));
      this.registerLootTable(LootTables.ENTITIES_SHEEP_PURPLE, sheepLootTableBuilderWithDrop(Blocks.PURPLE_WOOL));
      this.registerLootTable(LootTables.ENTITIES_SHEEP_RED, sheepLootTableBuilderWithDrop(Blocks.RED_WOOL));
      this.registerLootTable(LootTables.ENTITIES_SHEEP_WHITE, sheepLootTableBuilderWithDrop(Blocks.WHITE_WOOL));
      this.registerLootTable(LootTables.ENTITIES_SHEEP_YELLOW, sheepLootTableBuilderWithDrop(Blocks.YELLOW_WOOL));
      this.registerLootTable(EntityType.SHULKER, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.SHULKER_SHELL)).acceptCondition(RandomChanceWithLooting.builder(0.5F, 0.0625F))));
      this.registerLootTable(EntityType.SILVERFISH, LootTable.builder());
      this.registerLootTable(EntityType.SKELETON, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.ARROW).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.BONE).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))));
      this.registerLootTable(EntityType.SKELETON_HORSE, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.BONE).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))));
      this.registerLootTable(EntityType.SLIME, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.SLIME_BALL).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))));
      this.registerLootTable(EntityType.SNOW_GOLEM, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.SNOWBALL).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 15.0F))))));
      this.registerLootTable(EntityType.SPIDER, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.STRING).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.SPIDER_EYE).acceptFunction(SetCount.builder(RandomValueRange.of(-1.0F, 1.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))).acceptCondition(KilledByPlayer.builder())));
      this.registerLootTable(EntityType.SQUID, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.INK_SAC).acceptFunction(SetCount.builder(RandomValueRange.of(1.0F, 3.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))));
      this.registerLootTable(EntityType.STRAY, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.ARROW).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.BONE).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.TIPPED_ARROW).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)).func_216072_a(1)).acceptFunction(SetNBT.builder(Util.make(new CompoundNBT(), (p_218584_0_) -> {
         p_218584_0_.putString("Potion", "minecraft:slowness");
      })))).acceptCondition(KilledByPlayer.builder())));
      this.registerLootTable(EntityType.TRADER_LLAMA, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.LEATHER).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))));
      this.registerLootTable(EntityType.TROPICAL_FISH, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.TROPICAL_FISH).acceptFunction(SetCount.builder(ConstantRange.of(1))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.BONE_MEAL)).acceptCondition(RandomChance.builder(0.05F))));
      this.registerLootTable(EntityType.TURTLE, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Blocks.SEAGRASS).weight(3).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.BOWL)).acceptCondition(DamageSourceProperties.builder(DamageSourcePredicate.Builder.damageType().func_217950_h(true)))));
      this.registerLootTable(EntityType.VEX, LootTable.builder());
      this.registerLootTable(EntityType.VILLAGER, LootTable.builder());
      this.registerLootTable(EntityType.WANDERING_TRADER, LootTable.builder());
      this.registerLootTable(EntityType.VINDICATOR, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.EMERALD).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))).acceptCondition(KilledByPlayer.builder())));
      this.registerLootTable(EntityType.WITCH, LootTable.builder().addLootPool(LootPool.builder().rolls(RandomValueRange.of(1.0F, 3.0F)).addEntry(ItemLootEntry.builder(Items.GLOWSTONE_DUST).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))).addEntry(ItemLootEntry.builder(Items.SUGAR).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))).addEntry(ItemLootEntry.builder(Items.REDSTONE).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))).addEntry(ItemLootEntry.builder(Items.SPIDER_EYE).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))).addEntry(ItemLootEntry.builder(Items.GLASS_BOTTLE).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))).addEntry(ItemLootEntry.builder(Items.GUNPOWDER).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))).addEntry(ItemLootEntry.builder(Items.STICK).weight(2).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))));
      this.registerLootTable(EntityType.WITHER, LootTable.builder());
      this.registerLootTable(EntityType.WITHER_SKELETON, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.COAL).acceptFunction(SetCount.builder(RandomValueRange.of(-1.0F, 1.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.BONE).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Blocks.WITHER_SKELETON_SKULL)).acceptCondition(KilledByPlayer.builder()).acceptCondition(RandomChanceWithLooting.builder(0.025F, 0.01F))));
      this.registerLootTable(EntityType.WOLF, LootTable.builder());
      this.registerLootTable(EntityType.ZOMBIE, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.ROTTEN_FLESH).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.IRON_INGOT)).addEntry(ItemLootEntry.builder(Items.CARROT)).addEntry(ItemLootEntry.builder(Items.POTATO)).acceptCondition(KilledByPlayer.builder()).acceptCondition(RandomChanceWithLooting.builder(0.025F, 0.01F))));
      this.registerLootTable(EntityType.ZOMBIE_HORSE, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.ROTTEN_FLESH).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))));
      this.registerLootTable(EntityType.ZOMBIE_PIGMAN, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.ROTTEN_FLESH).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.GOLD_NUGGET).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.GOLD_INGOT)).acceptCondition(KilledByPlayer.builder()).acceptCondition(RandomChanceWithLooting.builder(0.025F, 0.01F))));
      this.registerLootTable(EntityType.ZOMBIE_VILLAGER, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.ROTTEN_FLESH).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.IRON_INGOT)).addEntry(ItemLootEntry.builder(Items.CARROT)).addEntry(ItemLootEntry.builder(Items.POTATO)).acceptCondition(KilledByPlayer.builder()).acceptCondition(RandomChanceWithLooting.builder(0.025F, 0.01F))));
   }

   public void accept(BiConsumer<ResourceLocation, LootTable.Builder> p_accept_1_) {
      this.addTables();
      Set<ResourceLocation> set = Sets.newHashSet();
      Iterator iterator = getKnownEntities().iterator();

      EntityType<?> entitytype;
      ResourceLocation resourcelocation;
      while(true) {
         if (!iterator.hasNext()) {
            this.lootTables.forEach(p_accept_1_::accept);
            return;
         }

         entitytype = (EntityType)iterator.next();
         resourcelocation = entitytype.getLootTable();
         if (isNonLiving(entitytype)) {
            if (resourcelocation != LootTables.EMPTY && this.lootTables.remove(resourcelocation) != null) {
               break;
            }
         } else if (resourcelocation != LootTables.EMPTY && set.add(resourcelocation)) {
            LootTable.Builder loottable$builder = this.lootTables.remove(resourcelocation);
            if (loottable$builder == null) {
               throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", resourcelocation, Registry.ENTITY_TYPE.getKey(entitytype)));
            }

            p_accept_1_.accept(resourcelocation, loottable$builder);
         }
      }

      throw new IllegalStateException(String.format("Weird loottable '%s' for '%s', not a LivingEntity so should not have loot", resourcelocation, Registry.ENTITY_TYPE.getKey(entitytype)));
   }

   protected Iterable<EntityType<?>> getKnownEntities() {
      return Registry.ENTITY_TYPE;
   }

   protected boolean isNonLiving(EntityType<?> entitytype) {
      return !NO_DROPS.contains(entitytype) && entitytype.getClassification() == EntityClassification.MISC; 
   }

   protected void registerLootTable(EntityType<?> type, LootTable.Builder table) {
      this.registerLootTable(type.getLootTable(), table);
   }

   protected void registerLootTable(ResourceLocation id, LootTable.Builder table) {
      this.lootTables.put(id, table);
   }
}