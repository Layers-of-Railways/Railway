package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.base.data.BuilderTransformers;
import com.railwayteam.railways.base.data.compat.emi.EmiRecipeDefaultsGen;
import com.railwayteam.railways.content.palettes.boiler.BoilerBlock;
import com.railwayteam.railways.content.palettes.boiler.BoilerCTBehaviour;
import com.railwayteam.railways.util.ColorUtils;
import com.simibubi.create.foundation.block.connected.SimpleCTBehaviour;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.railwayteam.railways.util.TextUtils.joinSpace;
import static com.railwayteam.railways.util.TextUtils.joinUnderscore;
import static com.simibubi.create.foundation.data.CreateRegistrate.connectedTextures;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

public class CRPalettes {
    private static final CreateRegistrate REGISTRATE = Railways.registrate().creativeModeTab(
        () -> CRItems.palettesCreativeTab, "Create Steam 'n' Rails: Palettes"
    );

    public static void register() { // registration order is important for a clean inventory layout
        for (Styles style : Styles.values())
            style.register(null);

        for (DyeColor dyeColor : DyeColor.values()) {
            for (Styles style : Styles.values())
                style.register(dyeColor);
        }
    }

    public static final Map<@Nullable DyeColor, TagKey<Item>> CYCLE_GROUPS = new HashMap<>(17, 2);

    static {
        CYCLE_GROUPS.put(null, CRTags.optionalTag(Registry.ITEM, Railways.asResource("palettes/cycle_groups/base")));
        for (DyeColor dyeColor : DyeColor.values()) {
            CYCLE_GROUPS.put(dyeColor, CRTags.optionalTag(Registry.ITEM, Railways.asResource("palettes/cycle_groups/" + dyeColor.name().toLowerCase(Locale.ROOT))));
        }
    }

    public static void provideLangEntries(BiConsumer<String, String> consumer) {
        for (DyeColor color : DyeColor.values()) {
            consumer.accept("tag.item.railways.palettes.cycle_groups."+color.getName(), joinSpace(ColorUtils.coloredName(color.getName()), "Locometal"));
        }
        consumer.accept("tag.item.railways.palettes.cycle_groups.base", "Locometal");

        for (Styles style : Styles.values()) {
            consumer.accept("tag.item.railways.palettes.dye_groups."+style.name().toLowerCase(Locale.ROOT), style.dyeGroupLang);
        }
    }

    public enum Styles {
        SLASHED(CRPalettes::slashedLocometal, true, "Slashed Locometal"),
        RIVETED(CRPalettes::rivetedLocometal, true, "Riveted Locometal"),
        PILLAR(CRPalettes::locometalPillar, true, "Locometal Pillars"),
        SMOKEBOX(CRPalettes::locometalSmokebox, true, "Locometal Smokeboxes"),
        PLATED(CRPalettes::platedLocometal, true, "Plated Locometal"),
        FLAT_SLASHED(CRPalettes::flatSlashedLocometal, true, "Flat Slashed Locometal"),
        FLAT_RIVETED(CRPalettes::flatRivetedLocometal, true, "Flat Riveted Locometal"),
        BRASS_WRAPPED_SLASHED(CRPalettes::brassWrappedLocometal, false, "Brass Wrapped Locometal"),
        BOILER(CRPalettes::locometalBoiler, false, "Locometal Boilers"),
        BRASS_WRAPPED_BOILER(CRPalettes::brassWrappedLocometalBoiler, false, "Brass Wrapped Locometal Boilers")
        ;

        private static Styles[] CYCLING = null;

        public static Styles[] getCyclingValues() {
            if (CYCLING == null) {
                int cyclingCount = 0;
                for (Styles style : Styles.values()) {
                    if (style.includeInCycleGroup) cyclingCount++;
                }
                CYCLING = new Styles[cyclingCount];
                int index = 0;
                for (Styles style : Styles.values()) {
                    if (style.includeInCycleGroup)
                        CYCLING[index++] = style;
                }
            }
            return Arrays.copyOf(CYCLING, CYCLING.length);
        }

