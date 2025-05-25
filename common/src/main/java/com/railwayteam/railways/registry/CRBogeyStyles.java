/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.registry;

import com.google.common.collect.ImmutableList;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.api.bogeymenu.v0.BogeyMenuManager;
import com.railwayteam.railways.api.bogeymenu.v0.entry.CategoryEntry;
import com.railwayteam.railways.content.custom_bogeys.renderer.narrow.NarrowDoubleScotchYokeBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.narrow.NarrowScotchYokeBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.narrow.NarrowSmallBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.HandcarBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.double_axle.ArchbarBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.double_axle.BlombergBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.double_axle.FreightBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.double_axle.ModernBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.double_axle.PassengerBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.double_axle.Y25BogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.large.LargeCreateStyled0100Renderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.large.LargeCreateStyled0120Renderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.large.LargeCreateStyled040Renderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.large.LargeCreateStyled060Renderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.large.LargeCreateStyled080Renderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.medium.Medium10010TenderRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.medium.Medium202TrailingRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.medium.Medium404TrailingRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.medium.Medium606TenderRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.medium.Medium606TrailingRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.medium.Medium808TenderRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.medium.MediumQuadrupleWheelRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.medium.MediumQuintupleWheelRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.medium.MediumSingleWheelRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.medium.MediumStandardRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.medium.MediumTripleWheelRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.single_axle.CoilspringBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.single_axle.LeafspringBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.single_axle.SingleaxleBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.triple_axle.HeavyweightBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.triple_axle.HeavyweightBogeyVisual;
import com.railwayteam.railways.content.custom_bogeys.renderer.standard.triple_axle.RadialBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.wide.WideComicallyLargeScotchYokeBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.wide.WideDefaultBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.wide.WideScotchYokeBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.special.invisible.InvisibleBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.special.invisible.InvisibleBogeyVisual;
import com.railwayteam.railways.content.custom_bogeys.special.monobogey.InvisibleMonoBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.special.monobogey.MonoBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.special.monobogey.MonoBogeyVisual;
import com.railwayteam.railways.impl.bogeymenu.v0.BogeyMenuManagerImpl;
import com.railwayteam.railways.registry.CRTrackMaterials.CRTrackType;
import com.railwayteam.railways.util.Utils;
import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.trains.CubeParticleData;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.content.trains.bogey.BogeyStyle.SizeRenderer;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;
import net.createmod.catnip.data.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.simibubi.create.AllBogeyStyles.STANDARD;
import static com.simibubi.create.AllBogeyStyles.STANDARD_CYCLE_GROUP;

public class CRBogeyStyles {
    public static final String SINGLEAXLE_CYCLE_GROUP = "singleaxles";
    public static final String DOUBLEAXLE_CYCLE_GROUP = "doubleaxles";
    public static final String TRIPLEAXLE_CYCLE_GROUP = "tripleaxles";
    public static final String QUADRUPLEAXLE_CYCLE_GROUP = "quadrupleaxles";
    public static final String QUINTUPLEAXLE_CYCLE_GROUP = "quintupleaxles";
    public static final String SEXTUPLEAXLE_CYCLE_GROUP = "sextupleaxles";

    public static final CategoryEntry STANDARD_CATEGORY = registerCategory(STANDARD_CYCLE_GROUP);
    public static final CategoryEntry SINGLEAXLE_CATEGORY = registerCategory(SINGLEAXLE_CYCLE_GROUP);
    public static final CategoryEntry DOUBLEAXLE_CATEGORY = registerCategory(DOUBLEAXLE_CYCLE_GROUP);
    public static final CategoryEntry TRIPLEAXLE_CATEGORY = registerCategory(TRIPLEAXLE_CYCLE_GROUP);
    public static final CategoryEntry QUADRUPLEAXLE_CATEGORY = registerCategory(QUADRUPLEAXLE_CYCLE_GROUP);
    public static final CategoryEntry QUINTUPLEAXLE_CATEGORY = registerCategory(QUINTUPLEAXLE_CYCLE_GROUP);
    public static final CategoryEntry SEXTUPLEAXLE_CATEGORY = registerCategory(SEXTUPLEAXLE_CYCLE_GROUP);

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
        AbstractBogeyBlock<?> bogeyBlock = style.getNextBlock(BogeySizes.LARGE);
        return bogeyBlock.getValidPathfindingTypes(style).contains(trackType) &&
                (trackType != CRTrackType.MONORAIL ^ bogeyBlock instanceof InvisibleMonoBogeyBlock);
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
        .displayName(Component.translatable("railways.bogeys.styles.monobogey"))
        .size(BogeySizes.SMALL, CRBlocks.MONO_BOGEY, () -> () -> new SizeRenderer(new MonoBogeyRenderer(), MonoBogeyVisual::new))
        .build();

