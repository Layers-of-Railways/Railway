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

package com.railwayteam.railways.content.fuel.tank;

import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.ModelFile;

public class FuelTankGenerator extends SpecialBlockStateGen {
    private final String prefix;

    public FuelTankGenerator() {
        this("");
    }

    public FuelTankGenerator(String prefix) {
        this.prefix = prefix;
    }

    @Override
    protected Property<?>[] getIgnoredProperties() {
        return new Property<?>[] { FuelTankBlock.LIGHT_LEVEL };
    }

    @Override
    protected int getXRotation(BlockState state) {
        return 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        return 0;
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                BlockState state) {
        Boolean top = state.getValue(FuelTankBlock.TOP);
        Boolean bottom = state.getValue(FuelTankBlock.BOTTOM);
        FuelTankBlock.Shape shape = state.getValue(FuelTankBlock.SHAPE);

        String shapeName = "middle";
        if (top && bottom)
            shapeName = "single";
        else if (top)
            shapeName = "top";
        else if (bottom)
            shapeName = "bottom";

        String modelName = shapeName + (shape == FuelTankBlock.Shape.PLAIN ? "" : "_" + shape.getSerializedName());

        if (!prefix.isEmpty())
            return prov.models()
                    .withExistingParent(prefix + modelName, prov.modLoc("block/fuel_tank/block_" + modelName))
                    .texture("0", prov.modLoc("block/" + prefix + "casing"))
                    .texture("1", prov.modLoc("block/" + prefix + "fuel_tank"))
                    .texture("3", prov.modLoc("block/" + prefix + "fuel_tank_window"))
                    .texture("4", prov.modLoc("block/" + prefix + "casing"))
                    .texture("5", prov.modLoc("block/" + prefix + "fuel_tank_window_single"))
                    .texture("particle", prov.modLoc("block/" + prefix + "fuel_tank"));

        return AssetLookup.partialBaseModel(ctx, prov, modelName);
    }
}