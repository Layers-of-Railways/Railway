/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.registry;

import com.railwayteam.railways.ModSetup;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.base.data.BuilderTransformers;
import com.railwayteam.railways.content.buffer.*;
import com.railwayteam.railways.content.buffer.headstock.CopycatHeadstockBarsBlock;
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
import com.railwayteam.railways.content.custom_bogeys.blocks.narrow.NarrowGaugeBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.blocks.narrow.NarrowGaugeBogeyBlock.NarrowGaugeStandardStyle;
import com.railwayteam.railways.content.custom_bogeys.blocks.standard.DoubleAxleBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.blocks.standard.SingleAxleBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.blocks.standard.TripleAxleBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.blocks.standard.large.*;
import com.railwayteam.railways.content.custom_bogeys.blocks.standard.medium.*;
import com.railwayteam.railways.content.custom_bogeys.blocks.wide.WideGaugeBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.blocks.wide.WideGaugeComicallyLargeBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.special.invisible.InvisibleBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.special.monobogey.InvisibleMonoBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.special.monobogey.MonoBogeyBlock;
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
import com.railwayteam.railways.content.smokestack.SmokeStackMovementBehaviour;
import com.railwayteam.railways.content.smokestack.SmokestackStyle;
import com.railwayteam.railways.content.smokestack.block.AxisSmokeStackBlock;
import com.railwayteam.railways.content.smokestack.block.DieselSmokeStackBlock;
import com.railwayteam.railways.content.smokestack.block.FacingSmokeStackBlock;
import com.railwayteam.railways.content.smokestack.block.SmokeStackBlock;
import com.railwayteam.railways.content.smokestack.block.SmokeStackBlock.RotationType;
import com.railwayteam.railways.content.switches.SwitchDisplaySource;
import com.railwayteam.railways.content.switches.TrackSwitchBlock;
import com.railwayteam.railways.content.switches.TrackSwitchBlockItem;
import com.railwayteam.railways.multiloader.CommonTags;
import com.railwayteam.railways.util.ShapeWrapper;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockItem;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackModel;
import com.simibubi.create.foundation.block.ItemUseOverrides;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.utility.Couple;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.*;
import java.util.function.Function;

import static com.simibubi.create.AllInteractionBehaviours.interactionBehaviour;
import static com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours.assignDataBehaviour;
import static com.simibubi.create.foundation.data.BuilderTransformers.copycat;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.*;

@SuppressWarnings("unused")
public class CRBlocks {

    private static final CreateRegistrate REGISTRATE = Railways.registrate();