    public static final BogeyStyle INVISIBLE = create("invisible", STANDARD_CYCLE_GROUP)
        .displayName(Component.translatable("railways.bogeys.styles.invisible"))
        .size(BogeySizes.SMALL, CRBlocks.INVISIBLE_BOGEY, () -> () -> new SizeRenderer(new InvisibleBogeyRenderer(), InvisibleBogeyVisual::new))
        .contactParticle(new CubeParticleData())
        .build();

    public static final BogeyStyle INVISIBLE_MONOBOGEY = create("invisible_monobogey", "monobogey")
        .displayName(Component.translatable("railways.bogeys.styles.invisible_monobogey"))
        .size(BogeySizes.SMALL, CRBlocks.INVISIBLE_MONO_BOGEY, () -> () -> new SizeRenderer(new InvisibleBogeyRenderer(), InvisibleBogeyVisual::new))
        .contactParticle(new CubeParticleData())
        .build();

    // Single Axles
    public static final BogeyStyle
        SINGLEAXLE = create("singleaxle", SINGLEAXLE_CYCLE_GROUP)
            .size(BogeySizes.SMALL, CRBlocks.SINGLEAXLE_BOGEY, () -> () -> new SizeRenderer(new SingleaxleBogeyRenderer(), SingleaxleBogeyVisual::new))
            .build(),
        LEAFSPRING = create("leafspring", SINGLEAXLE_CYCLE_GROUP)
            .size(BogeySizes.SMALL, CRBlocks.SINGLEAXLE_BOGEY, () -> () -> new SizeRenderer(new LeafspringBogeyRenderer(), LeafspringBogeyVisual::new))
            .build(),
        COILSPRING = create("coilspring", SINGLEAXLE_CYCLE_GROUP)
            .size(BogeySizes.SMALL, CRBlocks.SINGLEAXLE_BOGEY, () -> () -> new SizeRenderer(new CoilspringBogeyRenderer(), CoilspringBogeyVisual::new))
            .build();

    // Double Axles
    public static final BogeyStyle
        FREIGHT = create("freight", DOUBLEAXLE_CYCLE_GROUP)
            .size(BogeySizes.SMALL, CRBlocks.LARGE_PLATFORM_DOUBLEAXLE_BOGEY, () -> () -> new SizeRenderer(new FreightBogeyRenderer(), FreightBogeyVisual::new))
            .build(),
        ARCHBAR = create("archbar", DOUBLEAXLE_CYCLE_GROUP)
            .size(BogeySizes.SMALL, CRBlocks.LARGE_PLATFORM_DOUBLEAXLE_BOGEY, () -> () -> new SizeRenderer(new ArchbarBogeyRenderer(), ArchbarBogeyVisual::new))
            .build(),
        PASSENGER = create("passenger", DOUBLEAXLE_CYCLE_GROUP)
            .size(BogeySizes.SMALL, CRBlocks.DOUBLEAXLE_BOGEY, () -> () -> new SizeRenderer(new PassengerBogeyRenderer(), PassengerBogeyVisual::new))
            .build(),
        MODERN = create("modern", DOUBLEAXLE_CYCLE_GROUP)
            .size(BogeySizes.SMALL, CRBlocks.DOUBLEAXLE_BOGEY, () -> () -> new SizeRenderer(new ModernBogeyRenderer(), ModernBogeyVisual::new))
            .build(),
        BLOMBERG = create("blomberg", DOUBLEAXLE_CYCLE_GROUP)
            .size(BogeySizes.SMALL, CRBlocks.DOUBLEAXLE_BOGEY, () -> () -> new SizeRenderer(new BlombergBogeyRenderer(), BlombergBogeyVisual::new))
            .build(),
        Y25 = create("y25", DOUBLEAXLE_CYCLE_GROUP)
            .size(BogeySizes.SMALL, CRBlocks.LARGE_PLATFORM_DOUBLEAXLE_BOGEY, () -> () -> new SizeRenderer(new Y25BogeyRenderer(), Y25BogeyVisual::new))
            .build();

