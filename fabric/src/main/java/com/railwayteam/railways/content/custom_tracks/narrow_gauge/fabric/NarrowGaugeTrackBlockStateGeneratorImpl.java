package com.railwayteam.railways.content.custom_tracks.narrow_gauge.fabric;

import com.google.common.collect.ImmutableSet;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.custom_tracks.narrow_gauge.NarrowGaugeTrackBlockStateGenerator;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.simibubi.create.Create;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NarrowGaugeTrackBlockStateGeneratorImpl extends NarrowGaugeTrackBlockStateGenerator {

    private static final Set<String> COMPAT_MODS = ImmutableSet.of(
        "create"
    );

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        TrackShape value = state.getValue(TrackBlock.SHAPE);
        TrackMaterial material = ((TrackBlock) ctx.getEntry()).getMaterial();
        //Railways.LOGGER.warn("TrackShape: "+value.name()+", material: "+material.langName);
        if (value == TrackShape.NONE) {
            return prov.models()
                .getExistingFile(prov.mcLoc("block/air"));
        }

        String textureModId = Railways.MODID;
        String resName = material.resourceName().replaceFirst("_narrow", "");
        for (String modPrefix : COMPAT_MODS) {
            if (resName.startsWith(modPrefix+"_")) {
                textureModId = modPrefix;
                resName = resName.replaceFirst(modPrefix+"_", "");
                break;
            }
        }

        String prefix = "block/track/" + material.resourceName() + "/";
        String texturePrefix = "block/track/" + resName + "/";
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

        if (material == CRTrackMaterials.NARROW_GAUGE_ANDESITE) {
            BlockModelBuilder builder = prov.models().withExistingParent(prefix + value.getModel(), Railways.asResource("block/narrow_gauge_base/" + value.getModel()));
            for (String k : new String[]{"segment_left", "segment_right", "tie"}) { // obj_track
                prov.models()
                    .withExistingParent(prefix + k,
                        Railways.asResource("block/narrow_gauge_base/" + k))
                    .texture("0", Create.asResource("block/standard_track"))
                    .texture("1", Create.asResource("block/standard_track_mip"))
                    .texture("particle", material.particle);
            }
            return builder;
        }
        BlockModelBuilder builder = prov.models()
            .withExistingParent(prefix + value.getModel(),
                Railways.asResource("block/narrow_gauge_base/" + value.getModel()))
            .texture("particle", material.particle);
        for (String k : textureMap.keySet()) {
            builder = builder.texture(k, new ResourceLocation(textureModId, texturePrefix + textureMap.get(k) + resName));
        }
        for (String k : new String[]{"segment_left", "segment_right", "tie"}) { // obj_track
            prov.models()
                .withExistingParent(prefix + k,
                    Railways.asResource("block/narrow_gauge_base/" + k))
                .texture("0", new ResourceLocation(textureModId, texturePrefix + "standard_track_" + resName))
                .texture("1", new ResourceLocation(textureModId, texturePrefix + "standard_track_mip_" + resName))
                .texture("particle", material.particle);
        }
        return builder;
    }

    public static NarrowGaugeTrackBlockStateGenerator create() {
        return new NarrowGaugeTrackBlockStateGeneratorImpl();
    }
}
