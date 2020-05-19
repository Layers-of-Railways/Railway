package net.minecraft.data.loot;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.world.storage.loot.ConstantRange;
import net.minecraft.world.storage.loot.ItemLootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraft.world.storage.loot.functions.SetNBT;

public class GiftLootTables implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
   public void accept(BiConsumer<ResourceLocation, LootTable.Builder> p_accept_1_) {
      p_accept_1_.accept(LootTables.GAMEPLAY_CAT_MORNING_GIFT, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.RABBIT_HIDE).weight(10)).addEntry(ItemLootEntry.builder(Items.RABBIT_FOOT).weight(10)).addEntry(ItemLootEntry.builder(Items.CHICKEN).weight(10)).addEntry(ItemLootEntry.builder(Items.FEATHER).weight(10)).addEntry(ItemLootEntry.builder(Items.ROTTEN_FLESH).weight(10)).addEntry(ItemLootEntry.builder(Items.STRING).weight(10)).addEntry(ItemLootEntry.builder(Items.PHANTOM_MEMBRANE).weight(2))));
      p_accept_1_.accept(LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_ARMORER_GIFT, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.CHAINMAIL_HELMET)).addEntry(ItemLootEntry.builder(Items.CHAINMAIL_CHESTPLATE)).addEntry(ItemLootEntry.builder(Items.CHAINMAIL_LEGGINGS)).addEntry(ItemLootEntry.builder(Items.CHAINMAIL_BOOTS))));
      p_accept_1_.accept(LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_BUTCHER_GIFT, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.COOKED_RABBIT)).addEntry(ItemLootEntry.builder(Items.COOKED_CHICKEN)).addEntry(ItemLootEntry.builder(Items.COOKED_PORKCHOP)).addEntry(ItemLootEntry.builder(Items.COOKED_BEEF)).addEntry(ItemLootEntry.builder(Items.COOKED_MUTTON))));
      p_accept_1_.accept(LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_CARTOGRAPHER_GIFT, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.MAP)).addEntry(ItemLootEntry.builder(Items.PAPER))));
      p_accept_1_.accept(LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_CLERIC_GIFT, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.REDSTONE)).addEntry(ItemLootEntry.builder(Items.LAPIS_LAZULI))));
      p_accept_1_.accept(LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_FARMER_GIFT, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.BREAD)).addEntry(ItemLootEntry.builder(Items.PUMPKIN_PIE)).addEntry(ItemLootEntry.builder(Items.COOKIE))));
      p_accept_1_.accept(LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_FISHERMAN_GIFT, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.COD)).addEntry(ItemLootEntry.builder(Items.SALMON))));
      p_accept_1_.accept(LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_FLETCHER_GIFT, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.ARROW).weight(26)).addEntry(ItemLootEntry.builder(Items.TIPPED_ARROW).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(SetNBT.builder(Util.make(new CompoundNBT(), (p_218596_0_) -> {
         p_218596_0_.putString("Potion", "minecraft:swiftness");
      })))).addEntry(ItemLootEntry.builder(Items.TIPPED_ARROW).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(SetNBT.builder(Util.make(new CompoundNBT(), (p_218597_0_) -> {
         p_218597_0_.putString("Potion", "minecraft:slowness");
      })))).addEntry(ItemLootEntry.builder(Items.TIPPED_ARROW).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(SetNBT.builder(Util.make(new CompoundNBT(), (p_218608_0_) -> {
         p_218608_0_.putString("Potion", "minecraft:strength");
      })))).addEntry(ItemLootEntry.builder(Items.TIPPED_ARROW).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(SetNBT.builder(Util.make(new CompoundNBT(), (p_218606_0_) -> {
         p_218606_0_.putString("Potion", "minecraft:healing");
      })))).addEntry(ItemLootEntry.builder(Items.TIPPED_ARROW).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(SetNBT.builder(Util.make(new CompoundNBT(), (p_218603_0_) -> {
         p_218603_0_.putString("Potion", "minecraft:harming");
      })))).addEntry(ItemLootEntry.builder(Items.TIPPED_ARROW).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(SetNBT.builder(Util.make(new CompoundNBT(), (p_218607_0_) -> {
         p_218607_0_.putString("Potion", "minecraft:leaping");
      })))).addEntry(ItemLootEntry.builder(Items.TIPPED_ARROW).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(SetNBT.builder(Util.make(new CompoundNBT(), (p_218601_0_) -> {
         p_218601_0_.putString("Potion", "minecraft:regeneration");
      })))).addEntry(ItemLootEntry.builder(Items.TIPPED_ARROW).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(SetNBT.builder(Util.make(new CompoundNBT(), (p_218600_0_) -> {
         p_218600_0_.putString("Potion", "minecraft:fire_resistance");
      })))).addEntry(ItemLootEntry.builder(Items.TIPPED_ARROW).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(SetNBT.builder(Util.make(new CompoundNBT(), (p_218599_0_) -> {
         p_218599_0_.putString("Potion", "minecraft:water_breathing");
      })))).addEntry(ItemLootEntry.builder(Items.TIPPED_ARROW).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(SetNBT.builder(Util.make(new CompoundNBT(), (p_218598_0_) -> {
         p_218598_0_.putString("Potion", "minecraft:invisibility");
      })))).addEntry(ItemLootEntry.builder(Items.TIPPED_ARROW).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(SetNBT.builder(Util.make(new CompoundNBT(), (p_218605_0_) -> {
         p_218605_0_.putString("Potion", "minecraft:night_vision");
      })))).addEntry(ItemLootEntry.builder(Items.TIPPED_ARROW).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(SetNBT.builder(Util.make(new CompoundNBT(), (p_218602_0_) -> {
         p_218602_0_.putString("Potion", "minecraft:weakness");
      })))).addEntry(ItemLootEntry.builder(Items.TIPPED_ARROW).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(SetNBT.builder(Util.make(new CompoundNBT(), (p_218604_0_) -> {
         p_218604_0_.putString("Potion", "minecraft:poison");
      }))))));
      p_accept_1_.accept(LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_LEATHERWORKER_GIFT, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.LEATHER))));
      p_accept_1_.accept(LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_LIBRARIAN_GIFT, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.BOOK))));
      p_accept_1_.accept(LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_MASON_GIFT, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.CLAY))));
      p_accept_1_.accept(LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_SHEPHERD_GIFT, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.WHITE_WOOL)).addEntry(ItemLootEntry.builder(Items.ORANGE_WOOL)).addEntry(ItemLootEntry.builder(Items.MAGENTA_WOOL)).addEntry(ItemLootEntry.builder(Items.LIGHT_BLUE_WOOL)).addEntry(ItemLootEntry.builder(Items.YELLOW_WOOL)).addEntry(ItemLootEntry.builder(Items.LIME_WOOL)).addEntry(ItemLootEntry.builder(Items.PINK_WOOL)).addEntry(ItemLootEntry.builder(Items.GRAY_WOOL)).addEntry(ItemLootEntry.builder(Items.LIGHT_GRAY_WOOL)).addEntry(ItemLootEntry.builder(Items.CYAN_WOOL)).addEntry(ItemLootEntry.builder(Items.PURPLE_WOOL)).addEntry(ItemLootEntry.builder(Items.BLUE_WOOL)).addEntry(ItemLootEntry.builder(Items.BROWN_WOOL)).addEntry(ItemLootEntry.builder(Items.GREEN_WOOL)).addEntry(ItemLootEntry.builder(Items.RED_WOOL)).addEntry(ItemLootEntry.builder(Items.BLACK_WOOL))));
      p_accept_1_.accept(LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_TOOLSMITH_GIFT, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.STONE_PICKAXE)).addEntry(ItemLootEntry.builder(Items.STONE_AXE)).addEntry(ItemLootEntry.builder(Items.STONE_HOE)).addEntry(ItemLootEntry.builder(Items.STONE_SHOVEL))));
      p_accept_1_.accept(LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_WEAPONSMITH_GIFT, LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.STONE_AXE)).addEntry(ItemLootEntry.builder(Items.GOLDEN_AXE)).addEntry(ItemLootEntry.builder(Items.IRON_AXE))));
   }
}