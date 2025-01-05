/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
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
import com.railwayteam.railways.base.data.BuilderTransformers;
import com.railwayteam.railways.base.data.compat.emi.EmiRecipeDefaultsGen;
import com.railwayteam.railways.content.palettes.FloatingMetalLadderBlock;
import com.railwayteam.railways.content.palettes.PalettesColor;
import com.railwayteam.railways.content.palettes.boiler.BoilerBlock;
import com.railwayteam.railways.content.palettes.boiler.BoilerCTBehaviour;
import com.railwayteam.railways.content.palettes.smokebox.PalettesSmokeboxBlock;
import com.railwayteam.railways.content.palettes.smokebox.SmokeboxCTBehaviour;
import com.simibubi.create.content.decoration.MetalLadderBlock;
import com.simibubi.create.foundation.block.connected.SimpleCTBehaviour;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.utility.Pair;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.railwayteam.railways.util.TextUtils.*;
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
        }
    }

    public static @Nullable Pair<Styles, PalettesColor> getStyleForBlock(Block block) {
        return REVERSE_LOOKUP.get(block);
    }

    public enum Styles {
        SLASHED(CRPalettes::slashedLocometal, "Slashed Locometal"),
        RIVETED(CRPalettes::rivetedLocometal, "Riveted Locometal"),
        PILLAR(CRPalettes::locometalPillar, "Locometal Pillars"),
        SMOKEBOX(CRPalettes.locometalSmokebox(null),"Locometal Smokeboxes"),
        BRASS_WRAPPED_SMOKEBOX(CRPalettes.locometalSmokebox(Wrapping.BRASS), "Brass Wrapped Locometal Smokeboxes", CycleGroupCategory.WRAPPED_BRASS),
        COPPER_WRAPPED_SMOKEBOX(CRPalettes.locometalSmokebox(Wrapping.COPPER), "Copper Wrapped Locometal Smokeboxes", CycleGroupCategory.WRAPPED_COPPER),
        IRON_WRAPPED_SMOKEBOX(CRPalettes.locometalSmokebox(Wrapping.IRON), "Iron Wrapped Locometal Smokeboxes", CycleGroupCategory.WRAPPED_IRON),
        PLATED(CRPalettes::platedLocometal, "Plated Locometal"),
        FLAT_SLASHED(CRPalettes::flatSlashedLocometal, "Flat Slashed Locometal"),
        FLAT_RIVETED(CRPalettes::flatRivetedLocometal, "Flat Riveted Locometal"),
        BRASS_WRAPPED_SLASHED(CRPalettes::brassWrappedLocometal, "Brass Wrapped Locometal", CycleGroupCategory.WRAPPED_BRASS),
        COPPER_WRAPPED_SLASHED(CRPalettes::copperWrappedLocometal, "Copper Wrapped Locometal", CycleGroupCategory.WRAPPED_COPPER),
        IRON_WRAPPED_SLASHED(CRPalettes::ironWrappedLocometal, "Iron Wrapped Locometal", CycleGroupCategory.WRAPPED_IRON),
        BOILER(CRPalettes::locometalBoiler, "Locometal Boilers", null),
        BRASS_WRAPPED_BOILER(CRPalettes::brassWrappedLocometalBoiler, "Brass Wrapped Locometal Boilers", null),
        COPPER_WRAPPED_BOILER(CRPalettes::copperWrappedLocometalBoiler, "Copper Wrapped Locometal Boilers", null),
        IRON_WRAPPED_BOILER(CRPalettes::ironWrappedLocometalBoiler, "Iron Wrapped Locometal Boilers", null),
        END_LADDER(CRPalettes::endLadder, "Locometal End Ladders", CycleGroupCategory.LADDERS),
        RUNG_LADDER(CRPalettes::rungLadder, "Locometal Rung Ladders", CycleGroupCategory.LADDERS),
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
        public final @Nullable CycleGroupCategory cycleGroupCategory;
        public final String dyeGroupLang;

        Styles(PaletteBlockRegistrar registrar, String dyeGroupLang) {
            this(registrar, dyeGroupLang, CycleGroupCategory.BASE);
        }

        Styles(PaletteBlockRegistrar registrar, String dyeGroupLang, @Nullable CycleGroupCategory cycleGroupCategory) {
            this.registrar = registrar;
            this.dyeGroupLang = dyeGroupLang;
            this.dyeGroupTag = CRTags.optionalTag(BuiltInRegistries.ITEM, Railways.asResource("palettes/dye_groups/" + name().toLowerCase(Locale.ROOT)));
            this.cycleGroupCategory = cycleGroupCategory;
        }

        @SuppressWarnings("unchecked")
        private void register(PalettesColor palettesColor) {
            // we inject this transformer so that every registered block can be reverse-looked up
            // in the future we can of course extend this to apply any other transformations to all palettes blocks
            TransformerProvider transformer = new ChildTransformer(palettesColor);

            BlockEntry<?> registered = cycleGroupCategory != null
                ? registrar.register(transformer, palettesColor, dyeGroupTag, CYCLE_GROUPS.get(Pair.of(palettesColor, cycleGroupCategory)))
                : registrar.register(transformer, palettesColor, dyeGroupTag);

            blocks.put(palettesColor, registered);

            if (palettesColor.isNetherite()) {
                EmiRecipeDefaultsGen.TAG_DEFAULTS.put(dyeGroupTag, blocks.get(PalettesColor.NETHERITE).getId());
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

            private ChildTransformer(PalettesColor color) {
                this.color = color;
            }

            @Override
            public <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> get() {
                return b -> b.onRegister(this::onRegister);
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
    private static BlockEntry<?> locometalPillar(TransformerProvider transformer, PalettesColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "locometal_pillar"), RotatedPillarBlock::new)
            .transform(transformer.get())
            .transform(BuilderTransformers.locoMetalPillar(color))
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
            .onRegister(connectedTextures(() -> new SmokeboxCTBehaviour(CRSpriteShifts.getSmokebox(wrapping).get(color))))
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
            .transform(BuilderTransformers.locoMetalLadder(color, tags))
            .lang(joinSpace(colorName, "Locometal End Ladder"))
            .register();
    }

    @SafeVarargs
    private static BlockEntry<?> rungLadder(TransformerProvider transformer, PalettesColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "locometal_rung_ladder"), MetalLadderBlock::new)
            .transform(transformer.get())
            .transform(BuilderTransformers.locoMetalLadder(color, tags))
            .lang(joinSpace(colorName, "Locometal Rung Ladder"))
            .register();
    }

    public static class StyledList<T> implements Iterable<T> {
        private final Map<Styles, T> values = new EnumMap<>(Styles.class);

        public StyledList(Function<Styles, T> filler) {
            for (Styles style : Styles.values()) {
                values.put(style, filler.apply(style));
            }
        }

        @NotNull
        @Override
        public Iterator<T> iterator() {
            return new StyledListIterator();
        }

        private class StyledListIterator implements Iterator<T> {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < Styles.values().length;
            }

            @Override
            public T next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                return values.get(Styles.values()[index++]);
            }
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
            return new Iterator<T>() {
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

    public static class CycleCategoryList<T> implements Iterable<T> {
        private final Map<CycleGroupCategory, T> values = new EnumMap<>(CycleGroupCategory.class);

        public CycleCategoryList(Function<CycleGroupCategory, T> filler) {
            for (CycleGroupCategory category : CycleGroupCategory.values()) {
                values.put(category, filler.apply(category));
            }
        }

        @NotNull
        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                private int index = 0;

                @Override
                public boolean hasNext() {
                    return index < CycleGroupCategory.values().length;
                }

                @Override
                public T next() {
                    if (!hasNext())
                        throw new NoSuchElementException();
                    return values.get(CycleGroupCategory.values()[index++]);
                }
            };
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
        BASE("Locometal"),
        WRAPPED_BRASS("Brass Wrapped Locometal"),
        WRAPPED_COPPER("Copper Wrapped Locometal"),
        WRAPPED_IRON("Iron Wrapped Locometal"),
        LADDERS("Locometal Ladders"),
        ;
        public final String langName;

        CycleGroupCategory(String langName) {
            this.langName = langName;
        }

        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
