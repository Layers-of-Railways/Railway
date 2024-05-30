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

package com.railwayteam.railways.content.bogey_menu.handler;

import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeySizes.BogeySize;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.foundation.utility.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class BogeyMenuHandlerServer {
    @Nullable private static UUID currentPlayer = null;
    private static final HashMap<UUID, Pair<BogeyStyle, @Nullable BogeySize>> selectedStyles = new HashMap<>();


    public static @Nullable UUID getCurrentPlayer() {
        return currentPlayer;
    }

    public static void setCurrentPlayer(@Nullable UUID uuid) {
        currentPlayer = uuid;
    }

    public static void addStyle(UUID uuid, Pair<BogeyStyle, @Nullable BogeySize> pair) {
        selectedStyles.put(uuid, pair);
    }

    public static Pair<BogeyStyle, @Nullable BogeySize> getStyle(UUID uuid) {
        if (selectedStyles.containsKey(uuid))
            return selectedStyles.get(uuid);
        return Pair.of(AllBogeyStyles.STANDARD, BogeySizes.SMALL);
    }
}
