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

package com.railwayteam.railways.content.palettes.hazard_stripes;

import com.railwayteam.railways.content.palettes.PalettesColor;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class HazardStripesBlock extends Block implements IWrenchable {

    protected final PalettesColor mainColor;
    protected final PalettesColor baseColor;

    public static NonNullFunction<Properties, HazardStripesBlock> create(boolean isAxial, PalettesColor mainColor, PalettesColor baseColor) {
        return isAxial ? axial(mainColor, baseColor) : directional(mainColor, baseColor);
    }

    public static NonNullFunction<Properties, HazardStripesBlock> axial(PalettesColor mainColor, PalettesColor baseColor) {
        return properties -> new AxialHazardStripesBlock(properties, mainColor, baseColor);
    }

    public static NonNullFunction<Properties, HazardStripesBlock> directional(PalettesColor mainColor, PalettesColor baseColor) {
        return properties -> new DirectionalHazardStripesBlock(properties, mainColor, baseColor);
    }

    public HazardStripesBlock(Properties properties, PalettesColor mainColor, PalettesColor baseColor) {
        super(properties);
        this.mainColor = mainColor;
        this.baseColor = baseColor;
    }

    public PalettesColor getMainColor() {
        return mainColor;
    }

    public PalettesColor getBaseColor() {
        return baseColor;
    }

    public abstract int getYRot(BlockState state);
}
