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

import com.railwayteam.railways.content.palettes.painting.PaintFluid;
import com.railwayteam.railways.util.Utils;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.network.chat.MutableComponent;
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
    NETHERITE(  28, "netherite",    0x4e4449, MapColor.COLOR_BLACK,       () -> () -> Blocks.NETHERITE_BLOCK),
    BROWN(      12, "brown",        0x564239, MapColor.COLOR_BROWN,       AllPaletteStoneTypes.SCORIA),
    MAROON(     16, "maroon",       0x732232, MapColor.COLOR_RED),
    RED(        14, "red",          0x953340, MapColor.COLOR_RED,         AllPaletteStoneTypes.CRIMSITE),
    VERMILION(  29, "vermilion",    0xad4847, MapColor.COLOR_RED),
    ORANGE(      1, "orange",       0xc75e4e, MapColor.COLOR_ORANGE),
    GRANITE(    17, "granite",      0xa56d54, MapColor.COLOR_ORANGE,      AllPaletteStoneTypes.GRANITE),
    DRIPSTONE(  18, "dripstone",    0x95735c, MapColor.COLOR_YELLOW,      AllPaletteStoneTypes.DRIPSTONE),
    OCHRUM(     19, "ochrum",       0xaf8c54, MapColor.COLOR_YELLOW,      AllPaletteStoneTypes.OCHRUM),
    YELLOW(      4, "yellow",       0xd39a4a, MapColor.COLOR_YELLOW),
    CHARTREUSE( 20, "chartreuse",   0x7faf4a, MapColor.COLOR_LIGHT_GREEN),
    OLIVE_GREEN(30, "olive_green",  0x49733a, MapColor.COLOR_LIGHT_GREEN),
    LIME(        5, "lime",         0x3e882a, MapColor.COLOR_LIGHT_GREEN),
    GREEN(      13, "green",        0x1a7537, MapColor.COLOR_GREEN),
    PINE_GREEN( 21, "pine_green",   0x0e5331, MapColor.COLOR_GREEN),
    CYAN(        9, "cyan",         0x1b6557, MapColor.COLOR_CYAN,        AllPaletteStoneTypes.VERIDIUM),
    SEA_GREEN(  31, "sea_green",    0x2b5e65, MapColor.COLOR_CYAN),
    TURQUOISE(  22, "turquoise",    0x19827f, MapColor.COLOR_CYAN),
    LIGHT_BLUE(  3, "light_blue",   0x50a5c0, MapColor.COLOR_LIGHT_BLUE),
    BLUE(       11, "blue",         0x43607e, MapColor.COLOR_BLUE,        AllPaletteStoneTypes.ASURINE),
    ROYAL_BLUE( 23, "royal_blue",   0x373c69, MapColor.COLOR_BLUE),
    PURPLE(     10, "purple",       0x673f95, MapColor.COLOR_PURPLE),
    MAGENTA(     2, "magenta",      0xac3673, MapColor.COLOR_MAGENTA),
    PINK(        6, "pink",         0xdd6995, MapColor.COLOR_PINK),
    WHITE(       0, "white",        0xe6e6ee, MapColor.SNOW,              AllPaletteStoneTypes.CALCITE),
    DIORITE(    24, "diorite",      0xbfc0c4, MapColor.SNOW,              AllPaletteStoneTypes.DIORITE),
    LIMESTONE(  25, "limestone",    0xd1ccbe, MapColor.COLOR_LIGHT_GRAY,  AllPaletteStoneTypes.LIMESTONE),
    LIGHT_GRAY(  8, "light_gray",   0x979796, MapColor.COLOR_LIGHT_GRAY,  AllPaletteStoneTypes.ANDESITE),
    TUFF(       26, "tuff",         0x767670, MapColor.COLOR_LIGHT_GRAY,  AllPaletteStoneTypes.TUFF),
    GRAY(        7, "gray",         0x545455, MapColor.COLOR_GRAY,        AllPaletteStoneTypes.DEEPSLATE),
    SCORCHIA(   27, "scorchia",     0x28302f, MapColor.COLOR_GRAY,        AllPaletteStoneTypes.SCORCHIA),
    BLACK(      15, "black",        0x282930, MapColor.COLOR_BLACK),
    ;
    private static final IntFunction<PalettesColor> BY_ID = ByIdMap.continuous(PalettesColor::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    @SuppressWarnings("deprecation")
    public static final StringRepresentable.EnumCodec<PalettesColor> CODEC = StringRepresentable.fromEnum(PalettesColor::values);

    /** guaranteed to match the appropriate {@link DyeColor}, if such exists */
    private final int id;
    /** guaranteed to match the appropriate {@link DyeColor}, if such exists */
    private final @NotNull String name;
    private final int diffuseColor;
    private final @NotNull MapColor mapColor;
    private final @Nullable Supplier<Supplier<Block>> associatedBlock;

    PalettesColor(int id, @NotNull String name, int diffuseColor, @NotNull MapColor mapColor) {
        this(id, name, diffuseColor, mapColor, (Supplier<Supplier<Block>>) null);
    }

    PalettesColor(int id, @NotNull String name, int diffuseColor, @NotNull MapColor mapColor, @NotNull AllPaletteStoneTypes stoneType) {
        this(id, name, diffuseColor, mapColor, () -> () -> stoneType.getVariants().registeredBlocks.get(5).get()); // pillar
    }

    PalettesColor(int id, @NotNull String name, int diffuseColor, @NotNull MapColor mapColor, @Nullable Supplier<Supplier<Block>> associatedBlock) {
        this.id = id;
        this.name = name;
        this.diffuseColor = diffuseColor;
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

    public int getDiffuseColor() {
        return diffuseColor;
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

    public @NotNull String getPaintNameId() {
        return PaintFluid.LANG_PREFIX + getSerializedName();
    }

    public @NotNull MutableComponent getPaintName() {
        return Components.translatable(getPaintNameId());
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
