package net.minecraft.data.advancements;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.BeeNestDestroyedTrigger;
import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.advancements.criterion.BredAnimalsTrigger;
import net.minecraft.advancements.criterion.ConsumeItemTrigger;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.FilledBucketTrigger;
import net.minecraft.advancements.criterion.FishingRodHookedTrigger;
import net.minecraft.advancements.criterion.ItemDurabilityTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.PlacedBlockTrigger;
import net.minecraft.advancements.criterion.RightClickBlockWithItemTrigger;
import net.minecraft.advancements.criterion.TameAnimalTrigger;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

public class HusbandryAdvancements implements Consumer<Consumer<Advancement>> {
   private static final EntityType<?>[] BREEDABLE_ANIMALS = new EntityType[]{EntityType.HORSE, EntityType.SHEEP, EntityType.COW, EntityType.MOOSHROOM, EntityType.PIG, EntityType.CHICKEN, EntityType.WOLF, EntityType.OCELOT, EntityType.RABBIT, EntityType.LLAMA, EntityType.TURTLE, EntityType.CAT, EntityType.PANDA, EntityType.FOX, EntityType.BEE};
   private static final Item[] FISH_ITEMS = new Item[]{Items.COD, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.SALMON};
   private static final Item[] FISH_BUCKETS = new Item[]{Items.COD_BUCKET, Items.TROPICAL_FISH_BUCKET, Items.PUFFERFISH_BUCKET, Items.SALMON_BUCKET};
   private static final Item[] BALANCED_DIET = new Item[]{Items.APPLE, Items.MUSHROOM_STEW, Items.BREAD, Items.PORKCHOP, Items.COOKED_PORKCHOP, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.COOKED_COD, Items.COOKED_SALMON, Items.COOKIE, Items.MELON_SLICE, Items.BEEF, Items.COOKED_BEEF, Items.CHICKEN, Items.COOKED_CHICKEN, Items.ROTTEN_FLESH, Items.SPIDER_EYE, Items.CARROT, Items.POTATO, Items.BAKED_POTATO, Items.POISONOUS_POTATO, Items.GOLDEN_CARROT, Items.PUMPKIN_PIE, Items.RABBIT, Items.COOKED_RABBIT, Items.RABBIT_STEW, Items.MUTTON, Items.COOKED_MUTTON, Items.CHORUS_FRUIT, Items.BEETROOT, Items.BEETROOT_SOUP, Items.DRIED_KELP, Items.SUSPICIOUS_STEW, Items.SWEET_BERRIES, Items.HONEY_BOTTLE};