    // Triple Axles
    public static final BogeyStyle
        HEAVYWEIGHT = create("heavyweight", TRIPLEAXLE_CYCLE_GROUP)
            .size(BogeySizes.SMALL, CRBlocks.TRIPLEAXLE_BOGEY, () -> () -> new SizeRenderer(new HeavyweightBogeyRenderer(), HeavyweightBogeyVisual::new))
            .build(),
        RADIAL = create("radial", TRIPLEAXLE_CYCLE_GROUP)
            .size(BogeySizes.SMALL, CRBlocks.TRIPLEAXLE_BOGEY, () -> () -> new SizeRenderer(new RadialBogeyRenderer(), RadialBogeyVisual::new))
            .build();

    // Wide Bogeys
    public static final BogeyStyle
        WIDE_DEFAULT = create("wide_default", STANDARD_CYCLE_GROUP)
            .displayName(Component.translatable("railways.bogeys.styles.wide_default"))
            .size(BogeySizes.SMALL, CRBlocks.WIDE_DOUBLEAXLE_BOGEY, () -> () -> new SizeRenderer(new WideDefaultBogeyRenderer(), WideDefaultBogeyVisual::new))
            .size(BogeySizes.LARGE, CRBlocks.WIDE_SCOTCH_BOGEY, () -> () -> new SizeRenderer(new WideScotchYokeBogeyRenderer(), WideScotchYokeBogeyVisual::new))
            .build(),
        WIDE_COMICALLY_LARGE = create("wide_comically_large", STANDARD_CYCLE_GROUP)
            .displayName(Component.translatable("railways.bogeys.styles.wide_comically_large"))
            .size(BogeySizes.LARGE, CRBlocks.WIDE_COMICALLY_LARGE_BOGEY, () -> () -> new SizeRenderer(new WideComicallyLargeScotchYokeBogeyRenderer(), WideComicallyLargeScotchYokeBogeyVisual::new))
            .build();

    // Narrow Bogeys
    public static final BogeyStyle
        NARROW_DEFAULT = create("narrow_default", STANDARD_CYCLE_GROUP)
            .displayName(Component.translatable("railways.bogeys.styles.narrow_default"))
            .size(BogeySizes.SMALL, CRBlocks.NARROW_SMALL_BOGEY, () -> () -> new SizeRenderer(new NarrowSmallBogeyRenderer(), NarrowSmallBogeyVisual::new))
            .size(BogeySizes.LARGE, CRBlocks.NARROW_SCOTCH_BOGEY, () -> () -> new SizeRenderer(new NarrowScotchYokeBogeyRenderer(), NarrowScotchYokeBogeyVisual::new))
            .build(),
        NARROW_DOUBLE_SCOTCH = create("narrow_double_scotch", STANDARD_CYCLE_GROUP)
            .displayName(Component.translatable("railways.bogeys.styles.narrow_double_scotch"))
            .size(BogeySizes.LARGE, CRBlocks.NARROW_DOUBLE_SCOTCH_BOGEY, () -> () -> new SizeRenderer(new NarrowDoubleScotchYokeBogeyRenderer(), NarrowDoubleScotchYokeBogeyVisual::new))
            .build();

    // Handcar
    public static final BogeyStyle HANDCAR = create("handcar", "handcar_cycle_group")
        .size(BogeySizes.SMALL, CRBlocks.HANDCAR, () -> () -> new SizeRenderer(new HandcarBogeyRenderer(), HandcarBogeyVisual::new))
        .soundType(AllSoundEvents.COGS.getId())
        .build();

