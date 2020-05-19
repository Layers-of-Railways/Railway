package net.minecraft.data.loot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BeetrootBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarrotBlock;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.PotatoBlock;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.block.TNTBlock;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.IProperty;
import net.minecraft.state.properties.BedPart;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.storage.loot.AlternativesLootEntry;
import net.minecraft.world.storage.loot.BinomialRange;
import net.minecraft.world.storage.loot.ConstantRange;
import net.minecraft.world.storage.loot.DynamicLootEntry;
import net.minecraft.world.storage.loot.ILootConditionConsumer;
import net.minecraft.world.storage.loot.ILootFunctionConsumer;
import net.minecraft.world.storage.loot.IRandomRange;
import net.minecraft.world.storage.loot.IntClamper;
import net.minecraft.world.storage.loot.ItemLootEntry;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.StandaloneLootEntry;
import net.minecraft.world.storage.loot.conditions.BlockStateProperty;
import net.minecraft.world.storage.loot.conditions.EntityHasProperty;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.conditions.MatchTool;
import net.minecraft.world.storage.loot.conditions.RandomChance;
import net.minecraft.world.storage.loot.conditions.SurvivesExplosion;
import net.minecraft.world.storage.loot.conditions.TableBonus;
import net.minecraft.world.storage.loot.functions.ApplyBonus;
import net.minecraft.world.storage.loot.functions.CopyBlockState;
import net.minecraft.world.storage.loot.functions.CopyName;
import net.minecraft.world.storage.loot.functions.CopyNbt;
import net.minecraft.world.storage.loot.functions.ExplosionDecay;
import net.minecraft.world.storage.loot.functions.LimitCount;
import net.minecraft.world.storage.loot.functions.SetContents;
import net.minecraft.world.storage.loot.functions.SetCount;

