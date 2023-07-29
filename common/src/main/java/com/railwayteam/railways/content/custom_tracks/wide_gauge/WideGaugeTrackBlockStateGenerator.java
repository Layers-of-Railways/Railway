package com.railwayteam.railways.content.custom_tracks.wide_gauge;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.custom_tracks.CustomTrackBlockStateGenerator;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackShape;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.HashMap;
import java.util.Map;

public class WideGaugeTrackBlockStateGenerator extends CustomTrackBlockStateGenerator {
    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        TrackShape value = state.getValue(TrackBlock.SHAPE);
        TrackMaterial material = ((TrackBlock) ctx.getEntry()).getMaterial();
        //Railways.LOGGER.warn("TrackShape: "+value.name()+", material: "+material.langName);
        if (value == TrackShape.NONE) {
            return prov.models()
                .getExistingFile(prov.mcLoc("block/air"));
        }
        String prefix = "block/track/" + material.resourceName() + "/";
        Map<String, String> textureMap = new HashMap<>();//prefix + get() + material.resName()
        switch (value) {
            case TE, TN, TS, TW -> {
                //portal 1, 2, 3 portal, portal_mip, standard
                textureMap.put("1", "portal_track_");
                textureMap.put("2", "portal_track_mip_");
                textureMap.put("3", "standard_track_");
            }
            case AE, AW, AN, AS -> {
                //ascending 0, 1 standard, mip
                textureMap.put("0", "standard_track_");
                textureMap.put("1", "standard_track_mip_");
            }
            case CR_O, XO, ZO -> {
                //cross ortho 1, 2, 3, standard, mip, crossing
                //normal (x/z)_ortho 1, 2, standard mip
                textureMap.put("1", "standard_track_");
                textureMap.put("2", "standard_track_mip_");
                textureMap.put("3", "standard_track_crossing_");
            }
            default -> {
                //obj_track, 0, 1, 2, standard, mip, crossing
                textureMap.put("0", "standard_track_");
                textureMap.put("1", "standard_track_mip_");
                textureMap.put("2", "standard_track_crossing_");
            }
        }

        BlockModelBuilder builder = prov.models()
            .withExistingParent(prefix + value.getModel(),
                Railways.asResource("block/wide_gauge_base/" + value.getModel()))
            .texture("particle", material.particle);
        for (String k : textureMap.keySet()) {
            builder = builder.texture(k, Railways.asResource(prefix + textureMap.get(k) + material.resourceName().replaceFirst("_wide", "")));
        }
        for (String k : new String[]{"segment_left", "segment_right", "tie"}) { // obj_track
            prov.models()
                .withExistingParent(prefix + k,
                    Railways.asResource("block/wide_gauge_base/" + k))
                .texture("0", prefix + "standard_track_" + material.resourceName().replaceFirst("_wide", ""))
                .texture("1", prefix + "standard_track_mip_" + material.resourceName().replaceFirst("_wide", ""))
                .texture("particle", material.particle);
        }
        return builder;
    }
}


/*
model list

done: x_ortho
done: z_ortho
done: tie
done: segment left
done: segment right
done: teleport
todo: diag
todo: diag2
todo: ascending
todo: cross_ortho
todo: cross_diag
todo: cross_d1_xo
todo: cross_d1_zo
todo: cross_d2_xo
todo: cross_d2_zo
todo:
todo:
todo:
todo:
todo:
todo:
todo:
todo:

 */