        private final Map<@Nullable DyeColor, BlockEntry<?>> blocks = new HashMap<>(17, 2);
        private final PaletteBlockRegistrar registrar;
        public final TagKey<Item> dyeGroupTag;
        public final boolean includeInCycleGroup;
        public final String dyeGroupLang;

        Styles(PaletteBlockRegistrar registrar, boolean includeInCycleGroup, String dyeGroupLang) {
            this.registrar = registrar;
            this.dyeGroupLang = dyeGroupLang;
            this.dyeGroupTag = CRTags.optionalTag(Registry.ITEM, Railways.asResource("palettes/dye_groups/" + name().toLowerCase(Locale.ROOT)));
            this.includeInCycleGroup = includeInCycleGroup;
        }

        @SuppressWarnings("unchecked")
        private void register(@Nullable DyeColor dyeColor) {
            if (includeInCycleGroup) {
                blocks.put(dyeColor, registrar.register(dyeColor, dyeGroupTag, CYCLE_GROUPS.get(dyeColor)));
            } else {
                blocks.put(dyeColor, registrar.register(dyeColor, dyeGroupTag));
            }

            if (dyeColor == null) {
                EmiRecipeDefaultsGen.TAG_DEFAULTS.put(dyeGroupTag, blocks.get(null).getId());
            }
        }

        public BlockEntry<?> get(@Nullable DyeColor color) {
            return blocks.get(color);
        }

        public boolean contains(Block block) {
            return blocks.values().stream().anyMatch(e -> e.get() == block);
        }
    }

    @FunctionalInterface
    private interface PaletteBlockRegistrar {
        @SuppressWarnings("unchecked")
        @ApiStatus.NonExtendable
        default BlockEntry<?> register(@Nullable DyeColor color, TagKey<Item>... tags) {
            String colorString = color == null ? "" : color.name().toLowerCase(Locale.ROOT);
            return register(color, colorString, color == null ? "" : ColorUtils.coloredName(colorString), tags);
        }

        @SuppressWarnings("unchecked")
        @ApiStatus.OverrideOnly
        BlockEntry<?> register(@Nullable DyeColor color, String colorString, String colorName, TagKey<Item>... tags);
    }

    // no textures for these yet
//            COPPER_WRAPPED_LOCOMETAL = new EnumMap<>(DyeColor.class),
//            COPPER_WRAPPED_LOCOMETAL_BOILER = new EnumMap<>(DyeColor.class),
//            IRON_WRAPPED_LOCOMETAL = new EnumMap<>(DyeColor.class),
//            IRON_WRAPPED_LOCOMETAL_BOILER = new EnumMap<>(DyeColor.class);

