/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
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

package com.railwayteam.railways.compat.tracks;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.custom_tracks.CustomTrackBlockStateGenerator;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackShape;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.HashMap;
import java.util.Map;

public class WideGaugeCompatTrackBlockStateGenerator extends CustomTrackBlockStateGenerator {
    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        TrackShape value = state.getValue(TrackBlock.SHAPE);
        TrackMaterial material = ((TrackBlock) ctx.getEntry()).getMaterial();
        //Railways.LOGGER.warn("TrackShape: "+value.name()+", material: "+material.langName);
        if (value == TrackShape.NONE) {
            return prov.models()
                .getExistingFile(prov.mcLoc("block/air"));
        }

        String textureModId = material.id.getNamespace();
        String resName = material.resourceName().replaceFirst("_wide", "");

        String texturePrefix = "block/track/" + resName + "/";
        String outputPrefix = "block/track/compat/"+material.id.getNamespace()+"/" + material.resourceName() + "/";
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
            case CR_O, XO, ZO, ND, PD, CR_D, CR_NDX, CR_NDZ, CR_PDX, CR_PDZ -> { // switched a *lot* of models to json
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
            .withExistingParent(outputPrefix + value.getModel(),
                Railways.asResource("block/wide_gauge_base/" + value.getModel()))
            .texture("particle", material.particle);
        for (String k : textureMap.keySet()) {
            builder = builder.texture(k, new ResourceLocation(textureModId, texturePrefix + textureMap.get(k) + resName));
        }
        for (String k : new String[]{"segment_left", "segment_right", "tie"}) { // obj_track
            prov.models()
                .withExistingParent(outputPrefix + k,
                    Railways.asResource("block/wide_gauge_base/" + k))
                .texture("0", new ResourceLocation(textureModId, texturePrefix + "standard_track_" + resName))
                .texture("1", new ResourceLocation(textureModId, texturePrefix + "standard_track_mip_" + resName))
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
done: diag
done: diag2
done: ascending
done: cross_ortho
done: cross_diag
done: cross_d1_xo
done: cross_d1_zo
done: cross_d2_xo
done: cross_d2_zo

 */