    // Medium
    public static final BogeyStyle
        MEDIUM_STANDARD = create("medium_standard", DOUBLEAXLE_CYCLE_GROUP)
            .displayName(Component.translatable("railways.bogeys.styles.medium_standard"))
            .size(BogeySizes.SMALL, CRBlocks.MEDIUM_BOGEY, () -> () -> new SizeRenderer(new MediumStandardRenderer(), MediumStandardVisual::new))
            .build(),
        MEDIUM_SINGLE_WHEEL = create("medium_single_wheel", SINGLEAXLE_CYCLE_GROUP)
            .displayName(Component.translatable("railways.bogeys.styles.medium_single_wheel"))
            .size(BogeySizes.SMALL, CRBlocks.MEDIUM_BOGEY, () -> () -> new SizeRenderer(new MediumSingleWheelRenderer(), MediumSingleWheelVisual::new))
            .build(),
        MEDIUM_TRIPLE_WHEEL = create("medium_triple_wheel", TRIPLEAXLE_CYCLE_GROUP)
            .displayName(Component.translatable("railways.bogeys.styles.medium_triple_wheel"))
            .size(BogeySizes.SMALL, CRBlocks.MEDIUM_TRIPLE_WHEEL, () -> () -> new SizeRenderer(new MediumTripleWheelRenderer(), MediumTripleWheelVisual::new))
            .build(),
        MEDIUM_QUADRUPLE_WHEEL = create("medium_quadruple_wheel", QUADRUPLEAXLE_CYCLE_GROUP)
            .displayName(Component.translatable("railways.bogeys.styles.medium_quadruple_wheel"))
            .size(BogeySizes.SMALL, CRBlocks.MEDIUM_QUADRUPLE_WHEEL, () -> () -> new SizeRenderer(new MediumQuadrupleWheelRenderer(), MediumQuadrupleWheelVisual::new))
            .build(),
        MEDIUM_QUINTUPLE_WHEEL = create("medium_quintuple_wheel", QUINTUPLEAXLE_CYCLE_GROUP)
            .displayName(Component.translatable("railways.bogeys.styles.medium_quintuple_wheel"))
            .size(BogeySizes.SMALL, CRBlocks.MEDIUM_QUINTUPLE_WHEEL, () -> () -> new SizeRenderer(new MediumQuintupleWheelRenderer(), MediumQuintupleWheelVisual::new))
            .build(),
        MEDIUM_2_0_2_TRAILING = create("medium_2_0_2_trailing", SINGLEAXLE_CYCLE_GROUP)
            .displayName(Component.translatable("railways.bogeys.styles.medium_2_0_2_trailing"))
            .size(BogeySizes.SMALL, CRBlocks.MEDIUM_2_0_2_TRAILING, () -> () -> new SizeRenderer(new Medium202TrailingRenderer(), Medium202TrailingVisual::new))
            .build(),
        MEDIUM_4_0_4_TRAILING = create("medium_4_0_4_trailing", DOUBLEAXLE_CYCLE_GROUP)
            .displayName(Component.translatable("railways.bogeys.styles.medium_4_0_4_trailing"))
            .size(BogeySizes.SMALL, CRBlocks.MEDIUM_4_0_4_TRAILING, () -> () -> new SizeRenderer(new Medium404TrailingRenderer(), Medium404TrailingVisual::new))
            .build(),
        MEDIUM_6_0_6_TRAILING = create("medium_6_0_6_trailing", TRIPLEAXLE_CYCLE_GROUP)
            .displayName(Component.translatable("railways.bogeys.styles.medium_6_0_6_trailing"))
            .size(BogeySizes.SMALL, CRBlocks.MEDIUM_TRIPLE_WHEEL, () -> () -> new SizeRenderer(new Medium606TrailingRenderer(), Medium606TrailingVisual::new))
            .build(),
        MEDIUM_6_0_6_TENDER = create("medium_6_0_6_tender", TRIPLEAXLE_CYCLE_GROUP)
            .displayName(Component.translatable("railways.bogeys.styles.medium_6_0_6_tender"))
            .size(BogeySizes.SMALL, CRBlocks.MEDIUM_TRIPLE_WHEEL, () -> () -> new SizeRenderer(new Medium606TenderRenderer(), Medium606TenderVisual::new))
            .build(),
        MEDIUM_8_0_8_TENDER = create("medium_8_0_8_tender", QUADRUPLEAXLE_CYCLE_GROUP)
            .displayName(Component.translatable("railways.bogeys.styles.medium_8_0_8_tender"))
            .size(BogeySizes.SMALL, CRBlocks.MEDIUM_QUADRUPLE_WHEEL, () -> () -> new SizeRenderer(new Medium808TenderRenderer(), Medium808TenderVisual::new))
            .build(),
        MEDIUM_10_0_10_TENDER = create("medium_10_0_10_tender", QUINTUPLEAXLE_CYCLE_GROUP)
            .displayName(Component.translatable("railways.bogeys.styles.medium_10_0_10_tender"))
            .size(BogeySizes.SMALL, CRBlocks.MEDIUM_QUINTUPLE_WHEEL, () -> () -> new SizeRenderer(new Medium10010TenderRenderer(), Medium10010TenderVisual::new))
            .build();

