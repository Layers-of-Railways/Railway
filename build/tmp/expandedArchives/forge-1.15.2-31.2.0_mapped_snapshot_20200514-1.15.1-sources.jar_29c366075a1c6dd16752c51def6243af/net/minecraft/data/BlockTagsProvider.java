package net.minecraft.data;

import java.nio.file.Path;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class BlockTagsProvider extends TagsProvider<Block> {
   public BlockTagsProvider(DataGenerator generatorIn) {
      super(generatorIn, Registry.BLOCK);
   }

   protected void registerTags() {
      this.getBuilder(BlockTags.WOOL).add(Blocks.WHITE_WOOL, Blocks.ORANGE_WOOL, Blocks.MAGENTA_WOOL, Blocks.LIGHT_BLUE_WOOL, Blocks.YELLOW_WOOL, Blocks.LIME_WOOL, Blocks.PINK_WOOL, Blocks.GRAY_WOOL, Blocks.LIGHT_GRAY_WOOL, Blocks.CYAN_WOOL, Blocks.PURPLE_WOOL, Blocks.BLUE_WOOL, Blocks.BROWN_WOOL, Blocks.GREEN_WOOL, Blocks.RED_WOOL, Blocks.BLACK_WOOL);
      this.getBuilder(BlockTags.PLANKS).add(Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS);
      this.getBuilder(BlockTags.STONE_BRICKS).add(Blocks.STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS);
      this.getBuilder(BlockTags.WOODEN_BUTTONS).add(Blocks.OAK_BUTTON, Blocks.SPRUCE_BUTTON, Blocks.BIRCH_BUTTON, Blocks.JUNGLE_BUTTON, Blocks.ACACIA_BUTTON, Blocks.DARK_OAK_BUTTON);
      this.getBuilder(BlockTags.BUTTONS).add(BlockTags.WOODEN_BUTTONS).add(Blocks.STONE_BUTTON);
      this.getBuilder(BlockTags.CARPETS).add(Blocks.WHITE_CARPET, Blocks.ORANGE_CARPET, Blocks.MAGENTA_CARPET, Blocks.LIGHT_BLUE_CARPET, Blocks.YELLOW_CARPET, Blocks.LIME_CARPET, Blocks.PINK_CARPET, Blocks.GRAY_CARPET, Blocks.LIGHT_GRAY_CARPET, Blocks.CYAN_CARPET, Blocks.PURPLE_CARPET, Blocks.BLUE_CARPET, Blocks.BROWN_CARPET, Blocks.GREEN_CARPET, Blocks.RED_CARPET, Blocks.BLACK_CARPET);
      this.getBuilder(BlockTags.WOODEN_DOORS).add(Blocks.OAK_DOOR, Blocks.SPRUCE_DOOR, Blocks.BIRCH_DOOR, Blocks.JUNGLE_DOOR, Blocks.ACACIA_DOOR, Blocks.DARK_OAK_DOOR);
      this.getBuilder(BlockTags.WOODEN_STAIRS).add(Blocks.OAK_STAIRS, Blocks.SPRUCE_STAIRS, Blocks.BIRCH_STAIRS, Blocks.JUNGLE_STAIRS, Blocks.ACACIA_STAIRS, Blocks.DARK_OAK_STAIRS);
      this.getBuilder(BlockTags.WOODEN_SLABS).add(Blocks.OAK_SLAB, Blocks.SPRUCE_SLAB, Blocks.BIRCH_SLAB, Blocks.JUNGLE_SLAB, Blocks.ACACIA_SLAB, Blocks.DARK_OAK_SLAB);
      this.getBuilder(BlockTags.WOODEN_FENCES).add(Blocks.OAK_FENCE, Blocks.ACACIA_FENCE, Blocks.DARK_OAK_FENCE, Blocks.SPRUCE_FENCE, Blocks.BIRCH_FENCE, Blocks.JUNGLE_FENCE);
      this.getBuilder(BlockTags.DOORS).add(BlockTags.WOODEN_DOORS).add(Blocks.IRON_DOOR);
      this.getBuilder(BlockTags.SAPLINGS).add(Blocks.OAK_SAPLING, Blocks.SPRUCE_SAPLING, Blocks.BIRCH_SAPLING, Blocks.JUNGLE_SAPLING, Blocks.ACACIA_SAPLING, Blocks.DARK_OAK_SAPLING);
      this.getBuilder(BlockTags.DARK_OAK_LOGS).add(Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_WOOD);
      this.getBuilder(BlockTags.OAK_LOGS).add(Blocks.OAK_LOG, Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_LOG, Blocks.STRIPPED_OAK_WOOD);
      this.getBuilder(BlockTags.ACACIA_LOGS).add(Blocks.ACACIA_LOG, Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_LOG, Blocks.STRIPPED_ACACIA_WOOD);
      this.getBuilder(BlockTags.BIRCH_LOGS).add(Blocks.BIRCH_LOG, Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_LOG, Blocks.STRIPPED_BIRCH_WOOD);
      this.getBuilder(BlockTags.JUNGLE_LOGS).add(Blocks.JUNGLE_LOG, Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_WOOD);
      this.getBuilder(BlockTags.SPRUCE_LOGS).add(Blocks.SPRUCE_LOG, Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_WOOD);
      this.getBuilder(BlockTags.LOGS).add(BlockTags.DARK_OAK_LOGS).add(BlockTags.OAK_LOGS).add(BlockTags.ACACIA_LOGS).add(BlockTags.BIRCH_LOGS).add(BlockTags.JUNGLE_LOGS).add(BlockTags.SPRUCE_LOGS);
      this.getBuilder(BlockTags.ANVIL).add(Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL);
      this.getBuilder(BlockTags.SMALL_FLOWERS).add(Blocks.DANDELION, Blocks.POPPY, Blocks.BLUE_ORCHID, Blocks.ALLIUM, Blocks.AZURE_BLUET, Blocks.RED_TULIP, Blocks.ORANGE_TULIP, Blocks.WHITE_TULIP, Blocks.PINK_TULIP, Blocks.OXEYE_DAISY, Blocks.CORNFLOWER, Blocks.LILY_OF_THE_VALLEY, Blocks.WITHER_ROSE);
      this.getBuilder(BlockTags.ENDERMAN_HOLDABLE).add(BlockTags.SMALL_FLOWERS).add(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.SAND, Blocks.RED_SAND, Blocks.GRAVEL, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Blocks.TNT, Blocks.CACTUS, Blocks.CLAY, Blocks.PUMPKIN, Blocks.CARVED_PUMPKIN, Blocks.MELON, Blocks.MYCELIUM, Blocks.NETHERRACK);
      this.getBuilder(BlockTags.FLOWER_POTS).add(Blocks.FLOWER_POT, Blocks.POTTED_POPPY, Blocks.POTTED_BLUE_ORCHID, Blocks.POTTED_ALLIUM, Blocks.POTTED_AZURE_BLUET, Blocks.POTTED_RED_TULIP, Blocks.POTTED_ORANGE_TULIP, Blocks.POTTED_WHITE_TULIP, Blocks.POTTED_PINK_TULIP, Blocks.POTTED_OXEYE_DAISY, Blocks.POTTED_DANDELION, Blocks.POTTED_OAK_SAPLING, Blocks.POTTED_SPRUCE_SAPLING, Blocks.POTTED_BIRCH_SAPLING, Blocks.POTTED_JUNGLE_SAPLING, Blocks.POTTED_ACACIA_SAPLING, Blocks.POTTED_DARK_OAK_SAPLING, Blocks.POTTED_RED_MUSHROOM, Blocks.POTTED_BROWN_MUSHROOM, Blocks.POTTED_DEAD_BUSH, Blocks.POTTED_FERN, Blocks.POTTED_CACTUS, Blocks.POTTED_CORNFLOWER, Blocks.POTTED_LILY_OF_THE_VALLEY, Blocks.POTTED_WITHER_ROSE, Blocks.POTTED_BAMBOO);
      this.getBuilder(BlockTags.BANNERS).add(Blocks.WHITE_BANNER, Blocks.ORANGE_BANNER, Blocks.MAGENTA_BANNER, Blocks.LIGHT_BLUE_BANNER, Blocks.YELLOW_BANNER, Blocks.LIME_BANNER, Blocks.PINK_BANNER, Blocks.GRAY_BANNER, Blocks.LIGHT_GRAY_BANNER, Blocks.CYAN_BANNER, Blocks.PURPLE_BANNER, Blocks.BLUE_BANNER, Blocks.BROWN_BANNER, Blocks.GREEN_BANNER, Blocks.RED_BANNER, Blocks.BLACK_BANNER, Blocks.WHITE_WALL_BANNER, Blocks.ORANGE_WALL_BANNER, Blocks.MAGENTA_WALL_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, Blocks.YELLOW_WALL_BANNER, Blocks.LIME_WALL_BANNER, Blocks.PINK_WALL_BANNER, Blocks.GRAY_WALL_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, Blocks.CYAN_WALL_BANNER, Blocks.PURPLE_WALL_BANNER, Blocks.BLUE_WALL_BANNER, Blocks.BROWN_WALL_BANNER, Blocks.GREEN_WALL_BANNER, Blocks.RED_WALL_BANNER, Blocks.BLACK_WALL_BANNER);
      this.getBuilder(BlockTags.WOODEN_PRESSURE_PLATES).add(Blocks.OAK_PRESSURE_PLATE, Blocks.SPRUCE_PRESSURE_PLATE, Blocks.BIRCH_PRESSURE_PLATE, Blocks.JUNGLE_PRESSURE_PLATE, Blocks.ACACIA_PRESSURE_PLATE, Blocks.DARK_OAK_PRESSURE_PLATE);
      this.getBuilder(BlockTags.STAIRS).add(Blocks.OAK_STAIRS, Blocks.COBBLESTONE_STAIRS, Blocks.SPRUCE_STAIRS, Blocks.SANDSTONE_STAIRS, Blocks.ACACIA_STAIRS, Blocks.JUNGLE_STAIRS, Blocks.BIRCH_STAIRS, Blocks.DARK_OAK_STAIRS, Blocks.NETHER_BRICK_STAIRS, Blocks.STONE_BRICK_STAIRS, Blocks.BRICK_STAIRS, Blocks.PURPUR_STAIRS, Blocks.QUARTZ_STAIRS, Blocks.RED_SANDSTONE_STAIRS, Blocks.PRISMARINE_BRICK_STAIRS, Blocks.PRISMARINE_STAIRS, Blocks.DARK_PRISMARINE_STAIRS, Blocks.POLISHED_GRANITE_STAIRS, Blocks.SMOOTH_RED_SANDSTONE_STAIRS, Blocks.MOSSY_STONE_BRICK_STAIRS, Blocks.POLISHED_DIORITE_STAIRS, Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.END_STONE_BRICK_STAIRS, Blocks.STONE_STAIRS, Blocks.SMOOTH_SANDSTONE_STAIRS, Blocks.SMOOTH_QUARTZ_STAIRS, Blocks.GRANITE_STAIRS, Blocks.ANDESITE_STAIRS, Blocks.RED_NETHER_BRICK_STAIRS, Blocks.POLISHED_ANDESITE_STAIRS, Blocks.DIORITE_STAIRS);
      this.getBuilder(BlockTags.SLABS).add(Blocks.STONE_SLAB, Blocks.SMOOTH_STONE_SLAB, Blocks.STONE_BRICK_SLAB, Blocks.SANDSTONE_SLAB, Blocks.ACACIA_SLAB, Blocks.BIRCH_SLAB, Blocks.DARK_OAK_SLAB, Blocks.JUNGLE_SLAB, Blocks.OAK_SLAB, Blocks.SPRUCE_SLAB, Blocks.PURPUR_SLAB, Blocks.QUARTZ_SLAB, Blocks.RED_SANDSTONE_SLAB, Blocks.BRICK_SLAB, Blocks.COBBLESTONE_SLAB, Blocks.NETHER_BRICK_SLAB, Blocks.PETRIFIED_OAK_SLAB, Blocks.PRISMARINE_SLAB, Blocks.PRISMARINE_BRICK_SLAB, Blocks.DARK_PRISMARINE_SLAB, Blocks.POLISHED_GRANITE_SLAB, Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.POLISHED_DIORITE_SLAB, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.END_STONE_BRICK_SLAB, Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.SMOOTH_QUARTZ_SLAB, Blocks.GRANITE_SLAB, Blocks.ANDESITE_SLAB, Blocks.RED_NETHER_BRICK_SLAB, Blocks.POLISHED_ANDESITE_SLAB, Blocks.DIORITE_SLAB, Blocks.CUT_SANDSTONE_SLAB, Blocks.CUT_RED_SANDSTONE_SLAB);
      this.getBuilder(BlockTags.WALLS).add(Blocks.COBBLESTONE_WALL, Blocks.MOSSY_COBBLESTONE_WALL, Blocks.BRICK_WALL, Blocks.PRISMARINE_WALL, Blocks.RED_SANDSTONE_WALL, Blocks.MOSSY_STONE_BRICK_WALL, Blocks.GRANITE_WALL, Blocks.STONE_BRICK_WALL, Blocks.NETHER_BRICK_WALL, Blocks.ANDESITE_WALL, Blocks.RED_NETHER_BRICK_WALL, Blocks.SANDSTONE_WALL, Blocks.END_STONE_BRICK_WALL, Blocks.DIORITE_WALL);
      this.getBuilder(BlockTags.CORAL_PLANTS).add(Blocks.TUBE_CORAL, Blocks.BRAIN_CORAL, Blocks.BUBBLE_CORAL, Blocks.FIRE_CORAL, Blocks.HORN_CORAL);
      this.getBuilder(BlockTags.CORALS).add(BlockTags.CORAL_PLANTS).add(Blocks.TUBE_CORAL_FAN, Blocks.BRAIN_CORAL_FAN, Blocks.BUBBLE_CORAL_FAN, Blocks.FIRE_CORAL_FAN, Blocks.HORN_CORAL_FAN);
      this.getBuilder(BlockTags.WALL_CORALS).add(Blocks.TUBE_CORAL_WALL_FAN, Blocks.BRAIN_CORAL_WALL_FAN, Blocks.BUBBLE_CORAL_WALL_FAN, Blocks.FIRE_CORAL_WALL_FAN, Blocks.HORN_CORAL_WALL_FAN);
      this.getBuilder(BlockTags.SAND).add(Blocks.SAND, Blocks.RED_SAND);
      this.getBuilder(BlockTags.RAILS).add(Blocks.RAIL, Blocks.POWERED_RAIL, Blocks.DETECTOR_RAIL, Blocks.ACTIVATOR_RAIL);
      this.getBuilder(BlockTags.CORAL_BLOCKS).add(Blocks.TUBE_CORAL_BLOCK, Blocks.BRAIN_CORAL_BLOCK, Blocks.BUBBLE_CORAL_BLOCK, Blocks.FIRE_CORAL_BLOCK, Blocks.HORN_CORAL_BLOCK);
      this.getBuilder(BlockTags.ICE).add(Blocks.ICE, Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.FROSTED_ICE);
      this.getBuilder(BlockTags.VALID_SPAWN).add(Blocks.GRASS_BLOCK, Blocks.PODZOL);
      this.getBuilder(BlockTags.LEAVES).add(Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.ACACIA_LEAVES, Blocks.BIRCH_LEAVES);
      this.getBuilder(BlockTags.IMPERMEABLE).add(Blocks.GLASS, Blocks.WHITE_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS, Blocks.LIME_STAINED_GLASS, Blocks.PINK_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.CYAN_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS, Blocks.RED_STAINED_GLASS, Blocks.BLACK_STAINED_GLASS);
      this.getBuilder(BlockTags.WOODEN_TRAPDOORS).add(Blocks.ACACIA_TRAPDOOR, Blocks.BIRCH_TRAPDOOR, Blocks.DARK_OAK_TRAPDOOR, Blocks.JUNGLE_TRAPDOOR, Blocks.OAK_TRAPDOOR, Blocks.SPRUCE_TRAPDOOR);
      this.getBuilder(BlockTags.TRAPDOORS).add(BlockTags.WOODEN_TRAPDOORS).add(Blocks.IRON_TRAPDOOR);
      this.getBuilder(BlockTags.UNDERWATER_BONEMEALS).add(Blocks.SEAGRASS).add(BlockTags.CORALS).add(BlockTags.WALL_CORALS);
      this.getBuilder(BlockTags.BAMBOO_PLANTABLE_ON).add(Blocks.BAMBOO).add(Blocks.BAMBOO_SAPLING).add(Blocks.GRAVEL).add(BlockTags.SAND).add(Blocks.DIRT).add(Blocks.GRASS_BLOCK).add(Blocks.PODZOL).add(Blocks.COARSE_DIRT).add(Blocks.MYCELIUM);
      this.getBuilder(BlockTags.STANDING_SIGNS).add(Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN);
      this.getBuilder(BlockTags.WALL_SIGNS).add(Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN);
      this.getBuilder(BlockTags.SIGNS).add(BlockTags.STANDING_SIGNS).add(BlockTags.WALL_SIGNS);
      this.getBuilder(BlockTags.BEDS).add(Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED);
      this.getBuilder(BlockTags.FENCES).add(BlockTags.WOODEN_FENCES).add(Blocks.NETHER_BRICK_FENCE);
      this.getBuilder(BlockTags.DRAGON_IMMUNE).add(Blocks.BARRIER, Blocks.BEDROCK, Blocks.END_PORTAL, Blocks.END_PORTAL_FRAME, Blocks.END_GATEWAY, Blocks.COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.STRUCTURE_BLOCK, Blocks.JIGSAW, Blocks.MOVING_PISTON, Blocks.OBSIDIAN, Blocks.END_STONE, Blocks.IRON_BARS);
      this.getBuilder(BlockTags.WITHER_IMMUNE).add(Blocks.BARRIER, Blocks.BEDROCK, Blocks.END_PORTAL, Blocks.END_PORTAL_FRAME, Blocks.END_GATEWAY, Blocks.COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.STRUCTURE_BLOCK, Blocks.JIGSAW, Blocks.MOVING_PISTON);
      this.getBuilder(BlockTags.TALL_FLOWERS).add(Blocks.SUNFLOWER, Blocks.LILAC, Blocks.PEONY, Blocks.ROSE_BUSH);
      this.getBuilder(BlockTags.FLOWERS).add(BlockTags.SMALL_FLOWERS).add(BlockTags.TALL_FLOWERS);
      this.getBuilder(BlockTags.BEEHIVES).add(Blocks.BEE_NEST, Blocks.BEEHIVE);
      this.getBuilder(BlockTags.CROPS).add(Blocks.BEETROOTS, Blocks.CARROTS, Blocks.POTATOES, Blocks.WHEAT, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
      this.getBuilder(BlockTags.BEE_GROWABLES).add(BlockTags.CROPS).add(Blocks.SWEET_BERRY_BUSH);
      this.getBuilder(BlockTags.SHULKER_BOXES).add(Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX);
      this.getBuilder(BlockTags.PORTALS).add(Blocks.NETHER_PORTAL, Blocks.END_PORTAL, Blocks.END_GATEWAY);
   }

   /**
    * Resolves a Path for the location to save the given tag.
    */
   protected Path makePath(ResourceLocation id) {
      return this.generator.getOutputFolder().resolve("data/" + id.getNamespace() + "/tags/blocks/" + id.getPath() + ".json");
   }

   /**
    * Gets a name for this provider, to use in logging.
    */
   public String getName() {
      return "Block Tags";
   }

   protected void setCollection(TagCollection<Block> colectionIn) {
      BlockTags.setCollection(colectionIn);
   }
}