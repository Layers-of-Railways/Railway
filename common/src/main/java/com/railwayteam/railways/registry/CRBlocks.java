package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.base.data.BuilderTransformers;
import com.railwayteam.railways.content.buffer.*;
import com.railwayteam.railways.content.buffer.headstock.CopycatHeadstockBlock;
import com.railwayteam.railways.content.buffer.headstock.HeadstockBlock;
import com.railwayteam.railways.content.buffer.headstock.HeadstockStyle;
import com.railwayteam.railways.content.buffer.single_deco.GenericDyeableSingleBufferBlock;
import com.railwayteam.railways.content.buffer.single_deco.LinkPinBlock;
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
import com.railwayteam.railways.content.custom_bogeys.monobogey.InvisibleMonoBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.monobogey.MonoBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.narrow_gauge.NarrowGaugeBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.narrow_gauge.NarrowGaugeBogeyBlock.NarrowGaugeStandardStyle;
import com.railwayteam.railways.content.custom_bogeys.wide_gauge.WideGaugeBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.wide_gauge.WideGaugeComicallyLargeBogeyBlock;
import com.railwayteam.railways.content.custom_tracks.CustomTrackBlockStateGenerator;
import com.railwayteam.railways.content.custom_tracks.casing.CasingCollisionBlock;
import com.railwayteam.railways.content.custom_tracks.generic_crossing.GenericCrossingBlock;
import com.railwayteam.railways.content.custom_tracks.monorail.MonorailBlockStateGenerator;
import com.railwayteam.railways.content.custom_tracks.narrow_gauge.NarrowGaugeTrackBlockStateGenerator;
import com.railwayteam.railways.content.custom_tracks.wide_gauge.WideGaugeTrackBlockStateGenerator;
import com.railwayteam.railways.content.distant_signals.SemaphoreDisplayTarget;
import com.railwayteam.railways.content.handcar.HandcarBlock;
import com.railwayteam.railways.content.handcar.HandcarControlsInteractionBehaviour;
import com.railwayteam.railways.content.handcar.HandcarItem;
import com.railwayteam.railways.content.semaphore.SemaphoreBlock;
import com.railwayteam.railways.content.semaphore.SemaphoreItem;
import com.railwayteam.railways.content.smokestack.*;
import com.railwayteam.railways.content.switches.SwitchDisplaySource;
import com.railwayteam.railways.content.switches.TrackSwitchBlock;
import com.railwayteam.railways.content.switches.TrackSwitchBlockItem;
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
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.railwayteam.railways.content.conductor.vent.VentBlock.CONDUCTOR_VISIBLE;
import static com.simibubi.create.AllInteractionBehaviours.interactionBehaviour;
import static com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours.assignDataBehaviour;
import static com.simibubi.create.foundation.data.BuilderTransformers.copycat;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.*;

