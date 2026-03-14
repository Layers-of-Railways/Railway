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

import com.railwayteam.railways.ModSetup;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.base.EnumFilledList;
import com.railwayteam.railways.base.data.BuilderTransformers;
import com.railwayteam.railways.base.data.compat.emi.EmiRecipeDefaultsGen;
import com.railwayteam.railways.content.animated_flywheel.FlywheelMovementBehaviour;
import com.railwayteam.railways.content.palettes.FloatingMetalLadderBlock;
import com.railwayteam.railways.content.palettes.PalettesColor;
import com.railwayteam.railways.content.palettes.RotatedPillarWindowBlock;
import com.railwayteam.railways.content.palettes.boiler.BoilerBlock;
import com.railwayteam.railways.content.palettes.ct.BoilerCTBehaviour;
import com.railwayteam.railways.content.palettes.ct.PalettesPillarCTBehaviour;
import com.railwayteam.railways.content.palettes.doors.HingedDoorBlock;
import com.railwayteam.railways.content.palettes.doors.PalettesSlidingDoorBlock;
import com.railwayteam.railways.content.palettes.hazard_stripes.HazardStripesBlock;
import com.railwayteam.railways.content.palettes.painting.PaintFluid;
import com.railwayteam.railways.content.palettes.smokebox.PalettesSmokeboxBlock;
import com.railwayteam.railways.content.palettes.trapdoors.PalettesTrapDoorBlock;
import com.railwayteam.railways.util.BlockStateUtils;
import com.railwayteam.railways.util.TextUtils;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.contraptions.behaviour.TrapdoorMovingInteraction;
import com.simibubi.create.content.decoration.MetalLadderBlock;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorMovementBehaviour;
import com.simibubi.create.content.kinetics.flywheel.FlywheelBlock;
import com.simibubi.create.foundation.block.connected.SimpleCTBehaviour;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.utility.Pair;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.railwayteam.railways.util.TextUtils.*;
import static com.simibubi.create.AllInteractionBehaviours.interactionBehaviour;
import static com.simibubi.create.AllMovementBehaviours.movementBehaviour;
import static com.simibubi.create.foundation.data.CreateRegistrate.connectedTextures;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

public class CRPalettes {
    private static final CreateRegistrate REGISTRATE = Railways.registrate();
    private static final Map<Block, Pair<Styles, PalettesColor>> REVERSE_LOOKUP = new HashMap<>(Styles.values().length * PalettesColor.values().length, 2);

    public static void register() { // registration order is important for a clean inventory layout
        ModSetup.usePalettesTab();
        for (PalettesColor palettesColor : PalettesColor.values()) {
            for (Styles style : Styles.values()) {
                style.register(palettesColor);
            }
        }
        // reset tab, just to be safe
        ModSetup.useBaseTab();
    }

    public static final Map<Pair<PalettesColor, CycleGroupCategory>, TagKey<Item>> CYCLE_GROUPS =
        new HashMap<>(PalettesColor.values().length * CycleGroupCategory.values().length, 2);

    static {
        for (PalettesColor palettesColor : PalettesColor.values()) {
            for (CycleGroupCategory category : CycleGroupCategory.values()) {
                CYCLE_GROUPS.put(Pair.of(palettesColor, category), CRTags.optionalTag(BuiltInRegistries.ITEM, Railways.asResource("palettes/cycle_groups/" + palettesColor.getSerializedName()+"/"+category.getSerializedName())));
            }
        }
    }

    public static void provideLangEntries(BiConsumer<String, String> consumer) {
        for (PalettesColor color : PalettesColor.values()) {
            String colorLangName = color.isNetherite() ? null : snakeCaseToTitleCase(color.getName());
            for (CycleGroupCategory category : CycleGroupCategory.values()) {
                consumer.accept("tag.item.railways.palettes.cycle_groups."+color.getName()+"."+category.getSerializedName(), joinSpace(colorLangName, category.langName));
            }
        }

        for (Styles style : Styles.values()) {
            consumer.accept("tag.item.railways.palettes.dye_groups."+style.name().toLowerCase(Locale.ROOT), style.dyeGroupLang);
            consumer.accept("tag.block.railways.palettes.dye_groups."+style.name().toLowerCase(Locale.ROOT), style.dyeGroupLang);
        }

        for (PalettesColor color : PalettesColor.values()) {
            if (color.isNetherite()) continue;
            consumer.accept(
                PaintFluid.LANG_PREFIX + color.getSerializedName(),
                snakeCaseToTitleCase(color.getName()) + " Paint"
            );
        }
    }

    public static @Nullable Pair<Styles, PalettesColor> getStyleForBlock(Block block) {
        return REVERSE_LOOKUP.get(block);
    }

