package com.railwayteam.railways;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.railwayteam.railways.blocks.*;
import com.railwayteam.railways.entities.engineer.EngineerGolemEntity;
import com.railwayteam.railways.entities.engineer.EngineerGolemRenderer;
import com.railwayteam.railways.entities.SteadyMinecartEntity;
import com.railwayteam.railways.entities.SteadyMinecartRenderer;
import com.railwayteam.railways.items.EngineerGolemItem;
import com.railwayteam.railways.items.EngineersCapItem;
import com.railwayteam.railways.items.StationEditorItem;
import com.railwayteam.railways.items.WayPointToolItem;

import com.railwayteam.railways.util.UsefulAndRailwaysTags;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.entry.TileEntityEntry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import com.tterrag.registrate.Registrate;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import java.util.HashMap;


public class ModSetup {
  public static ItemGroup itemGroup = new ItemGroup(Railways.MODID) {
    @Override
    public ItemStack createIcon() {
            return new ItemStack(Items.MINECART);
        }
  };

  // we cache Registry entries in case other mod components need a convenient reference (including Registrate itself)
  public static BlockEntry<WayPointBlock> R_BLOCK_WAYPOINT;
  public static BlockEntry<StationSensorRailBlock> R_BLOCK_STATION_SENSOR;

  public static BlockEntry<LargeTrackBlock> R_BLOCK_LARGE_RAIL;
  public static BlockEntry<LargeSwitchTrackBlock> R_BLOCK_LARGE_SWITCH;

  public static BlockEntry<LargeTrackBlock> R_BLOCK_LARGE_RAIL_WOODEN;
  public static BlockEntry<LargeSwitchTrackBlock> R_BLOCK_LARGE_SWITCH_WOODEN;

  public static BlockEntry<Block> R_BLOCK_WHEEL;

public static BlockEntry<SignalBlock> R_BLOCK_SIGNAL;

  public static TileEntityEntry<StationSensorRailTileEntity> R_TE_STATION_SENSOR;

  public static ItemEntry<WayPointToolItem> R_ITEM_WAYPOINT_TOOL;
  public static ItemEntry<StationEditorItem> R_ITEM_STATION_EDITOR_TOOL;
  public static HashMap<DyeColor, ItemEntry<EngineersCapItem>> ENGINEERS_CAPS = new HashMap<>();
  public static ItemEntry<Item> R_ITEM_BOGIE;
  public static ItemEntry<EngineerGolemItem> R_ITEM_ENGINEER_GOLEM;

    public static RegistryEntry<EntityType<SteadyMinecartEntity>> R_ENTITY_STEADYCART;
  public static RegistryEntry<EntityType<EngineerGolemEntity>>  R_ENTITY_ENGINEER;

  public void init() {
  }

