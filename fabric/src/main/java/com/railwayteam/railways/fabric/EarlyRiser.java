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

package com.railwayteam.railways.fabric;

import com.chocohead.mm.api.ClassTinkerers;
import com.railwayteam.railways.registry.CRIcons;
import com.simibubi.create.foundation.gui.AllIcons;

import java.util.function.Supplier;

public class EarlyRiser implements Runnable {
    @Override
    public void run() {
        ClassTinkerers.enumBuilder("com.simibubi.create.content.contraptions.actors.roller.RollerBlockEntity$RollingMode", AllIcons.class)
                .addEnum("TRACK_REPLACE", () -> { // wrap up safely to prevent premature classloading
                    Supplier<Supplier<Object[]>> supplier = (() -> () -> new Object[] {CRIcons.I_SWAP_TRACKS});
                    return supplier.get().get();
                }).build();
    }
}
