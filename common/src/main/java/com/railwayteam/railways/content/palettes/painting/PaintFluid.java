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

package com.railwayteam.railways.content.palettes.painting;

import com.mojang.datafixers.util.Pair;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.palettes.PalettesColor;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Contract;

import java.util.Optional;

public class PaintFluid {
    public static final String LANG_PREFIX = "fluid.railways.paint.";

    public static Optional<PalettesColor> getColor(CompoundTag nbt) {
        if (nbt == null) return Optional.empty();
        Tag colorTag = nbt.get("Color");
        if (colorTag == null) return Optional.empty();

        return PalettesColor.CODEC.decode(NbtOps.INSTANCE, colorTag)
            .resultOrPartial(Util.prefix("Failed to decode color from NBT: ", Railways.LOGGER::error))
            .map(Pair::getFirst);
    }

    @Contract("_, !null -> param1")
    public static CompoundTag setColor(CompoundTag nbt, PalettesColor color) {
        if (color == null) return nbt;
        PalettesColor.CODEC.encodeStart(NbtOps.INSTANCE, color)
            .resultOrPartial(Util.prefix("Failed to encode color to NBT: ", Railways.LOGGER::error))
            .ifPresent(encoded -> nbt.put("Color", encoded));
        return nbt;
    }
}
