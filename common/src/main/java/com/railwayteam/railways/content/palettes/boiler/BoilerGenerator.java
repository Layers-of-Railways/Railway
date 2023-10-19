package com.railwayteam.railways.content.palettes.boiler;

import com.railwayteam.railways.Railways;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;

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
        return prov.models().withExistingParent(ctx.getName(), prov.modLoc("block/palettes/boiler/block"))
                .texture("0", Railways.asResource("block/palettes/" + color.name().toLowerCase() + "/boiler_gullet"));
    }
}
