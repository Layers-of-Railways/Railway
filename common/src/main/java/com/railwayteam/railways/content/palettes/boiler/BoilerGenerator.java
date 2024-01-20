package com.railwayteam.railways.content.palettes.boiler;

import com.railwayteam.railways.content.palettes.boiler.BoilerBlock.Style;
import com.railwayteam.railways.registry.CRPalettes.Wrapping;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class BoilerGenerator extends SpecialBlockStateGen {
    private final @Nullable DyeColor color;
    private final @Nullable Wrapping wrapping;

    public BoilerGenerator(@Nullable DyeColor color, @Nullable Wrapping wrapping) {
        this.color = color;
        this.wrapping = wrapping;
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
        String colorName = color == null ? "netherite" : color.name().toLowerCase(Locale.ROOT);
        return prov.models().withExistingParent(ctx.getName() + "_" + style.getSerializedName() + "_" + axis.getName(), prov.modLoc("block/palettes/boiler/boiler"))
            .customLoader(CustomObjModelBuilder::begin)
            .flipV(true)
            .modelLocation(prov.modLoc("models/block/palettes/boiler/boiler_"+axis.getName()+".obj"))
            .end()
            .texture("front", prov.modLoc("block/palettes/" + colorName + "/" + style.getTexture()))
            .texture("sides", prov.modLoc("block/palettes/" + colorName + "/" + (wrapping != null ? wrapping.prefix("wrapped_boiler_side") : "boiler_side")))
            .texture("particle", prov.modLoc("block/palettes/" + colorName + "/riveted_pillar_top"));
    }
}
