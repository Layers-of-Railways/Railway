package com.railwayteam.railways.registry;

import com.google.common.collect.ImmutableList;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.content.custom_bogeys.CategoryIcon;
import com.railwayteam.railways.content.custom_bogeys.invisible.InvisibleBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.monobogey.InvisibleMonoBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.monobogey.MonoBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.gauge.narrow.NarrowDoubleScotchYokeBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.gauge.narrow.NarrowScotchYokeBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.gauge.narrow.NarrowSmallBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.gauge.standard.HandcarBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.gauge.standard.double_axle.*;
import com.railwayteam.railways.content.custom_bogeys.renderer.gauge.standard.single_axle.CoilspringBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.gauge.standard.single_axle.LeafspringBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.gauge.standard.single_axle.SingleaxleBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.gauge.standard.triple_axle.HeavyweightBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.gauge.standard.triple_axle.RadialBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.gauge.wide.WideComicallyLargeScotchYokeBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.gauge.wide.WideDefaultBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.gauge.wide.WideScotchYokeBogeyRenderer;
import com.railwayteam.railways.registry.CRTrackMaterials.CRTrackType;
import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.CubeParticleData;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

import static com.railwayteam.railways.content.custom_bogeys.selection_menu.BogeyCategoryHandlerClient.registerStyleCategory;
import static com.simibubi.create.AllBogeyStyles.STANDARD_CYCLE_GROUP;

public class CRBogeyStyles {

    private static final Map<Pair<BogeyStyle, TrackType>, BogeyStyle> STYLES_FOR_GAUGES = new HashMap<>();
    private static final Map<BogeyStyle, BogeyStyle> STYLES_TO_STANDARD_GAUGE = new HashMap<>();

    public static void map(BogeyStyle from, TrackType toType, BogeyStyle toStyle) {
        map(from, toType, toStyle, true);
    }

    public static void map(BogeyStyle from, TrackType toType, BogeyStyle toStyle, boolean reverseToStandardGauge) {
        STYLES_FOR_GAUGES.put(Pair.of(from, toType), toStyle);
        if (reverseToStandardGauge)
            mapReverse(toStyle, from);
    }

    public static void mapReverse(BogeyStyle gaugeStyle, BogeyStyle standardStyle) {
        STYLES_TO_STANDARD_GAUGE.put(gaugeStyle, standardStyle);
    }

    public static boolean styleFitsTrack(BogeyStyle style, TrackType trackType) {
        if (style.getNextBlock(BogeySizes.LARGE) instanceof AbstractBogeyBlock<?> bogeyBlock) {
            return bogeyBlock.getValidPathfindingTypes(style).contains(trackType) && (trackType != CRTrackType.MONORAIL ^ bogeyBlock instanceof InvisibleMonoBogeyBlock);
        } else { // someone is doing something very weird. not going to stop them
            return true;
        }
    }

    public static Optional<BogeyStyle> getMapped(BogeyStyle from, TrackType toType) {
        return getMappedRecursive(from, toType, false);
    }