    public static @Nullable BlockState getPaintedState(BlockState state, PalettesColor color) {
        Pair<Styles, PalettesColor> info = getStyleForBlock(state.getBlock());
        if (info == null || info.getSecond() == color) return null;
        return BlockStateUtils.blockWithProperties(info.getFirst().get(color).get(), state);
    }

    public enum Styles {
        RIVETED(CRPalettes::rivetedLocometal, "Riveted Locometal"),
        SLASHED(CRPalettes::slashedLocometal, "Slashed Locometal"),
        BRASS_WRAPPED_SLASHED(CRPalettes::brassWrappedLocometal, "Brass Wrapped Locometal", CycleGroupCategory.WRAPPED_BRASS),
        COPPER_WRAPPED_SLASHED(CRPalettes::copperWrappedLocometal, "Copper Wrapped Locometal", CycleGroupCategory.WRAPPED_COPPER),
        IRON_WRAPPED_SLASHED(CRPalettes::ironWrappedLocometal, "Iron Wrapped Locometal", CycleGroupCategory.WRAPPED_IRON),
        VENT(CRPalettes::locometalVent, "Locometal Vents"),
        FLAT_RIVETED(CRPalettes::flatRivetedLocometal, "Flat Riveted Locometal"),
        FLAT_SLASHED(CRPalettes::flatSlashedLocometal, "Flat Slashed Locometal"),
        PLATED(CRPalettes::platedLocometal, "Plated Locometal"),
        PILLAR(CRPalettes::locometalPillar, "Locometal Pillars"),
        SMOKEBOX(CRPalettes.locometalSmokebox(null),"Locometal Smokeboxes"),
        BRASS_WRAPPED_SMOKEBOX(CRPalettes.locometalSmokebox(Wrapping.BRASS), "Brass Wrapped Locometal Smokeboxes", CycleGroupCategory.WRAPPED_BRASS),
        COPPER_WRAPPED_SMOKEBOX(CRPalettes.locometalSmokebox(Wrapping.COPPER), "Copper Wrapped Locometal Smokeboxes", CycleGroupCategory.WRAPPED_COPPER),
        IRON_WRAPPED_SMOKEBOX(CRPalettes.locometalSmokebox(Wrapping.IRON), "Iron Wrapped Locometal Smokeboxes", CycleGroupCategory.WRAPPED_IRON),
        BOILER(CRPalettes::locometalBoiler, "Locometal Boilers", null),
        BRASS_WRAPPED_BOILER(CRPalettes::brassWrappedLocometalBoiler, "Brass Wrapped Locometal Boilers", null),
        COPPER_WRAPPED_BOILER(CRPalettes::copperWrappedLocometalBoiler, "Copper Wrapped Locometal Boilers", null),
        IRON_WRAPPED_BOILER(CRPalettes::ironWrappedLocometalBoiler, "Iron Wrapped Locometal Boilers", null),
        FLYWHEEL(CRPalettes::flywheel, "Locometal Flywheels", null),
        END_LADDER(CRPalettes::endLadder, "Locometal End Ladders", CycleGroupCategory.LADDERS),
        RUNG_LADDER(CRPalettes::rungLadder, "Locometal Rung Ladders", CycleGroupCategory.LADDERS),
        TRAPDOOR(CRPalettes::locometalTrapdoor, "Locometal Trapdoors", null),
        HINGED_DOOR(CRPalettes::hingedLocometalDoor, "Hinged Locometal Doors", CycleGroupCategory.DOORS),
        SLIDING_DOOR(CRPalettes::slidingLocometalDoor, "Sliding Locometal Doors", CycleGroupCategory.DOORS),
        FOLDING_DOOR(CRPalettes::foldingLocometalDoor, "Folding Locometal Doors", CycleGroupCategory.DOORS),
        ROUND_PANE_WINDOW(CRPalettes.locometalWindow(WindowType.ROUND_PANE), "Round Pane Windows", CycleGroupCategory.WINDOWS),
        SINGLE_PANE_WINDOW(CRPalettes.locometalWindow(WindowType.SINGLE_PANE), "Single Pane Windows", CycleGroupCategory.WINDOWS),
        TWO_PANE_WINDOW(CRPalettes.locometalWindow(WindowType.TWO_PANE), "Two Pane Windows", CycleGroupCategory.WINDOWS),
        FOUR_PANE_WINDOW(CRPalettes.locometalWindow(WindowType.FOUR_PANE), "Four Pane Windows", CycleGroupCategory.WINDOWS),
        HAZARD_STRIPES_DIAGONAL_BLACK(CRPalettes.hazardStripes(false, PalettesColor.BLACK), "Diagonal Black Hazard Stripes", CycleGroupCategory.HAZARD_STRIPES_BLACK),
        HAZARD_STRIPES_CHEVRON_BLACK(CRPalettes.hazardStripes(true, PalettesColor.BLACK), "Chevron Black Hazard Stripes", CycleGroupCategory.HAZARD_STRIPES_BLACK),
        HAZARD_STRIPES_DIAGONAL_WHITE(CRPalettes.hazardStripes(false, PalettesColor.WHITE), "Diagonal White Hazard Stripes", CycleGroupCategory.HAZARD_STRIPES_WHITE),
        HAZARD_STRIPES_CHEVRON_WHITE(CRPalettes.hazardStripes(true, PalettesColor.WHITE), "Chevron White Hazard Stripes", CycleGroupCategory.HAZARD_STRIPES_WHITE),
        ;

