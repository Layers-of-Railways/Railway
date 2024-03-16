package com.railwayteam.railways.content.fuel.tank;

import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import io.github.fabricators_of_create.porting_lib.models.generators.ModelFile;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

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