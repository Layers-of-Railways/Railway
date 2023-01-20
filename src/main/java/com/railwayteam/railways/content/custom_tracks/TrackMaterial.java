package com.railwayteam.railways.content.custom_tracks;

import com.railwayteam.railways.base.data.recipe.RailwaysRecipeProvider.Ingredients;
import com.railwayteam.railways.registry.CRBlocks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.trains.track.TrackBlock;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.util.Lazy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Stream;

public enum TrackMaterial {
  ANDESITE("Andesite", Lazy.of(() -> AllBlocks.TRACK), Create.asResource("block/palettes/stone_types/polished/andesite_cut_polished"), Ingredient.EMPTY, Ingredient.EMPTY, true),

  ACACIA("Acacia", Lazy.of(() -> CRBlocks.ACACIA_TRACK), new ResourceLocation("block/acacia_planks"), Blocks.ACACIA_SLAB),
  BIRCH("Birch", Lazy.of(() -> CRBlocks.BIRCH_TRACK), new ResourceLocation("block/birch_planks"), Blocks.BIRCH_SLAB),
  CRIMSON("Crimson", Lazy.of(() -> CRBlocks.CRIMSON_TRACK), new ResourceLocation("block/crimson_planks"), Ingredient.of(Blocks.CRIMSON_SLAB), Ingredient.of(Items.GOLD_NUGGET)),
  DARK_OAK("Dark Oak", Lazy.of(() -> CRBlocks.DARK_OAK_TRACK), new ResourceLocation("block/dark_oak_planks"), Blocks.DARK_OAK_SLAB),
  JUNGLE("Jungle", Lazy.of(() -> CRBlocks.JUNGLE_TRACK), new ResourceLocation("block/jungle_planks"), Blocks.JUNGLE_SLAB),
  OAK("Oak", Lazy.of(() -> CRBlocks.OAK_TRACK), new ResourceLocation("block/oak_planks"), Blocks.OAK_SLAB),
  SPRUCE("Spruce", Lazy.of(() -> CRBlocks.SPRUCE_TRACK), new ResourceLocation("block/spruce_planks"), Blocks.SPRUCE_SLAB),
  WARPED("Warped", Lazy.of(() -> CRBlocks.WARPED_TRACK), new ResourceLocation("block/warped_planks"), Ingredient.of(Blocks.WARPED_SLAB), Ingredient.of(Items.GOLD_NUGGET)),
  BLACKSTONE("Blackstone", Lazy.of(() -> CRBlocks.BLACKSTONE_TRACK), new ResourceLocation("block/blackstone"), Ingredient.of(Blocks.BLACKSTONE_SLAB), Ingredient.of(Items.GOLD_NUGGET)),
  MANGROVE("Mangrove", Lazy.of(() -> CRBlocks.MANGROVE_TRACK), new ResourceLocation("block/mangrove_planks"), Ingredient.of(Blocks.MANGROVE_SLAB)),
  ;

  public final String langName;
  public final Supplier<BlockEntry<? extends TrackBlock>> trackBlock; //replace with supplier
  public final boolean createBuiltin;
  public final Ingredient sleeperIngredient;
  public final Ingredient railsIngredient;
  public final ResourceLocation particle;

  TrackMaterial(String langName, Supplier<BlockEntry<? extends TrackBlock>> trackBlock, ResourceLocation particle, ItemLike... items) {
    this(langName, trackBlock, particle, Ingredient.of(items));
  }

  TrackMaterial(String langName, Supplier<BlockEntry<? extends TrackBlock>> trackBlock, ResourceLocation particle, Ingredient sleeperIngredient) {
    this(langName, trackBlock, particle, sleeperIngredient, Ingredient.fromValues(
        Stream.of(new Ingredient.TagValue(Ingredients.ironNugget()), new Ingredient.TagValue(Ingredients.zincNugget()))), false);
  }

  TrackMaterial(String langName, Supplier<BlockEntry<? extends TrackBlock>> trackBlock, ResourceLocation particle, Ingredient sleeperIngredient, Ingredient railsIngredient) {
    this(langName, trackBlock, particle, sleeperIngredient, railsIngredient, false);
  }

  TrackMaterial(String langName, Supplier<BlockEntry<? extends TrackBlock>> trackBlock, ResourceLocation particle, Ingredient sleeperIngredient, Ingredient railsIngredient, boolean createBuiltin) {
    this.langName = langName;
    this.trackBlock = trackBlock;
//    Railways.LOGGER.info("Building track_material: "+this.langName+", trackBlock:"+this.trackBlock);
    this.createBuiltin = createBuiltin;
    this.sleeperIngredient = sleeperIngredient;
    this.railsIngredient = railsIngredient;
    this.particle = particle;
  }

  public BlockEntry<? extends TrackBlock> getTrackBlock() {
    return this.trackBlock.get();
  }

  public CustomTrackBlock create(BlockBehaviour.Properties properties) {
    return new CustomTrackBlock(properties, this);
  }

  public boolean isCustom() {
    return !createBuiltin;
  }

  public static TrackMaterial[] allCustom() {
    return Arrays.stream(values()).filter(TrackMaterial::isCustom).toArray(TrackMaterial[]::new);
  }

  public static List<BlockEntry<?>> allCustomBlocks() {
    List<BlockEntry<?>> list = new ArrayList<>();
    for (TrackMaterial material : allCustom()) {
      list.add(material.getTrackBlock());
    }
    return list;
  }

  public static List<BlockEntry<?>> allBlocks() {
    List<BlockEntry<?>> list = new ArrayList<>();
    for (TrackMaterial material : values()) {
      list.add(material.getTrackBlock());
    }
    return list;
  }

  public String resName() {
    return this.name().toLowerCase(Locale.ROOT);
  }

  public static TrackMaterial deserialize(String serializedName) {
    return valueOf(serializedName.toUpperCase());
  }
}
