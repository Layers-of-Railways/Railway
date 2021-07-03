package com.railwayteam.railways;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.railwayteam.railways.blocks.*;
import com.railwayteam.railways.entities.SteadyMinecartEntity;
import com.railwayteam.railways.entities.conductor.ConductorEntity;
import com.railwayteam.railways.entities.conductor.ConductorRenderer;
import com.railwayteam.railways.entities.handcar.HandcarEntity;
import com.railwayteam.railways.entities.handcar.HandcarRenderer;
import com.railwayteam.railways.items.*;
import com.railwayteam.railways.items.engineers_cap.EngineersCapItem;
import com.railwayteam.railways.util.RailwaysTags;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.entry.BlockEntry;
import com.simibubi.create.repack.registrate.util.entry.EntityEntry;
import com.simibubi.create.repack.registrate.util.entry.ItemEntry;
import com.simibubi.create.repack.registrate.util.entry.TileEntityEntry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;

import static com.simibubi.create.repack.registrate.providers.RegistrateLangProvider.toEnglishName;

@Mod.EventBusSubscriber(modid = "railways", bus = Mod.EventBusSubscriber.Bus.MOD)
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

  public static BlockEntry<HornBlock> R_BLOCK_HORN;

  public static BlockEntry<SpeedSignalBlock> R_BLOCK_NUMERICAL_SIGNAL;

  public static BlockEntry<BogieBlock> R_BLOCK_BOGIE;

  public static TileEntityEntry<StationSensorRailTileEntity> R_TE_STATION_SENSOR;
  public static TileEntityEntry<SignalTileEntity> R_TE_SIGNAL;
  public static TileEntityEntry<SpeedSignalTileEntity> R_TE_NUMERICAL_SIGNAL;

  public static ItemEntry<WayPointToolItem> R_ITEM_WAYPOINT_TOOL;
  public static ItemEntry<StationEditorItem> R_ITEM_STATION_EDITOR_TOOL;
  public static ItemEntry<HandcarItem> R_ITEM_HANDCAR;
  public static ItemEntry<Item> R_ITEM_WHISTLE;

  public static HashMap<DyeColor, ItemEntry<EngineersCapItem>> ENGINEERS_CAPS = new HashMap<>();
  public static HashMap<DyeColor, ItemEntry<ConductorItem>> CONDUCTOR_ITEMS = new HashMap<>();

  public static EntityEntry<Entity> R_ENTITY_STEADYCART;
  public static EntityEntry<ConductorEntity> R_ENTITY_CONDUCTOR;
  public static EntityEntry<HandcarEntity> R_ENTITY_HANDCAR;

  public void init() {
  }

  public static void register (CreateRegistrate reg) {
    // set item group for the following registry entries
    reg.itemGroup(()->itemGroup, Railways.MODID);

//      AllBlocks.SAIL_FRAME.get().getTags().add(UsefulTags.SailsTagLoc);
//      AllBlocks.SAIL.get().getTags().add(UsefulTags.SailsTagLoc);

    // right now we're registering a block and an item.
    // TODO: consider splitting into ::registerBlocks and ::registerItems, or even to dedicated files?
    R_BLOCK_WAYPOINT = reg.block(WayPointBlock.name, WayPointBlock::new)// tell Registrate how to create it
            .recipe((ctx, prov) -> {
              ctx.getEntry().recipe(ctx, prov, AllBlocks.SAIL.get());
              ctx.getEntry().recipe(ctx, prov, AllBlocks.SAIL_FRAME.get());
            })
      .properties(p->p.hardnessAndResistance(5.0f, 6.0f))    // set block properties
      .blockstate((ctx,prov) -> prov.simpleBlock(ctx.getEntry(),                 // block state determines the model
        prov.models().getExistingFile(prov.modLoc("block/"+ctx.getName())) // hence why that's tucked in here
      ))
      .simpleItem()     // nothing special about the item right now
      .lang("Waypoint") // give it a friendly name
      .register();      // pack it up for Registrate

    R_BLOCK_LARGE_RAIL = reg.block(LargeTrackBlock.name, LargeTrackBlock::new)
      .properties(p->p.hardnessAndResistance(10.0f, 10.0f).nonOpaque()) //.doesNotBlockMovement())
      .blockstate((ctx,prov) -> prov.getVariantBuilder(ctx.getEntry()).forAllStates(state -> {
        return ConfiguredModel.builder().modelFile(LargeTrackBlock.partialModel(ctx,prov,state.get(LargeTrackBlock.TRACK_SIDE).getString())).build();
      }))
      .item().model((ctx,prov) -> prov.singleTexture(
        ctx.getName(),
        prov.mcLoc("item/generated"),
        "layer0",
        prov.modLoc("item/wide_gauge/"+ctx.getName()))).tag(RailwaysTags.Tracks).build()
      .lang("Andesite Track")
            .register();

    R_BLOCK_LARGE_SWITCH = reg.block(LargeSwitchTrackBlock.name, LargeSwitchTrackBlock::new)
      .properties(p->p.hardnessAndResistance(10.0f, 10.0f).nonOpaque())//.doesNotBlockMovement())
      .blockstate((ctx,prov) -> prov.getVariantBuilder(ctx.getEntry()).forAllStates(state -> {
        return ConfiguredModel.builder().modelFile(
          LargeSwitchTrackBlock.partialModel(ctx,prov,state.get(LargeSwitchTrackBlock.SWITCH_SIDE).getString())).build();
      }))
      .item().model((ctx,prov) -> prov.singleTexture(
        ctx.getName(),
        prov.mcLoc("item/generated"),
        "layer0",
        prov.modLoc("item/wide_gauge/"+ctx.getName()))).tag(RailwaysTags.Tracks).build()
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
           LargeTrackBlock.partialModel(true, ctx,prov,state.get(LargeTrackBlock.TRACK_SIDE).getString())).build();
      }))
      .item().model((ctx,prov) -> prov.singleTexture(
        ctx.getName(),
        prov.mcLoc("item/generated"),
        "layer0",
        prov.modLoc("item/wide_gauge/"+ctx.getName()))).tag(RailwaysTags.Tracks).build()
      .lang("Wooden Track")
      .register();

    R_BLOCK_LARGE_SWITCH_WOODEN = reg.block(LargeSwitchTrackBlock.name + "_wooden", LargeSwitchTrackBlock::new)
      .properties(p->p.hardnessAndResistance(10.0f, 10.0f).nonOpaque().doesNotBlockMovement())
      .blockstate((ctx,prov) -> prov.getVariantBuilder(ctx.getEntry()).forAllStates(state -> {
        return ConfiguredModel.builder().modelFile(
          LargeSwitchTrackBlock.partialModel(true, ctx,prov,state.get(LargeSwitchTrackBlock.SWITCH_SIDE).getString())).build();
      }))
      .item().model((ctx,prov) -> prov.singleTexture(
        ctx.getName(),
        prov.mcLoc("item/generated"),
        "layer0",
        prov.modLoc("item/wide_gauge/"+ctx.getName()))).tag(RailwaysTags.Tracks).build()
      .lang("Wooden Switch")
      .recipe((ctx, prov) -> ShapelessRecipeBuilder.shapelessRecipe(ctx.get())
        .addIngredient(R_BLOCK_LARGE_RAIL_WOODEN.get(), 2)
        .addCriterion("has_wooden_tracks", prov.hasItem(R_BLOCK_LARGE_RAIL_WOODEN.get()))
        .build(prov))
            .register();

    R_BLOCK_SIGNAL = reg.block(SignalBlock.name, SignalBlock::new)
      .properties(p->p.hardnessAndResistance(10f, 10f).nonOpaque())
      .blockstate((ctx,prov) -> prov.horizontalBlock(ctx.getEntry(),
        (blockstate) -> (prov.models().getExistingFile(
          prov.modLoc("block/"+ctx.getName() + (blockstate.get(BlockStateProperties.POWERED) ? "_red" : "_green"))
      ))))
      .recipe((ctx, prov) -> ShapelessRecipeBuilder.shapelessRecipe(ctx.get())
                  .addIngredient(AllBlocks.ANDESITE_CASING.get())
                  .addIngredient(Items.REDSTONE_TORCH, 2)
                  .addCriterion("has_tracks", prov.hasItem(RailwaysTags.Tracks))
                  .addCriterion("has_andesite_casing", prov.hasItem(AllBlocks.ANDESITE_CASING.get()))
                  .build(prov))
      .item(SignalItem::new).build()
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
      .item().model((ctx, prov) -> {}).build()
      .blockstate((ctx, prov) -> {})