        private static final Map<CycleGroupCategory, Styles[]> CYCLING = new HashMap<>(CycleGroupCategory.values().length, 2);

        /** It is illegal to modify the return value */
        private static Styles[] getCyclingValues(CycleGroupCategory category) {
            if (!CYCLING.containsKey(category)) {
                int cyclingCount = 0;
                for (Styles style : Styles.values()) {
                    if (style.cycleGroupCategory == category) cyclingCount++;
                }
                Styles[] cycle = new Styles[cyclingCount];
                int index = 0;
                for (Styles style : Styles.values()) {
                    if (style.cycleGroupCategory == category)
                        cycle[index++] = style;
                }
                CYCLING.put(category, cycle);
            }
            return CYCLING.get(category);
        }

        private final Map<PalettesColor, BlockEntry<?>> blocks = new HashMap<>(17, 2);
        private final PaletteBlockRegistrar registrar;
        public final TagKey<Item> dyeGroupTag;
        public final TagKey<Block> dyeGroupBlockTag;
        public final @Nullable CycleGroupCategory cycleGroupCategory;
        public final String dyeGroupLang;

        Styles(PaletteBlockRegistrar registrar, String dyeGroupLang) {
            this(registrar, dyeGroupLang, CycleGroupCategory.BASE);
        }

        Styles(PaletteBlockRegistrar registrar, String dyeGroupLang, @Nullable CycleGroupCategory cycleGroupCategory) {
            this.registrar = registrar;
            this.dyeGroupLang = dyeGroupLang;
            this.dyeGroupTag = CRTags.optionalTag(BuiltInRegistries.ITEM, Railways.asResource("palettes/dye_groups/" + name().toLowerCase(Locale.ROOT)));
            this.dyeGroupBlockTag = CRTags.optionalTag(BuiltInRegistries.BLOCK, Railways.asResource("palettes/dye_groups/" + name().toLowerCase(Locale.ROOT)));
            this.cycleGroupCategory = cycleGroupCategory;
        }

        @SuppressWarnings("unchecked")
        private void register(PalettesColor palettesColor) {
            // we inject this transformer so that every registered block can be reverse-looked up
            // in the future we can of course extend this to apply any other transformations to all palettes blocks
            TransformerProvider transformer = new ChildTransformer(palettesColor, dyeGroupBlockTag);

            BlockEntry<?> registered = cycleGroupCategory != null
                ? registrar.register(transformer, palettesColor, dyeGroupTag, CYCLE_GROUPS.get(Pair.of(palettesColor, cycleGroupCategory)))
                : registrar.register(transformer, palettesColor, dyeGroupTag);

            blocks.put(palettesColor, registered);

            if (palettesColor.isNetherite()) {
                EmiRecipeDefaultsGen.TAG_DEFAULTS.put(dyeGroupTag, blocks.get(PalettesColor.NETHERITE).getId());
            }

            if (cycleGroupCategory != null && cycleGroupCategory.baseStyle.get() == this) {
                EmiRecipeDefaultsGen.TAG_DEFAULTS.put(CYCLE_GROUPS.get(Pair.of(palettesColor, cycleGroupCategory)), registered.getId());
            }
        }

        public BlockEntry<?> get(PalettesColor color) {
            return blocks.get(color);
        }

        public boolean contains(Block block) {
            return blocks.values().stream().anyMatch(e -> e.get() == block);
        }

        private class ChildTransformer implements TransformerProvider {
            private final PalettesColor color;
            private final TagKey<Block>[] blockTags;

            @SafeVarargs
            private ChildTransformer(PalettesColor color, TagKey<Block>... blockTags) {
                this.color = color;
                this.blockTags = blockTags;
            }

            @Override
            public <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> get() {
                return b -> b.onRegister(this::onRegister).tag(blockTags);
            }

            private void onRegister(Block block) {
                REVERSE_LOOKUP.put(block, Pair.of(Styles.this, color));
            }
        }
    }

