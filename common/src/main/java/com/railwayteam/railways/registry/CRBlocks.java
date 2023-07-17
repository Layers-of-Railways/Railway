package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.base.data.BuilderTransformers;
import com.railwayteam.railways.content.conductor.vent.CopycatVentModel;
import com.railwayteam.railways.content.conductor.vent.VentBlock;
import com.railwayteam.railways.content.conductor.whistle.ConductorWhistleFlagBlock;
import com.railwayteam.railways.content.conductor.whistle.ConductorWhistleItem;
import com.railwayteam.railways.content.coupling.TrackCouplerDisplaySource;
import com.railwayteam.railways.content.coupling.coupler.TrackCouplerBlock;
import com.railwayteam.railways.content.coupling.coupler.TrackCouplerBlockItem;
import com.railwayteam.railways.content.custom_bogeys.DoubleAxleBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.LargePlatformDoubleAxleBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.SingleAxleBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.TripleAxleBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.invisible.InvisibleBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.monobogey.MonoBogeyBlock;
import com.railwayteam.railways.content.custom_tracks.CustomTrackBlockStateGenerator;
import com.railwayteam.railways.content.custom_tracks.monorail.MonorailBlockStateGenerator;
import com.railwayteam.railways.content.distant_signals.SemaphoreDisplayTarget;
import com.railwayteam.railways.content.semaphore.SemaphoreBlock;
import com.railwayteam.railways.content.semaphore.SemaphoreItem;
import com.railwayteam.railways.content.smokestack.AxisSmokeStackBlock;
import com.railwayteam.railways.content.smokestack.DieselSmokeStackBlock;
import com.railwayteam.railways.content.smokestack.SmokeStackBlock;
import com.railwayteam.railways.content.smokestack.SmokeStackMovementBehaviour;
import com.railwayteam.railways.content.switches.SwitchDisplaySource;
import com.railwayteam.railways.content.switches.TrackSwitchBlock;
import com.railwayteam.railways.content.switches.TrackSwitchBlockItem;
import com.railwayteam.railways.content.tender.TenderBlock;
import com.railwayteam.railways.multiloader.CommonTags;
import com.railwayteam.railways.util.ShapeWrapper;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockItem;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackModel;
import com.simibubi.create.foundation.block.ItemUseOverrides;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.generators.ConfiguredModel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.railwayteam.railways.content.conductor.vent.VentBlock.CONDUCTOR_VISIBLE;
import static com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours.assignDataBehaviour;
import static com.simibubi.create.foundation.data.BuilderTransformers.copycat;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOnly;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class CRBlocks {

    private static final CreateRegistrate REGISTRATE = Railways.registrate();

    private static BlockEntry<TrackBlock> makeTrack(TrackMaterial material) {
        return makeTrack(material, new CustomTrackBlockStateGenerator()::generate);
    }

    private static BlockEntry<TrackBlock> makeTrack(TrackMaterial material, NonNullBiConsumer<DataGenContext<Block, TrackBlock>, RegistrateBlockstateProvider> blockstateGen) {
        return makeTrack(material, blockstateGen, (t) -> {
        });
    }

    private static BlockEntry<TrackBlock> makeTrack(TrackMaterial material, NonNullBiConsumer<DataGenContext<Block, TrackBlock>, RegistrateBlockstateProvider> blockstateGen, NonNullConsumer<? super TrackBlock> onRegister) {
        return makeTrack(material, blockstateGen, onRegister, (p) -> p);
    }

    private static BlockEntry<TrackBlock> makeTrack(TrackMaterial material, NonNullBiConsumer<DataGenContext<Block, TrackBlock>, RegistrateBlockstateProvider> blockstateGen, Function<BlockBehaviour.Properties, BlockBehaviour.Properties> collectProperties) {
        return makeTrack(material, blockstateGen, (t) -> {
        }, collectProperties);
    }

    private static BlockEntry<TrackBlock> makeTrack(TrackMaterial material, NonNullBiConsumer<DataGenContext<Block, TrackBlock>, RegistrateBlockstateProvider> blockstateGen, NonNullConsumer<? super TrackBlock> onRegister, Function<BlockBehaviour.Properties, BlockBehaviour.Properties> collectProperties) {
        List<TagKey<Block>> trackTags = new ArrayList<>();
        trackTags.add(AllTags.AllBlockTags.TRACKS.tag);
        if (material.trackType != CRTrackMaterials.CRTrackType.MONORAIL)
            trackTags.add(AllTags.AllBlockTags.GIRDABLE_TRACKS.tag);
        //noinspection unchecked
        return REGISTRATE.block("track_" + material.resourceName(), material::createBlock)
                .initialProperties(Material.STONE)
                .properties(p -> collectProperties.apply(p)
                        .color(MaterialColor.METAL)
                        .strength(0.8F)
                        .sound(SoundType.METAL)
                        .noOcclusion())
                .addLayer(() -> RenderType::cutoutMipped)
                .transform(pickaxeOnly())
                .blockstate(blockstateGen)
                .tag(CommonTags.RELOCATION_NOT_SUPPORTED.forge, CommonTags.RELOCATION_NOT_SUPPORTED.fabric)
                .tag((TagKey<Block>[]) trackTags.toArray(new TagKey[0])) // keep the cast, or stuff breaks
                .lang(material.langName + " Train Track")
                .onRegister(onRegister)
                .onRegister(CreateRegistrate.blockModel(() -> TrackModel::new))
                .onRegister(CRTrackMaterials::addToBlockEntityType)
                .item(TrackBlockItem::new)
                .model((c, p) -> p.generated(c, Railways.asResource("item/track/" + c.getName())))
                .build()
                .register();
    }

    private static BlockEntry<SmokeStackBlock> makeSmokeStack(String variant, SmokeStackBlock.SmokeStackType type, String description, VoxelShape shape, boolean emitStationarySmoke) {
        return makeSmokeStack(variant, type, description, false, ShapeWrapper.wrapped(shape), true, emitStationarySmoke);
    }

    private static BlockEntry<SmokeStackBlock> makeSmokeStack(String variant, SmokeStackBlock.SmokeStackType type, String description, VoxelShape shape, boolean spawnExtraSmoke, boolean emitStationarySmoke) {
        return makeSmokeStack(variant, type, description, false, ShapeWrapper.wrapped(shape), spawnExtraSmoke, emitStationarySmoke);
    }

    private static BlockEntry<SmokeStackBlock> makeSmokeStack(String variant, SmokeStackBlock.SmokeStackType type, String description, boolean rotates, ShapeWrapper shape, boolean spawnExtraSmoke, boolean emitStationarySmoke) {
        ResourceLocation modelLoc = Railways.asResource("block/smokestack/block_" + variant);
        MovementBehaviour movementBehaviour = new SmokeStackMovementBehaviour(spawnExtraSmoke);
        return REGISTRATE.block("smokestack_" + variant, p -> rotates ? new AxisSmokeStackBlock(p, type, shape, emitStationarySmoke) : new SmokeStackBlock(p, type, shape, emitStationarySmoke))
                .initialProperties(SharedProperties::softMetal)
                .blockstate((c, p) -> {
//                rotates ? p.axisBlock(c.get(), p.models().getExistingFile(modelLoc)) : null
                    if (rotates) {
                        p.getVariantBuilder(c.get())
                                .forAllStates(state -> ConfiguredModel.builder()
                                        .modelFile(p.models().getExistingFile(modelLoc))
                                        .rotationY((state.getValue(BlockStateProperties.HORIZONTAL_AXIS) == Direction.Axis.X ? 90 : 0))
                                        .build());
                    } else {
                        p.getVariantBuilder(c.get())
                                .forAllStates(state -> ConfiguredModel.builder()
                                        .modelFile(p.models().getExistingFile(modelLoc))
                                        .build());
                    }
                })
                .properties(p -> p.color(MaterialColor.COLOR_GRAY))
                .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
                .properties(p -> p.noOcclusion())
                .addLayer(() -> RenderType::cutoutMipped)
                .transform(pickaxeOnly())
                .onRegister(AllMovementBehaviours.movementBehaviour(movementBehaviour))
                .lang(description)
                .item()
                .model((c, p) -> p.withExistingParent("item/" + c.getName(), modelLoc))
                .build()
                .register();
    }

    public static final BlockEntry<TenderBlock> BLOCK_TENDER = null;

//  commented out because I'm pretty sure but not 100% that it was removed.
//    static {
//        REGISTRATE.startSection(AllSections.LOGISTICS);
//    }

    public static final BlockEntry<SemaphoreBlock> SEMAPHORE = REGISTRATE.block("semaphore", SemaphoreBlock::new)
            .initialProperties(SharedProperties::softMetal)
            //.blockstate((ctx,prov)->prov.horizontalBlock(ctx.get(), blockState -> prov.models()
            //.getExistingFile(prov.modLoc("block/" + ctx.getName() + "/block"))))
            .blockstate((ctx, prov) -> prov.getVariantBuilder(ctx.getEntry())
                    .forAllStates(state -> ConfiguredModel.builder()
                            .modelFile(prov.models().getExistingFile(prov.modLoc(
                                    "block/semaphore/block" +
                                            (state.getValue(SemaphoreBlock.FULL) ? "_full" : "") +
                                            (state.getValue(SemaphoreBlock.FLIPPED) ? "_flipped" : "") +
                                            (state.getValue(SemaphoreBlock.UPSIDE_DOWN) ? "_down" : ""))))
                            .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                            .build()
                    )
            )
            .properties(p -> p.color(MaterialColor.COLOR_GRAY))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .onRegister(assignDataBehaviour(new SemaphoreDisplayTarget()))
            .item(SemaphoreItem::new).transform(customItemModel())
            .transform(axeOnly())
            .addLayer(() -> RenderType::translucent)
            .register();

    public static final BlockEntry<TrackCouplerBlock> TRACK_COUPLER =
            REGISTRATE.block("track_coupler", TrackCouplerBlock::create)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(p -> p.color(MaterialColor.PODZOL))
                    .properties(p -> p.noOcclusion())
                    .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
                    .blockstate((c, p) -> {
                        p.getVariantBuilder(c.get()).forAllStatesExcept(state -> ConfiguredModel.builder()
                                .modelFile(AssetLookup.partialBaseModel(c, p, state.getValue(TrackCouplerBlock.MODE).getSerializedName()))
                                .build(), TrackCouplerBlock.POWERED);
                    })
                    .transform(pickaxeOnly())
                    .onRegister(assignDataBehaviour(new TrackCouplerDisplaySource(), "track_coupler_info"))
                    .lang("Train Coupler")
                    .item(TrackCouplerBlockItem.ofType(CREdgePointTypes.COUPLER))
                    .transform(customItemModel("_", "block_both"))
                    .register();

    public static final BlockEntry<TrackSwitchBlock> ANDESITE_SWITCH =
            REGISTRATE.block("track_switch_andesite", TrackSwitchBlock::manual)
                    .initialProperties(SharedProperties::softMetal)
                    .blockstate((c, p) -> p.getVariantBuilder(c.get())
                            .forAllStatesExcept(
                                    state -> ConfiguredModel.builder()
                                            .modelFile(p.models().getExistingFile(Railways.asResource("block/track_switch_andesite/block")))
                                            .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 90) % 360)
                                            .build(),
                                    TrackSwitchBlock.LOCKED//, TrackSwitchBlock.STATE
                            ))
                    .properties(p -> p.color(MaterialColor.PODZOL))
                    .properties(p -> p.noOcclusion())
                    .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
                    .transform(pickaxeOnly())
                    .onRegister(assignDataBehaviour(new SwitchDisplaySource()))
                    .onRegister(ItemUseOverrides::addBlock)
                    .lang("Andesite Track Switch")
                    .item(TrackSwitchBlockItem.ofType(CREdgePointTypes.SWITCH))
                    .transform(customItemModel())
                    .register();

    public static final BlockEntry<TrackSwitchBlock> BRASS_SWITCH =
            REGISTRATE.block("track_switch_brass", TrackSwitchBlock::automatic)
                    .initialProperties(SharedProperties::softMetal)
                    .blockstate((c, p) -> p.getVariantBuilder(c.get())
                            .forAllStatesExcept(state -> ConfiguredModel.builder()
                                    .modelFile(p.models().getExistingFile(Railways.asResource("block/track_switch_brass/block")))
                                    .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 90) % 360)
                                    .build(), TrackSwitchBlock.LOCKED))
                    .properties(p -> p.color(MaterialColor.TERRACOTTA_BROWN))
                    .properties(p -> p.noOcclusion())
                    .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
                    .transform(pickaxeOnly())
                    .onRegister(assignDataBehaviour(new SwitchDisplaySource()))
                    .onRegister(ItemUseOverrides::addBlock)
                    .lang("Brass Track Switch")
                    .item(TrackSwitchBlockItem.ofType(CREdgePointTypes.SWITCH))
                    .transform(customItemModel())
                    .register();

    public static final BlockEntry<TrackBlock> ACACIA_TRACK = makeTrack(CRTrackMaterials.ACACIA);
    public static final BlockEntry<TrackBlock> BIRCH_TRACK = makeTrack(CRTrackMaterials.BIRCH);
    public static final BlockEntry<TrackBlock> CRIMSON_TRACK = makeTrack(CRTrackMaterials.CRIMSON);
    public static final BlockEntry<TrackBlock> DARK_OAK_TRACK = makeTrack(CRTrackMaterials.DARK_OAK);
    public static final BlockEntry<TrackBlock> JUNGLE_TRACK = makeTrack(CRTrackMaterials.JUNGLE);
    public static final BlockEntry<TrackBlock> OAK_TRACK = makeTrack(CRTrackMaterials.OAK);
    public static final BlockEntry<TrackBlock> SPRUCE_TRACK = makeTrack(CRTrackMaterials.SPRUCE);
    public static final BlockEntry<TrackBlock> WARPED_TRACK = makeTrack(CRTrackMaterials.WARPED);
    public static final BlockEntry<TrackBlock> BLACKSTONE_TRACK = makeTrack(CRTrackMaterials.BLACKSTONE);
    public static final BlockEntry<TrackBlock> ENDER_TRACK = makeTrack(CRTrackMaterials.ENDER);
    public static final BlockEntry<TrackBlock> TIELESS_TRACK = makeTrack(CRTrackMaterials.TIELESS);
    public static final BlockEntry<TrackBlock> PHANTOM_TRACK = makeTrack(CRTrackMaterials.PHANTOM);
    public static final BlockEntry<TrackBlock> MANGROVE_TRACK = makeTrack(CRTrackMaterials.MANGROVE);
    public static final BlockEntry<TrackBlock> MONORAIL_TRACK = makeTrack(CRTrackMaterials.MONORAIL,
            new MonorailBlockStateGenerator()::generate, BlockBehaviour.Properties::randomTicks);

    public static final BlockEntry<MonoBogeyBlock> MONO_BOGEY =
            REGISTRATE.block("mono_bogey", MonoBogeyBlock::new)
                    .properties(p -> p.color(MaterialColor.PODZOL))
                    .transform(BuilderTransformers.monobogey())
                    .lang("Monorail Bogey")
                    .register();

    public static final BlockEntry<InvisibleBogeyBlock> INVISIBLE_BOGEY =
            REGISTRATE.block("invisible_bogey", InvisibleBogeyBlock::new)
                    .properties(p -> p.color(MaterialColor.PODZOL))
                    .transform(BuilderTransformers.invisibleBogey())
                    .lang("Invisible Bogey")
                    .register();

    public static final BlockEntry<SingleAxleBogeyBlock> SINGLEAXLE_BOGEY =
            REGISTRATE.block("singleaxle_bogey", SingleAxleBogeyBlock::new)
                    .properties(p -> p.color(MaterialColor.PODZOL))
                    .transform(BuilderTransformers.standardBogey())
                    .lang("Single Axle Bogey")
                    .register();

    public static final BlockEntry<DoubleAxleBogeyBlock> DOUBLEAXLE_BOGEY =
            REGISTRATE.block("doubleaxle_bogey", DoubleAxleBogeyBlock::new)
                    .properties(p -> p.color(MaterialColor.PODZOL))
                    .transform(BuilderTransformers.standardBogey())
                    .lang("Double Axle Bogey")
                    .register();

    public static final BlockEntry<LargePlatformDoubleAxleBogeyBlock> LARGE_PLATFORM_DOUBLEAXLE_BOGEY =
            REGISTRATE.block("large_platform_doubleaxle_bogey", LargePlatformDoubleAxleBogeyBlock::new)
                    .properties(p -> p.color(MaterialColor.PODZOL))
                    .transform(BuilderTransformers.standardBogey())
                    .lang("Large Platform Double Axle Bogey")
                    .register();

    public static final BlockEntry<TripleAxleBogeyBlock> TRIPLEAXLE_BOGEY =
            REGISTRATE.block("tripleaxle_bogey", TripleAxleBogeyBlock::new)
                    .properties(p -> p.color(MaterialColor.PODZOL))
                    .transform(BuilderTransformers.standardBogey())
                    .lang("Triple Axle Bogey")
                    .register();


    public static final BlockEntry<ConductorWhistleFlagBlock> CONDUCTOR_WHISTLE_FLAG =
            REGISTRATE.block("conductor_whistle", ConductorWhistleFlagBlock::new)
                    .initialProperties(SharedProperties::wooden)
                    .properties(p -> p.color(MaterialColor.COLOR_BROWN))
                    .properties(p -> p.noOcclusion())
                    .properties(p -> p.sound(SoundType.WOOD))
                    .properties(p -> p.instabreak())
                    .properties(p -> p.noLootTable())
                    .properties(p -> p.noCollission())
                    .blockstate((c, p) -> p.getVariantBuilder(c.get())
                            .forAllStates(state -> ConfiguredModel.builder()
                                    .modelFile(AssetLookup.partialBaseModel(c, p, "pole"))
                                    .build()))
                    .lang("Conductor Whistle")
                    .item(ConductorWhistleItem::new)
                    .transform(customItemModel())
                    .register();