    private static BlockEntry<TrackBlock> makeTrack(TrackMaterial material) {
        return makeTrack(material, CustomTrackBlockStateGenerator.create()::generate);
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
            .initialProperties(SharedProperties::stone)
            .properties(p -> collectProperties.apply(p)
                .mapColor(MapColor.METAL)
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
        return makeSmokeStack(variant, type, description, RotationType.NONE, ShapeWrapper.wrapped(shape), true, emitStationarySmoke);
    }

    private static BlockEntry<SmokeStackBlock> makeSmokeStack(String variant, SmokeStackBlock.SmokeStackType type, String description, VoxelShape shape, boolean spawnExtraSmoke, boolean emitStationarySmoke) {
        return makeSmokeStack(variant, type, description, RotationType.NONE, ShapeWrapper.wrapped(shape), spawnExtraSmoke, emitStationarySmoke);
    }

    @FunctionalInterface
    private interface SmokeStackFunction<T extends SmokeStackBlock> {
        T create(BlockBehaviour.Properties properties, SmokeStackBlock.SmokeStackType type, ShapeWrapper shape, boolean emitStationarySmoke, String variant);
    }

    private static BlockEntry<SmokeStackBlock> makeSmokeStack(String variant, SmokeStackBlock.SmokeStackType type, String description, RotationType rotType, ShapeWrapper shape, boolean spawnExtraSmoke, boolean emitStationarySmoke) {
        SmokeStackFunction<SmokeStackBlock> blockFunction = SmokeStackBlock::new;

        switch (rotType) {
            case NONE -> blockFunction = SmokeStackBlock::new;
            case AXIS -> blockFunction = AxisSmokeStackBlock::new;
            case FACING -> blockFunction = FacingSmokeStackBlock::new;
        }

        return makeSmokeStack(variant, type, description, shape, spawnExtraSmoke, emitStationarySmoke, BuilderTransformers.defaultSmokeStack(variant, rotType), blockFunction);
    }

    public static final HashMap<String, BlockStateBlockItemGroup<Couple<String>, SmokestackStyle>> SMOKESTACK_GROUP = new HashMap<>();

    private static BlockEntry<SmokeStackBlock> makeSmokeStack(String variant, SmokeStackBlock.SmokeStackType type, String description, ShapeWrapper shape, boolean spawnExtraSmoke, boolean emitStationarySmoke, NonNullBiConsumer<DataGenContext<Block, SmokeStackBlock>, RegistrateBlockstateProvider> blockStateProvider, SmokeStackFunction<SmokeStackBlock> blockFunction) {
        TagKey<Item> cycleTag = SmokestackStyle.variantToTagKey(variant);
        BlockEntry<SmokeStackBlock> BLOCK = REGISTRATE.block("smokestack_" + variant, p -> blockFunction.create(p, type, shape, emitStationarySmoke, variant))
            .initialProperties(SharedProperties::softMetal)
            .blockstate(blockStateProvider)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(pickaxeOnly())
            .onRegister(AllMovementBehaviours.movementBehaviour(new SmokeStackMovementBehaviour(spawnExtraSmoke)))
            .lang(description)
            .item(BlockStateBlockItem.create(SmokeStackBlock.STYLE, SmokestackStyle.STEEL, true))
                .lang(description)
                .tab(CRCreativeModeTabs.getBaseTabKey())
                .model((c, p) -> p.withExistingParent(c.getName(), Railways.asResource("block/smokestack_" + variant + "_steel")))
                .tag(cycleTag)
            .onRegisterAfter(Registries.ITEM, v -> {
                if (!variant.equals("caboosestyle"))
                    ItemDescription.useKey(v, "block.railways.smokestack");
            })
            .build()
            .register();

        if (!variant.equals("caboosestyle")) {
            BlockStateBlockItemGroup<Couple<String>, SmokestackStyle> group = new BlockStateBlockItemGroup<>(Couple.create("smokestack_" + variant + "_", description), SmokeStackBlock.STYLE, SmokestackStyle.values(), BLOCK,
                i -> i.tab(null)
                    .onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "block.railways.smokestack")),
                cycleTag, SmokestackStyle.STEEL, null);
            SMOKESTACK_GROUP.put(variant, group);
            group.registerDefaultEntry(SmokestackStyle.STEEL, ItemEntry.cast(REGISTRATE.get("smokestack_" + variant, Registries.ITEM)));
        }