  public static void register (Registrate reg) {

    // set item group for the following registry entries
    reg.itemGroup(()->itemGroup, Railways.MODID);

//      AllBlocks.SAIL_FRAME.get().getTags().add(UsefulTags.SailsTagLoc);
//      AllBlocks.SAIL.get().getTags().add(UsefulTags.SailsTagLoc);

    // right now we're registering a block and an item.
    // TODO: consider splitting into ::registerBlocks and ::registerItems, or even to dedicated files?
    R_BLOCK_WAYPOINT = reg.block(WayPointBlock.name, WayPointBlock::new)         // tell Registrate how to create it
      .properties(p->p.hardnessAndResistance(5.0f, 6.0f))    // set block properties
      .blockstate((ctx,prov) -> prov.simpleBlock(ctx.getEntry(),                 // block state determines the model
        prov.models().getExistingFile(prov.modLoc("block/"+ctx.getName())) // hence why that's tucked in here
      ))
      .recipe((ctx, prov) -> ShapedRecipeBuilder.shapedRecipe(ctx.get())
        .patternLine("   ")
        .patternLine(" A ")
        .patternLine(" T ")
        .key('A', AllBlocks.SAIL_FRAME.get())
        .key('T', Items.STICK)
        .addCriterion("has_sail", prov.hasItem(AllBlocks.SAIL_FRAME.get()))
        .build(prov))
      .simpleItem()     // nothing special about the item right now
      .lang("Waypoint") // give it a friendly name
      .register();      // pack it up for Registrate

    R_BLOCK_LARGE_RAIL = reg.block(LargeTrackBlock.name, LargeTrackBlock::new)
      .properties(p->p.hardnessAndResistance(10.0f, 10.0f).nonOpaque()) //.doesNotBlockMovement())
      .blockstate((ctx,prov) -> prov.getVariantBuilder(ctx.getEntry()).forAllStates(state -> {
        return ConfiguredModel.builder().modelFile(LargeTrackBlock.partialModel(ctx,prov,state.get(LargeTrackBlock.TRACK_SIDE).getName())).build();
      }))
      .item().model((ctx,prov) -> prov.singleTexture(
        ctx.getName(),
        prov.mcLoc("item/generated"),
        "layer0",
        prov.modLoc("item/wide_gauge/"+ctx.getName()))).build()
      .lang("Andesite Track")
      .register();

    R_BLOCK_LARGE_SWITCH = reg.block(LargeSwitchTrackBlock.name, LargeSwitchTrackBlock::new)
      .properties(p->p.hardnessAndResistance(10.0f, 10.0f).nonOpaque())//.doesNotBlockMovement())
      .blockstate((ctx,prov) -> prov.getVariantBuilder(ctx.getEntry()).forAllStates(state -> {
        return ConfiguredModel.builder().modelFile(
          LargeSwitchTrackBlock.partialModel(ctx,prov,state.get(LargeSwitchTrackBlock.SWITCH_SIDE).getName())).build();
      }))
      .item().model((ctx,prov) -> prov.singleTexture(
        ctx.getName(),
        prov.mcLoc("item/generated"),
        "layer0",
        prov.modLoc("item/wide_gauge/"+ctx.getName()))).build()
      .lang("Andesite Switch")
      .recipe((ctx, prov) -> ShapelessRecipeBuilder.shapelessRecipe(ctx.get())
        .addIngredient(R_BLOCK_LARGE_RAIL.get(), 2)
        .addCriterion("has_tracks", prov.hasItem(R_BLOCK_LARGE_RAIL.get()))
        .build(prov))
      .register();

    // TODO: there has to be a cleaner way of creating almost identical blocks than copy pasting

    R_BLOCK_LARGE_RAIL_WOODEN = reg.block(LargeTrackBlock.name + "_wooden", LargeTrackBlock::new)
      .properties(p->p.hardnessAndResistance(10.0f, 10.0f).nonOpaque().doesNotBlockMovement())
      .blockstate((ctx,prov) -> prov.getVariantBuilder(ctx.getEntry()).forAllStates(state -> {
         return ConfiguredModel.builder().modelFile(
           LargeTrackBlock.partialModel(true, ctx,prov,state.get(LargeTrackBlock.TRACK_SIDE).getName())).build();
      }))
      .item().model((ctx,prov) -> prov.singleTexture(
        ctx.getName(),
        prov.mcLoc("item/generated"),
        "layer0",
        prov.modLoc("item/wide_gauge/"+ctx.getName()))).build()
      .lang("Wooden Track")
      .register();

    R_BLOCK_LARGE_SWITCH_WOODEN = reg.block(LargeSwitchTrackBlock.name + "_wooden", LargeSwitchTrackBlock::new)
      .properties(p->p.hardnessAndResistance(10.0f, 10.0f).nonOpaque().doesNotBlockMovement())
      .blockstate((ctx,prov) -> prov.getVariantBuilder(ctx.getEntry()).forAllStates(state -> {
        return ConfiguredModel.builder().modelFile(
          LargeSwitchTrackBlock.partialModel(true, ctx,prov,state.get(LargeSwitchTrackBlock.SWITCH_SIDE).getName())).build();
      }))
      .item().model((ctx,prov) -> prov.singleTexture(
        ctx.getName(),
        prov.mcLoc("item/generated"),
        "layer0",
        prov.modLoc("item/wide_gauge/"+ctx.getName()))).build()
      .lang("Wooden Switch")
      .recipe((ctx, prov) -> ShapelessRecipeBuilder.shapelessRecipe(ctx.get())
        .addIngredient(R_BLOCK_LARGE_RAIL_WOODEN.get(), 2)
        .addCriterion("has_wooden_tracks", prov.hasItem(R_BLOCK_LARGE_RAIL_WOODEN.get()))
        .build(prov))
      .register();

    R_BLOCK_SIGNAL = reg.block(SignalBlock.name, SignalBlock::new)
      .properties(p->p.hardnessAndResistance(10f, 10f).nonOpaque())
      .blockstate((ctx,prov) -> prov.horizontalFaceBlock(ctx.getEntry(),
        (blockstate) -> (prov.models().getExistingFile(
          prov.modLoc("block/"+ctx.getName())// + (blockstate.get(BlockStateProperties.POWERED) ? "_red" : "_green"))
      ))))
      .simpleItem()
      .lang("Track Signal")
      .register();

    R_BLOCK_STATION_SENSOR = reg.block(StationSensorRailBlock.name, StationSensorRailBlock::new)
      .initialProperties(()->Blocks.DETECTOR_RAIL)
      .properties(p->p.nonOpaque().doesNotBlockMovement())
      .blockstate((ctx,prov) -> prov.getExistingVariantBuilder(ctx.getEntry()))
      .item().model((ctx,prov)-> prov.getExistingFile(prov.modLoc("block/" + ctx.getName()))).build()
      .tag(BlockTags.RAILS)
      .lang("Station Sensor")
      .recipe((ctx, prov) -> ShapedRecipeBuilder.shapedRecipe(ctx.get(), 6)
        .patternLine("I I")
        .patternLine("ILI")
        .patternLine("IRI")
        .key('L', AllItems.LAPIS_SHEET.get())
        .key('R', Tags.Items.DUSTS_REDSTONE)
        .key('I', Tags.Items.INGOTS_IRON)
        .addCriterion("has_lapis", prov.hasItem(Tags.Items.GEMS_LAPIS))
        .build(prov))
      .register();

    R_BLOCK_WHEEL = reg.block("wheel", Block::new)
      .lang("Wheel")
      .simpleItem()
      .defaultBlockstate()
      .recipe((ctx, prov) -> ShapedRecipeBuilder.shapedRecipe(ctx.get(), 4)
        .patternLine(" I ")
        .patternLine("ISI")
        .patternLine(" I ")
        .key('I', UsefulAndRailwaysTags.IronSheet)
        .key('S', AllBlocks.SHAFT.get())
        .addCriterion("has_iron_sheet", prov.hasItem(UsefulAndRailwaysTags.IronSheet))
        .build(prov))
      .register();

    R_TE_STATION_SENSOR = reg.tileEntity(StationSensorRailTileEntity.NAME, StationSensorRailTileEntity::new)
      .validBlock(()->R_BLOCK_STATION_SENSOR.get())
      .register();

    R_ITEM_WAYPOINT_TOOL = reg.item(WayPointToolItem.name, WayPointToolItem::new)
      .lang("Waypoint Tool")
      .recipe((ctx, prov) -> ShapelessRecipeBuilder.shapelessRecipe(ctx.get())
        .addIngredient(ItemTags.SIGNS)
        .addIngredient(AllItems.ANDESITE_ALLOY.get())
        .addCriterion("has_andesite_alloy", prov.hasItem(AllItems.ANDESITE_ALLOY.get()))
        .build(prov))
      .register();

//    ItemBuilder<EngineersCapItem, Registrate> engineersCapBuilder = reg.item(EngineersCapItem.name, EngineersCapItem::new)
//            .lang("Engineer's cap")
//            .model((ctx, prov) -> { // TODO: placeholder model
//              prov.singleTexture(
//                      ctx.getName(),
//                      prov.mcLoc("item/generated"),
//                      "layer0",
//                      prov.modLoc("item/waypoint_manager"));
//            });
//            .recipe((ctx, prov) -> ShapedRecipeBuilder.shapedRecipe(ctx.get())
//                    .patternLine("WWW")
//                    .patternLine("W W")
//                    .key('W', ItemTags.WOOL)
//                    .addCriterion("has_wool", prov.hasItem(ItemTags.WOOL))
//                    .build(prov));
    for(DyeColor color : DyeColor.values()) {
      ENGINEERS_CAPS.put(color, reg.item(EngineersCapItem.name + "_" + color.getName(),
      (p) -> new EngineersCapItem(p, color))
        .properties(p -> p.maxStackSize(1))
        .lang("Engineer's cap")
        .tag(UsefulAndRailwaysTags.EngineerCaps)
        .model((ctx, prov) -> {
          prov.singleTexture(
          ctx.getName(),
          prov.mcLoc("item/generated"),
          "layer0",
          prov.modLoc("item/engineer_caps/" + color.getName() + "_engineers_cap"));
        })
        .recipe((ctx, prov) -> {
          ShapedRecipeBuilder.shapedRecipe(ctx.get())
            .patternLine("WWW")
            .patternLine("W W")
            .key('W', Ingredient.deserialize(new Gson().fromJson("{\"item\": \"minecraft:" + color.getName() + "_wool\"}", JsonObject.class))) // wow the fact that i have to do this is so stupid
            .addCriterion("has_wool", prov.hasItem(ItemTags.WOOL))
            .build(prov, new ResourceLocation("railways", "engineer_caps/" + color.getName()));
          ShapelessRecipeBuilder.shapelessRecipe(ctx.get())
            .addIngredient(UsefulAndRailwaysTags.EngineerCaps)
            .addIngredient(color.getTag())
            .addCriterion("has_wool", prov.hasItem(ItemTags.WOOL))
            .build(prov, new ResourceLocation("railways", "engineer_caps/" + color.getName() + "_dye"));
          })
        .register());
      }

    R_ITEM_STATION_EDITOR_TOOL = reg.item(StationEditorItem.NAME, StationEditorItem::new)
      .lang("Station Editor")
      .register();

    R_ITEM_BOGIE = reg.item("bogie", Item::new)
      .lang("Bogie")
      .model((ctx, prov) -> { // TODO: placeholder model
        prov.singleTexture(
        ctx.getName(),
        prov.mcLoc("item/generated"),
        "layer0",
        prov.modLoc("item/waypoint_manager"));
      })
      .recipe((ctx, prov) -> ShapedRecipeBuilder.shapedRecipe(ctx.get())
        .patternLine("WBW")
        .patternLine("SMS")
        .patternLine("WBW")
        .key('W', R_BLOCK_WHEEL.get())
        .key('B', AllBlocks.METAL_BRACKET.get())
        .key('M', AllBlocks.MECHANICAL_BEARING.get())
        .key('S', AllBlocks.SHAFT.get())
        .addCriterion("has_wheel", prov.hasItem(R_BLOCK_WHEEL.get()))
        .build(prov))
      .register();

    R_ITEM_ENGINEER_GOLEM = reg.item("engineer_golem", EngineerGolemItem::new)
      .lang("Engineer golem")
      .model((ctx, prov) -> {
        prov.singleTexture(
        ctx.getName(),
        prov.mcLoc("item/generated"),
        "layer0",
        prov.modLoc("item/waypoint_manager"));
      })
      .register();

    R_ENTITY_STEADYCART = reg.<SteadyMinecartEntity>entity(SteadyMinecartEntity.name, SteadyMinecartEntity::new, EntityClassification.MISC)
      .lang("Steady Minecart")
      .register();

    R_ENTITY_ENGINEER   = reg.entity(EngineerGolemEntity.name, EngineerGolemEntity::new, EntityClassification.MISC)
      .lang(EngineerGolemEntity.defaultDisplayName)
      .register();
  }

  @OnlyIn(value=Dist.CLIENT)
  public static void registerRenderers () {
    RenderingRegistry.registerEntityRenderingHandler(R_ENTITY_STEADYCART.get(), SteadyMinecartRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(R_ENTITY_ENGINEER.get(), EngineerGolemRenderer::new);
  }
}