    @SafeVarargs
    private static BlockEntry<?> slashedLocometal(@Nullable DyeColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "slashed_locometal"), Block::new)
            .transform(BuilderTransformers.locoMetalBase(color, "slashed"))
            .onRegister(connectedTextures(() -> new SimpleCTBehaviour(CRSpriteShifts.SLASHED_LOCOMETAL.get(color))))
            .lang(joinSpace(colorName, "Slashed Locometal"))
            .item()
            .tag(tags)
            .build()
            .register();
    }

    @SafeVarargs
    private static BlockEntry<?> rivetedLocometal(@Nullable DyeColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "riveted_locometal"), Block::new)
            .transform(BuilderTransformers.locoMetalBase(color, "riveted"))
            .onRegister(connectedTextures(() -> new SimpleCTBehaviour(CRSpriteShifts.RIVETED_LOCOMETAL.get(color))))
            .lang(joinSpace(colorName, "Riveted Locometal"))
            .item()
            .tag(tags)
            .build()
            .register();
    }

    @SafeVarargs
    private static BlockEntry<?> locometalPillar(@Nullable DyeColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "locometal_pillar"), RotatedPillarBlock::new)
            .transform(BuilderTransformers.locoMetalPillar(color))
            .lang(joinSpace(colorName, "Locometal Pillar"))
            .item()
            .tag(tags)
            .build()
            .register();
    }

    @SafeVarargs
    public static BlockEntry<?> locometalSmokebox(@Nullable DyeColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "locometal_smokebox"), RotatedPillarBlock::new)
            .transform(BuilderTransformers.locoMetalSmokeBox(color))
            .lang(joinSpace(colorName, "Locometal Smokebox"))
            .item()
            .tag(tags)
            .build()
            .register();
    }

    @SafeVarargs
    public static BlockEntry<?> platedLocometal(@Nullable DyeColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "plated_locometal"), Block::new)
            .transform(BuilderTransformers.locoMetalBase(color, "sheeting"))
            .lang(joinSpace("Plated", colorName, "Locometal"))
            .item()
            .tag(tags)
            .build()
            .register();
    }

    @SafeVarargs
    public static BlockEntry<?> flatSlashedLocometal(@Nullable DyeColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "flat_slashed_locometal"), Block::new)
            .transform(BuilderTransformers.locoMetalBase(color, "annexed_slashed"))
            .lang(joinSpace("Flat", colorName, "Slashed Locometal"))
            .item()
            .tag(tags)
            .build()
            .register();
    }

    @SafeVarargs
    public static BlockEntry<?> flatRivetedLocometal(@Nullable DyeColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "flat_riveted_locometal"), Block::new)
            .transform(BuilderTransformers.locoMetalBase(color, "annexed_riveted"))
            .lang(joinSpace("Flat", colorName, "Riveted Locometal"))
            .item()
            .tag(tags)
            .build()
            .register();
    }

    @SafeVarargs
    public static BlockEntry<?> brassWrappedLocometal(@Nullable DyeColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "brass_wrapped_locometal"), Block::new)
            .transform(BuilderTransformers.locoMetalBase(color, "wrapped_slashed"))
            .onRegister(connectedTextures(() -> new SimpleCTBehaviour(CRSpriteShifts.BRASS_WRAPPED_LOCOMETAL.get(color))))
            .lang(joinSpace(colorName, "Brass Wrapped Locometal"))
            .item()
            .tag(tags)
            .build()
            .register();
    }

    @SafeVarargs
    public static BlockEntry<?> locometalBoiler(@Nullable DyeColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "locometal_boiler"), BoilerBlock::new)
            .transform(BuilderTransformers.locoMetalBoiler(color))
            .onRegister(connectedTextures(() -> new BoilerCTBehaviour(CRSpriteShifts.BOILER_SIDE.get(color))))
            .lang(joinSpace(colorName, "Locometal Boiler"))
            .item()
            .tag(tags)
            .transform(customItemModel(joinUnderscore(colorString, "locometal_boiler_gullet_x")))
            .register();
    }

    @SafeVarargs
    public static BlockEntry<?> brassWrappedLocometalBoiler(@Nullable DyeColor color, String colorString, String colorName, TagKey<Item>... tags) {
        return REGISTRATE.block(joinUnderscore(colorString, "brass_wrapped_locometal_boiler"), BoilerBlock::new)
            .transform(BuilderTransformers.locoMetalBoiler(color))
            .onRegister(connectedTextures(() -> new BoilerCTBehaviour(CRSpriteShifts.BRASS_WRAPPED_BOILER_SIDE.get(color))))
            .lang(joinSpace(colorName, "Brass Wrapped Locometal Boiler"))
            .item()
            .tag(tags)
            .transform(customItemModel(joinUnderscore(colorString, "brass_wrapped_locometal_boiler_gullet_x")))
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
            return new Iterator<T>() {
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
            };
        }
    }

    public static class CyclingStyleList<T> implements Iterable<T> {
        private final Map<Styles, T> values = new EnumMap<>(Styles.class);

        public CyclingStyleList(Function<Styles, T> filler) {
            for (Styles style : Styles.getCyclingValues()) {
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
                    return index < Styles.getCyclingValues().length;
                }

                @Override
                public T next() {
                    if (!hasNext())
                        throw new NoSuchElementException();
                    return values.get(Styles.getCyclingValues()[index++]);
                }
            };
        }
    }
}