    private static Optional<BogeyStyle> getMappedRecursive(BogeyStyle from, TrackType toType, boolean recursive) {
        if (from.getNextBlock(BogeySizes.LARGE) instanceof AbstractBogeyBlock<?> bogeyBlock && bogeyBlock.getValidPathfindingTypes(from).contains(toType))
            return Optional.of(from);
        Pair<BogeyStyle, TrackType> key = Pair.of(from, toType);
        if (STYLES_FOR_GAUGES.containsKey(key)) {
            return Optional.of(STYLES_FOR_GAUGES.get(key));
        } else if (toType == TrackType.STANDARD && STYLES_TO_STANDARD_GAUGE.containsKey(from)) {
            return Optional.of(STYLES_TO_STANDARD_GAUGE.get(from));
        } else if (toType != TrackType.STANDARD && !recursive) {
            return getMappedRecursive(from, TrackType.STANDARD, true).flatMap(standardStyle -> getMappedRecursive(standardStyle, toType, true));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<BogeyStyle> getMapped(BogeyStyle from, TrackType toType, boolean forceFit) {
        Optional<BogeyStyle> mapped = getMapped(from, toType);
        if (!forceFit || (toType == TrackType.STANDARD && mapped.isEmpty()))
            return mapped;
        if (mapped.isEmpty() || (mapped.get().getNextBlock(BogeySizes.LARGE) instanceof AbstractBogeyBlock<?> bogeyBlock
            && !bogeyBlock.getValidPathfindingTypes(mapped.get()).contains(toType))) { // if no (suitable) style found
            return AllBogeyStyles.BOGEY_STYLES.values().stream().filter((style) -> styleFitsTrack(style, toType)).findFirst();
        }
        return mapped;
    }

    private static final Set<BogeyStyle> SUB_LISTED_STYLES = new HashSet<>();
    private static final Map<BogeyStyle, List<BogeyStyle>> SUB_STYLES = new HashMap<>();

    public static void listUnder(BogeyStyle target, BogeyStyle parent) {
        SUB_LISTED_STYLES.add(target);
        List<BogeyStyle> sub = SUB_STYLES.computeIfAbsent(parent, s -> new ArrayList<>());
        if (!sub.contains(target))
            sub.add(target);
    }

    public static boolean hideInSelectionMenu(BogeyStyle style) {
        return SUB_LISTED_STYLES.contains(style);
    }

    private static final List<BogeyStyle> EMPTY = ImmutableList.of();

    public static List<BogeyStyle> getSubStyles(BogeyStyle style) {
        return SUB_STYLES.getOrDefault(style, EMPTY);
    }

    public static final BogeyStyle MONOBOGEY = create("monobogey", "monobogey")
        .displayName(Components.translatable("railways.bogeys.styles.monobogey"))
        .size(BogeySizes.SMALL, () -> MonoBogeyRenderer::new, CRBlocks.MONO_BOGEY)
        .build();

    public static final BogeyStyle INVISIBLE = create("invisible", Create.asResource(STANDARD_CYCLE_GROUP))
        .displayName(Components.translatable("railways.bogeys.styles.invisible"))
        .size(BogeySizes.SMALL, () -> InvisibleBogeyRenderer::new, CRBlocks.INVISIBLE_BOGEY)
        .contactParticle(new CubeParticleData())
        .build();

    public static final BogeyStyle INVISIBLE_MONOBOGEY = create("invisible_monobogey", "monobogey")
        .displayName(Components.translatable("railways.bogeys.styles.invisible_monobogey"))
        .size(BogeySizes.SMALL, () -> InvisibleBogeyRenderer::new, CRBlocks.INVISIBLE_MONO_BOGEY)
        .contactParticle(new CubeParticleData())
        .build();

    public static final String SINGLEAXLE_CYCLE_GROUP = "singleaxles";
    public static final String DOUBLEAXLE_CYCLE_GROUP = "doubleaxles";
    public static final String TRIPLEAXLE_CYCLE_GROUP = "tripleaxles";
    public static final String QUADRUPLEAXLE_CYCLE_GROUP = "quadrupleaxles";
    public static final String QUINTUPLEAXLE_CYCLE_GROUP = "quintupleaxles";

    // Single Axles
    public static final BogeyStyle
        SINGLEAXLE = create("singleaxle", SINGLEAXLE_CYCLE_GROUP)
            .size(BogeySizes.SMALL, () -> SingleaxleBogeyRenderer::new, CRBlocks.SINGLEAXLE_BOGEY)
            .build(),
        LEAFSPRING = create("leafspring", SINGLEAXLE_CYCLE_GROUP)
            .size(BogeySizes.SMALL, () -> LeafspringBogeyRenderer::new, CRBlocks.SINGLEAXLE_BOGEY)
            .build(),
        COILSPRING = create("coilspring", SINGLEAXLE_CYCLE_GROUP)
            .size(BogeySizes.SMALL, () -> CoilspringBogeyRenderer::new, CRBlocks.SINGLEAXLE_BOGEY)
            .build();

    // Double Axles
    public static final BogeyStyle
        FREIGHT = create("freight", DOUBLEAXLE_CYCLE_GROUP)
            .size(BogeySizes.SMALL, () -> FreightBogeyRenderer::new, CRBlocks.LARGE_PLATFORM_DOUBLEAXLE_BOGEY)
            .build(),
        ARCHBAR = create("archbar", DOUBLEAXLE_CYCLE_GROUP)
            .size(BogeySizes.SMALL, () -> ArchbarBogeyRenderer::new, CRBlocks.LARGE_PLATFORM_DOUBLEAXLE_BOGEY)
            .build(),
        PASSENGER = create("passenger", DOUBLEAXLE_CYCLE_GROUP)
            .size(BogeySizes.SMALL, () -> PassengerBogeyRenderer::new  , CRBlocks.DOUBLEAXLE_BOGEY)
            .build(),
        MODERN = create("modern", DOUBLEAXLE_CYCLE_GROUP)
            .size(BogeySizes.SMALL, () -> ModernBogeyRenderer::new, CRBlocks.DOUBLEAXLE_BOGEY)
            .build(),
        BLOMBERG = create("blomberg", DOUBLEAXLE_CYCLE_GROUP)
            .size(BogeySizes.SMALL, () -> BlombergBogeyRenderer::new, CRBlocks.DOUBLEAXLE_BOGEY)
            .build(),
        Y25 = create("y25", DOUBLEAXLE_CYCLE_GROUP)
            .size(BogeySizes.SMALL, () -> Y25BogeyRenderer::new, CRBlocks.LARGE_PLATFORM_DOUBLEAXLE_BOGEY)
            .build();

    // Triple Axles
    public static final BogeyStyle
        HEAVYWEIGHT = create("heavyweight", TRIPLEAXLE_CYCLE_GROUP)
            .size(BogeySizes.SMALL, () -> HeavyweightBogeyRenderer::new, CRBlocks.TRIPLEAXLE_BOGEY)
            .build(),
        RADIAL = create("radial", TRIPLEAXLE_CYCLE_GROUP)
            .size(BogeySizes.SMALL, () -> RadialBogeyRenderer::new, CRBlocks.TRIPLEAXLE_BOGEY)
            .build();

    // Wide Bogeys
    public static final BogeyStyle
        WIDE_DEFAULT = create("wide_default", Create.asResource(STANDARD_CYCLE_GROUP))
            .displayName(Components.translatable("railways.bogeys.styles.wide_default"))
            .size(BogeySizes.SMALL, () -> WideDefaultBogeyRenderer::new, CRBlocks.WIDE_DOUBLEAXLE_BOGEY)
            .size(BogeySizes.LARGE, () -> WideScotchYokeBogeyRenderer::new, CRBlocks.WIDE_SCOTCH_BOGEY)
            .build(),
        WIDE_COMICALLY_LARGE = create("wide_comically_large", Create.asResource(STANDARD_CYCLE_GROUP))
            .displayName(Components.translatable("railways.bogeys.styles.wide_comically_large"))
            .size(BogeySizes.LARGE, () -> WideComicallyLargeScotchYokeBogeyRenderer::new, CRBlocks.WIDE_COMICALLY_LARGE_BOGEY)
            .build();

    // Narrow Bogeys
    public static final BogeyStyle
        NARROW_DEFAULT = create("narrow_default", Create.asResource(STANDARD_CYCLE_GROUP))
            .displayName(Components.translatable("railways.bogeys.styles.narrow_default"))
            .size(BogeySizes.SMALL, () -> NarrowSmallBogeyRenderer::new, CRBlocks.NARROW_SMALL_BOGEY)
            .size(BogeySizes.LARGE, () -> NarrowScotchYokeBogeyRenderer::new, CRBlocks.NARROW_SCOTCH_BOGEY)
            .build(),
        NARROW_DOUBLE_SCOTCH = create("narrow_double_scotch", Create.asResource(STANDARD_CYCLE_GROUP))
            .displayName(Components.translatable("railways.bogeys.styles.narrow_double_scotch"))
            .size(BogeySizes.LARGE, () -> NarrowDoubleScotchYokeBogeyRenderer::new, CRBlocks.NARROW_DOUBLE_SCOTCH_BOGEY)
            .build();

    // Handcar
    public static final BogeyStyle HANDCAR = create("handcar", "handcar_cycle_group")
        .size(BogeySizes.SMALL, () -> HandcarBogeyRenderer::new, CRBlocks.HANDCAR)
        .soundType(AllSoundEvents.COGS.getId())
        .build();


    public static AllBogeyStyles.BogeyStyleBuilder create(String name, String cycleGroup) {
        return create(Railways.asResource(name), Railways.asResource(cycleGroup))
            .displayName(Components.translatable("railways.bogeys.styles." + name));
    }

    public static AllBogeyStyles.BogeyStyleBuilder create(String name, ResourceLocation cycleGroup) {
        return create(Railways.asResource(name), cycleGroup);
    }

    public static AllBogeyStyles.BogeyStyleBuilder create(ResourceLocation name, ResourceLocation cycleGroup) {
        return new AllBogeyStyles.BogeyStyleBuilder(name, cycleGroup);
    }

    public static void register() {
        Railways.LOGGER.info("Registered bogey styles from " + Railways.MODID);
        map(AllBogeyStyles.STANDARD, CRTrackType.WIDE_GAUGE, WIDE_DEFAULT);
        map(AllBogeyStyles.STANDARD, CRTrackType.NARROW_GAUGE, NARROW_DEFAULT);
        mapReverse(NARROW_DOUBLE_SCOTCH, AllBogeyStyles.STANDARD);
        mapReverse(WIDE_COMICALLY_LARGE, AllBogeyStyles.STANDARD);

        listUnder(WIDE_DEFAULT, AllBogeyStyles.STANDARD);
        listUnder(NARROW_DEFAULT, AllBogeyStyles.STANDARD);
    }

    @Environment(EnvType.CLIENT)
    public static void registerClient() {
        registerStyleCategory(Create.asResource(STANDARD_CYCLE_GROUP), CategoryIcon.standardSupplier("default_icon"));
        registerStyleCategory(SINGLEAXLE_CYCLE_GROUP, CategoryIcon.standardSupplier("singleaxle_icon"));
        registerStyleCategory(DOUBLEAXLE_CYCLE_GROUP, CategoryIcon.standardSupplier("freight_icon"));
        registerStyleCategory(TRIPLEAXLE_CYCLE_GROUP, CategoryIcon.standardSupplier("radial_icon"));
        registerStyleCategory(QUADRUPLEAXLE_CYCLE_GROUP, CategoryIcon.standardSupplier("quadrupleaxle_icon"));
        registerStyleCategory(QUINTUPLEAXLE_CYCLE_GROUP, CategoryIcon.standardSupplier("quintupleaxle_icon"));
        if (Mods.EXTENDEDBOGEYS.isLoaded) {
            registerStyleCategory("extendedbogeys", CategoryIcon.standardSupplier("eb_triple_axle_icon"));
        }
    }
}