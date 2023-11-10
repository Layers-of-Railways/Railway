package com.railwayteam.railways.content.palettes.boiler;

import com.railwayteam.railways.content.palettes.boiler.BoilerBlock.Style;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.loaders.ObjModelBuilder;

import java.util.Locale;

public class BoilerGenerator extends SpecialBlockStateGen {
    private final DyeColor color;

    public BoilerGenerator(DyeColor color) {
        this.color = color;
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
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        Style style = state.getValue(BoilerBlock.STYLE);
        Direction.Axis axis = state.getValue(BoilerBlock.HORIZONTAL_AXIS);
        // I know it's barbaric to have the rotation be separate models instead of in blockstate,
        // but when I do it in blockstate there's horrible shading issues for the z rotation
        return prov.models().withExistingParent(ctx.getName() + "_" + style.getSerializedName() + "_" + axis.getName(), prov.modLoc("block/palettes/boiler/boiler"))
            .customLoader(ObjModelBuilder::begin)
            .flipV(true)
            .modelLocation(prov.modLoc("models/block/palettes/boiler/boiler_"+axis.getName()+".obj"))
            .end()
            .texture("front", prov.modLoc("block/palettes/" + color.name().toLowerCase(Locale.ROOT) + "/" + style.getTexture()))
            .texture("sides", prov.modLoc("block/palettes/" + color.name().toLowerCase(Locale.ROOT) + "/boiler_side"))
            .texture("particle", prov.modLoc("block/palettes/" + color.name().toLowerCase(Locale.ROOT) + "/riveted_pillar_top"));
    }
}
