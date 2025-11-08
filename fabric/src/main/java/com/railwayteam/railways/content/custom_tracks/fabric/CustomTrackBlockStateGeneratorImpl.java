/*
 * Steam 'n' Rails
 * Copyright (c) 2023-2025 The Railways Team
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

package com.railwayteam.railways.content.custom_tracks.fabric;

import com.railwayteam.railways.annotation.multiloader.ImplClass;
import com.railwayteam.railways.content.custom_tracks.CustomTrackBlockStateGenerator;
import com.railwayteam.railways.content.custom_tracks.TransparentSegmentTrackBlock;
import com.railwayteam.railways.content.custom_tracks.gen_template.OutputPrefixer;
import com.railwayteam.railways.content.custom_tracks.gen_template.TextureKey;
import com.railwayteam.railways.content.custom_tracks.gen_template.TrackGenTemplate;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackShape;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import io.github.fabricators_of_create.porting_lib.models.generators.ModelFile;
import io.github.fabricators_of_create.porting_lib.models.generators.block.BlockModelBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

@ImplClass
public class CustomTrackBlockStateGeneratorImpl extends CustomTrackBlockStateGenerator {
    protected CustomTrackBlockStateGeneratorImpl(
        OutputPrefixer outputPrefixer,
        TrackGenTemplate template,
        Map<TrackShape, Map<String, TextureKey>> textureMap
    ) {
        super(outputPrefixer, template, textureMap);
    }

    public static CustomTrackBlockStateGenerator create(
        OutputPrefixer outputPrefixer,
        TrackGenTemplate template,
        Map<TrackShape, Map<String, TextureKey>> textureMap
    ) {
        return new CustomTrackBlockStateGeneratorImpl(outputPrefixer, template, textureMap);
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        TrackShape shape = state.getValue(TrackBlock.SHAPE);
        TrackMaterial material = ((TrackBlock) ctx.getEntry()).getMaterial();

        String outputPrefix = outputPrefixer.getOutputPrefix(material);

        if (shape == TrackShape.NONE) {
            // special models guarded by TrackShape.NONE to prevent repeat generation
            for (String k : new String[]{"segment_left", "segment_right", "tie"}) { // obj_track
                var model = prov.models()
                    .withExistingParent(outputPrefix + k, template.getParentModel(material, k))
                    .texture("0", template.getTexture(material, TextureKey.STANDARD_TRACK))
                    .texture("1", template.getTexture(material, TextureKey.STANDARD_TRACK_MIP))
                    .texture("particle", template.getTexture(material, TextureKey.PARTICLE));

                if (!k.equals("tie") && state.getBlock() instanceof TransparentSegmentTrackBlock) {
                    // fixme change the above to `if (k.equals("tie") || ...)` (not done yet to keep datagen diffs readable)
                    model.renderType(new ResourceLocation("cutout_mipped"));
                }
            }

            return prov.models()
                .getExistingFile(prov.mcLoc("block/air"));
        }

        BlockModelBuilder builder = prov.models()
            .withExistingParent(outputPrefix + shape.getModel(),
                template.getParentModel(material, shape.getModel()))
            .texture("particle", template.getTexture(material, TextureKey.PARTICLE));

        for (var entry : textureMap.get(shape).entrySet()) {
            builder = builder.texture(entry.getKey(), template.getTexture(material, entry.getValue()));
        }

        return builder;
    }
}
