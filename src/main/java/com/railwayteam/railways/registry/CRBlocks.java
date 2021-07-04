package com.railwayteam.railways.registry;

import com.railwayteam.railways.content.blocks.*;
import com.railwayteam.railways.content.items.BogieItem;
import com.railwayteam.railways.content.items.SignalItem;
import com.railwayteam.railways.util.RailwaysTags;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.providers.RegistrateRecipeProvider;
import com.simibubi.create.repack.registrate.util.entry.BlockEntry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.Tags;

public class CRBlocks {
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

    public static void register(CreateRegistrate reg) {
        // TODO: consider splitting into ::registerBlocks and ::registerItems, or even to dedicated files?
        R_BLOCK_WAYPOINT = reg.block(WayPointBlock.name, WayPointBlock::new)// tell Registrate how to create it
                .recipe((ctx, prov) -> {
                    ctx.getEntry().recipe(ctx, prov, AllBlocks.SAIL.get());
                    ctx.getEntry().recipe(ctx, prov, AllBlocks.SAIL_FRAME.get());
                })
                .properties(p -> p.hardnessAndResistance(5.0f, 6.0f))    // set block properties
                .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(),                 // block state determines the model
                        prov.models().getExistingFile(prov.modLoc("block/" + ctx.getName())) // hence why that's tucked in here
                ))
                .simpleItem()     // nothing special about the item right now
                .lang("Waypoint") // give it a friendly name
                .register();      // pack it up for Registrate

        R_BLOCK_LARGE_RAIL = reg.block(LargeTrackBlock.name, LargeTrackBlock::new)
                .properties(p -> p.hardnessAndResistance(10.0f, 10.0f).nonOpaque()) //.doesNotBlockMovement())
                .blockstate((ctx, prov) -> prov.getVariantBuilder(ctx.getEntry()).forAllStates(state -> {
                    return ConfiguredModel.builder().modelFile(LargeTrackBlock.partialModel(ctx, prov, state.get(LargeTrackBlock.TRACK_SIDE).getString())).build();
                }))
                .item().model((ctx, prov) -> prov.singleTexture(
                        ctx.getName(),
                        prov.mcLoc("item/generated"),
                        "layer0",
                        prov.modLoc("item/wide_gauge/" + ctx.getName()))).tag(RailwaysTags.Tracks).build()
                .lang("Andesite Track")
                .register();

        R_BLOCK_LARGE_SWITCH = reg.block(LargeSwitchTrackBlock.name, LargeSwitchTrackBlock::new)
                .properties(p -> p.hardnessAndResistance(10.0f, 10.0f).nonOpaque())//.doesNotBlockMovement())
                .blockstate((ctx, prov) -> prov.getVariantBuilder(ctx.getEntry()).forAllStates(state -> {
                    return ConfiguredModel.builder().modelFile(
                            LargeSwitchTrackBlock.partialModel(ctx, prov, state.get(LargeSwitchTrackBlock.SWITCH_SIDE).getString())).build();
                }))
                .item().model((ctx, prov) -> prov.singleTexture(
                        ctx.getName(),
                        prov.mcLoc("item/generated"),
                        "layer0",
                        prov.modLoc("item/wide_gauge/" + ctx.getName()))).tag(RailwaysTags.Tracks).build()
                .lang("Andesite Switch")
                .recipe((ctx, prov) -> ShapelessRecipeBuilder.shapelessRecipe(ctx.get())
                        .addIngredient(R_BLOCK_LARGE_RAIL.get(), 2)
                        .addCriterion("has_tracks", RegistrateRecipeProvider.hasItem(R_BLOCK_LARGE_RAIL.get()))
                        .build(prov))
                .register();

        // TODO: there has to be a cleaner way of creating almost identical blocks than copy pasting

        R_BLOCK_LARGE_RAIL_WOODEN = reg.block(LargeTrackBlock.name + "_wooden", LargeTrackBlock::new)
                .properties(p -> p.hardnessAndResistance(10.0f, 10.0f).nonOpaque().doesNotBlockMovement())
                .blockstate((ctx, prov) -> prov.getVariantBuilder(ctx.getEntry()).forAllStates(state -> {
                    return ConfiguredModel.builder().modelFile(
                            LargeTrackBlock.partialModel(true, ctx, prov, state.get(LargeTrackBlock.TRACK_SIDE).getString())).build();
                }))
                .item().model((ctx, prov) -> prov.singleTexture(
                        ctx.getName(),
                        prov.mcLoc("item/generated"),
                        "layer0",
                        prov.modLoc("item/wide_gauge/" + ctx.getName()))).tag(RailwaysTags.Tracks).build()
                .lang("Wooden Track")
                .register();

        R_BLOCK_LARGE_SWITCH_WOODEN = reg.block(LargeSwitchTrackBlock.name + "_wooden", LargeSwitchTrackBlock::new)
                .properties(p -> p.hardnessAndResistance(10.0f, 10.0f).nonOpaque().doesNotBlockMovement())
                .blockstate((ctx, prov) -> prov.getVariantBuilder(ctx.getEntry()).forAllStates(state -> {
                    return ConfiguredModel.builder().modelFile(
                            LargeSwitchTrackBlock.partialModel(true, ctx, prov, state.get(LargeSwitchTrackBlock.SWITCH_SIDE).getString())).build();
                }))
                .item().model((ctx, prov) -> prov.singleTexture(
                        ctx.getName(),
                        prov.mcLoc("item/generated"),
                        "layer0",
                        prov.modLoc("item/wide_gauge/" + ctx.getName()))).tag(RailwaysTags.Tracks).build()
                .lang("Wooden Switch")
                .recipe((ctx, prov) -> ShapelessRecipeBuilder.shapelessRecipe(ctx.get())
                        .addIngredient(R_BLOCK_LARGE_RAIL_WOODEN.get(), 2)
                        .addCriterion("has_wooden_tracks", RegistrateRecipeProvider.hasItem(R_BLOCK_LARGE_RAIL_WOODEN.get()))
                        .build(prov))
                .register();

        R_BLOCK_SIGNAL = reg.block(SignalBlock.name, SignalBlock::new)
                .properties(p -> p.hardnessAndResistance(10f, 10f).nonOpaque())
                .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.getEntry(),
                        (blockstate) -> (prov.models().getExistingFile(
                                prov.modLoc("block/" + ctx.getName() + (blockstate.get(BlockStateProperties.POWERED) ? "_red" : "_green"))
                        ))))
                .recipe((ctx, prov) -> ShapelessRecipeBuilder.shapelessRecipe(ctx.get())
                        .addIngredient(AllBlocks.ANDESITE_CASING.get())
                        .addIngredient(Items.REDSTONE_TORCH, 2)
                        .addCriterion("has_tracks", RegistrateRecipeProvider.hasItem(RailwaysTags.Tracks))
                        .addCriterion("has_andesite_casing", RegistrateRecipeProvider.hasItem(AllBlocks.ANDESITE_CASING.get()))
                        .build(prov))
                .item(SignalItem::new).build()
                .lang("Track Signal")
                .register();

        R_BLOCK_STATION_SENSOR = reg.block(StationSensorRailBlock.name, StationSensorRailBlock::new)
                .initialProperties(() -> Blocks.DETECTOR_RAIL)
                .properties(p -> p.nonOpaque().doesNotBlockMovement())
                .blockstate((ctx, prov) -> prov.getExistingVariantBuilder(ctx.getEntry()))
                .item().model((ctx, prov) -> prov.getExistingFile(prov.modLoc("block/" + ctx.getName()))).build()
                .tag(BlockTags.RAILS)
                .lang("Station Sensor")
                .recipe((ctx, prov) -> ShapedRecipeBuilder.shapedRecipe(ctx.get(), 6)
                        .patternLine("I I")
                        .patternLine("ILI")
                        .patternLine("IRI")
                        .key('L', AllItems.LAPIS_SHEET.get())
                        .key('R', Tags.Items.DUSTS_REDSTONE)
                        .key('I', Tags.Items.INGOTS_IRON)
                        .addCriterion("has_lapis", RegistrateRecipeProvider.hasItem(Tags.Items.GEMS_LAPIS))
                        .build(prov))
                .register();

        R_BLOCK_WHEEL = reg.block("wheel", Block::new)
                .lang("Wheel")
                .item().model((ctx, prov) -> {
                }).build()
                .blockstate((ctx, prov) -> {
                })
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
                .properties(p -> p.hardnessAndResistance(10f, 10f).nonOpaque())
                .item().model((ctx, prov) -> prov.getExistingFile(prov.modLoc("item/horn"))).build()
                .blockstate((ctx, prov) -> prov.horizontalFaceBlock(ctx.getEntry(),
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
                        .addCriterion("has_brass", RegistrateRecipeProvider.hasItem(AllItems.BRASS_INGOT.get()))
                        .build(prov))
                .lang("Horn")
                .register();

        R_BLOCK_NUMERICAL_SIGNAL = reg.block("speed_signal", SpeedSignalBlock::new)
                .properties(p -> p.hardnessAndResistance(10f, 10f).nonOpaque())
                .simpleItem()
                .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.getEntry(),
                        (blockstate) -> (prov.models().getExistingFile(
                                prov.modLoc("block/speed_signal")
                        ))))
                .recipe((ctx, prov) -> ShapelessRecipeBuilder.shapelessRecipe(ctx.get())
                        .addIngredient(AllBlocks.ANDESITE_CASING.get())
                        .addIngredient(AllBlocks.NIXIE_TUBE.get())
                        .addCriterion("has_nixie_tube", RegistrateRecipeProvider.hasItem(AllBlocks.NIXIE_TUBE.get()))
                        .build(prov))
                .lang("Speed Signal")
                .register();

        R_BLOCK_BOGIE = reg.block("bogie", BogieBlock::new)
                .lang("Bogie")
                .blockstate((smth1, smth2) -> {
                })
                .item(BogieItem::new).model((smth1, smth2) -> {
                }).build()
                .recipe((ctx, prov) -> ShapedRecipeBuilder.shapedRecipe(ctx.get())
                        .patternLine("WBW")
                        .patternLine("SMS")
                        .patternLine("WBW")
                        .key('W', R_BLOCK_WHEEL.get())
                        .key('B', AllBlocks.METAL_BRACKET.get())
                        .key('M', AllBlocks.MECHANICAL_BEARING.get())
                        .key('S', AllBlocks.SHAFT.get())
                        .addCriterion("has_wheel", RegistrateRecipeProvider.hasItem(R_BLOCK_WHEEL.get()))
                        .build(prov))
                .register();
    }
}
