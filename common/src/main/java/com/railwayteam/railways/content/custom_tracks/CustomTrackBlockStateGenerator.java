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

package com.railwayteam.railways.content.custom_tracks;

import com.railwayteam.railways.Railways;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.HashMap;
import java.util.Map;

public class CustomTrackBlockStateGenerator extends SpecialBlockStateGen {
  @Override
  protected int getXRotation(BlockState state) {
    return 0;
  }

  @Override
  protected int getYRotation(BlockState state) {
    return state.getValue(TrackBlock.SHAPE)
        .getModelRotation();
  }

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
              Create.asResource("block/track/" + value.getModel()))
        .texture("particle", material.particle);
    for (String k : textureMap.keySet()) {
      builder = builder.texture(k, Railways.asResource(prefix + textureMap.get(k) + material.resourceName()));
    }
    for (String k : new String[]{"segment_left", "segment_right", "tie"}) { // obj_track
      prov.models()
          .withExistingParent(prefix + k,
              Create.asResource("block/track/" + k))
          .texture("0", prefix + "standard_track_" + material.resourceName())
          .texture("1", prefix + "standard_track_mip_" + material.resourceName())
          .texture("particle", material.particle);
    }
    return builder;
  }
}

/*
 * All track models from track.json blockstate:
 * create:block/track/cross_diag
 * create:block/track/diag_2
 * create:block/track/cross_d1_xo
 * create:block/track/x_ortho
 * create:block/track/diag
 * create:block/track/teleport
 * create:block/track/cross_ortho
 * create:block/track/cross_d2_xo
 * create:block/track/ascending
 * create:block/track/z_ortho
 * minecraft:block/air
 * create:block/track/cross_d2_zo
 * create:block/track/cross_d1_zo
 *
 * textures: (teleport is exception here)
 * 0 - standard_track
 * 1 - standard_track_mip
 * 2 - standard_track_crossing
 * particle - create:block/palettes/stone_types/polished/andesite_cut_polished
 */