    // Large
    public static final BogeyStyle
        LARGE_CREATE_STYLED_0_4_0 = create("large_create_style_0_4_0", DOUBLEAXLE_CYCLE_GROUP)
            .displayName(Component.translatable("railways.bogeys.styles.large_create_style_0_4_0"))
            .size(BogeySizes.LARGE, CRBlocks.LARGE_CREATE_STYLE_0_4_0, () -> () -> new SizeRenderer(new LargeCreateStyled040Renderer(), LargeCreateStyled040Visual::new))
            .build(),
        LARGE_CREATE_STYLED_0_6_0 = create("large_create_style_0_6_0", TRIPLEAXLE_CYCLE_GROUP)
            .displayName(Component.translatable("railways.bogeys.styles.large_create_style_0_6_0"))
            .size(BogeySizes.LARGE, CRBlocks.LARGE_CREATE_STYLE_0_6_0, () -> () -> new SizeRenderer(new LargeCreateStyled060Renderer(), LargeCreateStyled060Visual::new))
            .build(),
        LARGE_CREATE_STYLED_0_8_0 = create("large_create_style_0_8_0", QUADRUPLEAXLE_CYCLE_GROUP)
            .displayName(Component.translatable("railways.bogeys.styles.large_create_style_0_8_0"))
            .size(BogeySizes.LARGE, CRBlocks.LARGE_CREATE_STYLE_0_8_0, () -> () -> new SizeRenderer(new LargeCreateStyled080Renderer(), LargeCreateStyled080Visual::new))
            .build(),
        LARGE_CREATE_STYLED_0_10_0 = create("large_create_style_0_10_0", QUINTUPLEAXLE_CYCLE_GROUP)
            .displayName(Component.translatable("railways.bogeys.styles.large_create_style_0_10_0"))
            .size(BogeySizes.LARGE, CRBlocks.LARGE_CREATE_STYLE_0_10_0, () -> () -> new SizeRenderer(new LargeCreateStyled0100Renderer(), LargeCreateStyled0100Visual::new))
            .build(),
        LARGE_CREATE_STYLED_0_12_0 = create("large_create_style_0_12_0", SEXTUPLEAXLE_CYCLE_GROUP)
            .displayName(Component.translatable("railways.bogeys.styles.large_create_style_0_12_0"))
            .size(BogeySizes.LARGE, CRBlocks.LARGE_CREATE_STYLE_0_12_0, () -> () -> new SizeRenderer(new LargeCreateStyled0120Renderer(), LargeCreateStyled0120Visual::new))
            .build();


    public static BogeyStyle.Builder create(String name, String cycleGroup) {
        return create(Railways.asResource(name), Railways.asResource(cycleGroup))
            .displayName(Component.translatable("railways.bogeys.styles." + name));
    }

    public static BogeyStyle.Builder create(String name, ResourceLocation cycleGroup) {
        return create(Railways.asResource(name), cycleGroup);
    }

    public static BogeyStyle.Builder create(ResourceLocation name, ResourceLocation cycleGroup) {
        return new BogeyStyle.Builder(name, cycleGroup);
    }

    public static CategoryEntry registerCategory(String name) {
        return registerCategory(Railways.MOD_ID, name);
    }

    public static CategoryEntry registerCategory(ResourceLocation id) {
        return registerCategory(id.getPath(), id.getNamespace());
    }

    public static CategoryEntry registerCategory(String modid, String name) {
        Component categoryName = Component.translatable(modid + ".gui.bogey_menu.category." + name);
        ResourceLocation categoryId = new ResourceLocation(modid, "bogey_menu/category/" + name);

        return BogeyMenuManager.INSTANCE.registerCategory(categoryName, categoryId);
    }

    private static void addToCategory(CategoryEntry category, BogeyStyle style) {
        addToCategory(category, style, BogeyMenuManagerImpl.defaultScale);
    }

    private static void addToCategory(CategoryEntry category, BogeyStyle style, float scale) {
        String bogeyName = style.id.getPath();
        if (style == STANDARD) bogeyName = "default";
        ResourceLocation icon = Railways.asResource("textures/gui/bogey_icons/" + bogeyName + "_icon.png");

        BogeyMenuManager.INSTANCE.addToCategory(category, style, icon, scale);
    }

    private static void setScalesForSizes(BogeyStyle style, BogeySizes.BogeySize size, float scale) {
        BogeyMenuManager.INSTANCE.setScalesForBogeySizes(style, size, scale);
    }

