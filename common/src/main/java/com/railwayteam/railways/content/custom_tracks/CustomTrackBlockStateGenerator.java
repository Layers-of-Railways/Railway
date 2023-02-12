package com.railwayteam.railways.content.custom_tracks;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.mixin_interfaces.IHasTrackMaterial;
import com.simibubi.create.content.logistics.trains.track.TrackBlock;
import com.simibubi.create.content.logistics.trains.track.TrackShape;
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
    TrackMaterial material = ((IHasTrackMaterial) ctx.getEntry()).getMaterial();
    //Railways.LOGGER.warn("TrackShape: "+value.name()+", material: "+material.langName);
    if (value == TrackShape.NONE) {
      return prov.models()
          .getExistingFile(prov.mcLoc("block/air"));
    }
    String prefix = "block/track/" + material.resName() + "/";
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
              Railways.asResource("block/track_base/" + value.getModel()))
        .texture("particle", material.particle);
    for (String k : textureMap.keySet()) {
      builder = builder.texture(k, Railways.asResource(prefix + textureMap.get(k) + material.resName()));
    }
    for (String k : new String[]{"segment_left", "segment_right", "tie"}) {
      prov.models()
          .withExistingParent(prefix + k,
              Railways.asResource("block/track_base/" + k))
          .texture("1", prefix + "standard_track_" + material.resName())
          .texture("2", prefix + "standard_track_mip_" + material.resName())
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