//      .recipe((ctx, prov) -> ShapedRecipeBuilder.shapedRecipe(ctx.get(), 4)
//        .patternLine(" I ")
//        .patternLine("ISI")
//        .patternLine(" I ")
//        .key('I', UsefulAndRailwaysTags.IronSheet)
//        .key('S', AllBlocks.SHAFT.get())
//        .addCriterion("has_iron_sheet", prov.hasItem(UsefulAndRailwaysTags.IronSheet))
//        .build(prov))
      .register();

    R_BLOCK_HORN = reg.block("horn", HornBlock::new)
            .properties(p->p.hardnessAndResistance(10f, 10f).nonOpaque())
            .item().model((ctx, prov) -> prov.getExistingFile(prov.modLoc("item/horn"))).build()
            .blockstate((ctx,prov) -> prov.horizontalFaceBlock(ctx.getEntry(),
                    (blockstate) -> (prov.models().getExistingFile(
                            prov.modLoc("block/horn/horn_" + (blockstate.get(HorizontalFaceBlock.FACE) == AttachFace.WALL ? "side" : "bottom") + "_" + blockstate.get(HornBlock.HORNS))
                    ))))
//            .loot((t, block) -> {
//              t.registerLootTable(block, new LootTable.Builder().addLootPool(new LootPool.Builder().acceptCondition(new BlockStateProperty.Builder(block).properties(StatePropertiesPredicate.Builder.create().exactMatch(HornBlock.HORNS, 0)))));
//            })
            .recipe((ctx, prov) -> ShapedRecipeBuilder.shapedRecipe(ctx.getEntry())
                    .patternLine("B")
                    .patternLine("B")
                    .patternLine("B")
                    .key('B', AllItems.BRASS_INGOT.get())
                    .addCriterion("has_brass", prov.hasItem(AllItems.BRASS_INGOT.get()))
                    .build(prov))
            .lang("Horn")
            .register();

    R_BLOCK_NUMERICAL_SIGNAL = reg.block("speed_signal", SpeedSignalBlock::new)
            .properties(p->p.hardnessAndResistance(10f, 10f).nonOpaque())
            .simpleItem()
            .blockstate((ctx,prov) -> prov.horizontalBlock(ctx.getEntry(),
                    (blockstate) -> (prov.models().getExistingFile(
                            prov.modLoc("block/speed_signal")
                    ))))
            .recipe((ctx, prov) -> ShapelessRecipeBuilder.shapelessRecipe(ctx.get())
                      .addIngredient(AllBlocks.ANDESITE_CASING.get())
                      .addIngredient(AllBlocks.NIXIE_TUBE.get())
                      .addCriterion("has_nixie_tube", prov.hasItem(AllBlocks.NIXIE_TUBE.get()))
                      .build(prov))
            .lang("Speed Signal")
            .register();

    R_BLOCK_BOGIE = reg.block("bogie", BogieBlock::new)
            .lang("Bogie")
            .blockstate((smth1, smth2) -> {})
            .item(BogieItem::new).model((smth1, smth2) -> {}).build()
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

    R_TE_STATION_SENSOR = reg.tileEntity(StationSensorRailTileEntity.NAME, StationSensorRailTileEntity::new)
      .validBlock(()->R_BLOCK_STATION_SENSOR.get())
      .register();

    R_TE_SIGNAL = reg.tileEntity(SignalTileEntity.NAME, SignalTileEntity::new)
      .validBlock(()->R_BLOCK_SIGNAL.get())
      .register();

    R_TE_NUMERICAL_SIGNAL = reg.tileEntity("numerical_signal", SpeedSignalTileEntity::new)
            .validBlock(()->R_BLOCK_NUMERICAL_SIGNAL.get())
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
//
//            .recipe((ctx, prov) -> ShapedRecipeBuilder.shapedRecipe(ctx.get())
//                    .patternLine("WWW")
//                    .patternLine("W W")
//                    .key('W', ItemTags.WOOL)
//                    .addCriterion("has_wool", prov.hasItem(ItemTags.WOOL))
//                    .build(prov));
    ENGINEERS_CAPS = new HashMap<>();
    CONDUCTOR_ITEMS = new HashMap<>();
    for(DyeColor color : DyeColor.values()) {
      ENGINEERS_CAPS.put(color, reg.item(EngineersCapItem.name + "_" + color,
      (p) -> new EngineersCapItem(p, color))
              .lang(toEnglishName(color.getTranslationKey() + "_engineer's_cap"))
        .properties(p -> p.maxStackSize(1))
        .tag(RailwaysTags.EngineerCaps)
        .model((ctx, prov) -> {
          prov.singleTexture(
          ctx.getName(),
          prov.mcLoc("item/generated"),
          "layer0",
          prov.modLoc("item/engineer_caps/" + color.getString() + "_engineers_cap"));
        })
        .recipe((ctx, prov) -> {
          ShapedRecipeBuilder.shapedRecipe(ctx.get())
            .patternLine("WWW")
            .patternLine("W W")
            .key('W', Ingredient.deserialize(new Gson().fromJson("{\"item\": \"minecraft:" + color.getString() + "_wool\"}", JsonObject.class))) // wow the fact that i have to do this is so stupid
            .addCriterion("has_wool", prov.hasItem(ItemTags.WOOL))
            .build(prov, new ResourceLocation("railways", "engineer_caps/" + color.getString()));
          ShapelessRecipeBuilder.shapelessRecipe(ctx.get())
            .addIngredient(RailwaysTags.EngineerCaps)
            .addIngredient(color.getTag())
            .addCriterion("has_wool", prov.hasItem(ItemTags.WOOL))
            .build(prov, new ResourceLocation("railways", "engineer_caps/" + color.getString() + "_dye"));
          })
        .register());

      CONDUCTOR_ITEMS.put(color, reg.item("conductor" + "_" + color, p -> new ConductorItem(p, color))
              .lang(toEnglishName(color.getTranslationKey() + "_conductor"))
              .model((ctx, prov) -> {
                prov.singleTexture(
                        ctx.getName(),
                        prov.mcLoc("item/generated"),
                        "layer0",
                        prov.modLoc("item/conductors/" + color.getTranslationKey() + "_conductor"));
              })
              .properties(p -> p.maxStackSize(1))
              .register()
      );
      }

    R_ITEM_STATION_EDITOR_TOOL = reg.item(StationEditorItem.NAME, StationEditorItem::new)
      .lang("Station Editor")
      .register();

    R_ITEM_HANDCAR = reg.item("handcar", HandcarItem::new)
            .lang("Handcar")
