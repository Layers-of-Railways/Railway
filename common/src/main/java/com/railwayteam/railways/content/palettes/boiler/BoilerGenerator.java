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

package com.railwayteam.railways.content.palettes.boiler;

import com.railwayteam.railways.registry.CRPalettes.Wrapping;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class BoilerGenerator extends SpecialBlockStateGen {
    protected final @Nullable DyeColor color;
    protected final @Nullable Wrapping wrapping;

    protected BoilerGenerator(@Nullable DyeColor color, @Nullable Wrapping wrapping) {
        this.color = color;
        this.wrapping = wrapping;
    }

    @ExpectPlatform
    public static BoilerGenerator create(@Nullable DyeColor color, @Nullable Wrapping wrapping) {
        throw new AssertionError();
    }

    @Override
    protected int getXRotation(BlockState state) {
        return 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        return 0;
    }
}