//    static {
//        REGISTRATE.startSection(AllSections.PALETTES);
//    }

    /*
    smokestacks:
    caboosestyle
    coalburner
    diesel
    oilburner
    streamlined
    woodburner
     */
    public static final BlockEntry<SmokeStackBlock>
            CABOOSESTYLE_STACK = makeSmokeStack("caboosestyle", new SmokeStackBlock.SmokeStackType(0.5, 10 / 16.0d, 0.5), "Caboose Smokestack", true, com.railwayteam.railways.util.ShapeWrapper.wrapped(CRShapes.CABOOSE_STACK), false, true),
            LONG_STACK = makeSmokeStack("long", new SmokeStackBlock.SmokeStackType(0.5, 10 / 16.0d, 0.5), "Long Smokestack", true, ShapeWrapper.wrapped(CRShapes.LONG_STACK), true, true),
            COALBURNER_STACK = makeSmokeStack("coalburner", new SmokeStackBlock.SmokeStackType(0.5, 1.0, 0.5), "Coalburner Smokestack", CRShapes.COAL_STACK, true),
            OILBURNER_STACK = makeSmokeStack("oilburner", new SmokeStackBlock.SmokeStackType(new Vec3(0.5, 0.4, 0.5), new Vec3(0.2, 0.2, 0.2)), "Oilburner Smokestack", CRShapes.OIL_STACK, true),
            STREAMLINED_STACK = makeSmokeStack("streamlined", new SmokeStackBlock.SmokeStackType(new Vec3(0.5, 0.2, 0.5), new Vec3(0.25, 0.2, 0.25)), "Streamlined Smokestack", CRShapes.STREAMLINED_STACK, true),
            WOODBURNER_STACK = makeSmokeStack("woodburner", new SmokeStackBlock.SmokeStackType(0.5, 12 / 16.0d, 0.5), "Woodburner Smokestack", CRShapes.WOOD_STACK, true);

    public static final BlockEntry<DieselSmokeStackBlock> DIESEL_STACK = REGISTRATE.block("smokestack_diesel", p -> new DieselSmokeStackBlock(p, new SmokeStackBlock.SmokeStackType(0.5, 0.25, 0.5), ShapeWrapper.wrapped(CRShapes.DIESEL_STACK), false))
            .initialProperties(SharedProperties::softMetal)
            .blockstate((c, p) -> p.getVariantBuilder(c.get())
                    .forAllStates(state -> ConfiguredModel.builder()
                            .modelFile(p.models().getExistingFile(Railways.asResource("block/smokestack/block_diesel_case")))
                            .build()))
            .properties(p -> p.color(MaterialColor.COLOR_GRAY))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .properties(p -> p.noOcclusion())
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(pickaxeOnly())
            .onRegister(AllMovementBehaviours.movementBehaviour(new SmokeStackMovementBehaviour(true, false, false)))
            .lang("Radiator Fan")
            .item()
            .model((c, p) -> p.withExistingParent("item/" + c.getName(), Railways.asResource("block/smokestack/block_diesel")))
            .build()
            .register();

    public static final BlockEntry<VentBlock> CONDUCTOR_VENT =
            REGISTRATE.block("conductor_vent", VentBlock::create)
                    .transform(copycat())
                    .blockstate((c, p) -> p.getVariantBuilder(c.get())
                            .forAllStates(state -> ConfiguredModel.builder()
                                    .modelFile(p.models().getExistingFile(state.getValue(CONDUCTOR_VISIBLE) ?
                                            Railways.asResource("block/copycat_vent_visible") :
                                            new ResourceLocation("block/air")))
                                    .build()))
                    .properties(p -> p.isSuffocating((state, level, pos) -> false))
                    .onRegister(CreateRegistrate.blockModel(() -> CopycatVentModel::create))
                    .lang("Vent Block")
                    .recipe((c, p) -> p.stonecutting(DataIngredient.items(AllBlocks.INDUSTRIAL_IRON_BLOCK), c, 2))
                    .item()
                    .transform(customItemModel("copycat_vent"))
                    .register();

  /*
    BLOCK_TENDER = reg.block("tender", TenderBlock::new)
    .blockstate((ctx,prov)->
      prov.getVariantBuilder(ctx.get()).forAllStates(state -> {
        ResourceLocation loc = prov.modLoc("block/tender/" + state.getValue(TenderBlock.SHAPE).getSerializedName());
        return ConfiguredModel.builder().modelFile(prov.models().getExistingFile(loc))
        .rotationY(switch(state.getValue(TenderBlock.FACING)) {
          case SOUTH -> 180;
          case EAST  ->  90;
          case WEST  -> -90;
          default    ->   0;
        })
        .build();
      }))
    .item()
      .model((ctx,prov)-> prov.getExistingFile(prov.modLoc("tender")))
      .build()
    .lang("Tender")
    .addLayer(()-> RenderType::cutoutMipped)
    .register();*/

    @SuppressWarnings("EmptyMethod")
    public static void register() {
    }
}