//            .model((ctx, prov) -> {}) // TODO: handcar is invisible as an item even though it uses the same methods as others???
            .model((ctx, prov) -> {
              prov.singleTexture(
                      ctx.getName(),
                      prov.mcLoc("item/generated"),
                      "layer0",
                      prov.modLoc("item/waypoint_manager"));
            })
            .properties(p -> p.maxStackSize(1))
            .recipe((ctx, prov) -> ShapedRecipeBuilder.shapedRecipe(ctx.getEntry())
              .patternLine("W W")
              .patternLine("CBC")
              .patternLine("W W")
              .key('W', R_BLOCK_WHEEL.get())
              .key('C', AllBlocks.ANDESITE_CASING.get())
              .key('B', AllBlocks.WOODEN_BRACKET.get())
              .addCriterion("has_wheel", prov.hasItem(R_BLOCK_WHEEL.get()))
            .build(prov))
            .register();

    R_ITEM_WHISTLE = reg.item("whistle", Item::new)
            .lang("Whistle")
            .properties(p -> p.maxStackSize(1))
            .model((ctx, prov) -> {})
            .recipe((ctx, prov) -> ShapedRecipeBuilder.shapedRecipe(ctx.getEntry())
              .patternLine("B")
              .patternLine("B")
              .patternLine("A")
              .key('B', AllItems.BRASS_INGOT.get())
              .key('A', AllItems.ANDESITE_ALLOY.get())
              .addCriterion("has_brass", prov.hasItem(AllItems.BRASS_INGOT.get()))
              .build(prov))
            .register();

    R_ENTITY_STEADYCART = reg.entity(SteadyMinecartEntity.name, SteadyMinecartEntity::new, EntityClassification.MISC)
      .lang("Steady Minecart")
      .register();

    R_ENTITY_CONDUCTOR = reg.entity(ConductorEntity.name, ConductorEntity::new, EntityClassification.MISC)
      .lang(ConductorEntity.defaultDisplayName).properties(p -> p.size(0.5F, 1.3F))
      .register();

    R_ENTITY_HANDCAR = reg.entity(HandcarEntity.name, HandcarEntity::new, EntityClassification.MISC)
            .lang("Handcar")
            .properties(p -> p.size(2, 1.7F))
            .register();
  }

  @OnlyIn(value=Dist.CLIENT)
  public static void registerRenderers () {
    RenderingRegistry.registerEntityRenderingHandler(R_ENTITY_CONDUCTOR.get(), ConductorRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(R_ENTITY_HANDCAR.get(), HandcarRenderer::new);
  }

  @SubscribeEvent
  public static void createEntityAttributes(EntityAttributeCreationEvent event) {
    event.put(ModSetup.R_ENTITY_CONDUCTOR.get(), LivingEntity.createLivingAttributes().add(Attributes.GENERIC_FOLLOW_RANGE, 16).build());
//    event.put(ModSetup.R_ENTITY_HANDCAR.get(), LivingEntity.createLivingAttributes().add(Attributes.GENERIC_FOLLOW_RANGE, 16).build());
  }
}