@SuppressWarnings("unused")
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

    private static BlockEntry<TrackBlock> makeTrack(NonNullBiConsumer<DataGenContext<Block, TrackBlock>, RegistrateBlockstateProvider> blockstateGen, Function<BlockBehaviour.Properties, BlockBehaviour.Properties> collectProperties) {
        return makeTrack(CRTrackMaterials.MONORAIL, blockstateGen, (t) -> {
        }, collectProperties);
    }

    private static BlockEntry<TrackBlock> makeTrack(TrackMaterial material, NonNullBiConsumer<DataGenContext<Block, TrackBlock>, RegistrateBlockstateProvider> blockstateGen, NonNullConsumer<? super TrackBlock> onRegister, Function<BlockBehaviour.Properties, BlockBehaviour.Properties> collectProperties) {
        List<TagKey<Block>> trackTags = new ArrayList<>();
        trackTags.add(AllTags.AllBlockTags.TRACKS.tag);
        if (material.trackType != CRTrackMaterials.CRTrackType.MONORAIL)
            trackTags.add(AllTags.AllBlockTags.GIRDABLE_TRACKS.tag);
        List<TagKey<Item>> itemTags = new ArrayList<>();
        if (material == CRTrackMaterials.PHANTOM || material == CRTrackMaterials.getWide(CRTrackMaterials.PHANTOM) || material == CRTrackMaterials.getNarrow(CRTrackMaterials.PHANTOM)) {
            itemTags.add(CRTags.AllItemTags.PHANTOM_TRACK_REVEALING.tag);
        }
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
            .tag((TagKey<Item>[]) itemTags.toArray(new TagKey[0]))
            .build()
            .register();
    }

    private static BlockEntry<SmokeStackBlock> makeSmokeStack(String variant, SmokeStackBlock.SmokeStackType type, String description, VoxelShape shape, boolean emitStationarySmoke) {
        return makeSmokeStack(variant, type, description, false, ShapeWrapper.wrapped(shape), true, emitStationarySmoke);
    }

    private static BlockEntry<SmokeStackBlock> makeSmokeStack(String variant, SmokeStackBlock.SmokeStackType type, String description, VoxelShape shape, boolean spawnExtraSmoke, boolean emitStationarySmoke) {
        return makeSmokeStack(variant, type, description, false, ShapeWrapper.wrapped(shape), spawnExtraSmoke, emitStationarySmoke);
    }

    @FunctionalInterface
    private interface SmokeStackFunction<T extends SmokeStackBlock> {
        T create(BlockBehaviour.Properties properties, SmokeStackBlock.SmokeStackType type, ShapeWrapper shape, boolean emitStationarySmoke);
    }

    private static BlockEntry<SmokeStackBlock> makeSmokeStack(String variant, SmokeStackBlock.SmokeStackType type, String description, boolean rotates, ShapeWrapper shape, boolean spawnExtraSmoke, boolean emitStationarySmoke) {
        ResourceLocation modelLoc = Railways.asResource("block/smokestack/block_" + variant);
        return makeSmokeStack(variant, type, description, rotates, shape, spawnExtraSmoke, emitStationarySmoke, (c, p) -> {
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
        }, rotates ? AxisSmokeStackBlock::new : SmokeStackBlock::new);
    }

    private static BlockEntry<SmokeStackBlock> makeSmokeStack(String variant, SmokeStackBlock.SmokeStackType type, String description, boolean rotates, ShapeWrapper shape, boolean spawnExtraSmoke, boolean emitStationarySmoke, NonNullBiConsumer<DataGenContext<Block, SmokeStackBlock>, RegistrateBlockstateProvider> blockStateProvider, SmokeStackFunction<SmokeStackBlock> blockFunction) {
        ResourceLocation modelLoc = Railways.asResource("block/smokestack/block_" + variant);
        MovementBehaviour movementBehaviour = new SmokeStackMovementBehaviour(spawnExtraSmoke);
        return REGISTRATE.block("smokestack_" + variant, p -> blockFunction.create(p, type, shape, emitStationarySmoke))
            .initialProperties(SharedProperties::softMetal)
            .blockstate(blockStateProvider::accept)
            .properties(p -> p.color(MaterialColor.COLOR_GRAY))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(pickaxeOnly())
            .onRegister(AllMovementBehaviours.movementBehaviour(movementBehaviour))
            .lang(description)
            .item()
            .model((c, p) -> p.withExistingParent("item/" + c.getName(), modelLoc))
            .build()
            .register();
    }


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
            .properties(BlockBehaviour.Properties::noOcclusion)
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .blockstate((c, p) -> p.getVariantBuilder(c.get()).forAllStatesExcept(state -> ConfiguredModel.builder()
                            .modelFile(AssetLookup.partialBaseModel(c, p, state.getValue(TrackCouplerBlock.MODE).getSerializedName()))
                            .build(), TrackCouplerBlock.POWERED))
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
            .properties(BlockBehaviour.Properties::noOcclusion)
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
            .properties(BlockBehaviour.Properties::noOcclusion)
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .transform(pickaxeOnly())
            .onRegister(assignDataBehaviour(new SwitchDisplaySource()))
            .onRegister(ItemUseOverrides::addBlock)
            .lang("Brass Track Switch")
            .item(TrackSwitchBlockItem.ofType(CREdgePointTypes.SWITCH))
            .transform(customItemModel())
            .register();

    public static final BlockEntry<CasingCollisionBlock> CASING_COLLISION = REGISTRATE.block("casing_collision", CasingCollisionBlock::create)
        .initialProperties((new Material.Builder(MaterialColor.METAL))
            .replaceable()
            .build())
        .blockstate((c, p) -> p.simpleBlock(c.get(), p.models()
            .withExistingParent(c.getName(), p.mcLoc("block/air"))))
        .lang("Track Casing Collision Block")
        .register();

    static {
        Railways.registrate().creativeModeTab(() -> CRItems.tracksCreativeTab, "Create Steam 'n' Rails: Tracks");
    }

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

    public static final Map<TrackMaterial, NonNullSupplier<TrackBlock>> WIDE_GAUGE_TRACKS = new HashMap<>();
    public static final Map<TrackMaterial, NonNullSupplier<TrackBlock>> NARROW_GAUGE_TRACKS = new HashMap<>();

    static {
        for (TrackMaterial wideMaterial : CRTrackMaterials.WIDE_GAUGE.values()) {
            WIDE_GAUGE_TRACKS.put(wideMaterial, makeTrack(wideMaterial, new WideGaugeTrackBlockStateGenerator()::generate));
        }

        for (TrackMaterial narrowMaterial : CRTrackMaterials.NARROW_GAUGE.values()) {
            NARROW_GAUGE_TRACKS.put(narrowMaterial, makeTrack(narrowMaterial, new NarrowGaugeTrackBlockStateGenerator()::generate));
        }
    }

    public static final BlockEntry<TrackBlock> MONORAIL_TRACK = makeTrack(
        new MonorailBlockStateGenerator()::generate, BlockBehaviour.Properties::randomTicks);

    static {
        Railways.registrate().creativeModeTab(() -> CRItems.mainCreativeTab);
    }

    public static final BlockEntry<MonoBogeyBlock> MONO_BOGEY =
        REGISTRATE.block("mono_bogey", MonoBogeyBlock::new)
            .properties(p -> p.color(MaterialColor.PODZOL))
            .transform(BuilderTransformers.monobogey())
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .lang("Monorail Bogey")
            .register();

    public static final BlockEntry<InvisibleBogeyBlock> INVISIBLE_BOGEY =
        REGISTRATE.block("invisible_bogey", InvisibleBogeyBlock::new)
            .properties(p -> p.color(MaterialColor.PODZOL))
            .transform(BuilderTransformers.invisibleBogey())
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .lang("Invisible Bogey")
            .register();

    public static final BlockEntry<InvisibleMonoBogeyBlock> INVISIBLE_MONO_BOGEY =
        REGISTRATE.block("invisible_mono_bogey", InvisibleMonoBogeyBlock::new)
            .properties(p -> p.color(MaterialColor.PODZOL))
            .transform(BuilderTransformers.invisibleMonoBogey())
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .lang("Invisible Mono Bogey")
            .register();

    public static final BlockEntry<SingleAxleBogeyBlock> SINGLEAXLE_BOGEY =
        REGISTRATE.block("singleaxle_bogey", SingleAxleBogeyBlock::new)
            .properties(p -> p.color(MaterialColor.PODZOL))
            .transform(BuilderTransformers.standardBogey()) // handles SAFE_NBT
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

    public static final BlockEntry<WideGaugeBogeyBlock> WIDE_DOUBLEAXLE_BOGEY =
        REGISTRATE.block("wide_doubleaxle_bogey", WideGaugeBogeyBlock.create(false))
            .properties(p -> p.color(MaterialColor.PODZOL))
            .transform(BuilderTransformers.wideBogey())
            .lang("Wide Gauge Double Axle Bogey")
            .register();

    public static final BlockEntry<WideGaugeBogeyBlock> WIDE_SCOTCH_BOGEY =
        REGISTRATE.block("wide_scotch_bogey", WideGaugeBogeyBlock.create(true))
            .properties(p -> p.color(MaterialColor.PODZOL))
            .transform(BuilderTransformers.wideBogey())
            .lang("Wide Gauge Scotch Yoke Bogey")
            .register();

    public static final BlockEntry<WideGaugeComicallyLargeBogeyBlock> WIDE_COMICALLY_LARGE_BOGEY =
        REGISTRATE.block("wide_comically_large_bogey", WideGaugeComicallyLargeBogeyBlock::new)
            .properties(p -> p.color(MaterialColor.PODZOL))
            .transform(BuilderTransformers.wideBogey())
            .lang("Wide Gauge Comically Large Bogey")
            .register();

    public static final BlockEntry<NarrowGaugeBogeyBlock> NARROW_SMALL_BOGEY =
        REGISTRATE.block("narrow_small_bogey", NarrowGaugeBogeyBlock.create(NarrowGaugeStandardStyle.SMALL))
            .properties(p -> p.color(MaterialColor.PODZOL))
            .transform(BuilderTransformers.narrowBogey())
            .lang("Narrow Gauge Small Bogey")
            .register();

    public static final BlockEntry<NarrowGaugeBogeyBlock> NARROW_SCOTCH_BOGEY =
        REGISTRATE.block("narrow_scotch_bogey", NarrowGaugeBogeyBlock.create(NarrowGaugeStandardStyle.SCOTCH_YOKE))
            .properties(p -> p.color(MaterialColor.PODZOL))
            .transform(BuilderTransformers.narrowBogey())
            .lang("Narrow Gauge Scotch Yoke Bogey")
            .register();

    public static final BlockEntry<NarrowGaugeBogeyBlock> NARROW_DOUBLE_SCOTCH_BOGEY =
        REGISTRATE.block("narrow_double_scotch_bogey", NarrowGaugeBogeyBlock.create(NarrowGaugeStandardStyle.DOUBLE_SCOTCH_YOKE))
            .properties(p -> p.color(MaterialColor.PODZOL))
            .transform(BuilderTransformers.narrowBogey())
            .lang("Narrow Gauge Double Scotch Yoke Bogey")
            .register();

    public static final BlockEntry<HandcarBlock> HANDCAR =
        REGISTRATE.block("handcar", HandcarBlock::new)
            .properties(p -> p.color(MaterialColor.PODZOL))
            .transform(BuilderTransformers.handcar())
            .onRegister(interactionBehaviour(new HandcarControlsInteractionBehaviour()))
            .item(HandcarItem::new)
            .properties(p -> p.stacksTo(1))
            .model((c, p) -> p.generated(c, Railways.asResource("item/" + c.getName())))
            .build()
            .lang("Handcar")
            .register();

    public static final BlockEntry<ConductorWhistleFlagBlock> CONDUCTOR_WHISTLE_FLAG =
        REGISTRATE.block("conductor_whistle", ConductorWhistleFlagBlock::new)
            .initialProperties(SharedProperties::wooden)
            .properties(p -> p
                    .color(MaterialColor.COLOR_BROWN)
                    .noOcclusion()
                    .sound(SoundType.WOOD)
                    .instabreak()
                    .noLootTable()
                    .noCollission()
            )
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
        OILBURNER_STACK = makeSmokeStack("oilburner", new SmokeStackBlock.SmokeStackType(new Vec3(0.5, 0.4, 0.5), new Vec3(0.2, 0.2, 0.2)), "Oilburner Smokestack", false, ShapeWrapper.wrapped(CRShapes.OIL_STACK), true, true, (c, p) -> {
            p.getVariantBuilder(c.get())
                .forAllStates(state -> ConfiguredModel.builder()
                    .modelFile(p.models().getExistingFile(Railways.asResource("block/smokestack/block_oilburner" + (state.getValue(OilburnerSmokeStackBlock.ENCASED) ? "_encased" : ""))))
                    .build());
        }, OilburnerSmokeStackBlock::new),
        STREAMLINED_STACK = makeSmokeStack("streamlined", new SmokeStackBlock.SmokeStackType(new Vec3(0.5, 0.2, 0.5), new Vec3(0.25, 0.2, 0.25)), "Streamlined Smokestack", CRShapes.STREAMLINED_STACK, true),
        WOODBURNER_STACK = makeSmokeStack("woodburner", new SmokeStackBlock.SmokeStackType(0.5, 12 / 16.0d, 0.5), "Woodburner Smokestack", CRShapes.WOOD_STACK, true);

    public static final BlockEntry<DieselSmokeStackBlock> DIESEL_STACK = REGISTRATE.block("smokestack_diesel", p -> new DieselSmokeStackBlock(p, ShapeWrapper.wrapped(CRShapes.DIESEL_STACK)))
        .initialProperties(SharedProperties::softMetal)
        .blockstate((c, p) -> p.getVariantBuilder(c.get())
            .forAllStatesExcept(state -> {
                Direction dir = state.getValue(BlockStateProperties.FACING);
                return ConfiguredModel.builder()
                    .modelFile(p.models().getExistingFile(Railways.asResource("block/smokestack/block_diesel_case")))
                    .rotationX(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0)
                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                    .build();
            }, DieselSmokeStackBlock.WATERLOGGED, DieselSmokeStackBlock.ENABLED, DieselSmokeStackBlock.POWERED))
        .properties(p -> p.color(MaterialColor.COLOR_GRAY))
        .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
        .properties(BlockBehaviour.Properties::noOcclusion)
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

    public static final BlockEntry<StandardTrackBufferBlock> TRACK_BUFFER = REGISTRATE.block("buffer", StandardTrackBufferBlock::new)
        .initialProperties(SharedProperties::softMetal)
        .properties(p -> p.color(MaterialColor.PODZOL))
        .properties(BlockBehaviour.Properties::noOcclusion)
        .properties(BlockBehaviour.Properties::noCollission)
        .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
        .blockstate((c, p) -> p.getVariantBuilder(c.getEntry())
                .forAllStatesExcept(state -> ConfiguredModel.builder()
                        .modelFile(p.models().getExistingFile(state.getValue(StandardTrackBufferBlock.STYLE).getModel()))
                        .rotationY(((int) state.getValue(StandardTrackBufferBlock.FACING).toYRot() + 180) % 360)
                        .build(), StandardTrackBufferBlock.WATERLOGGED
                )
        )
        .tag(AllTags.AllBlockTags.MOVABLE_EMPTY_COLLIDER.tag)
        .transform(axeOrPickaxe())
        .transform(BuilderTransformers.variantBuffer())
        .lang("Track Buffer")
        .item(TrackBufferBlockItem.ofType(CREdgePointTypes.BUFFER))
        .transform(BuilderTransformers.variantBufferItem())
        .transform(customItemModel())
        .register();

    public static final BlockEntry<NarrowTrackBufferBlock> TRACK_BUFFER_NARROW = REGISTRATE.block("buffer_narrow", NarrowTrackBufferBlock::new)
        .initialProperties(SharedProperties::softMetal)
        .properties(p -> p.color(MaterialColor.PODZOL))
        .properties(BlockBehaviour.Properties::noOcclusion)
        .properties(BlockBehaviour.Properties::noCollission)
        .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
        .blockstate((c, p) -> p.getVariantBuilder(c.getEntry())
            .forAllStatesExcept(state -> ConfiguredModel.builder()
                .modelFile(p.models().getExistingFile(state.getValue(NarrowTrackBufferBlock.STYLE).getModel()))
                .rotationY(((int) state.getValue(NarrowTrackBufferBlock.FACING).toYRot() + 180) % 360)
                .build(), NarrowTrackBufferBlock.WATERLOGGED
            )
        )
        .tag(AllTags.AllBlockTags.MOVABLE_EMPTY_COLLIDER.tag)
        .transform(axeOrPickaxe())
        .transform(BuilderTransformers.variantBuffer())
        .lang("Narrow Track Buffer")
        .loot((p, b) -> p.dropOther(b, CRBlocks.TRACK_BUFFER.get()))
        .register();

    public static final BlockEntry<MonoTrackBufferBlock> TRACK_BUFFER_MONO = REGISTRATE.block("buffer_mono", MonoTrackBufferBlock::new)
        .initialProperties(SharedProperties::softMetal)
        .properties(p -> p.color(MaterialColor.PODZOL))
        .properties(BlockBehaviour.Properties::noOcclusion)
        .properties(BlockBehaviour.Properties::noCollission)
        .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
        .blockstate((c, p) -> p.getVariantBuilder(c.getEntry())
            .forAllStatesExcept(state -> {
                boolean hanging = state.getValue(MonoTrackBufferBlock.UPSIDE_DOWN);
                return ConfiguredModel.builder()
                    .modelFile(p.models().getExistingFile(state.getValue(MonoTrackBufferBlock.STYLE).getModel()))
                    .rotationX(hanging ? 180 : 0)
                    .rotationY(((int) state.getValue(MonoTrackBufferBlock.FACING).toYRot() + (hanging ? 0 : 180)) % 360)
                    .build();
                }, MonoTrackBufferBlock.WATERLOGGED
            )
        )
        .tag(AllTags.AllBlockTags.MOVABLE_EMPTY_COLLIDER.tag)
        .transform(axeOrPickaxe())
        .transform(BuilderTransformers.variantBuffer())
        .lang("Monorail Track Buffer")
        .loot((p, b) -> p.dropOther(b, CRBlocks.TRACK_BUFFER.get()))
        .register();

    public static final BlockEntry<WideTrackBufferBlock> TRACK_BUFFER_WIDE = REGISTRATE.block("buffer_wide", WideTrackBufferBlock::new)
        .initialProperties(SharedProperties::softMetal)
        .properties(p -> p.color(MaterialColor.PODZOL))
        .properties(BlockBehaviour.Properties::noOcclusion)
        .properties(BlockBehaviour.Properties::noCollission)
        .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
        .blockstate((c, p) -> p.getVariantBuilder(c.getEntry())
            .forAllStatesExcept(state -> ConfiguredModel.builder()
                .modelFile(p.models().getExistingFile(Railways.asResource("block/buffer/wide_buffer_stop")))
                .rotationY(((int) state.getValue(NarrowTrackBufferBlock.FACING).toYRot() + 180) % 360)
                .build(), WideTrackBufferBlock.WATERLOGGED
            )
        )
        .tag(AllTags.AllBlockTags.MOVABLE_EMPTY_COLLIDER.tag)
        .transform(axeOrPickaxe())
        .transform(BuilderTransformers.variantBuffer())
        .lang("Wide Track Buffer")
        .loot((p, b) -> p.dropOther(b, CRBlocks.TRACK_BUFFER.get()))
        .register();

    public static final BlockEntry<LinkPinBlock> LINK_AND_PIN = REGISTRATE.block("link_and_pin", LinkPinBlock::new)
        .initialProperties(SharedProperties::softMetal)
        .properties(p -> p.sound(SoundType.COPPER))
        .blockstate((c, p) -> p.getVariantBuilder(c.getEntry())
            .forAllStatesExcept(state -> ConfiguredModel.builder()
                .modelFile(p.models().getExistingFile(state.getValue(LinkPinBlock.STYLE).getModel()))
                .rotationY(((int) state.getValue(LinkPinBlock.FACING).toYRot() + 180) % 360)
                .build(), LinkPinBlock.WATERLOGGED
            )
        )
        .transform(pickaxeOnly())
        .transform(BuilderTransformers.variantBuffer())
        .lang("Deco Coupler")
        /*.transform(LINK_PIN_GROUP.registerBlockItems())
        .item()
        .transform(BuilderTransformers.variantBufferItem())
        .model((c, p) -> p.withExistingParent("item/" + c.getName(), Railways.asResource("block/buffer/single_deco/link_and_pin")))
        .build()*/
        .register();

    public static final BlockStateBlockItemGroup<Void, LinkPinBlock.Style> LINK_AND_PIN_GROUP
        = new BlockStateBlockItemGroup<>(null, LinkPinBlock.STYLE, LinkPinBlock.Style.values(), LINK_AND_PIN,
        BuilderTransformers.variantBufferItem(), CRTags.AllItemTags.DECO_COUPLERS.tag);

    public static final BlockEntry<GenericDyeableSingleBufferBlock> BIG_BUFFER = REGISTRATE.block("big_buffer", GenericDyeableSingleBufferBlock.createFactory(CRShapes.BIG_BUFFER))
        .initialProperties(SharedProperties::softMetal)
        .properties(p -> p.sound(SoundType.COPPER))
        .blockstate((c, p) -> p.getVariantBuilder(c.getEntry())
            .forAllStatesExcept(state -> ConfiguredModel.builder()
                .modelFile(p.models().getExistingFile(p.modLoc("block/buffer/single_deco/big_buffer")))
                .rotationY(((int) state.getValue(LinkPinBlock.FACING).toYRot() + 180) % 360)
                .build(), GenericDyeableSingleBufferBlock.WATERLOGGED
            )
        )
        .transform(axeOrPickaxe())
        .transform(BuilderTransformers.variantBuffer())
        .lang("Big Buffer")
        .item()
        .transform(BuilderTransformers.variantBufferItem())
        .model((c, p) -> p.withExistingParent("item/" + c.getName(), Railways.asResource("block/buffer/single_deco/big_buffer")))
        .build()
        .register();

    public static final BlockEntry<GenericDyeableSingleBufferBlock> SMALL_BUFFER = REGISTRATE.block("small_buffer", GenericDyeableSingleBufferBlock.createFactory(CRShapes.SMALL_BUFFER))
        .initialProperties(SharedProperties::softMetal)
        .properties(p -> p.sound(SoundType.COPPER))
        .blockstate((c, p) -> p.getVariantBuilder(c.getEntry())
            .forAllStatesExcept(state -> ConfiguredModel.builder()
                .modelFile(p.models().getExistingFile(p.modLoc("block/buffer/single_deco/small_buffer")))
                .rotationY(((int) state.getValue(LinkPinBlock.FACING).toYRot() + 180) % 360)
                .build(), GenericDyeableSingleBufferBlock.WATERLOGGED
            )
        )
        .transform(axeOrPickaxe())
        .transform(BuilderTransformers.variantBuffer())
        .lang("Small Buffer")
        .item()
        .transform(BuilderTransformers.variantBufferItem())
        .model((c, p) -> p.withExistingParent("item/" + c.getName(), Railways.asResource("block/buffer/single_deco/small_buffer")))
        .build()
        .register();

    public static final BlockEntry<HeadstockBlock> HEADSTOCK = REGISTRATE.block("headstock", HeadstockBlock::new)
        .initialProperties(SharedProperties::softMetal)
        .properties(p -> p.sound(SoundType.COPPER))
        .blockstate((c, p) -> p.getVariantBuilder(c.getEntry())
            .forAllStatesExcept(state -> ConfiguredModel.builder()
                .modelFile(p.models().getExistingFile(state.getValue(HeadstockBlock.STYLE).getModel(false)))
                .rotationY(((int) state.getValue(HeadstockBlock.FACING).toYRot() + 180) % 360)
                .build(), HeadstockBlock.WATERLOGGED
            )
        )
        .transform(axeOrPickaxe())
        .transform(BuilderTransformers.variantBuffer())
        .lang("Headstock")
        /*.item()
        .transform(BuilderTransformers.variantBufferItem())
        .model((c, p) -> p.withExistingParent("item/" + c.getName(), Railways.asResource("block/buffer/headstock/wooden_headstock_buffer")))
        .build()*/
        .register();

    public static final BlockStateBlockItemGroup<Boolean, HeadstockStyle> HEADSTOCK_GROUP
        = new BlockStateBlockItemGroup<>(false, HeadstockBlock.STYLE, HeadstockStyle.values(), HEADSTOCK,
        BuilderTransformers.variantBufferItem(), CRTags.AllItemTags.WOODEN_HEADSTOCKS.tag);

    public static final BlockEntry<CopycatHeadstockBlock> COPYCAT_HEADSTOCK = REGISTRATE.block("copycat_headstock", CopycatHeadstockBlock::new)
        .initialProperties(SharedProperties::softMetal)
        .properties(p -> p.sound(SoundType.COPPER))
        .blockstate((c, p) -> p.getVariantBuilder(c.getEntry())
            .forAllStatesExcept(state -> ConfiguredModel.builder()
                .modelFile(p.models().getExistingFile(state.getValue(CopycatHeadstockBlock.STYLE).getModel(true)))
                .rotationY(((int) state.getValue(CopycatHeadstockBlock.FACING).toYRot() + 180) % 360)
                .build(), CopycatHeadstockBlock.WATERLOGGED
            )
        )
        .transform(axeOrPickaxe())
        .transform(BuilderTransformers.copycatHeadstock())
        .lang("Copycat Headstock")
        /*.item()
        .transform(BuilderTransformers.copycatHeadstockItem())
        .model((c, p) -> p.withExistingParent("item/" + c.getName(), Railways.asResource("block/buffer/headstock/copycat_headstock_buffer")))
        .build()*/
        .register();

    public static final BlockStateBlockItemGroup<Boolean, HeadstockStyle> COPYCAT_HEADSTOCK_GROUP
        = new BlockStateBlockItemGroup<>(true, CopycatHeadstockBlock.STYLE, HeadstockStyle.values(), COPYCAT_HEADSTOCK,
        BuilderTransformers.copycatHeadstockItem(), CRTags.AllItemTags.COPYCAT_HEADSTOCKS.tag);

    public static final BlockEntry<GenericCrossingBlock> GENERIC_CROSSING =
        REGISTRATE.block("generic_crossing", GenericCrossingBlock::new)
            .transform(BuilderTransformers.genericCrossing())
            .initialProperties(Material.STONE)
            .properties(p -> p
                .color(MaterialColor.METAL)
                .strength(0.8F)
                .sound(SoundType.METAL)
                .noOcclusion().noLootTable())
            .tag(AllTags.AllBlockTags.TRACKS.tag)
            .tag(AllTags.AllBlockTags.GIRDABLE_TRACKS.tag)
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(pickaxeOnly())
            .blockstate((c, p) -> p.simpleBlock(c.get(), p.models()
                .withExistingParent(c.getName(), p.modLoc("block/invisible"))))
            .lang("Generic Crossing")
            .register();

    @SuppressWarnings("EmptyMethod")
    public static void register() {
    }
}