   public void accept(Consumer<Advancement> p_accept_1_) {
      Advancement advancement = Advancement.Builder.builder().withDisplay(Blocks.HAY_BLOCK, new TranslationTextComponent("advancements.husbandry.root.title"), new TranslationTextComponent("advancements.husbandry.root.description"), new ResourceLocation("textures/gui/advancements/backgrounds/husbandry.png"), FrameType.TASK, false, false, false).withCriterion("consumed_item", ConsumeItemTrigger.Instance.any()).register(p_accept_1_, "husbandry/root");
      Advancement advancement1 = Advancement.Builder.builder().withParent(advancement).withDisplay(Items.WHEAT, new TranslationTextComponent("advancements.husbandry.plant_seed.title"), new TranslationTextComponent("advancements.husbandry.plant_seed.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).withRequirementsStrategy(IRequirementsStrategy.OR).withCriterion("wheat", PlacedBlockTrigger.Instance.placedBlock(Blocks.WHEAT)).withCriterion("pumpkin_stem", PlacedBlockTrigger.Instance.placedBlock(Blocks.PUMPKIN_STEM)).withCriterion("melon_stem", PlacedBlockTrigger.Instance.placedBlock(Blocks.MELON_STEM)).withCriterion("beetroots", PlacedBlockTrigger.Instance.placedBlock(Blocks.BEETROOTS)).withCriterion("nether_wart", PlacedBlockTrigger.Instance.placedBlock(Blocks.NETHER_WART)).register(p_accept_1_, "husbandry/plant_seed");
      Advancement advancement2 = Advancement.Builder.builder().withParent(advancement).withDisplay(Items.WHEAT, new TranslationTextComponent("advancements.husbandry.breed_an_animal.title"), new TranslationTextComponent("advancements.husbandry.breed_an_animal.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).withRequirementsStrategy(IRequirementsStrategy.OR).withCriterion("bred", BredAnimalsTrigger.Instance.any()).register(p_accept_1_, "husbandry/breed_an_animal");
      Advancement advancement3 = this.makeBalancedDiet(Advancement.Builder.builder()).withParent(advancement1).withDisplay(Items.APPLE, new TranslationTextComponent("advancements.husbandry.balanced_diet.title"), new TranslationTextComponent("advancements.husbandry.balanced_diet.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).withRewards(AdvancementRewards.Builder.experience(100)).register(p_accept_1_, "husbandry/balanced_diet");
      Advancement advancement4 = Advancement.Builder.builder().withParent(advancement1).withDisplay(Items.DIAMOND_HOE, new TranslationTextComponent("advancements.husbandry.break_diamond_hoe.title"), new TranslationTextComponent("advancements.husbandry.break_diamond_hoe.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).withRewards(AdvancementRewards.Builder.experience(100)).withCriterion("broke_hoe", ItemDurabilityTrigger.Instance.forItemDamage(ItemPredicate.Builder.create().item(Items.DIAMOND_HOE).build(), MinMaxBounds.IntBound.exactly(0))).register(p_accept_1_, "husbandry/break_diamond_hoe");
      Advancement advancement5 = Advancement.Builder.builder().withParent(advancement).withDisplay(Items.LEAD, new TranslationTextComponent("advancements.husbandry.tame_an_animal.title"), new TranslationTextComponent("advancements.husbandry.tame_an_animal.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("tamed_animal", TameAnimalTrigger.Instance.any()).register(p_accept_1_, "husbandry/tame_an_animal");
      Advancement advancement6 = this.makeBredAllAnimals(Advancement.Builder.builder()).withParent(advancement2).withDisplay(Items.GOLDEN_CARROT, new TranslationTextComponent("advancements.husbandry.breed_all_animals.title"), new TranslationTextComponent("advancements.husbandry.breed_all_animals.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).withRewards(AdvancementRewards.Builder.experience(100)).register(p_accept_1_, "husbandry/bred_all_animals");
      Advancement advancement7 = this.makeFish(Advancement.Builder.builder()).withParent(advancement).withRequirementsStrategy(IRequirementsStrategy.OR).withDisplay(Items.FISHING_ROD, new TranslationTextComponent("advancements.husbandry.fishy_business.title"), new TranslationTextComponent("advancements.husbandry.fishy_business.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).register(p_accept_1_, "husbandry/fishy_business");
      Advancement advancement8 = this.makeFishBucket(Advancement.Builder.builder()).withParent(advancement7).withRequirementsStrategy(IRequirementsStrategy.OR).withDisplay(Items.PUFFERFISH_BUCKET, new TranslationTextComponent("advancements.husbandry.tactical_fishing.title"), new TranslationTextComponent("advancements.husbandry.tactical_fishing.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).register(p_accept_1_, "husbandry/tactical_fishing");
      Advancement advancement9 = this.func_218460_e(Advancement.Builder.builder()).withParent(advancement5).withDisplay(Items.COD, new TranslationTextComponent("advancements.husbandry.complete_catalogue.title"), new TranslationTextComponent("advancements.husbandry.complete_catalogue.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).withRewards(AdvancementRewards.Builder.experience(50)).register(p_accept_1_, "husbandry/complete_catalogue");
      Advancement advancement10 = Advancement.Builder.builder().withParent(advancement).withCriterion("safely_harvest_honey", RightClickBlockWithItemTrigger.Instance.func_226699_a_(BlockPredicate.Builder.func_226243_a_().func_226244_a_(BlockTags.BEEHIVES), ItemPredicate.Builder.create().item(Items.GLASS_BOTTLE))).withDisplay(Items.HONEY_BOTTLE, new TranslationTextComponent("advancements.husbandry.safely_harvest_honey.title"), new TranslationTextComponent("advancements.husbandry.safely_harvest_honey.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).register(p_accept_1_, "husbandry/safely_harvest_honey");
      Advancement advancement11 = Advancement.Builder.builder().withParent(advancement).withCriterion("silk_touch_nest", BeeNestDestroyedTrigger.Instance.func_226229_a_(Blocks.BEE_NEST, ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))), MinMaxBounds.IntBound.exactly(3))).withDisplay(Blocks.BEE_NEST, new TranslationTextComponent("advancements.husbandry.silk_touch_nest.title"), new TranslationTextComponent("advancements.husbandry.silk_touch_nest.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).register(p_accept_1_, "husbandry/silk_touch_nest");
   }

   /**
    * Adds all the items in {@link #BALANCED_DIET} to the given advancement's criteria
    */
   private Advancement.Builder makeBalancedDiet(Advancement.Builder builder) {
      for(Item item : BALANCED_DIET) {
         builder.withCriterion(Registry.ITEM.getKey(item).getPath(), ConsumeItemTrigger.Instance.forItem(item));
      }

      return builder;
   }

   private Advancement.Builder makeBredAllAnimals(Advancement.Builder builder) {
      for(EntityType<?> entitytype : BREEDABLE_ANIMALS) {
         builder.withCriterion(EntityType.getKey(entitytype).toString(), BredAnimalsTrigger.Instance.forParent(EntityPredicate.Builder.create().type(entitytype)));
      }

      return builder;
   }

   private Advancement.Builder makeFishBucket(Advancement.Builder builder) {
      for(Item item : FISH_BUCKETS) {
         builder.withCriterion(Registry.ITEM.getKey(item).getPath(), FilledBucketTrigger.Instance.forItem(ItemPredicate.Builder.create().item(item).build()));
      }

      return builder;
   }

   private Advancement.Builder makeFish(Advancement.Builder builder) {
      for(Item item : FISH_ITEMS) {
         builder.withCriterion(Registry.ITEM.getKey(item).getPath(), FishingRodHookedTrigger.Instance.create(ItemPredicate.ANY, EntityPredicate.ANY, ItemPredicate.Builder.create().item(item).build()));
      }

      return builder;
   }

   private Advancement.Builder func_218460_e(Advancement.Builder p_218460_1_) {
      CatEntity.TEXTURE_BY_ID.forEach((p_218461_1_, p_218461_2_) -> {
         p_218460_1_.withCriterion(p_218461_2_.getPath(), TameAnimalTrigger.Instance.create(EntityPredicate.Builder.create().func_217986_a(p_218461_2_).build()));
      });
      return p_218460_1_;
   }
}