        return BLOCK;
    }

    public static final BlockEntry<SemaphoreBlock> SEMAPHORE = REGISTRATE.block("semaphore", SemaphoreBlock::new)
        .initialProperties(SharedProperties::softMetal)
        .transform(BuilderTransformers.semaphore())
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
        .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
        .onRegister(assignDataBehaviour(new SemaphoreDisplayTarget()))
        .item(SemaphoreItem::new).transform(customItemModel())
        .transform(axeOnly())
        .addLayer(() -> RenderType::translucent)
        .register();

    public static final BlockEntry<TrackCouplerBlock> TRACK_COUPLER =
            REGISTRATE.block("track_coupler", TrackCouplerBlock::create)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(p -> p.mapColor(MapColor.PODZOL))
                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
                    .transform(BuilderTransformers.trackCoupler())
                    .transform(pickaxeOnly())
                    .onRegister(assignDataBehaviour(new TrackCouplerDisplaySource(), "track_coupler_info"))
                    .lang("Train Coupler")
                    .item(TrackCouplerBlockItem.ofType(CREdgePointTypes.COUPLER))
                    .transform(customItemModel("_", "block_both"))
                    .register();

    public static final BlockEntry<TrackSwitchBlock> ANDESITE_SWITCH =
        REGISTRATE.block("track_switch_andesite", TrackSwitchBlock::manual)
            .initialProperties(SharedProperties::softMetal)
            .transform(BuilderTransformers.trackSwitch(true))
                    .properties(p -> p.mapColor(MapColor.PODZOL))
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
            .transform(BuilderTransformers.trackSwitch(false))
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_BROWN))
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
        .properties(p -> p.mapColor(MapColor.METAL)
            .noOcclusion()
            .replaceable())
        .transform(BuilderTransformers.casingCollision())
        .lang("Track Casing Collision Block")
        .register();

    static {
        ModSetup.useTracksTab();
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
    public static final BlockEntry<TrackBlock> CHERRY_TRACK = makeTrack(CRTrackMaterials.CHERRY);
    public static final BlockEntry<TrackBlock> BAMBOO_TRACK = makeTrack(CRTrackMaterials.BAMBOO);
    public static final BlockEntry<TrackBlock> STRIPPED_BAMBOO_TRACK = makeTrack(CRTrackMaterials.STRIPPED_BAMBOO);

    public static final Map<TrackMaterial, NonNullSupplier<TrackBlock>> WIDE_GAUGE_TRACKS = new HashMap<>();
    public static final Map<TrackMaterial, NonNullSupplier<TrackBlock>> NARROW_GAUGE_TRACKS = new HashMap<>();

    // Sorts it by ID to prevent tags moving around for no reason
    static {
        List<TrackMaterial> wideMaterials = new ArrayList<>(CRTrackMaterials.WIDE_GAUGE.values());
        wideMaterials.sort(Comparator.comparing((t -> t.id)));
        for (TrackMaterial wideMaterial : wideMaterials) {
            WIDE_GAUGE_TRACKS.put(wideMaterial, makeTrack(wideMaterial, WideGaugeTrackBlockStateGenerator.create()::generate));
        }

        List<TrackMaterial> narrowMaterials = new ArrayList<>(CRTrackMaterials.NARROW_GAUGE.values());
        narrowMaterials.sort(Comparator.comparing((t -> t.id)));
        for (TrackMaterial narrowMaterial : narrowMaterials) {
            NARROW_GAUGE_TRACKS.put(narrowMaterial, makeTrack(narrowMaterial, NarrowGaugeTrackBlockStateGenerator.create()::generate));
        }
    }

    public static final BlockEntry<TrackBlock> MONORAIL_TRACK = makeTrack(
            MonorailBlockStateGenerator.create()::generate, BlockBehaviour.Properties::randomTicks);

    static {
        ModSetup.useBaseTab();
    }

    public static final BlockEntry<MonoBogeyBlock> MONO_BOGEY =
        REGISTRATE.block("mono_bogey", MonoBogeyBlock::new)
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .transform(BuilderTransformers.monobogey())
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .lang("Monorail Bogey")
            .register();

    public static final BlockEntry<InvisibleBogeyBlock> INVISIBLE_BOGEY =
        REGISTRATE.block("invisible_bogey", InvisibleBogeyBlock::new)
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .transform(BuilderTransformers.invisibleBogey())
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .lang("Invisible Bogey")
            .register();

    public static final BlockEntry<InvisibleMonoBogeyBlock> INVISIBLE_MONO_BOGEY =
        REGISTRATE.block("invisible_mono_bogey", InvisibleMonoBogeyBlock::new)
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .transform(BuilderTransformers.invisibleMonoBogey())
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .lang("Invisible Mono Bogey")
            .register();

    public static final BlockEntry<SingleAxleBogeyBlock> SINGLEAXLE_BOGEY =
        REGISTRATE.block("singleaxle_bogey", SingleAxleBogeyBlock::new)
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .transform(BuilderTransformers.standardBogey()) // handles SAFE_NBT
            .lang("Single Axle Bogey")
            .register();

    public static final BlockEntry<DoubleAxleBogeyBlock> DOUBLEAXLE_BOGEY =
        REGISTRATE.block("doubleaxle_bogey", DoubleAxleBogeyBlock::new)
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .transform(BuilderTransformers.standardBogey())
            .lang("Double Axle Bogey")
            .register();

    //fixme todo Implement Datafixer for this and remove it.
    @Deprecated
    public static final BlockEntry<DoubleAxleBogeyBlock> LARGE_PLATFORM_DOUBLEAXLE_BOGEY =
        REGISTRATE.block("large_platform_doubleaxle_bogey", DoubleAxleBogeyBlock::new)
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .transform(BuilderTransformers.standardBogey())
            .lang("Large Platform Double Axle Bogey")
            .register();

    public static final BlockEntry<TripleAxleBogeyBlock> TRIPLEAXLE_BOGEY =
        REGISTRATE.block("tripleaxle_bogey", TripleAxleBogeyBlock::new)
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .transform(BuilderTransformers.standardBogey())
            .lang("Triple Axle Bogey")
            .register();

    public static final BlockEntry<WideGaugeBogeyBlock> WIDE_DOUBLEAXLE_BOGEY =
        REGISTRATE.block("wide_doubleaxle_bogey", WideGaugeBogeyBlock.create(false))
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .transform(BuilderTransformers.wideBogey())
            .lang("Wide Gauge Double Axle Bogey")
            .register();

    public static final BlockEntry<WideGaugeBogeyBlock> WIDE_SCOTCH_BOGEY =
        REGISTRATE.block("wide_scotch_bogey", WideGaugeBogeyBlock.create(true))
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .transform(BuilderTransformers.wideBogey())
            .lang("Wide Gauge Scotch Yoke Bogey")
            .register();

    public static final BlockEntry<WideGaugeComicallyLargeBogeyBlock> WIDE_COMICALLY_LARGE_BOGEY =
        REGISTRATE.block("wide_comically_large_bogey", WideGaugeComicallyLargeBogeyBlock::new)
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .transform(BuilderTransformers.wideBogey())
            .lang("Wide Gauge Comically Large Bogey")
            .register();

    public static final BlockEntry<NarrowGaugeBogeyBlock> NARROW_SMALL_BOGEY =
        REGISTRATE.block("narrow_small_bogey", NarrowGaugeBogeyBlock.create(NarrowGaugeStandardStyle.SMALL))
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .transform(BuilderTransformers.narrowBogey())
            .lang("Narrow Gauge Small Bogey")
            .register();

    public static final BlockEntry<NarrowGaugeBogeyBlock> NARROW_SCOTCH_BOGEY =
        REGISTRATE.block("narrow_scotch_bogey", NarrowGaugeBogeyBlock.create(NarrowGaugeStandardStyle.SCOTCH_YOKE))
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .transform(BuilderTransformers.narrowBogey())
            .lang("Narrow Gauge Scotch Yoke Bogey")
            .register();

    public static final BlockEntry<NarrowGaugeBogeyBlock> NARROW_DOUBLE_SCOTCH_BOGEY =
        REGISTRATE.block("narrow_double_scotch_bogey", NarrowGaugeBogeyBlock.create(NarrowGaugeStandardStyle.DOUBLE_SCOTCH_YOKE))
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .transform(BuilderTransformers.narrowBogey())
            .lang("Narrow Gauge Double Scotch Yoke Bogey")
            .register();

    public static final BlockEntry<HandcarBlock> HANDCAR =
        REGISTRATE.block("handcar", HandcarBlock::new)
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .transform(BuilderTransformers.handcar())
            .onRegister(interactionBehaviour(new HandcarControlsInteractionBehaviour()))
            .item(HandcarItem::new)
            .properties(p -> p.stacksTo(1))
            .model((c, p) -> p.generated(c, Railways.asResource("item/" + c.getName())))
            .build()
            .lang("Handcar")
            .register();

    public static final BlockEntry<MediumBogeyBlock> MEDIUM_BOGEY =
            REGISTRATE.block("medium_bogey", MediumBogeyBlock::new)
                    .properties(p -> p.mapColor(MapColor.PODZOL))
                    .transform(BuilderTransformers.standardBogey())
                    .lang("Medium Bogey")
                    .register();

    public static final BlockEntry<MediumTripleWheelBogeyBlock> MEDIUM_TRIPLE_WHEEL =
            REGISTRATE.block("medium_triple_wheel", MediumTripleWheelBogeyBlock::new)
                    .properties(p -> p.mapColor(MapColor.PODZOL))
                    .transform(BuilderTransformers.standardBogey())
                    .lang("Medium Triple Wheel Bogey")
                    .register();

    public static final BlockEntry<MediumQuadrupleWheelBogeyBlock> MEDIUM_QUADRUPLE_WHEEL =
            REGISTRATE.block("medium_quadruple_wheel", MediumQuadrupleWheelBogeyBlock::new)
                    .properties(p -> p.mapColor(MapColor.PODZOL))
                    .transform(BuilderTransformers.standardBogey())
                    .lang("Medium Quadruple Wheel Bogey")
                    .register();

    public static final BlockEntry<MediumQuintupleWheelBogeyBlock> MEDIUM_QUINTUPLE_WHEEL =
            REGISTRATE.block("medium_quintuple_wheel", MediumQuintupleWheelBogeyBlock::new)
                    .properties(p -> p.mapColor(MapColor.PODZOL))
                    .transform(BuilderTransformers.standardBogey())
                    .lang("Medium Quintuple Wheel Bogey")
                    .register();

    public static final BlockEntry<Medium202TrailingBogeyBlock> MEDIUM_2_0_2_TRAILING =
            REGISTRATE.block("medium_2_0_2_trailing", Medium202TrailingBogeyBlock::new)
                    .properties(p -> p.mapColor(MapColor.PODZOL))
                    .transform(BuilderTransformers.standardBogey())
                    .lang("Medium 2-0-2 Trailing Bogey")
                    .register();

    public static final BlockEntry<Medium404TrailingBogeyBlock> MEDIUM_4_0_4_TRAILING =
            REGISTRATE.block("medium_4_0_4_trailing", Medium404TrailingBogeyBlock::new)
                    .properties(p -> p.mapColor(MapColor.PODZOL))
                    .transform(BuilderTransformers.standardBogey())
                    .lang("Medium 4-0-4 Trailing Bogey")
                    .register();

    public static final BlockEntry<LargeCreateStyle040BogeyBlock> LARGE_CREATE_STYLE_0_4_0 =
            REGISTRATE.block("large_create_styled_0_4_0", LargeCreateStyle040BogeyBlock::new)
                    .properties(p -> p.mapColor(MapColor.PODZOL))
                    .transform(BuilderTransformers.standardBogey())
                    .lang("Large Create Styled 0-4-0")
                    .register();

    public static final BlockEntry<LargeCreateStyle060BogeyBlock> LARGE_CREATE_STYLE_0_6_0 =
            REGISTRATE.block("large_create_styled_0_6_0", LargeCreateStyle060BogeyBlock::new)
                    .properties(p -> p.mapColor(MapColor.PODZOL))
                    .transform(BuilderTransformers.standardBogey())
                    .lang("Large Create Styled 0-6-0")
                    .register();

    public static final BlockEntry<LargeCreateStyle080BogeyBlock> LARGE_CREATE_STYLE_0_8_0 =
            REGISTRATE.block("large_create_styled_0_8_0", LargeCreateStyle080BogeyBlock::new)
                    .properties(p -> p.mapColor(MapColor.PODZOL))
                    .transform(BuilderTransformers.standardBogey())
                    .lang("Large Create Styled 0-8-0")
                    .register();

    public static final BlockEntry<LargeCreateStyle0100BogeyBlock> LARGE_CREATE_STYLE_0_10_0 =
            REGISTRATE.block("large_create_styled_0_10_0", LargeCreateStyle0100BogeyBlock::new)
                    .properties(p -> p.mapColor(MapColor.PODZOL))
                    .transform(BuilderTransformers.standardBogey())
                    .lang("Large Create Styled 0-10-0")
                    .register();

    public static final BlockEntry<LargeCreateStyle0120BogeyBlock> LARGE_CREATE_STYLE_0_12_0 =
            REGISTRATE.block("large_create_styled_0_12_0", LargeCreateStyle0120BogeyBlock::new)
                    .properties(p -> p.mapColor(MapColor.PODZOL))
                    .transform(BuilderTransformers.standardBogey())
                    .lang("Large Create Styled 0-12-0")
                    .register();



    public static final BlockEntry<ConductorWhistleFlagBlock> CONDUCTOR_WHISTLE_FLAG =
        REGISTRATE.block("conductor_whistle", ConductorWhistleFlagBlock::new)
            .initialProperties(SharedProperties::wooden)
            .properties(p -> p
                    .mapColor(MapColor.COLOR_BROWN)
                    .noOcclusion()
                    .sound(SoundType.WOOD)
                    .instabreak()
                    .noLootTable()
                    .noCollission()

            )
            .transform(BuilderTransformers.conductorWhistleFlag())
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
        CABOOSESTYLE_STACK = makeSmokeStack("caboosestyle", new SmokeStackBlock.SmokeStackType(0.5, 10 / 16.0d, 0.5), "Caboose Smokestack", RotationType.AXIS, ShapeWrapper.wrapped(CRShapes.CABOOSE_STACK), false, true),
        LONG_STACK = makeSmokeStack("long", new SmokeStackBlock.SmokeStackType(0.5, 10 / 16.0d, 0.5), "Double Smokestack", RotationType.AXIS, ShapeWrapper.wrapped(CRShapes.LONG_STACK), true, true),
        COALBURNER_STACK = makeSmokeStack("coalburner", new SmokeStackBlock.SmokeStackType(0.5, 1.0, 0.5), "Coalburner Smokestack", CRShapes.COAL_STACK, true),
        OILBURNER_STACK = makeSmokeStack("oilburner", new SmokeStackBlock.SmokeStackType(new Vec3(0.5, 0.4, 0.5), new Vec3(0.2, 0.2, 0.2)), "Oilburner Smokestack", RotationType.NONE, ShapeWrapper.wrapped(CRShapes.OIL_STACK), true, true),
        STREAMLINED_STACK = makeSmokeStack("streamlined", new SmokeStackBlock.SmokeStackType(new Vec3(0.5, 0.2, 0.5), new Vec3(0.25, 0.2, 0.25)), "Streamlined Smokestack", RotationType.FACING, ShapeWrapper.wrapped(CRShapes.STREAMLINED_STACK), true, true),
        WOODBURNER_STACK = makeSmokeStack("woodburner", new SmokeStackBlock.SmokeStackType(0.5, 12 / 16.0d, 0.5), "Woodburner Smokestack", CRShapes.WOOD_STACK, true);

    public static final BlockEntry<DieselSmokeStackBlock> DIESEL_STACK = REGISTRATE.block("smokestack_diesel", p -> new DieselSmokeStackBlock(p, ShapeWrapper.wrapped(CRShapes.DIESEL_STACK)))
        .initialProperties(SharedProperties::softMetal)
        .transform(BuilderTransformers.dieselSmokeStack())
        .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
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
            .transform(BuilderTransformers.conductorVent())
            .properties(p -> p.isSuffocating((state, level, pos) -> false))
            .onRegister(CreateRegistrate.blockModel(() -> CopycatVentModel::create))
            .lang("Vent Block")
            .recipe((c, p) -> p.stonecutting(DataIngredient.items(AllBlocks.INDUSTRIAL_IRON_BLOCK.get()), RecipeCategory.TRANSPORTATION, c, 2))
            .item()
            .transform(customItemModel("copycat_vent"))
            .register();

    public static final BlockEntry<StandardTrackBufferBlock> TRACK_BUFFER = REGISTRATE.block("buffer", StandardTrackBufferBlock::new)
        .initialProperties(SharedProperties::softMetal)
        .properties(p -> p.mapColor(MapColor.PODZOL))
        .properties(BlockBehaviour.Properties::noOcclusion)
        .properties(BlockBehaviour.Properties::noCollission)
        .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
        .transform(BuilderTransformers.bufferBlockState(state -> state.getValue(StandardTrackBufferBlock.STYLE).getModel(), state -> state.getValue(StandardTrackBufferBlock.FACING)))
        .tag(AllTags.AllBlockTags.MOVABLE_EMPTY_COLLIDER.tag)
        .transform(axeOrPickaxe())
        .transform(BuilderTransformers.variantBuffer())
        .lang("Track Buffer")
        .item(TrackBufferBlockItem.ofType(CREdgePointTypes.BUFFER))
        .transform(BuilderTransformers.variantBufferItem())
        .transform(customItemModel())
        .onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "block.railways.track_buffer"))
        .register();

    public static final BlockEntry<NarrowTrackBufferBlock> TRACK_BUFFER_NARROW = REGISTRATE.block("buffer_narrow", NarrowTrackBufferBlock::new)
        .initialProperties(SharedProperties::softMetal)
        .properties(p -> p.mapColor(MapColor.PODZOL))
        .properties(BlockBehaviour.Properties::noOcclusion)
        .properties(BlockBehaviour.Properties::noCollission)
        .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
        .transform(BuilderTransformers.bufferBlockState(state -> state.getValue(NarrowTrackBufferBlock.STYLE).getModel(), state -> state.getValue(NarrowTrackBufferBlock.FACING)))
        .tag(AllTags.AllBlockTags.MOVABLE_EMPTY_COLLIDER.tag)
        .transform(axeOrPickaxe())
        .transform(BuilderTransformers.variantBuffer())
        .lang("Narrow Track Buffer")
        .loot((p, b) -> p.dropOther(b, CRBlocks.TRACK_BUFFER.get()))
        .register();

    public static final BlockEntry<MonoTrackBufferBlock> TRACK_BUFFER_MONO = REGISTRATE.block("buffer_mono", MonoTrackBufferBlock::new)
        .initialProperties(SharedProperties::softMetal)
        .properties(p -> p.mapColor(MapColor.PODZOL))
        .properties(BlockBehaviour.Properties::noOcclusion)
        .properties(BlockBehaviour.Properties::noCollission)
        .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
        .transform(BuilderTransformers.monoBuffer())
        .tag(AllTags.AllBlockTags.MOVABLE_EMPTY_COLLIDER.tag)
        .transform(axeOrPickaxe())
        .transform(BuilderTransformers.variantBuffer())
        .lang("Monorail Track Buffer")
        .loot((p, b) -> p.dropOther(b, CRBlocks.TRACK_BUFFER.get()))
        .register();

    public static final BlockEntry<WideTrackBufferBlock> TRACK_BUFFER_WIDE = REGISTRATE.block("buffer_wide", WideTrackBufferBlock::new)
        .initialProperties(SharedProperties::softMetal)
        .properties(p -> p.mapColor(MapColor.PODZOL))
        .properties(BlockBehaviour.Properties::noOcclusion)
        .properties(BlockBehaviour.Properties::noCollission)
        .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
        .transform(BuilderTransformers.bufferBlockState(state -> Railways.asResource("block/buffer/wide_buffer_stop"), state -> state.getValue(WideTrackBufferBlock.FACING)))
        .tag(AllTags.AllBlockTags.MOVABLE_EMPTY_COLLIDER.tag)
        .transform(axeOrPickaxe())
        .transform(BuilderTransformers.variantBuffer())
        .lang("Wide Track Buffer")
        .loot((p, b) -> p.dropOther(b, CRBlocks.TRACK_BUFFER.get()))
        .register();

    public static final BlockEntry<LinkPinBlock> LINK_AND_PIN = REGISTRATE.block("link_and_pin", LinkPinBlock::new)
        .initialProperties(SharedProperties::softMetal)
        .properties(p -> p.sound(SoundType.COPPER))
        .transform(BuilderTransformers.linkAndPin())
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
        BuilderTransformers.variantBufferItem(), CRTags.AllItemTags.DECO_COUPLERS.tag, "block.railways.headstock");

    public static final BlockEntry<GenericDyeableSingleBufferBlock> BIG_BUFFER = REGISTRATE.block("big_buffer", GenericDyeableSingleBufferBlock.createFactory(CRShapes.BIG_BUFFER))
        .initialProperties(SharedProperties::softMetal)
        .properties(p -> p.sound(SoundType.COPPER))
        .transform(BuilderTransformers.bigBuffer())
        .transform(axeOrPickaxe())
        .transform(BuilderTransformers.variantBuffer())
        .lang("Big Buffer")
        .item()
        .transform(BuilderTransformers.variantBufferItem())
        .model((c, p) -> p.withExistingParent("item/" + c.getName(), Railways.asResource("block/buffer/single_deco/big_buffer")))
        .onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "block.railways.buffer"))
        .build()
        .register();

    public static final BlockEntry<GenericDyeableSingleBufferBlock> SMALL_BUFFER = REGISTRATE.block("small_buffer", GenericDyeableSingleBufferBlock.createFactory(CRShapes.SMALL_BUFFER))
        .initialProperties(SharedProperties::softMetal)
        .properties(p -> p.sound(SoundType.COPPER))
        .transform(BuilderTransformers.smallBuffer())
        .transform(axeOrPickaxe())
        .transform(BuilderTransformers.variantBuffer())
        .lang("Small Buffer")
        .item()
        .transform(BuilderTransformers.variantBufferItem())
        .model((c, p) -> p.withExistingParent("item/" + c.getName(), Railways.asResource("block/buffer/single_deco/small_buffer")))
        .onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "block.railways.buffer"))
        .build()
        .register();

    public static final BlockEntry<HeadstockBlock> HEADSTOCK = REGISTRATE.block("headstock", HeadstockBlock::new)
        .initialProperties(SharedProperties::softMetal)
        .properties(p -> p.sound(SoundType.COPPER))
        .transform(BuilderTransformers.headstock())
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
        BuilderTransformers.variantBufferItem(), CRTags.AllItemTags.WOODEN_HEADSTOCKS.tag, "block.railways.headstock_wood");

    public static final BlockEntry<CopycatHeadstockBarsBlock> COPYCAT_HEADSTOCK_BARS = REGISTRATE.block("copycat_headstock_bars", CopycatHeadstockBarsBlock::new)
        .transform(BuilderTransformers.copycatHeadstockBars()) // it's all platform-dependent :(
        .register();

    public static final BlockEntry<CopycatHeadstockBlock> COPYCAT_HEADSTOCK = REGISTRATE.block("copycat_headstock", CopycatHeadstockBlock::new)
        .initialProperties(SharedProperties::softMetal)
        .properties(p -> p.sound(SoundType.COPPER))
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
        BuilderTransformers.copycatHeadstockItem(), CRTags.AllItemTags.COPYCAT_HEADSTOCKS.tag, "block.railways.headstock");

    public static final BlockEntry<GenericCrossingBlock> GENERIC_CROSSING =
        REGISTRATE.block("generic_crossing", GenericCrossingBlock::new)
            .transform(BuilderTransformers.genericCrossing())
            .initialProperties(SharedProperties::stone)
            .properties(p -> p
                .mapColor(MapColor.METAL)
                .strength(0.8F)
                .sound(SoundType.METAL)
                .noOcclusion().noLootTable())
            .tag(AllTags.AllBlockTags.TRACKS.tag)
            .tag(AllTags.AllBlockTags.GIRDABLE_TRACKS.tag)
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(pickaxeOnly())
            .transform(BuilderTransformers.invisibleBlockState())
            .lang("Generic Crossing")
            .register();

    @ExpectPlatform
    public static void platformBasedRegistration() {
        throw new AssertionError();
    }

    public static void register() {
        platformBasedRegistration();
    }
}