    @FunctionalInterface
    private interface PaletteBlockRegistrar {
        @SuppressWarnings("unchecked")
        @ApiStatus.NonExtendable
        default BlockEntry<?> register(TransformerProvider transformer, PalettesColor color, TagKey<Item>... tags) {
            String colorString = color.isNetherite() ? "" : color.getSerializedName();
            return register(transformer, color, colorString, color.isNetherite() ? "" : snakeCaseToTitleCase(colorString), tags);
        }

        @SuppressWarnings("unchecked")
        @ApiStatus.OverrideOnly
        BlockEntry<?> register(TransformerProvider transformer, PalettesColor color, String colorString, String colorName, TagKey<Item>... tags);
    }

    @FunctionalInterface
    private interface TransformerProvider {
        <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> get();
    }

    @SafeVarargs
    private static BlockEntry<?> slashedLocometal(TransformerProvider transformer, PalettesColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "slashed_locometal"), Block::new)
            .transform(transformer.get())
            .transform(BuilderTransformers.locoMetalBase(color, "slashed"))
            .onRegister(connectedTextures(() -> new SimpleCTBehaviour(CRSpriteShifts.SLASHED_LOCOMETAL.get(color))))
            .lang(joinSpace(colorName, "Slashed Locometal"))
            .item()
            .transform(BuilderTransformers.locoMetalItem(color))
            .tag(tags)
            .onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "block.railways.generic_radial"))
            .build()
            .register();
    }

    @SafeVarargs
    private static BlockEntry<?> rivetedLocometal(TransformerProvider transformer, PalettesColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "riveted_locometal"), Block::new)
            .transform(transformer.get())
            .transform(BuilderTransformers.locoMetalBase(color, "riveted"))
            .onRegister(connectedTextures(() -> new SimpleCTBehaviour(CRSpriteShifts.RIVETED_LOCOMETAL.get(color))))
            .lang(joinSpace(colorName, "Riveted Locometal"))
            .item()
            .transform(BuilderTransformers.locoMetalItem(color))
            .tag(tags)
            .onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "block.railways.generic_radial"))
            .build()
            .register();
    }

    @SafeVarargs
    private static BlockEntry<?> locometalVent(TransformerProvider transformer, PalettesColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "locometal_vent"), Block::new)
            .transform(transformer.get())
            .transform(BuilderTransformers.locoMetalBase(color, "vent"))
            .onRegister(connectedTextures(() -> new SimpleCTBehaviour(CRSpriteShifts.LOCOMETAL_VENT.get(color))))
            .lang(joinSpace(colorName, "Locometal Vent"))
            .item()
            .transform(BuilderTransformers.locoMetalItem(color))
            .tag(tags)
            .onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "block.railways.generic_radial"))
            .build()
            .register();
    }

    @SafeVarargs
    private static BlockEntry<?> locometalPillar(TransformerProvider transformer, PalettesColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "locometal_pillar"), RotatedPillarBlock::new)
            .transform(transformer.get())
            .transform(BuilderTransformers.locoMetalPillar(color))
            .onRegister(connectedTextures(() -> new PalettesPillarCTBehaviour(CRSpriteShifts.RIVETED_LOCOMETAL_PILLAR.get(color))))
            .lang(joinSpace(colorName, "Locometal Pillar"))
            .item()
            .transform(BuilderTransformers.locoMetalItem(color))
            .tag(tags)
            .onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "block.railways.generic_radial"))
            .build()
            .register();
    }

    private static PaletteBlockRegistrar locometalSmokebox(@Nullable Wrapping wrapping) {
        return (transformer, color, colorString, colorName, tags) ->
            locometalSmokebox(transformer, color, colorString, colorName, wrapping, tags);
    }

    @SafeVarargs
    private static BlockEntry<?> locometalSmokebox(TransformerProvider transformer, PalettesColor color, String colorString, String colorName, @Nullable Wrapping wrapping, TagKey<Item>... tags) {
        String wrappingName = wrapping == null ? null : wrapping.prefix("wrapped");
        String wrappingLangName = wrapping == null
            ? null
            : snakeCaseToTitleCase(wrapping.name()) + " Wrapped";
        return REGISTRATE.block(joinUnderscore(colorString, wrappingName, "locometal_smokebox"), PalettesSmokeboxBlock::new)
            .transform(transformer.get())
            .transform(BuilderTransformers.locoMetalSmokeBox(color, wrapping))
            .onRegister(connectedTextures(() -> new PalettesPillarCTBehaviour(CRSpriteShifts.getSmokebox(wrapping).get(color))))
            .lang(joinSpace(colorName, wrappingLangName, "Locometal Smokebox"))
            .item()
            .transform(BuilderTransformers.locoMetalItem(color))
            .tag(tags)
            .onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "block.railways.generic_radial"))
            .build()
            .register();
    }

    @SafeVarargs
    private static BlockEntry<?> platedLocometal(TransformerProvider transformer, PalettesColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "plated_locometal"), Block::new)
            .transform(transformer.get())
            .transform(BuilderTransformers.locoMetalBase(color, "sheeting"))
            .lang(joinSpace("Plated", colorName, "Locometal"))
            .item()
            .transform(BuilderTransformers.locoMetalItem(color))
            .tag(tags)
            .onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "block.railways.generic_radial"))
            .build()
            .register();
    }

    @SafeVarargs
    private static BlockEntry<?> flatSlashedLocometal(TransformerProvider transformer, PalettesColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "flat_slashed_locometal"), Block::new)
            .transform(transformer.get())
            .transform(BuilderTransformers.locoMetalBase(color, "annexed_slashed"))
            .lang(joinSpace("Flat", colorName, "Slashed Locometal"))
            .item()
            .transform(BuilderTransformers.locoMetalItem(color))
            .tag(tags)
            .onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "block.railways.generic_radial"))
            .build()
            .register();
    }

    @SafeVarargs
    private static BlockEntry<?> flatRivetedLocometal(TransformerProvider transformer, PalettesColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "flat_riveted_locometal"), Block::new)
            .transform(transformer.get())
            .transform(BuilderTransformers.locoMetalBase(color, "annexed_riveted"))
            .lang(joinSpace("Flat", colorName, "Riveted Locometal"))
            .item()
            .transform(BuilderTransformers.locoMetalItem(color))
            .tag(tags)
            .onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "block.railways.generic_radial"))
            .build()
            .register();
    }

    @SafeVarargs
    private static BlockEntry<?> brassWrappedLocometal(TransformerProvider transformer, PalettesColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "brass_wrapped_locometal"), Block::new)
            .transform(transformer.get())
            .transform(BuilderTransformers.locoMetalBase(color, "wrapped_slashed"))
            .onRegister(connectedTextures(() -> new SimpleCTBehaviour(CRSpriteShifts.BRASS_WRAPPED_LOCOMETAL.get(color))))
            .lang(joinSpace(colorName, "Brass Wrapped Locometal"))
            .item()
            .transform(BuilderTransformers.locoMetalItem(color))
            .tag(tags)
            .build()
            .register();
    }

    @SafeVarargs
    private static BlockEntry<?> copperWrappedLocometal(TransformerProvider transformer, PalettesColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "copper_wrapped_locometal"), Block::new)
            .transform(transformer.get())
            .transform(BuilderTransformers.locoMetalBase(color, "copper_wrapped_slashed"))
            .onRegister(connectedTextures(() -> new SimpleCTBehaviour(CRSpriteShifts.COPPER_WRAPPED_LOCOMETAL.get(color))))
            .lang(joinSpace(colorName, "Copper Wrapped Locometal"))
            .item()
            .transform(BuilderTransformers.locoMetalItem(color))
            .tag(tags)
            .build()
            .register();
    }

    @SafeVarargs
    private static BlockEntry<?> ironWrappedLocometal(TransformerProvider transformer, PalettesColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "iron_wrapped_locometal"), Block::new)
            .transform(transformer.get())
            .transform(BuilderTransformers.locoMetalBase(color, "iron_wrapped_slashed"))
            .onRegister(connectedTextures(() -> new SimpleCTBehaviour(CRSpriteShifts.IRON_WRAPPED_LOCOMETAL.get(color))))
            .lang(joinSpace(colorName, "Iron Wrapped Locometal"))
            .item()
            .transform(BuilderTransformers.locoMetalItem(color))
            .tag(tags)
            .build()
            .register();
    }

    @SafeVarargs
    private static BlockEntry<?> locometalBoiler(TransformerProvider transformer, PalettesColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "locometal_boiler"), BoilerBlock::new)
            .transform(transformer.get())
            .transform(BuilderTransformers.locoMetalBoiler(color, null))
            .onRegister(connectedTextures(() -> new BoilerCTBehaviour(CRSpriteShifts.BOILER_SIDE.get(color))))
            .lang(joinSpace(colorName, "Locometal Boiler"))
            .item()
            .tag(tags)
            .transform(customItemModel(join("/", "palettes", color.getSerializedName(), "locometal_boiler_flat_x")))
            .register();
    }

    @SafeVarargs
    private static BlockEntry<?> brassWrappedLocometalBoiler(TransformerProvider transformer, PalettesColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "brass_wrapped_locometal_boiler"), BoilerBlock::new)
            .transform(transformer.get())
            .transform(BuilderTransformers.locoMetalBoiler(color, Wrapping.BRASS))
            .onRegister(connectedTextures(() -> new BoilerCTBehaviour(CRSpriteShifts.BRASS_WRAPPED_BOILER_SIDE.get(color))))
            .lang(joinSpace(colorName, "Brass Wrapped Locometal Boiler"))
            .item()
            .tag(tags)
            .transform(customItemModel(join("/", "palettes", color.getSerializedName(), "brass_wrapped_locometal_boiler_flat_x")))
            .register();
    }

    @SafeVarargs
    private static BlockEntry<?> copperWrappedLocometalBoiler(TransformerProvider transformer, PalettesColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "copper_wrapped_locometal_boiler"), BoilerBlock::new)
            .transform(transformer.get())
            .transform(BuilderTransformers.locoMetalBoiler(color, Wrapping.COPPER))
            .onRegister(connectedTextures(() -> new BoilerCTBehaviour(CRSpriteShifts.COPPER_WRAPPED_BOILER_SIDE.get(color))))
            .lang(joinSpace(colorName, "Copper Wrapped Locometal Boiler"))
            .item()
            .tag(tags)
            .transform(customItemModel(join("/", "palettes", color.getSerializedName(), "copper_wrapped_locometal_boiler_flat_x")))
            .register();
    }

    @SafeVarargs
    private static BlockEntry<?> ironWrappedLocometalBoiler(TransformerProvider transformer, PalettesColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "iron_wrapped_locometal_boiler"), BoilerBlock::new)
            .transform(transformer.get())
            .transform(BuilderTransformers.locoMetalBoiler(color, Wrapping.IRON))
            .onRegister(connectedTextures(() -> new BoilerCTBehaviour(CRSpriteShifts.IRON_WRAPPED_BOILER_SIDE.get(color))))
            .lang(joinSpace(colorName, "Iron Wrapped Locometal Boiler"))
            .item()
            .tag(tags)
            .transform(customItemModel(join("/", "palettes", color.getSerializedName(), "iron_wrapped_locometal_boiler_flat_x")))
            .register();
    }

    @SafeVarargs
    private static BlockEntry<?> endLadder(TransformerProvider transformer, PalettesColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "locometal_end_ladder"), FloatingMetalLadderBlock::new)
            .transform(transformer.get())
            .transform(BuilderTransformers.locoMetalLadder(color, "end", tags))
            .lang(joinSpace(colorName, "Locometal End Ladder"))
            .register();
    }

    @SafeVarargs
    private static BlockEntry<?> rungLadder(TransformerProvider transformer, PalettesColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "locometal_rung_ladder"), MetalLadderBlock::new)
            .transform(transformer.get())
            .transform(BuilderTransformers.locoMetalLadder(color, "rung", tags))
            .lang(joinSpace(colorName, "Locometal Rung Ladder"))
            .register();
    }

    @SafeVarargs
    private static BlockEntry<?> flywheel(TransformerProvider transformer, PalettesColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "locometal_flywheel"), FlywheelBlock::new)
            .transform(transformer.get())
            .transform(BuilderTransformers.locoMetalFlywheel(color, tags))
            .onRegister(movementBehaviour(new FlywheelMovementBehaviour()))
            .lang(joinSpace(colorName, "Locometal Flywheel"))
            .register();
    }

    // is this overcomplicated and silly? yes. does `hingedLocometalDoor` explode if you simply try to pass in an empty array? also yes.
    @SuppressWarnings("unchecked")
    private static final TagKey<Block>[] NO_TAGS = (TagKey<Block>[]) new TagKey[0];
    @SuppressWarnings("unchecked")
    private static final TagKey<Block>[] NO_DOUBLE_DOOR_TAGS = (TagKey<Block>[]) new TagKey[]{AllTags.AllBlockTags.NON_DOUBLE_DOOR.tag};

    @SafeVarargs
    private static BlockEntry<?> hingedLocometalDoor(TransformerProvider transformer, PalettesColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "hinged_locometal_door"), HingedDoorBlock::new)
            .transform(transformer.get())
            .transform(BuilderTransformers.locometalDoor(color, "hinged", tags, NO_TAGS))
            .transform(BuilderTransformers.locometalHingedDoorBlockState(color, "hinged"))
            .lang(joinSpace(colorName, "Hinged Locometal Door"))
            .register();
    }

    @SafeVarargs
    private static BlockEntry<?> slidingLocometalDoor(TransformerProvider transformer, PalettesColor color, String colorString, String colorName, TagKey<Item>... tags) {
        TagKey<Item>[] slidingTags = Arrays.copyOf(tags, tags.length + 1);
        slidingTags[tags.length] = AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag;
        return REGISTRATE.block(joinUnderscore(colorString, "sliding_locometal_door"), PalettesSlidingDoorBlock.create(false, color))
            .transform(transformer.get())
            .transform(BuilderTransformers.locometalDoor(color, "sliding", slidingTags, NO_DOUBLE_DOOR_TAGS))
            .transform(BuilderTransformers.locometalSlidingDoorBlockState(color, "sliding"))
            .onRegister(movementBehaviour(new SlidingDoorMovementBehaviour()))
            .lang(joinSpace(colorName, "Sliding Locometal Door"))
            .register();
    }

    @SafeVarargs
    private static BlockEntry<?> foldingLocometalDoor(TransformerProvider transformer, PalettesColor color, String colorString, String colorName, TagKey<Item>... tags) {
        TagKey<Item>[] foldingTags = Arrays.copyOf(tags, tags.length + 1);
        foldingTags[tags.length] = AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag;
        return REGISTRATE.block(joinUnderscore(colorString, "folding_locometal_door"), PalettesSlidingDoorBlock.create(true, color))
            .transform(transformer.get())
            .transform(BuilderTransformers.locometalDoor(color, "folding", foldingTags, NO_DOUBLE_DOOR_TAGS))
            .transform(BuilderTransformers.locometalFoldingDoorBlockState(color, "folding"))
            .onRegister(movementBehaviour(new SlidingDoorMovementBehaviour()))
            .lang(joinSpace(colorName, "Folding Locometal Door"))
            .register();
    }

    private static BlockBehaviour.Properties glassProperties(BlockBehaviour.Properties p) {
        return p.isValidSpawn(CRPalettes::never)
            .isRedstoneConductor(CRPalettes::never)
            .isSuffocating(CRPalettes::never)
            .isViewBlocking(CRPalettes::never)
            .instrument(NoteBlockInstrument.HAT)
            .strength(0.3F)
            .sound(SoundType.GLASS)
            .noOcclusion();
    }

    private static <A, B, C> boolean never(A $, B $$, C $$$) {
        return false;
    }

    private static <A, B, C, D> boolean never(A $, B $$, C $$$, D $$$$) {
        return false;
    }

    private static PaletteBlockRegistrar locometalWindow(WindowType type) {
        return (TransformerProvider transformer, PalettesColor color, String colorString, String colorName, TagKey<Item>... tags) ->
            REGISTRATE.block(joinUnderscore(colorString, type.getSerializedName() + "_locometal_window"), RotatedPillarWindowBlock::transparent)
                .transform(transformer.get())
                .transform(BuilderTransformers.locometalWindow(color, type))
                .properties(CRPalettes::glassProperties)
                .lang(joinSpace(colorName, type.getLangName(), "Locometal Window"))
                .addLayer(() -> RenderType::cutoutMipped)
                .tag(BlockTags.IMPERMEABLE)
                .removeTag(ProviderType.BLOCK_TAGS, AllTags.AllBlockTags.WRENCH_PICKUP.tag)
                .loot(RegistrateBlockLootTables::dropWhenSilkTouch)
                .onRegister(connectedTextures(() -> new PalettesPillarCTBehaviour(CRSpriteShifts.WINDOWS.get(type).get(color))))
                .item()
                .transform(BuilderTransformers.locoMetalItem(color))
                .tag(tags)
                .onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "block.railways.generic_radial"))
                .build()
                .register();
    }

    @SafeVarargs
    private static BlockEntry<?> locometalTrapdoor(TransformerProvider transformer, PalettesColor color, String colorString, String colorName, TagKey<Item>... tags) {
        TagKey<Item>[] trapdoorTags = Arrays.copyOf(tags, tags.length + 1);
        trapdoorTags[trapdoorTags.length - 1] = ItemTags.TRAPDOORS;
        return REGISTRATE.block(joinUnderscore(colorString, "locometal_trapdoor"), PalettesTrapDoorBlock::new)
            .transform(transformer.get())
            .transform(BuilderTransformers.locometalTrapdoor(color))
            .lang(joinSpace(colorName, "Locometal Trapdoor"))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .addLayer(() -> RenderType::cutoutMipped)
            .tag(BlockTags.TRAPDOORS)
            .onRegister(interactionBehaviour(new TrapdoorMovingInteraction()))
            .item()
            .transform(BuilderTransformers.locoMetalItem(color))
            .tag(trapdoorTags)
            .build()
            .register();
    }

    private static PaletteBlockRegistrar hazardStripes(boolean chevron, PalettesColor base) {
        return (transformer, color, colorString, colorName, tags) -> {
            colorName = colorName.isEmpty() ? "Locometal" : colorName;
            return REGISTRATE.block(
                    joinUnderscore(colorString, "hazard_stripes", chevron ? "chevron" : "diagonal", "on", base.getSerializedName()),
                    HazardStripesBlock.directional(color, base)
                )
                .transform(transformer.get())
                .transform(BuilderTransformers.locoMetalBase(color, null))
                .transform(BuilderTransformers.hazardStripes(chevron))
                .lang(joinSpace(colorName, "on", snakeCaseToTitleCase(base.getName()), chevron ? "Chevron" : "Hazard Stripes"))
                .item()
                .transform(BuilderTransformers.locoMetalItem(color))
                .tag(tags)
                .build()
                .register();
        };
    }

    public static class StyledList<T> extends EnumFilledList<Styles, T> {
        public StyledList(Function<Styles, T> filler) {
            super(Styles.class, filler);
        }
    }

    public static class CyclingStyleList<T> implements Iterable<T> {
        private final CycleGroupCategory category;
        private final Map<Styles, T> values = new EnumMap<>(Styles.class);

        public CyclingStyleList(CycleGroupCategory category, Function<Styles, T> filler) {
            this.category = category;
            for (Styles style : Styles.getCyclingValues(category)) {
                values.put(style, filler.apply(style));
            }
        }

        @NotNull
        @Override
        public Iterator<T> iterator() {
            return new Iterator<>() {
                private int index = 0;

                @Override
                public boolean hasNext() {
                    return index < Styles.getCyclingValues(category).length;
                }

                @Override
                public T next() {
                    if (!hasNext())
                        throw new NoSuchElementException();
                    return values.get(Styles.getCyclingValues(category)[index++]);
                }
            };
        }
    }

    public static class CycleCategoryList<T> extends EnumFilledList<CycleGroupCategory, T> {
        public CycleCategoryList(Function<CycleGroupCategory, T> filler) {
            super(CycleGroupCategory.class, filler);
        }
    }

    public static class PalettesColorList<T> extends EnumFilledList<PalettesColor, T> {
        public PalettesColorList(Function<PalettesColor, T> filler) {
            super(PalettesColor.class, filler);
        }
    }

    public static class DyedOnlyPalettesColorList<T> extends PalettesColorList<T> {
        public DyedOnlyPalettesColorList(Function<PalettesColor, T> filler) {
            super(filler);
        }

        @Override
        protected boolean filter(PalettesColor value) {
            return !value.isNetherite();
        }
    }

    public static class VanillaDyedOnlyPalettesColorList<T> extends PalettesColorList<T> {
        public VanillaDyedOnlyPalettesColorList(BiFunction<PalettesColor, DyeColor, T> filler) {
            super(c -> filler.apply(c, c.toDyeColor()));
        }

        @Override
        protected boolean filter(PalettesColor value) {
            return value.isMainSeries();
        }
    }

    public static class WindowTypeList<T> extends EnumFilledList<WindowType, T> {
        public WindowTypeList(Function<WindowType, T> filler) {
            super(WindowType.class, filler);
        }
    }

    public enum Wrapping {
        BRASS(false),
        COPPER(true),
        IRON(true);
        private final boolean doPrefix;

        Wrapping(boolean doPrefix) {
            this.doPrefix = doPrefix;
        }

        public String prefix(String base) {
            return doPrefix ? name().toLowerCase(Locale.ROOT) + "_" + base : base;
        }
    }

    public enum CycleGroupCategory {
        BASE("Locometal", () -> Styles.RIVETED),
        WRAPPED_BRASS("Brass Wrapped Locometal", () -> Styles.BRASS_WRAPPED_SLASHED),
        WRAPPED_COPPER("Copper Wrapped Locometal", () -> Styles.COPPER_WRAPPED_SLASHED),
        WRAPPED_IRON("Iron Wrapped Locometal", () -> Styles.IRON_WRAPPED_SLASHED),
        LADDERS("Locometal Ladders", () -> Styles.END_LADDER),
        DOORS("Locometal Doors", () -> Styles.HINGED_DOOR),
        WINDOWS("Locometal Windows", () -> Styles.SINGLE_PANE_WINDOW),
        HAZARD_STRIPES_BLACK("Hazard Stripes (Black Base)", () -> Styles.HAZARD_STRIPES_DIAGONAL_BLACK),
        HAZARD_STRIPES_WHITE("Hazard Stripes (White Base)", () -> Styles.HAZARD_STRIPES_DIAGONAL_WHITE),
        ;
        public final String langName;
        public final Supplier<Styles> baseStyle;

        CycleGroupCategory(String langName, Supplier<Styles> baseStyle) {
            this.langName = langName;
            this.baseStyle = baseStyle;
        }

        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public TagKey<Item> getTag(@NotNull PalettesColor color) {
            return CYCLE_GROUPS.get(Pair.of(color, this));
        }
    }

    public enum WindowType {
        ROUND_PANE,
        SINGLE_PANE,
        TWO_PANE,
        FOUR_PANE
        ;

        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public String getLangName() {
            return TextUtils.snakeCaseToTitleCase(getSerializedName());
        }

        public String getTextureName() {
            return getSerializedName() + "_window";
        }
    }
}