public class BlockLootTables implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
   private static final ILootCondition.IBuilder SILK_TOUCH = MatchTool.builder(ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))));
   private static final ILootCondition.IBuilder NO_SILK_TOUCH = SILK_TOUCH.inverted();
   private static final ILootCondition.IBuilder SHEARS = MatchTool.builder(ItemPredicate.Builder.create().item(Items.SHEARS));
   private static final ILootCondition.IBuilder SILK_TOUCH_OR_SHEARS = SHEARS.alternative(SILK_TOUCH);
   private static final ILootCondition.IBuilder NOT_SILK_TOUCH_OR_SHEARS = SILK_TOUCH_OR_SHEARS.inverted();
   private static final Set<Item> IMMUNE_TO_EXPLOSIONS = Stream.of(Blocks.DRAGON_EGG, Blocks.BEACON, Blocks.CONDUIT, Blocks.SKELETON_SKULL, Blocks.WITHER_SKELETON_SKULL, Blocks.PLAYER_HEAD, Blocks.ZOMBIE_HEAD, Blocks.CREEPER_HEAD, Blocks.DRAGON_HEAD, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX).map(IItemProvider::asItem).collect(ImmutableSet.toImmutableSet());
   private static final float[] DEFAULT_SAPLING_DROP_RATES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};
   private static final float[] RARE_SAPLING_DROP_RATES = new float[]{0.025F, 0.027777778F, 0.03125F, 0.041666668F, 0.1F};
   private final Map<ResourceLocation, LootTable.Builder> lootTables = Maps.newHashMap();

   protected static <T> T withExplosionDecay(IItemProvider p_218552_0_, ILootFunctionConsumer<T> p_218552_1_) {
      return (T)(!IMMUNE_TO_EXPLOSIONS.contains(p_218552_0_.asItem()) ? p_218552_1_.acceptFunction(ExplosionDecay.builder()) : p_218552_1_.cast());
   }

   protected static <T> T withSurvivesExplosion(IItemProvider p_218560_0_, ILootConditionConsumer<T> p_218560_1_) {
      return (T)(!IMMUNE_TO_EXPLOSIONS.contains(p_218560_0_.asItem()) ? p_218560_1_.acceptCondition(SurvivesExplosion.builder()) : p_218560_1_.cast());
   }

   protected static LootTable.Builder dropping(IItemProvider p_218546_0_) {
      return LootTable.builder().addLootPool(withSurvivesExplosion(p_218546_0_, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(p_218546_0_))));
   }

   protected static LootTable.Builder dropping(Block p_218494_0_, ILootCondition.IBuilder p_218494_1_, LootEntry.Builder<?> p_218494_2_) {
      return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(((StandaloneLootEntry.Builder)ItemLootEntry.builder(p_218494_0_).acceptCondition(p_218494_1_)).alternatively(p_218494_2_)));
   }

   protected static LootTable.Builder droppingWithSilkTouch(Block p_218519_0_, LootEntry.Builder<?> p_218519_1_) {
      return dropping(p_218519_0_, SILK_TOUCH, p_218519_1_);
   }

   protected static LootTable.Builder droppingWithShears(Block p_218511_0_, LootEntry.Builder<?> p_218511_1_) {
      return dropping(p_218511_0_, SHEARS, p_218511_1_);
   }

   protected static LootTable.Builder droppingWithSilkTouchOrShears(Block p_218535_0_, LootEntry.Builder<?> p_218535_1_) {
      return dropping(p_218535_0_, SILK_TOUCH_OR_SHEARS, p_218535_1_);
   }

   protected static LootTable.Builder droppingWithSilkTouch(Block p_218515_0_, IItemProvider p_218515_1_) {
      return droppingWithSilkTouch(p_218515_0_, withSurvivesExplosion(p_218515_0_, ItemLootEntry.builder(p_218515_1_)));
   }

   protected static LootTable.Builder droppingRandomly(IItemProvider p_218463_0_, IRandomRange p_218463_1_) {
      return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(withExplosionDecay(p_218463_0_, ItemLootEntry.builder(p_218463_0_).acceptFunction(SetCount.builder(p_218463_1_)))));
   }

   protected static LootTable.Builder droppingWithSilkTouchOrRandomly(Block p_218530_0_, IItemProvider p_218530_1_, IRandomRange p_218530_2_) {
      return droppingWithSilkTouch(p_218530_0_, withExplosionDecay(p_218530_0_, ItemLootEntry.builder(p_218530_1_).acceptFunction(SetCount.builder(p_218530_2_))));
   }

   protected static LootTable.Builder onlyWithSilkTouch(IItemProvider p_218561_0_) {
      return LootTable.builder().addLootPool(LootPool.builder().acceptCondition(SILK_TOUCH).rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(p_218561_0_)));
   }

   protected static LootTable.Builder droppingAndFlowerPot(IItemProvider p_218523_0_) {
      return LootTable.builder().addLootPool(withSurvivesExplosion(Blocks.FLOWER_POT, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Blocks.FLOWER_POT)))).addLootPool(withSurvivesExplosion(p_218523_0_, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(p_218523_0_))));
   }

   protected static LootTable.Builder droppingSlab(Block p_218513_0_) {
      return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(withExplosionDecay(p_218513_0_, ItemLootEntry.builder(p_218513_0_).acceptFunction(SetCount.builder(ConstantRange.of(2)).acceptCondition(BlockStateProperty.builder(p_218513_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withProp(SlabBlock.TYPE, SlabType.DOUBLE)))))));
   }

   protected static <T extends Comparable<T> & IStringSerializable> LootTable.Builder droppingWhen(Block p_218562_0_, IProperty<T> p_218562_1_, T p_218562_2_) {
      return LootTable.builder().addLootPool(withSurvivesExplosion(p_218562_0_, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(p_218562_0_).acceptCondition(BlockStateProperty.builder(p_218562_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withProp(p_218562_1_, p_218562_2_))))));
   }

   protected static LootTable.Builder droppingWithName(Block p_218481_0_) {
      return LootTable.builder().addLootPool(withSurvivesExplosion(p_218481_0_, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(p_218481_0_).acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY)))));
   }

   protected static LootTable.Builder droppingWithContents(Block p_218544_0_) {
      return LootTable.builder().addLootPool(withSurvivesExplosion(p_218544_0_, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(p_218544_0_).acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY)).acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).replaceOperation("Lock", "BlockEntityTag.Lock").replaceOperation("LootTable", "BlockEntityTag.LootTable").replaceOperation("LootTableSeed", "BlockEntityTag.LootTableSeed")).acceptFunction(SetContents.func_215920_b().func_216075_a(DynamicLootEntry.func_216162_a(ShulkerBoxBlock.CONTENTS))))));
   }

   protected static LootTable.Builder droppingWithPatterns(Block p_218559_0_) {
      return LootTable.builder().addLootPool(withSurvivesExplosion(p_218559_0_, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(p_218559_0_).acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY)).acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).replaceOperation("Patterns", "BlockEntityTag.Patterns")))));
   }

   private static LootTable.Builder func_229436_h_(Block p_229436_0_) {
      return LootTable.builder().addLootPool(LootPool.builder().acceptCondition(SILK_TOUCH).rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(p_229436_0_).acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).replaceOperation("Bees", "BlockEntityTag.Bees")).acceptFunction(CopyBlockState.func_227545_a_(p_229436_0_).func_227552_a_(BeehiveBlock.HONEY_LEVEL))));
   }

   private static LootTable.Builder func_229437_i_(Block p_229437_0_) {
      return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(((StandaloneLootEntry.Builder)ItemLootEntry.builder(p_229437_0_).acceptCondition(SILK_TOUCH)).acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).replaceOperation("Bees", "BlockEntityTag.Bees")).acceptFunction(CopyBlockState.func_227545_a_(p_229437_0_).func_227552_a_(BeehiveBlock.HONEY_LEVEL)).alternatively(ItemLootEntry.builder(p_229437_0_))));
   }

   protected static LootTable.Builder droppingItemWithFortune(Block p_218476_0_, Item p_218476_1_) {
      return droppingWithSilkTouch(p_218476_0_, withExplosionDecay(p_218476_0_, ItemLootEntry.builder(p_218476_1_).acceptFunction(ApplyBonus.oreDrops(Enchantments.FORTUNE))));
   }

   /**
    * Creates a builder that drops the given IItemProvider in amounts between 0 and 2, most often 0. Only used in
    * vanilla for huge mushroom blocks.
    */
   protected static LootTable.Builder droppingItemRarely(Block p_218491_0_, IItemProvider p_218491_1_) {
      return droppingWithSilkTouch(p_218491_0_, withExplosionDecay(p_218491_0_, ItemLootEntry.builder(p_218491_1_).acceptFunction(SetCount.builder(RandomValueRange.of(-6.0F, 2.0F))).acceptFunction(LimitCount.func_215911_a(IntClamper.func_215848_a(0)))));
   }

   protected static LootTable.Builder droppingSeeds(Block p_218570_0_) {
      return droppingWithShears(p_218570_0_, withExplosionDecay(p_218570_0_, (ItemLootEntry.builder(Items.WHEAT_SEEDS).acceptCondition(RandomChance.builder(0.125F))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE, 2))));
   }

   /**
    * Creates a builder that drops the given IItemProvider in amounts between 0 and 3, based on the AGE property. Only
    * used in vanilla for pumpkin and melon stems.
    */
   protected static LootTable.Builder droppingByAge(Block p_218475_0_, Item p_218475_1_) {
      return LootTable.builder().addLootPool(withExplosionDecay(p_218475_0_, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(p_218475_1_).acceptFunction(SetCount.builder(BinomialRange.of(3, 0.06666667F)).acceptCondition(BlockStateProperty.builder(p_218475_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(StemBlock.AGE, 0)))).acceptFunction(SetCount.builder(BinomialRange.of(3, 0.13333334F)).acceptCondition(BlockStateProperty.builder(p_218475_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(StemBlock.AGE, 1)))).acceptFunction(SetCount.builder(BinomialRange.of(3, 0.2F)).acceptCondition(BlockStateProperty.builder(p_218475_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(StemBlock.AGE, 2)))).acceptFunction(SetCount.builder(BinomialRange.of(3, 0.26666668F)).acceptCondition(BlockStateProperty.builder(p_218475_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(StemBlock.AGE, 3)))).acceptFunction(SetCount.builder(BinomialRange.of(3, 0.33333334F)).acceptCondition(BlockStateProperty.builder(p_218475_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(StemBlock.AGE, 4)))).acceptFunction(SetCount.builder(BinomialRange.of(3, 0.4F)).acceptCondition(BlockStateProperty.builder(p_218475_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(StemBlock.AGE, 5)))).acceptFunction(SetCount.builder(BinomialRange.of(3, 0.46666667F)).acceptCondition(BlockStateProperty.builder(p_218475_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(StemBlock.AGE, 6)))).acceptFunction(SetCount.builder(BinomialRange.of(3, 0.53333336F)).acceptCondition(BlockStateProperty.builder(p_218475_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(StemBlock.AGE, 7)))))));
   }

   private static LootTable.Builder func_229435_c_(Block p_229435_0_, Item p_229435_1_) {
      return LootTable.builder().addLootPool(withExplosionDecay(p_229435_0_, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(p_229435_1_).acceptFunction(SetCount.builder(BinomialRange.of(3, 0.53333336F))))));
   }

   protected static LootTable.Builder onlyWithShears(IItemProvider p_218486_0_) {
      return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(SHEARS).addEntry(ItemLootEntry.builder(p_218486_0_)));
   }

   /**
    * Used for all leaves, drops self with silk touch, otherwise drops the second Block param with the passed chances
    * for fortune levels, adding in sticks.
    */
   protected static LootTable.Builder droppingWithChancesAndSticks(Block p_218540_0_, Block p_218540_1_, float... p_218540_2_) {
      return droppingWithSilkTouchOrShears(p_218540_0_, withSurvivesExplosion(p_218540_0_, ItemLootEntry.builder(p_218540_1_)).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, p_218540_2_))).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(NOT_SILK_TOUCH_OR_SHEARS).addEntry(withExplosionDecay(p_218540_0_, ItemLootEntry.builder(Items.STICK).acceptFunction(SetCount.builder(RandomValueRange.of(1.0F, 2.0F)))).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F))));
   }

   /**
    * Used for oak and dark oak, same as droppingWithChancesAndSticks but adding in apples.
    */
   protected static LootTable.Builder droppingWithChancesSticksAndApples(Block p_218526_0_, Block p_218526_1_, float... p_218526_2_) {
      return droppingWithChancesAndSticks(p_218526_0_, p_218526_1_, p_218526_2_).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(NOT_SILK_TOUCH_OR_SHEARS).addEntry(withSurvivesExplosion(p_218526_0_, ItemLootEntry.builder(Items.APPLE)).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))));
   }

   /**
    * Drops the first item parameter always, and the second item parameter plus more of the first when the loot
    * condition is met, applying fortune to only the second argument.
    */
   protected static LootTable.Builder droppingAndBonusWhen(Block p_218541_0_, Item p_218541_1_, Item p_218541_2_, ILootCondition.IBuilder p_218541_3_) {
      return withExplosionDecay(p_218541_0_, LootTable.builder().addLootPool(LootPool.builder().addEntry(((StandaloneLootEntry.Builder)ItemLootEntry.builder(p_218541_1_).acceptCondition(p_218541_3_)).alternatively(ItemLootEntry.builder(p_218541_2_)))).addLootPool(LootPool.builder().acceptCondition(p_218541_3_).addEntry(ItemLootEntry.builder(p_218541_2_).acceptFunction(ApplyBonus.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286F, 3)))));
   }

   public static LootTable.Builder func_218482_a() {
      return LootTable.builder();
   }

   protected void addTables() {
      this.registerDropSelfLootTable(Blocks.GRANITE);
      this.registerDropSelfLootTable(Blocks.POLISHED_GRANITE);
      this.registerDropSelfLootTable(Blocks.DIORITE);
      this.registerDropSelfLootTable(Blocks.POLISHED_DIORITE);
      this.registerDropSelfLootTable(Blocks.ANDESITE);
      this.registerDropSelfLootTable(Blocks.POLISHED_ANDESITE);
      this.registerDropSelfLootTable(Blocks.DIRT);
      this.registerDropSelfLootTable(Blocks.COARSE_DIRT);
      this.registerDropSelfLootTable(Blocks.COBBLESTONE);
      this.registerDropSelfLootTable(Blocks.OAK_PLANKS);
      this.registerDropSelfLootTable(Blocks.SPRUCE_PLANKS);
      this.registerDropSelfLootTable(Blocks.BIRCH_PLANKS);
      this.registerDropSelfLootTable(Blocks.JUNGLE_PLANKS);
      this.registerDropSelfLootTable(Blocks.ACACIA_PLANKS);
      this.registerDropSelfLootTable(Blocks.DARK_OAK_PLANKS);
      this.registerDropSelfLootTable(Blocks.OAK_SAPLING);
      this.registerDropSelfLootTable(Blocks.SPRUCE_SAPLING);
      this.registerDropSelfLootTable(Blocks.BIRCH_SAPLING);
      this.registerDropSelfLootTable(Blocks.JUNGLE_SAPLING);
      this.registerDropSelfLootTable(Blocks.ACACIA_SAPLING);
      this.registerDropSelfLootTable(Blocks.DARK_OAK_SAPLING);
      this.registerDropSelfLootTable(Blocks.SAND);
      this.registerDropSelfLootTable(Blocks.RED_SAND);
      this.registerDropSelfLootTable(Blocks.GOLD_ORE);
      this.registerDropSelfLootTable(Blocks.IRON_ORE);
      this.registerDropSelfLootTable(Blocks.OAK_LOG);
      this.registerDropSelfLootTable(Blocks.SPRUCE_LOG);
      this.registerDropSelfLootTable(Blocks.BIRCH_LOG);
      this.registerDropSelfLootTable(Blocks.JUNGLE_LOG);
      this.registerDropSelfLootTable(Blocks.ACACIA_LOG);
      this.registerDropSelfLootTable(Blocks.DARK_OAK_LOG);
      this.registerDropSelfLootTable(Blocks.STRIPPED_SPRUCE_LOG);
      this.registerDropSelfLootTable(Blocks.STRIPPED_BIRCH_LOG);
      this.registerDropSelfLootTable(Blocks.STRIPPED_JUNGLE_LOG);
      this.registerDropSelfLootTable(Blocks.STRIPPED_ACACIA_LOG);
      this.registerDropSelfLootTable(Blocks.STRIPPED_DARK_OAK_LOG);
      this.registerDropSelfLootTable(Blocks.STRIPPED_OAK_LOG);
      this.registerDropSelfLootTable(Blocks.OAK_WOOD);
      this.registerDropSelfLootTable(Blocks.SPRUCE_WOOD);
      this.registerDropSelfLootTable(Blocks.BIRCH_WOOD);
      this.registerDropSelfLootTable(Blocks.JUNGLE_WOOD);
      this.registerDropSelfLootTable(Blocks.ACACIA_WOOD);
      this.registerDropSelfLootTable(Blocks.DARK_OAK_WOOD);
      this.registerDropSelfLootTable(Blocks.STRIPPED_OAK_WOOD);
      this.registerDropSelfLootTable(Blocks.STRIPPED_SPRUCE_WOOD);
      this.registerDropSelfLootTable(Blocks.STRIPPED_BIRCH_WOOD);
      this.registerDropSelfLootTable(Blocks.STRIPPED_JUNGLE_WOOD);
      this.registerDropSelfLootTable(Blocks.STRIPPED_ACACIA_WOOD);
      this.registerDropSelfLootTable(Blocks.STRIPPED_DARK_OAK_WOOD);
      this.registerDropSelfLootTable(Blocks.SPONGE);
      this.registerDropSelfLootTable(Blocks.WET_SPONGE);
      this.registerDropSelfLootTable(Blocks.LAPIS_BLOCK);
      this.registerDropSelfLootTable(Blocks.SANDSTONE);
      this.registerDropSelfLootTable(Blocks.CHISELED_SANDSTONE);
      this.registerDropSelfLootTable(Blocks.CUT_SANDSTONE);
      this.registerDropSelfLootTable(Blocks.NOTE_BLOCK);
      this.registerDropSelfLootTable(Blocks.POWERED_RAIL);
      this.registerDropSelfLootTable(Blocks.DETECTOR_RAIL);
      this.registerDropSelfLootTable(Blocks.STICKY_PISTON);
      this.registerDropSelfLootTable(Blocks.PISTON);
      this.registerDropSelfLootTable(Blocks.WHITE_WOOL);
      this.registerDropSelfLootTable(Blocks.ORANGE_WOOL);
      this.registerDropSelfLootTable(Blocks.MAGENTA_WOOL);
      this.registerDropSelfLootTable(Blocks.LIGHT_BLUE_WOOL);
      this.registerDropSelfLootTable(Blocks.YELLOW_WOOL);
      this.registerDropSelfLootTable(Blocks.LIME_WOOL);
      this.registerDropSelfLootTable(Blocks.PINK_WOOL);
      this.registerDropSelfLootTable(Blocks.GRAY_WOOL);
      this.registerDropSelfLootTable(Blocks.LIGHT_GRAY_WOOL);
      this.registerDropSelfLootTable(Blocks.CYAN_WOOL);
      this.registerDropSelfLootTable(Blocks.PURPLE_WOOL);
      this.registerDropSelfLootTable(Blocks.BLUE_WOOL);
      this.registerDropSelfLootTable(Blocks.BROWN_WOOL);
      this.registerDropSelfLootTable(Blocks.GREEN_WOOL);
      this.registerDropSelfLootTable(Blocks.RED_WOOL);
      this.registerDropSelfLootTable(Blocks.BLACK_WOOL);
      this.registerDropSelfLootTable(Blocks.DANDELION);
      this.registerDropSelfLootTable(Blocks.POPPY);
      this.registerDropSelfLootTable(Blocks.BLUE_ORCHID);
      this.registerDropSelfLootTable(Blocks.ALLIUM);
      this.registerDropSelfLootTable(Blocks.AZURE_BLUET);
      this.registerDropSelfLootTable(Blocks.RED_TULIP);
      this.registerDropSelfLootTable(Blocks.ORANGE_TULIP);
      this.registerDropSelfLootTable(Blocks.WHITE_TULIP);
      this.registerDropSelfLootTable(Blocks.PINK_TULIP);
      this.registerDropSelfLootTable(Blocks.OXEYE_DAISY);
      this.registerDropSelfLootTable(Blocks.CORNFLOWER);
      this.registerDropSelfLootTable(Blocks.WITHER_ROSE);
      this.registerDropSelfLootTable(Blocks.LILY_OF_THE_VALLEY);
      this.registerDropSelfLootTable(Blocks.BROWN_MUSHROOM);
      this.registerDropSelfLootTable(Blocks.RED_MUSHROOM);
      this.registerDropSelfLootTable(Blocks.GOLD_BLOCK);
      this.registerDropSelfLootTable(Blocks.IRON_BLOCK);
      this.registerDropSelfLootTable(Blocks.BRICKS);
      this.registerDropSelfLootTable(Blocks.MOSSY_COBBLESTONE);
      this.registerDropSelfLootTable(Blocks.OBSIDIAN);
      this.registerDropSelfLootTable(Blocks.TORCH);
      this.registerDropSelfLootTable(Blocks.OAK_STAIRS);
      this.registerDropSelfLootTable(Blocks.REDSTONE_WIRE);
      this.registerDropSelfLootTable(Blocks.DIAMOND_BLOCK);
      this.registerDropSelfLootTable(Blocks.CRAFTING_TABLE);
      this.registerDropSelfLootTable(Blocks.OAK_SIGN);
      this.registerDropSelfLootTable(Blocks.SPRUCE_SIGN);
      this.registerDropSelfLootTable(Blocks.BIRCH_SIGN);
      this.registerDropSelfLootTable(Blocks.ACACIA_SIGN);
      this.registerDropSelfLootTable(Blocks.JUNGLE_SIGN);
      this.registerDropSelfLootTable(Blocks.DARK_OAK_SIGN);
      this.registerDropSelfLootTable(Blocks.LADDER);
      this.registerDropSelfLootTable(Blocks.RAIL);
      this.registerDropSelfLootTable(Blocks.COBBLESTONE_STAIRS);
      this.registerDropSelfLootTable(Blocks.LEVER);
      this.registerDropSelfLootTable(Blocks.STONE_PRESSURE_PLATE);
      this.registerDropSelfLootTable(Blocks.OAK_PRESSURE_PLATE);
      this.registerDropSelfLootTable(Blocks.SPRUCE_PRESSURE_PLATE);
      this.registerDropSelfLootTable(Blocks.BIRCH_PRESSURE_PLATE);
      this.registerDropSelfLootTable(Blocks.JUNGLE_PRESSURE_PLATE);
      this.registerDropSelfLootTable(Blocks.ACACIA_PRESSURE_PLATE);
      this.registerDropSelfLootTable(Blocks.DARK_OAK_PRESSURE_PLATE);
      this.registerDropSelfLootTable(Blocks.REDSTONE_TORCH);
      this.registerDropSelfLootTable(Blocks.STONE_BUTTON);
      this.registerDropSelfLootTable(Blocks.CACTUS);
      this.registerDropSelfLootTable(Blocks.SUGAR_CANE);
      this.registerDropSelfLootTable(Blocks.JUKEBOX);
      this.registerDropSelfLootTable(Blocks.OAK_FENCE);
      this.registerDropSelfLootTable(Blocks.PUMPKIN);
      this.registerDropSelfLootTable(Blocks.NETHERRACK);
      this.registerDropSelfLootTable(Blocks.SOUL_SAND);
      this.registerDropSelfLootTable(Blocks.CARVED_PUMPKIN);
      this.registerDropSelfLootTable(Blocks.JACK_O_LANTERN);
      this.registerDropSelfLootTable(Blocks.REPEATER);
      this.registerDropSelfLootTable(Blocks.OAK_TRAPDOOR);
      this.registerDropSelfLootTable(Blocks.SPRUCE_TRAPDOOR);
      this.registerDropSelfLootTable(Blocks.BIRCH_TRAPDOOR);
      this.registerDropSelfLootTable(Blocks.JUNGLE_TRAPDOOR);
      this.registerDropSelfLootTable(Blocks.ACACIA_TRAPDOOR);
      this.registerDropSelfLootTable(Blocks.DARK_OAK_TRAPDOOR);
      this.registerDropSelfLootTable(Blocks.STONE_BRICKS);
      this.registerDropSelfLootTable(Blocks.MOSSY_STONE_BRICKS);
      this.registerDropSelfLootTable(Blocks.CRACKED_STONE_BRICKS);
      this.registerDropSelfLootTable(Blocks.CHISELED_STONE_BRICKS);
      this.registerDropSelfLootTable(Blocks.IRON_BARS);
      this.registerDropSelfLootTable(Blocks.OAK_FENCE_GATE);
      this.registerDropSelfLootTable(Blocks.BRICK_STAIRS);
      this.registerDropSelfLootTable(Blocks.STONE_BRICK_STAIRS);
      this.registerDropSelfLootTable(Blocks.LILY_PAD);
      this.registerDropSelfLootTable(Blocks.NETHER_BRICKS);
      this.registerDropSelfLootTable(Blocks.NETHER_BRICK_FENCE);
      this.registerDropSelfLootTable(Blocks.NETHER_BRICK_STAIRS);
      this.registerDropSelfLootTable(Blocks.CAULDRON);
      this.registerDropSelfLootTable(Blocks.END_STONE);
      this.registerDropSelfLootTable(Blocks.REDSTONE_LAMP);
      this.registerDropSelfLootTable(Blocks.SANDSTONE_STAIRS);
      this.registerDropSelfLootTable(Blocks.TRIPWIRE_HOOK);
      this.registerDropSelfLootTable(Blocks.EMERALD_BLOCK);
      this.registerDropSelfLootTable(Blocks.SPRUCE_STAIRS);
      this.registerDropSelfLootTable(Blocks.BIRCH_STAIRS);
      this.registerDropSelfLootTable(Blocks.JUNGLE_STAIRS);
      this.registerDropSelfLootTable(Blocks.COBBLESTONE_WALL);
      this.registerDropSelfLootTable(Blocks.MOSSY_COBBLESTONE_WALL);
      this.registerDropSelfLootTable(Blocks.FLOWER_POT);
      this.registerDropSelfLootTable(Blocks.OAK_BUTTON);
      this.registerDropSelfLootTable(Blocks.SPRUCE_BUTTON);
      this.registerDropSelfLootTable(Blocks.BIRCH_BUTTON);
      this.registerDropSelfLootTable(Blocks.JUNGLE_BUTTON);
      this.registerDropSelfLootTable(Blocks.ACACIA_BUTTON);
      this.registerDropSelfLootTable(Blocks.DARK_OAK_BUTTON);
      this.registerDropSelfLootTable(Blocks.SKELETON_SKULL);
      this.registerDropSelfLootTable(Blocks.WITHER_SKELETON_SKULL);
      this.registerDropSelfLootTable(Blocks.ZOMBIE_HEAD);
      this.registerDropSelfLootTable(Blocks.CREEPER_HEAD);
      this.registerDropSelfLootTable(Blocks.DRAGON_HEAD);
      this.registerDropSelfLootTable(Blocks.ANVIL);
      this.registerDropSelfLootTable(Blocks.CHIPPED_ANVIL);
      this.registerDropSelfLootTable(Blocks.DAMAGED_ANVIL);
      this.registerDropSelfLootTable(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
      this.registerDropSelfLootTable(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
      this.registerDropSelfLootTable(Blocks.COMPARATOR);
      this.registerDropSelfLootTable(Blocks.DAYLIGHT_DETECTOR);
      this.registerDropSelfLootTable(Blocks.REDSTONE_BLOCK);
      this.registerDropSelfLootTable(Blocks.QUARTZ_BLOCK);
      this.registerDropSelfLootTable(Blocks.CHISELED_QUARTZ_BLOCK);
      this.registerDropSelfLootTable(Blocks.QUARTZ_PILLAR);
      this.registerDropSelfLootTable(Blocks.QUARTZ_STAIRS);
      this.registerDropSelfLootTable(Blocks.ACTIVATOR_RAIL);
      this.registerDropSelfLootTable(Blocks.WHITE_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.ORANGE_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.MAGENTA_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.LIGHT_BLUE_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.YELLOW_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.LIME_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.PINK_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.GRAY_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.LIGHT_GRAY_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.CYAN_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.PURPLE_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.BLUE_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.BROWN_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.GREEN_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.RED_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.BLACK_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.ACACIA_STAIRS);
      this.registerDropSelfLootTable(Blocks.DARK_OAK_STAIRS);
      this.registerDropSelfLootTable(Blocks.SLIME_BLOCK);
      this.registerDropSelfLootTable(Blocks.IRON_TRAPDOOR);
      this.registerDropSelfLootTable(Blocks.PRISMARINE);
      this.registerDropSelfLootTable(Blocks.PRISMARINE_BRICKS);
      this.registerDropSelfLootTable(Blocks.DARK_PRISMARINE);
      this.registerDropSelfLootTable(Blocks.PRISMARINE_STAIRS);
      this.registerDropSelfLootTable(Blocks.PRISMARINE_BRICK_STAIRS);
      this.registerDropSelfLootTable(Blocks.DARK_PRISMARINE_STAIRS);
      this.registerDropSelfLootTable(Blocks.HAY_BLOCK);
      this.registerDropSelfLootTable(Blocks.WHITE_CARPET);
      this.registerDropSelfLootTable(Blocks.ORANGE_CARPET);
      this.registerDropSelfLootTable(Blocks.MAGENTA_CARPET);
      this.registerDropSelfLootTable(Blocks.LIGHT_BLUE_CARPET);
      this.registerDropSelfLootTable(Blocks.YELLOW_CARPET);
      this.registerDropSelfLootTable(Blocks.LIME_CARPET);
      this.registerDropSelfLootTable(Blocks.PINK_CARPET);
      this.registerDropSelfLootTable(Blocks.GRAY_CARPET);
      this.registerDropSelfLootTable(Blocks.LIGHT_GRAY_CARPET);
      this.registerDropSelfLootTable(Blocks.CYAN_CARPET);
      this.registerDropSelfLootTable(Blocks.PURPLE_CARPET);
      this.registerDropSelfLootTable(Blocks.BLUE_CARPET);
      this.registerDropSelfLootTable(Blocks.BROWN_CARPET);
      this.registerDropSelfLootTable(Blocks.GREEN_CARPET);
      this.registerDropSelfLootTable(Blocks.RED_CARPET);
      this.registerDropSelfLootTable(Blocks.BLACK_CARPET);
      this.registerDropSelfLootTable(Blocks.TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.COAL_BLOCK);
      this.registerDropSelfLootTable(Blocks.RED_SANDSTONE);
      this.registerDropSelfLootTable(Blocks.CHISELED_RED_SANDSTONE);
      this.registerDropSelfLootTable(Blocks.CUT_RED_SANDSTONE);
      this.registerDropSelfLootTable(Blocks.RED_SANDSTONE_STAIRS);
      this.registerDropSelfLootTable(Blocks.SMOOTH_STONE);
      this.registerDropSelfLootTable(Blocks.SMOOTH_SANDSTONE);
      this.registerDropSelfLootTable(Blocks.SMOOTH_QUARTZ);
      this.registerDropSelfLootTable(Blocks.SMOOTH_RED_SANDSTONE);
      this.registerDropSelfLootTable(Blocks.SPRUCE_FENCE_GATE);
      this.registerDropSelfLootTable(Blocks.BIRCH_FENCE_GATE);
      this.registerDropSelfLootTable(Blocks.JUNGLE_FENCE_GATE);
      this.registerDropSelfLootTable(Blocks.ACACIA_FENCE_GATE);
      this.registerDropSelfLootTable(Blocks.DARK_OAK_FENCE_GATE);
      this.registerDropSelfLootTable(Blocks.SPRUCE_FENCE);
      this.registerDropSelfLootTable(Blocks.BIRCH_FENCE);
      this.registerDropSelfLootTable(Blocks.JUNGLE_FENCE);
      this.registerDropSelfLootTable(Blocks.ACACIA_FENCE);
      this.registerDropSelfLootTable(Blocks.DARK_OAK_FENCE);
      this.registerDropSelfLootTable(Blocks.END_ROD);
      this.registerDropSelfLootTable(Blocks.PURPUR_BLOCK);
      this.registerDropSelfLootTable(Blocks.PURPUR_PILLAR);
      this.registerDropSelfLootTable(Blocks.PURPUR_STAIRS);
      this.registerDropSelfLootTable(Blocks.END_STONE_BRICKS);
      this.registerDropSelfLootTable(Blocks.MAGMA_BLOCK);
      this.registerDropSelfLootTable(Blocks.NETHER_WART_BLOCK);
      this.registerDropSelfLootTable(Blocks.RED_NETHER_BRICKS);
      this.registerDropSelfLootTable(Blocks.BONE_BLOCK);
      this.registerDropSelfLootTable(Blocks.OBSERVER);
      this.registerDropSelfLootTable(Blocks.WHITE_GLAZED_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.ORANGE_GLAZED_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.MAGENTA_GLAZED_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.YELLOW_GLAZED_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.LIME_GLAZED_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.PINK_GLAZED_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.GRAY_GLAZED_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.CYAN_GLAZED_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.PURPLE_GLAZED_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.BLUE_GLAZED_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.BROWN_GLAZED_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.GREEN_GLAZED_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.RED_GLAZED_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.BLACK_GLAZED_TERRACOTTA);
      this.registerDropSelfLootTable(Blocks.WHITE_CONCRETE);
      this.registerDropSelfLootTable(Blocks.ORANGE_CONCRETE);
      this.registerDropSelfLootTable(Blocks.MAGENTA_CONCRETE);
      this.registerDropSelfLootTable(Blocks.LIGHT_BLUE_CONCRETE);
      this.registerDropSelfLootTable(Blocks.YELLOW_CONCRETE);
      this.registerDropSelfLootTable(Blocks.LIME_CONCRETE);
      this.registerDropSelfLootTable(Blocks.PINK_CONCRETE);
      this.registerDropSelfLootTable(Blocks.GRAY_CONCRETE);
      this.registerDropSelfLootTable(Blocks.LIGHT_GRAY_CONCRETE);
      this.registerDropSelfLootTable(Blocks.CYAN_CONCRETE);
      this.registerDropSelfLootTable(Blocks.PURPLE_CONCRETE);
      this.registerDropSelfLootTable(Blocks.BLUE_CONCRETE);
      this.registerDropSelfLootTable(Blocks.BROWN_CONCRETE);
      this.registerDropSelfLootTable(Blocks.GREEN_CONCRETE);
      this.registerDropSelfLootTable(Blocks.RED_CONCRETE);
      this.registerDropSelfLootTable(Blocks.BLACK_CONCRETE);
      this.registerDropSelfLootTable(Blocks.WHITE_CONCRETE_POWDER);
      this.registerDropSelfLootTable(Blocks.ORANGE_CONCRETE_POWDER);
      this.registerDropSelfLootTable(Blocks.MAGENTA_CONCRETE_POWDER);
      this.registerDropSelfLootTable(Blocks.LIGHT_BLUE_CONCRETE_POWDER);
      this.registerDropSelfLootTable(Blocks.YELLOW_CONCRETE_POWDER);
      this.registerDropSelfLootTable(Blocks.LIME_CONCRETE_POWDER);
      this.registerDropSelfLootTable(Blocks.PINK_CONCRETE_POWDER);
      this.registerDropSelfLootTable(Blocks.GRAY_CONCRETE_POWDER);
      this.registerDropSelfLootTable(Blocks.LIGHT_GRAY_CONCRETE_POWDER);
      this.registerDropSelfLootTable(Blocks.CYAN_CONCRETE_POWDER);
      this.registerDropSelfLootTable(Blocks.PURPLE_CONCRETE_POWDER);
      this.registerDropSelfLootTable(Blocks.BLUE_CONCRETE_POWDER);
      this.registerDropSelfLootTable(Blocks.BROWN_CONCRETE_POWDER);
      this.registerDropSelfLootTable(Blocks.GREEN_CONCRETE_POWDER);
      this.registerDropSelfLootTable(Blocks.RED_CONCRETE_POWDER);
      this.registerDropSelfLootTable(Blocks.BLACK_CONCRETE_POWDER);
      this.registerDropSelfLootTable(Blocks.KELP);
      this.registerDropSelfLootTable(Blocks.DRIED_KELP_BLOCK);
      this.registerDropSelfLootTable(Blocks.DEAD_TUBE_CORAL_BLOCK);
      this.registerDropSelfLootTable(Blocks.DEAD_BRAIN_CORAL_BLOCK);
      this.registerDropSelfLootTable(Blocks.DEAD_BUBBLE_CORAL_BLOCK);
      this.registerDropSelfLootTable(Blocks.DEAD_FIRE_CORAL_BLOCK);
      this.registerDropSelfLootTable(Blocks.DEAD_HORN_CORAL_BLOCK);
      this.registerDropSelfLootTable(Blocks.CONDUIT);
      this.registerDropSelfLootTable(Blocks.DRAGON_EGG);
      this.registerDropSelfLootTable(Blocks.BAMBOO);
      this.registerDropSelfLootTable(Blocks.POLISHED_GRANITE_STAIRS);
      this.registerDropSelfLootTable(Blocks.SMOOTH_RED_SANDSTONE_STAIRS);
      this.registerDropSelfLootTable(Blocks.MOSSY_STONE_BRICK_STAIRS);
      this.registerDropSelfLootTable(Blocks.POLISHED_DIORITE_STAIRS);
      this.registerDropSelfLootTable(Blocks.MOSSY_COBBLESTONE_STAIRS);
      this.registerDropSelfLootTable(Blocks.END_STONE_BRICK_STAIRS);
      this.registerDropSelfLootTable(Blocks.STONE_STAIRS);
      this.registerDropSelfLootTable(Blocks.SMOOTH_SANDSTONE_STAIRS);
      this.registerDropSelfLootTable(Blocks.SMOOTH_QUARTZ_STAIRS);
      this.registerDropSelfLootTable(Blocks.GRANITE_STAIRS);
      this.registerDropSelfLootTable(Blocks.ANDESITE_STAIRS);
      this.registerDropSelfLootTable(Blocks.RED_NETHER_BRICK_STAIRS);
      this.registerDropSelfLootTable(Blocks.POLISHED_ANDESITE_STAIRS);
      this.registerDropSelfLootTable(Blocks.DIORITE_STAIRS);
      this.registerDropSelfLootTable(Blocks.BRICK_WALL);
      this.registerDropSelfLootTable(Blocks.PRISMARINE_WALL);
      this.registerDropSelfLootTable(Blocks.RED_SANDSTONE_WALL);
      this.registerDropSelfLootTable(Blocks.MOSSY_STONE_BRICK_WALL);
      this.registerDropSelfLootTable(Blocks.GRANITE_WALL);
      this.registerDropSelfLootTable(Blocks.STONE_BRICK_WALL);
      this.registerDropSelfLootTable(Blocks.NETHER_BRICK_WALL);
      this.registerDropSelfLootTable(Blocks.ANDESITE_WALL);
      this.registerDropSelfLootTable(Blocks.RED_NETHER_BRICK_WALL);
      this.registerDropSelfLootTable(Blocks.SANDSTONE_WALL);
      this.registerDropSelfLootTable(Blocks.END_STONE_BRICK_WALL);
      this.registerDropSelfLootTable(Blocks.DIORITE_WALL);
      this.registerDropSelfLootTable(Blocks.LOOM);
      this.registerDropSelfLootTable(Blocks.SCAFFOLDING);
      this.registerDropSelfLootTable(Blocks.HONEY_BLOCK);
      this.registerDropSelfLootTable(Blocks.HONEYCOMB_BLOCK);
      this.registerDropping(Blocks.FARMLAND, Blocks.DIRT);
      this.registerDropping(Blocks.TRIPWIRE, Items.STRING);
      this.registerDropping(Blocks.GRASS_PATH, Blocks.DIRT);
      this.registerDropping(Blocks.KELP_PLANT, Blocks.KELP);
      this.registerDropping(Blocks.BAMBOO_SAPLING, Blocks.BAMBOO);
      this.registerLootTable(Blocks.STONE, (p_218490_0_) -> {
         return droppingWithSilkTouch(p_218490_0_, Blocks.COBBLESTONE);
      });
      this.registerLootTable(Blocks.GRASS_BLOCK, (p_218529_0_) -> {
         return droppingWithSilkTouch(p_218529_0_, Blocks.DIRT);
      });
      this.registerLootTable(Blocks.PODZOL, (p_218514_0_) -> {
         return droppingWithSilkTouch(p_218514_0_, Blocks.DIRT);
      });
      this.registerLootTable(Blocks.MYCELIUM, (p_218501_0_) -> {
         return droppingWithSilkTouch(p_218501_0_, Blocks.DIRT);
      });
      this.registerLootTable(Blocks.TUBE_CORAL_BLOCK, (p_218539_0_) -> {
         return droppingWithSilkTouch(p_218539_0_, Blocks.DEAD_TUBE_CORAL_BLOCK);
      });
      this.registerLootTable(Blocks.BRAIN_CORAL_BLOCK, (p_218462_0_) -> {
         return droppingWithSilkTouch(p_218462_0_, Blocks.DEAD_BRAIN_CORAL_BLOCK);
      });
      this.registerLootTable(Blocks.BUBBLE_CORAL_BLOCK, (p_218505_0_) -> {
         return droppingWithSilkTouch(p_218505_0_, Blocks.DEAD_BUBBLE_CORAL_BLOCK);
      });
      this.registerLootTable(Blocks.FIRE_CORAL_BLOCK, (p_218499_0_) -> {
         return droppingWithSilkTouch(p_218499_0_, Blocks.DEAD_FIRE_CORAL_BLOCK);
      });
      this.registerLootTable(Blocks.HORN_CORAL_BLOCK, (p_218502_0_) -> {
         return droppingWithSilkTouch(p_218502_0_, Blocks.DEAD_HORN_CORAL_BLOCK);
      });
      this.registerLootTable(Blocks.BOOKSHELF, (p_218534_0_) -> {
         return droppingWithSilkTouchOrRandomly(p_218534_0_, Items.BOOK, ConstantRange.of(3));
      });
      this.registerLootTable(Blocks.CLAY, (p_218465_0_) -> {
         return droppingWithSilkTouchOrRandomly(p_218465_0_, Items.CLAY_BALL, ConstantRange.of(4));
      });
      this.registerLootTable(Blocks.ENDER_CHEST, (p_218558_0_) -> {
         return droppingWithSilkTouchOrRandomly(p_218558_0_, Blocks.OBSIDIAN, ConstantRange.of(8));
      });
      this.registerLootTable(Blocks.SNOW_BLOCK, (p_218556_0_) -> {
         return droppingWithSilkTouchOrRandomly(p_218556_0_, Items.SNOWBALL, ConstantRange.of(4));
      });
      this.registerLootTable(Blocks.CHORUS_PLANT, droppingRandomly(Items.CHORUS_FRUIT, RandomValueRange.of(0.0F, 1.0F)));
      this.registerFlowerPot(Blocks.POTTED_OAK_SAPLING);
      this.registerFlowerPot(Blocks.POTTED_SPRUCE_SAPLING);
      this.registerFlowerPot(Blocks.POTTED_BIRCH_SAPLING);
      this.registerFlowerPot(Blocks.POTTED_JUNGLE_SAPLING);
      this.registerFlowerPot(Blocks.POTTED_ACACIA_SAPLING);
      this.registerFlowerPot(Blocks.POTTED_DARK_OAK_SAPLING);
      this.registerFlowerPot(Blocks.POTTED_FERN);
      this.registerFlowerPot(Blocks.POTTED_DANDELION);
      this.registerFlowerPot(Blocks.POTTED_POPPY);
      this.registerFlowerPot(Blocks.POTTED_BLUE_ORCHID);
      this.registerFlowerPot(Blocks.POTTED_ALLIUM);
      this.registerFlowerPot(Blocks.POTTED_AZURE_BLUET);
      this.registerFlowerPot(Blocks.POTTED_RED_TULIP);
      this.registerFlowerPot(Blocks.POTTED_ORANGE_TULIP);
      this.registerFlowerPot(Blocks.POTTED_WHITE_TULIP);
      this.registerFlowerPot(Blocks.POTTED_PINK_TULIP);
      this.registerFlowerPot(Blocks.POTTED_OXEYE_DAISY);
      this.registerFlowerPot(Blocks.POTTED_CORNFLOWER);
      this.registerFlowerPot(Blocks.POTTED_LILY_OF_THE_VALLEY);
      this.registerFlowerPot(Blocks.POTTED_WITHER_ROSE);
      this.registerFlowerPot(Blocks.POTTED_RED_MUSHROOM);
      this.registerFlowerPot(Blocks.POTTED_BROWN_MUSHROOM);
      this.registerFlowerPot(Blocks.POTTED_DEAD_BUSH);
      this.registerFlowerPot(Blocks.POTTED_CACTUS);
      this.registerFlowerPot(Blocks.POTTED_BAMBOO);
      this.registerLootTable(Blocks.ACACIA_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.BIRCH_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.BRICK_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.COBBLESTONE_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.DARK_OAK_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.DARK_PRISMARINE_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.JUNGLE_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.NETHER_BRICK_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.OAK_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.PETRIFIED_OAK_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.PRISMARINE_BRICK_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.PRISMARINE_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.PURPUR_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.QUARTZ_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.RED_SANDSTONE_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.SANDSTONE_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.CUT_RED_SANDSTONE_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.CUT_SANDSTONE_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.SPRUCE_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.STONE_BRICK_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.STONE_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.SMOOTH_STONE_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.POLISHED_GRANITE_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.SMOOTH_RED_SANDSTONE_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.MOSSY_STONE_BRICK_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.POLISHED_DIORITE_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.MOSSY_COBBLESTONE_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.END_STONE_BRICK_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.SMOOTH_SANDSTONE_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.SMOOTH_QUARTZ_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.GRANITE_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.ANDESITE_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.RED_NETHER_BRICK_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.POLISHED_ANDESITE_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.DIORITE_SLAB, BlockLootTables::droppingSlab);
      this.registerLootTable(Blocks.ACACIA_DOOR, (p_218483_0_) -> {
         return droppingWhen(p_218483_0_, DoorBlock.HALF, DoubleBlockHalf.LOWER);
      });
      this.registerLootTable(Blocks.BIRCH_DOOR, (p_218528_0_) -> {
         return droppingWhen(p_218528_0_, DoorBlock.HALF, DoubleBlockHalf.LOWER);
      });
      this.registerLootTable(Blocks.DARK_OAK_DOOR, (p_218468_0_) -> {
         return droppingWhen(p_218468_0_, DoorBlock.HALF, DoubleBlockHalf.LOWER);
      });
      this.registerLootTable(Blocks.IRON_DOOR, (p_218510_0_) -> {
         return droppingWhen(p_218510_0_, DoorBlock.HALF, DoubleBlockHalf.LOWER);
      });
      this.registerLootTable(Blocks.JUNGLE_DOOR, (p_218498_0_) -> {
         return droppingWhen(p_218498_0_, DoorBlock.HALF, DoubleBlockHalf.LOWER);
      });
      this.registerLootTable(Blocks.OAK_DOOR, (p_218480_0_) -> {
         return droppingWhen(p_218480_0_, DoorBlock.HALF, DoubleBlockHalf.LOWER);
      });
      this.registerLootTable(Blocks.SPRUCE_DOOR, (p_218527_0_) -> {
         return droppingWhen(p_218527_0_, DoorBlock.HALF, DoubleBlockHalf.LOWER);
      });
      this.registerLootTable(Blocks.BLACK_BED, (p_218567_0_) -> {
         return droppingWhen(p_218567_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.registerLootTable(Blocks.BLUE_BED, (p_218555_0_) -> {
         return droppingWhen(p_218555_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.registerLootTable(Blocks.BROWN_BED, (p_218543_0_) -> {
         return droppingWhen(p_218543_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.registerLootTable(Blocks.CYAN_BED, (p_218479_0_) -> {
         return droppingWhen(p_218479_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.registerLootTable(Blocks.GRAY_BED, (p_218521_0_) -> {
         return droppingWhen(p_218521_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.registerLootTable(Blocks.GREEN_BED, (p_218470_0_) -> {
         return droppingWhen(p_218470_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.registerLootTable(Blocks.LIGHT_BLUE_BED, (p_218536_0_) -> {
         return droppingWhen(p_218536_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.registerLootTable(Blocks.LIGHT_GRAY_BED, (p_218545_0_) -> {
         return droppingWhen(p_218545_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.registerLootTable(Blocks.LIME_BED, (p_218557_0_) -> {
         return droppingWhen(p_218557_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.registerLootTable(Blocks.MAGENTA_BED, (p_218566_0_) -> {
         return droppingWhen(p_218566_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.registerLootTable(Blocks.PURPLE_BED, (p_218520_0_) -> {
         return droppingWhen(p_218520_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.registerLootTable(Blocks.ORANGE_BED, (p_218472_0_) -> {
         return droppingWhen(p_218472_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.registerLootTable(Blocks.PINK_BED, (p_218537_0_) -> {
         return droppingWhen(p_218537_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.registerLootTable(Blocks.RED_BED, (p_218549_0_) -> {
         return droppingWhen(p_218549_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.registerLootTable(Blocks.WHITE_BED, (p_218569_0_) -> {
         return droppingWhen(p_218569_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.registerLootTable(Blocks.YELLOW_BED, (p_218517_0_) -> {
         return droppingWhen(p_218517_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.registerLootTable(Blocks.LILAC, (p_218488_0_) -> {
         return droppingWhen(p_218488_0_, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
      });
      this.registerLootTable(Blocks.SUNFLOWER, (p_218503_0_) -> {
         return droppingWhen(p_218503_0_, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
      });
      this.registerLootTable(Blocks.PEONY, (p_218497_0_) -> {
         return droppingWhen(p_218497_0_, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
      });
      this.registerLootTable(Blocks.ROSE_BUSH, (p_218504_0_) -> {
         return droppingWhen(p_218504_0_, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
      });
      this.registerLootTable(Blocks.TNT, LootTable.builder().addLootPool(withSurvivesExplosion(Blocks.TNT, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Blocks.TNT).acceptCondition(BlockStateProperty.builder(Blocks.TNT).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withBoolProp(TNTBlock.UNSTABLE, false)))))));
      this.registerLootTable(Blocks.COCOA, (p_218516_0_) -> {
         return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(withExplosionDecay(p_218516_0_, ItemLootEntry.builder(Items.COCOA_BEANS).acceptFunction(SetCount.builder(ConstantRange.of(3)).acceptCondition(BlockStateProperty.builder(p_218516_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(CocoaBlock.AGE, 2)))))));
      });
      this.registerLootTable(Blocks.SEA_PICKLE, (p_218478_0_) -> {
         return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(withExplosionDecay(Blocks.SEA_PICKLE, ItemLootEntry.builder(p_218478_0_).acceptFunction(SetCount.builder(ConstantRange.of(2)).acceptCondition(BlockStateProperty.builder(p_218478_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SeaPickleBlock.PICKLES, 2)))).acceptFunction(SetCount.builder(ConstantRange.of(3)).acceptCondition(BlockStateProperty.builder(p_218478_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SeaPickleBlock.PICKLES, 3)))).acceptFunction(SetCount.builder(ConstantRange.of(4)).acceptCondition(BlockStateProperty.builder(p_218478_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SeaPickleBlock.PICKLES, 4)))))));
      });
      this.registerLootTable(Blocks.COMPOSTER, (p_218551_0_) -> {
         return LootTable.builder().addLootPool(LootPool.builder().addEntry(withExplosionDecay(p_218551_0_, ItemLootEntry.builder(Items.COMPOSTER)))).addLootPool(LootPool.builder().addEntry(ItemLootEntry.builder(Items.BONE_MEAL)).acceptCondition(BlockStateProperty.builder(p_218551_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(ComposterBlock.LEVEL, 8))));
      });
      this.registerLootTable(Blocks.BEACON, BlockLootTables::droppingWithName);
      this.registerLootTable(Blocks.BREWING_STAND, BlockLootTables::droppingWithName);
      this.registerLootTable(Blocks.CHEST, BlockLootTables::droppingWithName);
      this.registerLootTable(Blocks.DISPENSER, BlockLootTables::droppingWithName);
      this.registerLootTable(Blocks.DROPPER, BlockLootTables::droppingWithName);
      this.registerLootTable(Blocks.ENCHANTING_TABLE, BlockLootTables::droppingWithName);
      this.registerLootTable(Blocks.FURNACE, BlockLootTables::droppingWithName);
      this.registerLootTable(Blocks.HOPPER, BlockLootTables::droppingWithName);
      this.registerLootTable(Blocks.TRAPPED_CHEST, BlockLootTables::droppingWithName);
      this.registerLootTable(Blocks.SMOKER, BlockLootTables::droppingWithName);
      this.registerLootTable(Blocks.BLAST_FURNACE, BlockLootTables::droppingWithName);
      this.registerLootTable(Blocks.BARREL, BlockLootTables::droppingWithName);
      this.registerLootTable(Blocks.CARTOGRAPHY_TABLE, BlockLootTables::droppingWithName);
      this.registerLootTable(Blocks.FLETCHING_TABLE, BlockLootTables::droppingWithName);
      this.registerLootTable(Blocks.GRINDSTONE, BlockLootTables::droppingWithName);
      this.registerLootTable(Blocks.LECTERN, BlockLootTables::droppingWithName);
      this.registerLootTable(Blocks.SMITHING_TABLE, BlockLootTables::droppingWithName);
      this.registerLootTable(Blocks.STONECUTTER, BlockLootTables::droppingWithName);
      this.registerLootTable(Blocks.BELL, BlockLootTables::dropping);
      this.registerLootTable(Blocks.LANTERN, BlockLootTables::dropping);
      this.registerLootTable(Blocks.SHULKER_BOX, BlockLootTables::droppingWithContents);
      this.registerLootTable(Blocks.BLACK_SHULKER_BOX, BlockLootTables::droppingWithContents);
      this.registerLootTable(Blocks.BLUE_SHULKER_BOX, BlockLootTables::droppingWithContents);
      this.registerLootTable(Blocks.BROWN_SHULKER_BOX, BlockLootTables::droppingWithContents);
      this.registerLootTable(Blocks.CYAN_SHULKER_BOX, BlockLootTables::droppingWithContents);
      this.registerLootTable(Blocks.GRAY_SHULKER_BOX, BlockLootTables::droppingWithContents);
      this.registerLootTable(Blocks.GREEN_SHULKER_BOX, BlockLootTables::droppingWithContents);
      this.registerLootTable(Blocks.LIGHT_BLUE_SHULKER_BOX, BlockLootTables::droppingWithContents);
      this.registerLootTable(Blocks.LIGHT_GRAY_SHULKER_BOX, BlockLootTables::droppingWithContents);
      this.registerLootTable(Blocks.LIME_SHULKER_BOX, BlockLootTables::droppingWithContents);
      this.registerLootTable(Blocks.MAGENTA_SHULKER_BOX, BlockLootTables::droppingWithContents);
      this.registerLootTable(Blocks.ORANGE_SHULKER_BOX, BlockLootTables::droppingWithContents);
      this.registerLootTable(Blocks.PINK_SHULKER_BOX, BlockLootTables::droppingWithContents);
      this.registerLootTable(Blocks.PURPLE_SHULKER_BOX, BlockLootTables::droppingWithContents);
      this.registerLootTable(Blocks.RED_SHULKER_BOX, BlockLootTables::droppingWithContents);
      this.registerLootTable(Blocks.WHITE_SHULKER_BOX, BlockLootTables::droppingWithContents);
      this.registerLootTable(Blocks.YELLOW_SHULKER_BOX, BlockLootTables::droppingWithContents);
      this.registerLootTable(Blocks.BLACK_BANNER, BlockLootTables::droppingWithPatterns);
      this.registerLootTable(Blocks.BLUE_BANNER, BlockLootTables::droppingWithPatterns);
      this.registerLootTable(Blocks.BROWN_BANNER, BlockLootTables::droppingWithPatterns);
      this.registerLootTable(Blocks.CYAN_BANNER, BlockLootTables::droppingWithPatterns);
      this.registerLootTable(Blocks.GRAY_BANNER, BlockLootTables::droppingWithPatterns);
      this.registerLootTable(Blocks.GREEN_BANNER, BlockLootTables::droppingWithPatterns);
      this.registerLootTable(Blocks.LIGHT_BLUE_BANNER, BlockLootTables::droppingWithPatterns);
      this.registerLootTable(Blocks.LIGHT_GRAY_BANNER, BlockLootTables::droppingWithPatterns);
      this.registerLootTable(Blocks.LIME_BANNER, BlockLootTables::droppingWithPatterns);
      this.registerLootTable(Blocks.MAGENTA_BANNER, BlockLootTables::droppingWithPatterns);
      this.registerLootTable(Blocks.ORANGE_BANNER, BlockLootTables::droppingWithPatterns);
      this.registerLootTable(Blocks.PINK_BANNER, BlockLootTables::droppingWithPatterns);
      this.registerLootTable(Blocks.PURPLE_BANNER, BlockLootTables::droppingWithPatterns);
      this.registerLootTable(Blocks.RED_BANNER, BlockLootTables::droppingWithPatterns);
      this.registerLootTable(Blocks.WHITE_BANNER, BlockLootTables::droppingWithPatterns);
      this.registerLootTable(Blocks.YELLOW_BANNER, BlockLootTables::droppingWithPatterns);
      this.registerLootTable(Blocks.PLAYER_HEAD, (p_218565_0_) -> {
         return LootTable.builder().addLootPool(withSurvivesExplosion(p_218565_0_, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(p_218565_0_).acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).replaceOperation("Owner", "SkullOwner")))));
      });
      this.registerLootTable(Blocks.BEE_NEST, BlockLootTables::func_229436_h_);
      this.registerLootTable(Blocks.BEEHIVE, BlockLootTables::func_229437_i_);
      this.registerLootTable(Blocks.BIRCH_LEAVES, (p_218473_0_) -> {
         return droppingWithChancesAndSticks(p_218473_0_, Blocks.BIRCH_SAPLING, DEFAULT_SAPLING_DROP_RATES);
      });
      this.registerLootTable(Blocks.ACACIA_LEAVES, (p_218518_0_) -> {
         return droppingWithChancesAndSticks(p_218518_0_, Blocks.ACACIA_SAPLING, DEFAULT_SAPLING_DROP_RATES);
      });
      this.registerLootTable(Blocks.JUNGLE_LEAVES, (p_218477_0_) -> {
         return droppingWithChancesAndSticks(p_218477_0_, Blocks.JUNGLE_SAPLING, RARE_SAPLING_DROP_RATES);
      });
      this.registerLootTable(Blocks.SPRUCE_LEAVES, (p_218500_0_) -> {
         return droppingWithChancesAndSticks(p_218500_0_, Blocks.SPRUCE_SAPLING, DEFAULT_SAPLING_DROP_RATES);
      });
      this.registerLootTable(Blocks.OAK_LEAVES, (p_218506_0_) -> {
         return droppingWithChancesSticksAndApples(p_218506_0_, Blocks.OAK_SAPLING, DEFAULT_SAPLING_DROP_RATES);
      });
      this.registerLootTable(Blocks.DARK_OAK_LEAVES, (p_218471_0_) -> {
         return droppingWithChancesSticksAndApples(p_218471_0_, Blocks.DARK_OAK_SAPLING, DEFAULT_SAPLING_DROP_RATES);
      });
      ILootCondition.IBuilder ilootcondition$ibuilder = BlockStateProperty.builder(Blocks.BEETROOTS).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(BeetrootBlock.BEETROOT_AGE, 3));
      this.registerLootTable(Blocks.BEETROOTS, droppingAndBonusWhen(Blocks.BEETROOTS, Items.BEETROOT, Items.BEETROOT_SEEDS, ilootcondition$ibuilder));
      ILootCondition.IBuilder ilootcondition$ibuilder1 = BlockStateProperty.builder(Blocks.WHEAT).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(CropsBlock.AGE, 7));
      this.registerLootTable(Blocks.WHEAT, droppingAndBonusWhen(Blocks.WHEAT, Items.WHEAT, Items.WHEAT_SEEDS, ilootcondition$ibuilder1));
      ILootCondition.IBuilder ilootcondition$ibuilder2 = BlockStateProperty.builder(Blocks.CARROTS).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(CarrotBlock.AGE, 7));
      this.registerLootTable(Blocks.CARROTS, withExplosionDecay(Blocks.CARROTS, LootTable.builder().addLootPool(LootPool.builder().addEntry(ItemLootEntry.builder(Items.CARROT))).addLootPool(LootPool.builder().acceptCondition(ilootcondition$ibuilder2).addEntry(ItemLootEntry.builder(Items.CARROT).acceptFunction(ApplyBonus.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286F, 3))))));
      ILootCondition.IBuilder ilootcondition$ibuilder3 = BlockStateProperty.builder(Blocks.POTATOES).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(PotatoBlock.AGE, 7));
      this.registerLootTable(Blocks.POTATOES, withExplosionDecay(Blocks.POTATOES, LootTable.builder().addLootPool(LootPool.builder().addEntry(ItemLootEntry.builder(Items.POTATO))).addLootPool(LootPool.builder().acceptCondition(ilootcondition$ibuilder3).addEntry(ItemLootEntry.builder(Items.POTATO).acceptFunction(ApplyBonus.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286F, 3)))).addLootPool(LootPool.builder().acceptCondition(ilootcondition$ibuilder3).addEntry(ItemLootEntry.builder(Items.POISONOUS_POTATO).acceptCondition(RandomChance.builder(0.02F))))));
      this.registerLootTable(Blocks.SWEET_BERRY_BUSH, (p_218538_0_) -> {
         return withExplosionDecay(p_218538_0_, LootTable.builder().addLootPool(LootPool.builder().acceptCondition(BlockStateProperty.builder(Blocks.SWEET_BERRY_BUSH).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SweetBerryBushBlock.AGE, 3))).addEntry(ItemLootEntry.builder(Items.SWEET_BERRIES)).acceptFunction(SetCount.builder(RandomValueRange.of(2.0F, 3.0F))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE))).addLootPool(LootPool.builder().acceptCondition(BlockStateProperty.builder(Blocks.SWEET_BERRY_BUSH).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SweetBerryBushBlock.AGE, 2))).addEntry(ItemLootEntry.builder(Items.SWEET_BERRIES)).acceptFunction(SetCount.builder(RandomValueRange.of(1.0F, 2.0F))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE))));
      });
      this.registerLootTable(Blocks.BROWN_MUSHROOM_BLOCK, (p_229434_0_) -> {
         return droppingItemRarely(p_229434_0_, Blocks.BROWN_MUSHROOM);
      });
      this.registerLootTable(Blocks.RED_MUSHROOM_BLOCK, (p_229433_0_) -> {
         return droppingItemRarely(p_229433_0_, Blocks.RED_MUSHROOM);
      });
      this.registerLootTable(Blocks.COAL_ORE, (p_229432_0_) -> {
         return droppingItemWithFortune(p_229432_0_, Items.COAL);
      });
      this.registerLootTable(Blocks.EMERALD_ORE, (p_229431_0_) -> {
         return droppingItemWithFortune(p_229431_0_, Items.EMERALD);
      });
      this.registerLootTable(Blocks.NETHER_QUARTZ_ORE, (p_218554_0_) -> {
         return droppingItemWithFortune(p_218554_0_, Items.QUARTZ);
      });
      this.registerLootTable(Blocks.DIAMOND_ORE, (p_218568_0_) -> {
         return droppingItemWithFortune(p_218568_0_, Items.DIAMOND);
      });
      this.registerLootTable(Blocks.LAPIS_ORE, (p_218548_0_) -> {
         return droppingWithSilkTouch(p_218548_0_, withExplosionDecay(p_218548_0_, ItemLootEntry.builder(Items.LAPIS_LAZULI).acceptFunction(SetCount.builder(RandomValueRange.of(4.0F, 9.0F))).acceptFunction(ApplyBonus.oreDrops(Enchantments.FORTUNE))));
      });
      this.registerLootTable(Blocks.COBWEB, (p_218487_0_) -> {
         return droppingWithSilkTouchOrShears(p_218487_0_, withSurvivesExplosion(p_218487_0_, ItemLootEntry.builder(Items.STRING)));
      });
      this.registerLootTable(Blocks.DEAD_BUSH, (p_218525_0_) -> {
         return droppingWithShears(p_218525_0_, withExplosionDecay(p_218525_0_, ItemLootEntry.builder(Items.STICK).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F)))));
      });
      this.registerLootTable(Blocks.SEAGRASS, BlockLootTables::onlyWithShears);
      this.registerLootTable(Blocks.VINE, BlockLootTables::onlyWithShears);
      this.registerLootTable(Blocks.TALL_SEAGRASS, onlyWithShears(Blocks.SEAGRASS));
      this.registerLootTable(Blocks.LARGE_FERN, (p_218572_0_) -> {
         return droppingWithShears(Blocks.FERN, ((StandaloneLootEntry.Builder)withSurvivesExplosion(p_218572_0_, ItemLootEntry.builder(Items.WHEAT_SEEDS)).acceptCondition(BlockStateProperty.builder(p_218572_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withProp(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER)))).acceptCondition(RandomChance.builder(0.125F)));
      });
      this.registerLootTable(Blocks.TALL_GRASS, droppingWithShears(Blocks.GRASS, ((StandaloneLootEntry.Builder)withSurvivesExplosion(Blocks.TALL_GRASS, ItemLootEntry.builder(Items.WHEAT_SEEDS)).acceptCondition(BlockStateProperty.builder(Blocks.TALL_GRASS).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withProp(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER)))).acceptCondition(RandomChance.builder(0.125F))));
      this.registerLootTable(Blocks.MELON_STEM, (p_218550_0_) -> {
         return droppingByAge(p_218550_0_, Items.MELON_SEEDS);
      });
      this.registerLootTable(Blocks.ATTACHED_MELON_STEM, (p_218531_0_) -> {
         return func_229435_c_(p_218531_0_, Items.MELON_SEEDS);
      });
      this.registerLootTable(Blocks.PUMPKIN_STEM, (p_218467_0_) -> {
         return droppingByAge(p_218467_0_, Items.PUMPKIN_SEEDS);
      });
      this.registerLootTable(Blocks.ATTACHED_PUMPKIN_STEM, (p_218509_0_) -> {
         return func_229435_c_(p_218509_0_, Items.PUMPKIN_SEEDS);
      });
      this.registerLootTable(Blocks.CHORUS_FLOWER, (p_218512_0_) -> {
         return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(withSurvivesExplosion(p_218512_0_, ItemLootEntry.builder(p_218512_0_)).acceptCondition(EntityHasProperty.builder(LootContext.EntityTarget.THIS))));
      });
      this.registerLootTable(Blocks.FERN, BlockLootTables::droppingSeeds);
      this.registerLootTable(Blocks.GRASS, BlockLootTables::droppingSeeds);
      this.registerLootTable(Blocks.GLOWSTONE, (p_218496_0_) -> {
         return droppingWithSilkTouch(p_218496_0_, withExplosionDecay(p_218496_0_, ItemLootEntry.builder(Items.GLOWSTONE_DUST).acceptFunction(SetCount.builder(RandomValueRange.of(2.0F, 4.0F))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE)).acceptFunction(LimitCount.func_215911_a(IntClamper.func_215843_a(1, 4)))));
      });
      this.registerLootTable(Blocks.MELON, (p_218532_0_) -> {
         return droppingWithSilkTouch(p_218532_0_, withExplosionDecay(p_218532_0_, ItemLootEntry.builder(Items.MELON_SLICE).acceptFunction(SetCount.builder(RandomValueRange.of(3.0F, 7.0F))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE)).acceptFunction(LimitCount.func_215911_a(IntClamper.func_215851_b(9)))));
      });
      this.registerLootTable(Blocks.REDSTONE_ORE, (p_218464_0_) -> {
         return droppingWithSilkTouch(p_218464_0_, withExplosionDecay(p_218464_0_, ItemLootEntry.builder(Items.REDSTONE).acceptFunction(SetCount.builder(RandomValueRange.of(4.0F, 5.0F))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE))));
      });
      this.registerLootTable(Blocks.SEA_LANTERN, (p_218571_0_) -> {
         return droppingWithSilkTouch(p_218571_0_, withExplosionDecay(p_218571_0_, ItemLootEntry.builder(Items.PRISMARINE_CRYSTALS).acceptFunction(SetCount.builder(RandomValueRange.of(2.0F, 3.0F))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE)).acceptFunction(LimitCount.func_215911_a(IntClamper.func_215843_a(1, 5)))));
      });
      this.registerLootTable(Blocks.NETHER_WART, (p_218553_0_) -> {
         return LootTable.builder().addLootPool(withExplosionDecay(p_218553_0_, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(Items.NETHER_WART).acceptFunction(SetCount.builder(RandomValueRange.of(2.0F, 4.0F)).acceptCondition(BlockStateProperty.builder(p_218553_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(NetherWartBlock.AGE, 3)))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE).acceptCondition(BlockStateProperty.builder(p_218553_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(NetherWartBlock.AGE, 3)))))));
      });
      this.registerLootTable(Blocks.SNOW, (p_218485_0_) -> {
         return LootTable.builder().addLootPool(LootPool.builder().acceptCondition(EntityHasProperty.builder(LootContext.EntityTarget.THIS)).addEntry(AlternativesLootEntry.builder(AlternativesLootEntry.builder(ItemLootEntry.builder(Items.SNOWBALL).acceptCondition(BlockStateProperty.builder(p_218485_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 1))), ((StandaloneLootEntry.Builder)ItemLootEntry.builder(Items.SNOWBALL).acceptCondition(BlockStateProperty.builder(p_218485_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 2)))).acceptFunction(SetCount.builder(ConstantRange.of(2))), ((StandaloneLootEntry.Builder)ItemLootEntry.builder(Items.SNOWBALL).acceptCondition(BlockStateProperty.builder(p_218485_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 3)))).acceptFunction(SetCount.builder(ConstantRange.of(3))), ((StandaloneLootEntry.Builder)ItemLootEntry.builder(Items.SNOWBALL).acceptCondition(BlockStateProperty.builder(p_218485_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 4)))).acceptFunction(SetCount.builder(ConstantRange.of(4))), ((StandaloneLootEntry.Builder)ItemLootEntry.builder(Items.SNOWBALL).acceptCondition(BlockStateProperty.builder(p_218485_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 5)))).acceptFunction(SetCount.builder(ConstantRange.of(5))), ((StandaloneLootEntry.Builder)ItemLootEntry.builder(Items.SNOWBALL).acceptCondition(BlockStateProperty.builder(p_218485_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 6)))).acceptFunction(SetCount.builder(ConstantRange.of(6))), ((StandaloneLootEntry.Builder)ItemLootEntry.builder(Items.SNOWBALL).acceptCondition(BlockStateProperty.builder(p_218485_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 7)))).acceptFunction(SetCount.builder(ConstantRange.of(7))), ItemLootEntry.builder(Items.SNOWBALL).acceptFunction(SetCount.builder(ConstantRange.of(8)))).acceptCondition(NO_SILK_TOUCH), AlternativesLootEntry.builder(ItemLootEntry.builder(Blocks.SNOW).acceptCondition(BlockStateProperty.builder(p_218485_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 1))), ItemLootEntry.builder(Blocks.SNOW).acceptFunction(SetCount.builder(ConstantRange.of(2))).acceptCondition(BlockStateProperty.builder(p_218485_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 2))), ItemLootEntry.builder(Blocks.SNOW).acceptFunction(SetCount.builder(ConstantRange.of(3))).acceptCondition(BlockStateProperty.builder(p_218485_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 3))), ItemLootEntry.builder(Blocks.SNOW).acceptFunction(SetCount.builder(ConstantRange.of(4))).acceptCondition(BlockStateProperty.builder(p_218485_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 4))), ItemLootEntry.builder(Blocks.SNOW).acceptFunction(SetCount.builder(ConstantRange.of(5))).acceptCondition(BlockStateProperty.builder(p_218485_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 5))), ItemLootEntry.builder(Blocks.SNOW).acceptFunction(SetCount.builder(ConstantRange.of(6))).acceptCondition(BlockStateProperty.builder(p_218485_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 6))), ItemLootEntry.builder(Blocks.SNOW).acceptFunction(SetCount.builder(ConstantRange.of(7))).acceptCondition(BlockStateProperty.builder(p_218485_0_).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(SnowBlock.LAYERS, 7))), ItemLootEntry.builder(Blocks.SNOW_BLOCK)))));
      });
      this.registerLootTable(Blocks.GRAVEL, (p_218533_0_) -> {
         return droppingWithSilkTouch(p_218533_0_, withSurvivesExplosion(p_218533_0_, ((StandaloneLootEntry.Builder)ItemLootEntry.builder(Items.FLINT).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.1F, 0.14285715F, 0.25F, 1.0F))).alternatively(ItemLootEntry.builder(p_218533_0_))));
      });
      this.registerLootTable(Blocks.CAMPFIRE, (p_218469_0_) -> {
         return droppingWithSilkTouch(p_218469_0_, withSurvivesExplosion(p_218469_0_, ItemLootEntry.builder(Items.CHARCOAL).acceptFunction(SetCount.builder(ConstantRange.of(2)))));
      });
      this.registerSilkTouch(Blocks.GLASS);
      this.registerSilkTouch(Blocks.WHITE_STAINED_GLASS);
      this.registerSilkTouch(Blocks.ORANGE_STAINED_GLASS);
      this.registerSilkTouch(Blocks.MAGENTA_STAINED_GLASS);
      this.registerSilkTouch(Blocks.LIGHT_BLUE_STAINED_GLASS);
      this.registerSilkTouch(Blocks.YELLOW_STAINED_GLASS);
      this.registerSilkTouch(Blocks.LIME_STAINED_GLASS);
      this.registerSilkTouch(Blocks.PINK_STAINED_GLASS);
      this.registerSilkTouch(Blocks.GRAY_STAINED_GLASS);
      this.registerSilkTouch(Blocks.LIGHT_GRAY_STAINED_GLASS);
      this.registerSilkTouch(Blocks.CYAN_STAINED_GLASS);
      this.registerSilkTouch(Blocks.PURPLE_STAINED_GLASS);
      this.registerSilkTouch(Blocks.BLUE_STAINED_GLASS);
      this.registerSilkTouch(Blocks.BROWN_STAINED_GLASS);
      this.registerSilkTouch(Blocks.GREEN_STAINED_GLASS);
      this.registerSilkTouch(Blocks.RED_STAINED_GLASS);
      this.registerSilkTouch(Blocks.BLACK_STAINED_GLASS);
      this.registerSilkTouch(Blocks.GLASS_PANE);
      this.registerSilkTouch(Blocks.WHITE_STAINED_GLASS_PANE);
      this.registerSilkTouch(Blocks.ORANGE_STAINED_GLASS_PANE);
      this.registerSilkTouch(Blocks.MAGENTA_STAINED_GLASS_PANE);
      this.registerSilkTouch(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
      this.registerSilkTouch(Blocks.YELLOW_STAINED_GLASS_PANE);
      this.registerSilkTouch(Blocks.LIME_STAINED_GLASS_PANE);
      this.registerSilkTouch(Blocks.PINK_STAINED_GLASS_PANE);
      this.registerSilkTouch(Blocks.GRAY_STAINED_GLASS_PANE);
      this.registerSilkTouch(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
      this.registerSilkTouch(Blocks.CYAN_STAINED_GLASS_PANE);
      this.registerSilkTouch(Blocks.PURPLE_STAINED_GLASS_PANE);
      this.registerSilkTouch(Blocks.BLUE_STAINED_GLASS_PANE);
      this.registerSilkTouch(Blocks.BROWN_STAINED_GLASS_PANE);
      this.registerSilkTouch(Blocks.GREEN_STAINED_GLASS_PANE);
      this.registerSilkTouch(Blocks.RED_STAINED_GLASS_PANE);
      this.registerSilkTouch(Blocks.BLACK_STAINED_GLASS_PANE);
      this.registerSilkTouch(Blocks.ICE);
      this.registerSilkTouch(Blocks.PACKED_ICE);
      this.registerSilkTouch(Blocks.BLUE_ICE);
      this.registerSilkTouch(Blocks.TURTLE_EGG);
      this.registerSilkTouch(Blocks.MUSHROOM_STEM);
      this.registerSilkTouch(Blocks.DEAD_TUBE_CORAL);
      this.registerSilkTouch(Blocks.DEAD_BRAIN_CORAL);
      this.registerSilkTouch(Blocks.DEAD_BUBBLE_CORAL);
      this.registerSilkTouch(Blocks.DEAD_FIRE_CORAL);
      this.registerSilkTouch(Blocks.DEAD_HORN_CORAL);
      this.registerSilkTouch(Blocks.TUBE_CORAL);
      this.registerSilkTouch(Blocks.BRAIN_CORAL);
      this.registerSilkTouch(Blocks.BUBBLE_CORAL);
      this.registerSilkTouch(Blocks.FIRE_CORAL);
      this.registerSilkTouch(Blocks.HORN_CORAL);
      this.registerSilkTouch(Blocks.DEAD_TUBE_CORAL_FAN);
      this.registerSilkTouch(Blocks.DEAD_BRAIN_CORAL_FAN);
      this.registerSilkTouch(Blocks.DEAD_BUBBLE_CORAL_FAN);
      this.registerSilkTouch(Blocks.DEAD_FIRE_CORAL_FAN);
      this.registerSilkTouch(Blocks.DEAD_HORN_CORAL_FAN);
      this.registerSilkTouch(Blocks.TUBE_CORAL_FAN);
      this.registerSilkTouch(Blocks.BRAIN_CORAL_FAN);
      this.registerSilkTouch(Blocks.BUBBLE_CORAL_FAN);
      this.registerSilkTouch(Blocks.FIRE_CORAL_FAN);
      this.registerSilkTouch(Blocks.HORN_CORAL_FAN);
      this.registerSilkTouch(Blocks.INFESTED_STONE, Blocks.STONE);
      this.registerSilkTouch(Blocks.INFESTED_COBBLESTONE, Blocks.COBBLESTONE);
      this.registerSilkTouch(Blocks.INFESTED_STONE_BRICKS, Blocks.STONE_BRICKS);
      this.registerSilkTouch(Blocks.INFESTED_MOSSY_STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS);
      this.registerSilkTouch(Blocks.INFESTED_CRACKED_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS);
      this.registerSilkTouch(Blocks.INFESTED_CHISELED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS);
      this.registerLootTable(Blocks.CAKE, func_218482_a());
      this.registerLootTable(Blocks.FROSTED_ICE, func_218482_a());
      this.registerLootTable(Blocks.SPAWNER, func_218482_a());
   }

   public void accept(BiConsumer<ResourceLocation, LootTable.Builder> p_accept_1_) {
      this.addTables();
      Set<ResourceLocation> set = Sets.newHashSet();

      for(Block block : getKnownBlocks()) {
         ResourceLocation resourcelocation = block.getLootTable();
         if (resourcelocation != LootTables.EMPTY && set.add(resourcelocation)) {
            LootTable.Builder loottable$builder = this.lootTables.remove(resourcelocation);
            if (loottable$builder == null) {
               throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", resourcelocation, Registry.BLOCK.getKey(block)));
            }

            p_accept_1_.accept(resourcelocation, loottable$builder);
         }
      }

      if (!this.lootTables.isEmpty()) {
         throw new IllegalStateException("Created block loot tables for non-blocks: " + this.lootTables.keySet());
      }
   }

   protected Iterable<Block> getKnownBlocks() {
       return Registry.BLOCK;
   }

   public void registerFlowerPot(Block flowerPot) {
      this.registerLootTable(flowerPot, (p_229438_0_) -> {
         return droppingAndFlowerPot(((FlowerPotBlock)p_229438_0_).func_220276_d());
      });
   }

   public void registerSilkTouch(Block blockIn, Block silkTouchDrop) {
      this.registerLootTable(blockIn, onlyWithSilkTouch(silkTouchDrop));
   }

   public void registerDropping(Block blockIn, IItemProvider drop) {
      this.registerLootTable(blockIn, dropping(drop));
   }

   public void registerSilkTouch(Block blockIn) {
      this.registerSilkTouch(blockIn, blockIn);
   }

   public void registerDropSelfLootTable(Block p_218492_1_) {
      this.registerDropping(p_218492_1_, p_218492_1_);
   }

   protected void registerLootTable(Block blockIn, Function<Block, LootTable.Builder> factory) {
      this.registerLootTable(blockIn, factory.apply(blockIn));
   }

   protected void registerLootTable(Block blockIn, LootTable.Builder table) {
      this.lootTables.put(blockIn.getLootTable(), table);
   }
}