package net.minecraft.tags;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public class BlockTags {
   private static TagCollection<Block> collection = new TagCollection<>((p_203641_0_) -> {
      return Optional.empty();
   }, "", false, "");
   private static int generation;
   public static final Tag<Block> WOOL = makeWrapperTag("wool");
   public static final Tag<Block> PLANKS = makeWrapperTag("planks");
   public static final Tag<Block> STONE_BRICKS = makeWrapperTag("stone_bricks");
   public static final Tag<Block> WOODEN_BUTTONS = makeWrapperTag("wooden_buttons");
   public static final Tag<Block> BUTTONS = makeWrapperTag("buttons");
   public static final Tag<Block> CARPETS = makeWrapperTag("carpets");
   public static final Tag<Block> WOODEN_DOORS = makeWrapperTag("wooden_doors");
   public static final Tag<Block> WOODEN_STAIRS = makeWrapperTag("wooden_stairs");
   public static final Tag<Block> WOODEN_SLABS = makeWrapperTag("wooden_slabs");
   public static final Tag<Block> WOODEN_FENCES = makeWrapperTag("wooden_fences");
   public static final Tag<Block> WOODEN_PRESSURE_PLATES = makeWrapperTag("wooden_pressure_plates");
   public static final Tag<Block> WOODEN_TRAPDOORS = makeWrapperTag("wooden_trapdoors");
   public static final Tag<Block> DOORS = makeWrapperTag("doors");
   public static final Tag<Block> SAPLINGS = makeWrapperTag("saplings");
   public static final Tag<Block> LOGS = makeWrapperTag("logs");
   public static final Tag<Block> DARK_OAK_LOGS = makeWrapperTag("dark_oak_logs");
   public static final Tag<Block> OAK_LOGS = makeWrapperTag("oak_logs");
   public static final Tag<Block> BIRCH_LOGS = makeWrapperTag("birch_logs");
   public static final Tag<Block> ACACIA_LOGS = makeWrapperTag("acacia_logs");
   public static final Tag<Block> JUNGLE_LOGS = makeWrapperTag("jungle_logs");
   public static final Tag<Block> SPRUCE_LOGS = makeWrapperTag("spruce_logs");
   public static final Tag<Block> BANNERS = makeWrapperTag("banners");
   public static final Tag<Block> SAND = makeWrapperTag("sand");
   public static final Tag<Block> STAIRS = makeWrapperTag("stairs");
   public static final Tag<Block> SLABS = makeWrapperTag("slabs");
   public static final Tag<Block> WALLS = makeWrapperTag("walls");
   public static final Tag<Block> ANVIL = makeWrapperTag("anvil");
   public static final Tag<Block> RAILS = makeWrapperTag("rails");
   public static final Tag<Block> LEAVES = makeWrapperTag("leaves");
   public static final Tag<Block> TRAPDOORS = makeWrapperTag("trapdoors");
   public static final Tag<Block> SMALL_FLOWERS = makeWrapperTag("small_flowers");
   public static final Tag<Block> BEDS = makeWrapperTag("beds");
   public static final Tag<Block> FENCES = makeWrapperTag("fences");
   public static final Tag<Block> TALL_FLOWERS = makeWrapperTag("tall_flowers");
   public static final Tag<Block> FLOWERS = makeWrapperTag("flowers");
   public static final Tag<Block> SHULKER_BOXES = makeWrapperTag("shulker_boxes");
   public static final Tag<Block> FLOWER_POTS = makeWrapperTag("flower_pots");
   public static final Tag<Block> ENDERMAN_HOLDABLE = makeWrapperTag("enderman_holdable");
   public static final Tag<Block> ICE = makeWrapperTag("ice");
   public static final Tag<Block> VALID_SPAWN = makeWrapperTag("valid_spawn");
   public static final Tag<Block> IMPERMEABLE = makeWrapperTag("impermeable");
   public static final Tag<Block> UNDERWATER_BONEMEALS = makeWrapperTag("underwater_bonemeals");
   public static final Tag<Block> CORAL_BLOCKS = makeWrapperTag("coral_blocks");
   public static final Tag<Block> WALL_CORALS = makeWrapperTag("wall_corals");
   public static final Tag<Block> CORAL_PLANTS = makeWrapperTag("coral_plants");
   public static final Tag<Block> CORALS = makeWrapperTag("corals");
   public static final Tag<Block> BAMBOO_PLANTABLE_ON = makeWrapperTag("bamboo_plantable_on");
   public static final Tag<Block> STANDING_SIGNS = makeWrapperTag("standing_signs");
   public static final Tag<Block> WALL_SIGNS = makeWrapperTag("wall_signs");
   public static final Tag<Block> SIGNS = makeWrapperTag("signs");
   public static final Tag<Block> DRAGON_IMMUNE = makeWrapperTag("dragon_immune");
   public static final Tag<Block> WITHER_IMMUNE = makeWrapperTag("wither_immune");
   public static final Tag<Block> BEEHIVES = makeWrapperTag("beehives");
   public static final Tag<Block> CROPS = makeWrapperTag("crops");
   public static final Tag<Block> BEE_GROWABLES = makeWrapperTag("bee_growables");
   public static final Tag<Block> PORTALS = makeWrapperTag("portals");

   public static void setCollection(TagCollection<Block> collectionIn) {
      collection = collectionIn;
      ++generation;
   }

   public static TagCollection<Block> getCollection() {
      return collection;
   }

   public static int getGeneration() {
      return generation;
   }

   private static Tag<Block> makeWrapperTag(String id) {
      return new BlockTags.Wrapper(new ResourceLocation(id));
   }

   public static class Wrapper extends Tag<Block> {
      private int lastKnownGeneration = -1;
      private Tag<Block> cachedTag;

      public Wrapper(ResourceLocation resourceLocationIn) {
         super(resourceLocationIn);
      }

      /**
       * Returns true if this set contains the specified element.
       */
      public boolean contains(Block itemIn) {
         if (this.lastKnownGeneration != BlockTags.generation) {
            this.cachedTag = BlockTags.collection.getOrCreate(this.getId());
            this.lastKnownGeneration = BlockTags.generation;
         }

         return this.cachedTag.contains(itemIn);
      }

      public Collection<Block> getAllElements() {
         if (this.lastKnownGeneration != BlockTags.generation) {
            this.cachedTag = BlockTags.collection.getOrCreate(this.getId());
            this.lastKnownGeneration = BlockTags.generation;
         }

         return this.cachedTag.getAllElements();
      }

      public Collection<Tag.ITagEntry<Block>> getEntries() {
         if (this.lastKnownGeneration != BlockTags.generation) {
            this.cachedTag = BlockTags.collection.getOrCreate(this.getId());
            this.lastKnownGeneration = BlockTags.generation;
         }

         return this.cachedTag.getEntries();
      }
   }
}