    public static void register() {
        Railways.LOGGER.info("Registered bogey styles from " + Railways.MOD_ID);

        map(AllBogeyStyles.STANDARD, CRTrackType.WIDE_GAUGE, WIDE_DEFAULT);
        map(AllBogeyStyles.STANDARD, CRTrackType.NARROW_GAUGE, NARROW_DEFAULT);
        mapReverse(NARROW_DOUBLE_SCOTCH, AllBogeyStyles.STANDARD);
        mapReverse(WIDE_COMICALLY_LARGE, AllBogeyStyles.STANDARD);

        listUnder(WIDE_DEFAULT, AllBogeyStyles.STANDARD);
        listUnder(NARROW_DEFAULT, AllBogeyStyles.STANDARD);

        // Set scale's for BogeySize's
        setScalesForSizes(WIDE_DEFAULT, BogeySizes.SMALL, 20);

        // Standard Category
        addToCategory(STANDARD_CATEGORY, INVISIBLE);
        addToCategory(STANDARD_CATEGORY, WIDE_COMICALLY_LARGE, 17);
        addToCategory(STANDARD_CATEGORY, STANDARD);
        addToCategory(STANDARD_CATEGORY, NARROW_DOUBLE_SCOTCH);

        // Single Axle Category
        addToCategory(SINGLEAXLE_CATEGORY, SINGLEAXLE);
        addToCategory(SINGLEAXLE_CATEGORY, COILSPRING);
        addToCategory(SINGLEAXLE_CATEGORY, LEAFSPRING);
        addToCategory(SINGLEAXLE_CATEGORY, MEDIUM_SINGLE_WHEEL);
        addToCategory(SINGLEAXLE_CATEGORY, MEDIUM_2_0_2_TRAILING);

        // Double Axle Category
        addToCategory(DOUBLEAXLE_CATEGORY, MODERN);
        addToCategory(DOUBLEAXLE_CATEGORY, BLOMBERG);
        addToCategory(DOUBLEAXLE_CATEGORY, Y25);
        addToCategory(DOUBLEAXLE_CATEGORY, FREIGHT);
        addToCategory(DOUBLEAXLE_CATEGORY, PASSENGER);
        addToCategory(DOUBLEAXLE_CATEGORY, ARCHBAR);
        addToCategory(DOUBLEAXLE_CATEGORY, MEDIUM_STANDARD);
        addToCategory(DOUBLEAXLE_CATEGORY, MEDIUM_4_0_4_TRAILING);
        addToCategory(DOUBLEAXLE_CATEGORY, LARGE_CREATE_STYLED_0_4_0);

        // Triple Axle Category
        addToCategory(TRIPLEAXLE_CATEGORY, HEAVYWEIGHT, 20);
        addToCategory(TRIPLEAXLE_CATEGORY, RADIAL, 20);
        addToCategory(TRIPLEAXLE_CATEGORY, MEDIUM_6_0_6_TRAILING, 20);
        addToCategory(TRIPLEAXLE_CATEGORY, MEDIUM_6_0_6_TENDER, 20);
        addToCategory(TRIPLEAXLE_CATEGORY, LARGE_CREATE_STYLED_0_6_0, 20);

        // Quadruple Axle Category
        addToCategory(QUADRUPLEAXLE_CATEGORY, MEDIUM_QUADRUPLE_WHEEL, 19);
        addToCategory(QUADRUPLEAXLE_CATEGORY, MEDIUM_8_0_8_TENDER, 19);
        addToCategory(QUADRUPLEAXLE_CATEGORY, LARGE_CREATE_STYLED_0_8_0, 17);

        // Quintuple Axle Category
        addToCategory(QUINTUPLEAXLE_CATEGORY, MEDIUM_QUINTUPLE_WHEEL, 17);
        addToCategory(QUINTUPLEAXLE_CATEGORY, MEDIUM_10_0_10_TENDER, 17);
        addToCategory(QUINTUPLEAXLE_CATEGORY, LARGE_CREATE_STYLED_0_10_0, 15);

        // Sextuple Axle Category
        addToCategory(SEXTUPLEAXLE_CATEGORY, LARGE_CREATE_STYLED_0_12_0, 13);

        if (Utils.isDevEnv()) {
            CategoryEntry ALL_TEST_CATEGORY = registerCategory(Railways.MOD_ID, "all_test");
            for (BogeyStyle style : AllBogeyStyles.BOGEY_STYLES.values()) {
                if (hideInSelectionMenu(style)) continue;
                addToCategory(ALL_TEST_CATEGORY, style);
            }
        }
    }
}