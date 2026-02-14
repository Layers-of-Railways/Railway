package com.railwayteam.railways.content.palettes.boiler.fabric;

import com.railwayteam.railways.annotation.multiloader.ImplClass;
import com.railwayteam.railways.content.palettes.PalettesColor;
import com.railwayteam.railways.content.palettes.boiler.BoilerBlock;
import com.railwayteam.railways.content.palettes.boiler.BoilerGenerator;
import com.railwayteam.railways.registry.CRPalettes.Wrapping;
import com.railwayteam.railways.util.TextUtils;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import io.github.fabricators_of_create.porting_lib.models.generators.ModelFile;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ImplClass
public class BoilerGeneratorImpl extends BoilerGenerator {

    protected BoilerGeneratorImpl(@NotNull PalettesColor color, @Nullable Wrapping wrapping) {
        super(color, wrapping);
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        BoilerBlock.Style style = state.getValue(BoilerBlock.STYLE);
        Direction.Axis axis = state.getValue(BoilerBlock.HORIZONTAL_AXIS);
        boolean raised = state.getValue(BoilerBlock.RAISED);

        // I know it's barbaric to have the rotation be separate models instead of in blockstate,
        // but when I do it in blockstate there's horrible shading issues for the z rotation
        String colorName = color.getSerializedName();
        return prov.models().withExistingParent("block/palettes/" + TextUtils.prefixToFolder(ctx.getName(), colorName) + "_" + style.getSerializedName() + "_" + axis.getName() + (raised ? "_raised" : ""), prov.modLoc("block/palettes/boiler/boiler"))
            .customLoader(com.railwayteam.railways.content.boiler.fabric.ObjModelBuilder::begin)
            .flipV(true)
            .modelLocation(prov.modLoc("models/block/palettes/boiler/boiler_"+axis.getName()+(raised ? "_raised" : "")+".obj"))
            .end()
            .texture("front", prov.modLoc("block/palettes/" + colorName + "/" + style.getTexture()))
            .texture("sides", prov.modLoc("block/palettes/" + colorName + "/" + (wrapping != null ? wrapping.prefix("wrapped_boiler_side") : "boiler_side")))
            .texture("particle", prov.modLoc("block/palettes/" + colorName + "/riveted_pillar_top"));
    }

    public static BoilerGenerator create(@NotNull PalettesColor color, @Nullable Wrapping wrapping) {
        return new BoilerGeneratorImpl(color, wrapping);
    }
}
