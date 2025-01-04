/*
 * Steam 'n' Rails
 * Copyright (c) 2025 The Railways Team
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

package com.railwayteam.railways.content.palettes;

import com.railwayteam.railways.util.Utils;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntFunction;
import java.util.function.Supplier;

public enum PalettesColor implements StringRepresentable {
    NETHERITE(  28, "netherite",    MapColor.COLOR_BLACK,       () -> () -> Blocks.NETHERITE_BLOCK),
    BROWN(      12, "brown",        MapColor.COLOR_BROWN,       AllPaletteStoneTypes.SCORIA),
    MAROON(     16, "maroon",       MapColor.COLOR_RED),
    RED(        14, "red",          MapColor.COLOR_RED,         AllPaletteStoneTypes.CRIMSITE),
    ORANGE(      1, "orange",       MapColor.COLOR_ORANGE),
    GRANITE(    17, "granite",      MapColor.COLOR_ORANGE,      AllPaletteStoneTypes.GRANITE),
    DRIPSTONE(  18, "dripstone",    MapColor.COLOR_YELLOW,      AllPaletteStoneTypes.DRIPSTONE),
    OCHRUM(     19, "ochrum",       MapColor.COLOR_YELLOW,      AllPaletteStoneTypes.OCHRUM),
    YELLOW(      4, "yellow",       MapColor.COLOR_YELLOW),
    CHARTREUSE( 20, "chartreuse",   MapColor.COLOR_LIGHT_GREEN),
    LIME(        5, "lime",         MapColor.COLOR_LIGHT_GREEN),
    GREEN(      13, "green",        MapColor.COLOR_GREEN),
    PINE_GREEN( 21, "pine_green",   MapColor.COLOR_GREEN),
    CYAN(        9, "cyan",         MapColor.COLOR_CYAN,        AllPaletteStoneTypes.VERIDIUM),
    TURQUOISE(  22, "turquoise",    MapColor.COLOR_CYAN),
    LIGHT_BLUE(  3, "light_blue",   MapColor.COLOR_LIGHT_BLUE,  AllPaletteStoneTypes.ASURINE),
    BLUE(       11, "blue",         MapColor.COLOR_BLUE),
    ROYAL_BLUE( 23, "royal_blue",   MapColor.COLOR_BLUE),
    PURPLE(     10, "purple",       MapColor.COLOR_PURPLE),
    MAGENTA(     2, "magenta",      MapColor.COLOR_MAGENTA),
    PINK(        6, "pink",         MapColor.COLOR_PINK),
    WHITE(       0, "white",        MapColor.SNOW,              AllPaletteStoneTypes.CALCITE),
    DIORITE(    24, "diorite",      MapColor.SNOW,              AllPaletteStoneTypes.DIORITE),
    LIMESTONE(  25, "limestone",    MapColor.COLOR_LIGHT_GRAY,  AllPaletteStoneTypes.LIMESTONE),
    LIGHT_GRAY(  8, "light_gray",   MapColor.COLOR_LIGHT_GRAY,  AllPaletteStoneTypes.ANDESITE),
    TUFF(       26, "tuff",         MapColor.COLOR_LIGHT_GRAY,  AllPaletteStoneTypes.TUFF),
    GRAY(        7, "gray",         MapColor.COLOR_GRAY,        AllPaletteStoneTypes.DEEPSLATE),
    SCORCHIA(   27, "scorchia",     MapColor.COLOR_GRAY,        AllPaletteStoneTypes.SCORCHIA),
    BLACK(      15, "black",        MapColor.COLOR_BLACK),
    ;
    private static final IntFunction<PalettesColor> BY_ID = ByIdMap.continuous(PalettesColor::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    @SuppressWarnings("deprecation")
    public static final StringRepresentable.EnumCodec<PalettesColor> CODEC = StringRepresentable.fromEnum(PalettesColor::values);

    /** guaranteed to match the appropriate {@link DyeColor}, if such exists */
    private final int id;
    /** guaranteed to match the appropriate {@link DyeColor}, if such exists */
    private final @NotNull String name;
    private final @NotNull MapColor mapColor;
    private final @Nullable Supplier<Supplier<Block>> associatedBlock;

    PalettesColor(int id, @NotNull String name, @NotNull MapColor mapColor) {
        this(id, name, mapColor, (Supplier<Supplier<Block>>) null);
    }

    PalettesColor(int id, @NotNull String name, @NotNull MapColor mapColor, @NotNull AllPaletteStoneTypes stoneType) {
        this(id, name, mapColor, () -> () -> stoneType.getVariants().registeredBlocks.get(5).get()); // pillar
    }

    PalettesColor(int id, @NotNull String name, @NotNull MapColor mapColor, @Nullable Supplier<Supplier<Block>> associatedBlock) {
        this.id = id;
        this.name = name;
        this.mapColor = mapColor;
        this.associatedBlock = associatedBlock;
    }

    /**
     * @return whether this color has a corresponding {@link DyeColor}
     */
    public boolean isMainSeries() {
        return id < 16;
    }

    public boolean isNetherite() {
        return this == NETHERITE;
    }

    public @Nullable DyeColor toDyeColor() {
        return isMainSeries() ? DyeColor.byId(id) : null;
    }

    public @NotNull DyeColor toDyeColor(@NotNull DyeColor fallback) {
        return isMainSeries() ? DyeColor.byId(id) : fallback;
    }

    public int getId() {
        return id;
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull MapColor getMapColor() {
        return mapColor;
    }

    public @Nullable Block getAssociatedBlock() {
        if (associatedBlock == null) return null;
        return associatedBlock.get().get();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }

    @SuppressWarnings("unused")
    public static PalettesColor fromDyeColor(DyeColor dyeColor) {
        return byId(dyeColor.getId());
    }

    public static PalettesColor byId(int colorId) {
        return BY_ID.apply(colorId);
    }

    @SuppressWarnings("unused")
    @Nullable
    @Contract("_,!null->!null;_,null->_")
    public static PalettesColor byName(String translationKey, @Nullable PalettesColor fallback) {
        PalettesColor palettesColor = CODEC.byName(translationKey);
        return palettesColor != null ? palettesColor : fallback;
    }

    static {
        if (Utils.isDevEnv()) {
            // verify that each id is unique
            int[] counts = new int[PalettesColor.values().length];
            for (PalettesColor color : PalettesColor.values()) {
                try {
                    counts[color.id]++;
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new IllegalStateException("Invalid id found in PalettesColor");
                }
            }
            for (int count : counts) {
                if (count != 1) {
                    throw new IllegalStateException("Duplicate or missing id found in PalettesColor");
                }
            }

            // verify that main-series colors match their corresponding DyeColor
            for (PalettesColor color : PalettesColor.values()) {
                if (!color.isMainSeries()) continue;
                DyeColor dyeColor = color.toDyeColor();
                if (dyeColor == null) {
                    throw new IllegalStateException("Main-series color " + color + " has no corresponding DyeColor");
                }
                if (!dyeColor.getSerializedName().equals(color.getSerializedName())) {
                    throw new IllegalStateException("Main-series color " + color + " has a mismatched DyeColor");
                }
                if (!color.getMapColor().equals(dyeColor.getMapColor())) {
                    throw new IllegalStateException("Main-series color " + color + " has a mismatched MapColor");
                }
            }
        }
    }
}
