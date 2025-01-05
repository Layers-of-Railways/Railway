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

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.palettes.PalettesColor;
import com.railwayteam.railways.registry.CRPalettes.Wrapping;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTType;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.block.render.SpriteShifter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public class CRSpriteShifts {
    public static final CTSpriteShiftEntry FUEL_TANK = getCT(AllCTTypes.RECTANGLE, "fuel_tank"),
        FUEL_TANK_TOP = getCT(AllCTTypes.RECTANGLE, "fuel_tank_top"),
        FUEL_TANK_INNER = getCT(AllCTTypes.RECTANGLE, "fuel_tank_inner");

    public static final EnumMap<PalettesColor, CTSpriteShiftEntry>
        SLASHED_LOCOMETAL = new EnumMap<>(PalettesColor.class),
        RIVETED_LOCOMETAL = new EnumMap<>(PalettesColor.class),
        SMOKEBOX = new EnumMap<>(PalettesColor.class),
        BRASS_WRAPPED_SMOKEBOX = new EnumMap<>(PalettesColor.class),
        COPPER_WRAPPED_SMOKEBOX = new EnumMap<>(PalettesColor.class),
        IRON_WRAPPED_SMOKEBOX = new EnumMap<>(PalettesColor.class),
        BRASS_WRAPPED_LOCOMETAL = new EnumMap<>(PalettesColor.class),
        COPPER_WRAPPED_LOCOMETAL = new EnumMap<>(PalettesColor.class),
        IRON_WRAPPED_LOCOMETAL = new EnumMap<>(PalettesColor.class),
        BOILER_SIDE = new EnumMap<>(PalettesColor.class),
        BRASS_WRAPPED_BOILER_SIDE = new EnumMap<>(PalettesColor.class),
        COPPER_WRAPPED_BOILER_SIDE = new EnumMap<>(PalettesColor.class),
        IRON_WRAPPED_BOILER_SIDE = new EnumMap<>(PalettesColor.class);

    public static EnumMap<PalettesColor, CTSpriteShiftEntry> getSmokebox(@Nullable Wrapping wrapping) {
        if (wrapping == null) return SMOKEBOX;
        return switch (wrapping) {
            case BRASS -> BRASS_WRAPPED_SMOKEBOX;
            case COPPER -> COPPER_WRAPPED_SMOKEBOX;
            case IRON -> IRON_WRAPPED_SMOKEBOX;
        };
    }

    private static void initLocometal(@NotNull PalettesColor color) {
        SLASHED_LOCOMETAL.put(color, locometal(color, "slashed"));
        RIVETED_LOCOMETAL.put(color, locometal(color, "riveted"));
        SMOKEBOX.put(color, locometalSmokebox(color, "tank_side"));
        BRASS_WRAPPED_SMOKEBOX.put(color, locometalSmokebox(color, "wrapped_tank_side"));
        COPPER_WRAPPED_SMOKEBOX.put(color, locometalSmokebox(color, "copper_wrapped_tank_side"));
        IRON_WRAPPED_SMOKEBOX.put(color, locometalSmokebox(color, "iron_wrapped_tank_side"));
        BRASS_WRAPPED_LOCOMETAL.put(color, locometal(color, "wrapped_slashed"));
        COPPER_WRAPPED_LOCOMETAL.put(color, locometal(color, "copper_wrapped_slashed"));
        IRON_WRAPPED_LOCOMETAL.put(color, locometal(color, "iron_wrapped_slashed"));
        BOILER_SIDE.put(color, locometalBoiler(color, "boiler_side"));
        BRASS_WRAPPED_BOILER_SIDE.put(color, locometalBoiler(color, "wrapped_boiler_side"));
        COPPER_WRAPPED_BOILER_SIDE.put(color, locometalBoiler(color, "copper_wrapped_boiler_side"));
        IRON_WRAPPED_BOILER_SIDE.put(color, locometalBoiler(color, "iron_wrapped_boiler_side"));
    }

    static {
        for (PalettesColor color : PalettesColor.values()) {
            initLocometal(color);
        }
    }


    //
    private static CTSpriteShiftEntry locometal(@NotNull PalettesColor color, String name) {
        String colorName = color.getSerializedName();
        return omni("palettes/" + colorName + "/" + name);
    }

    private static CTSpriteShiftEntry locometalBoiler(@NotNull PalettesColor color, String name) {
        String colorName = color.getSerializedName();
        return horizontalKryppers("palettes/" + colorName + "/" + name);
    }

    private static CTSpriteShiftEntry locometalSmokebox(@NotNull PalettesColor color, String name) {
        String colorName = color.getSerializedName();
        return verticalPinkmachine("palettes/" + colorName + "/" + name);
    }

    private static CTSpriteShiftEntry omni(String name) {
        return getCT(AllCTTypes.OMNIDIRECTIONAL, name);
    }

    @SuppressWarnings("unused")
    private static CTSpriteShiftEntry horizontal(String name) {
        return getCT(AllCTTypes.HORIZONTAL, name);
    }

    private static CTSpriteShiftEntry horizontalKryppers(String name) {
        return getCT(AllCTTypes.HORIZONTAL_KRYPPERS, name);
    }

    @SuppressWarnings("unused")
    private static CTSpriteShiftEntry vertical(String name) {
        return getCT(AllCTTypes.VERTICAL, name);
    }

    private static CTSpriteShiftEntry verticalPinkmachine(String name) {
        return getCT(CRCTTypes.VERTICAL_PINKMACHINE, name);
    }

    //

    @SuppressWarnings("unused")
    private static SpriteShiftEntry get(String originalLocation, String targetLocation) {
        return SpriteShifter.get(Railways.asResource(originalLocation), Railways.asResource(targetLocation));
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName, String connectedTextureName) {
        return CTSpriteShifter.getCT(type, Railways.asResource("block/" + blockTextureName),
                Railways.asResource("block/" + connectedTextureName + "_connected"));
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName) {
        return getCT(type, blockTextureName, blockTextureName);
    }

    public static void register() {}
}
