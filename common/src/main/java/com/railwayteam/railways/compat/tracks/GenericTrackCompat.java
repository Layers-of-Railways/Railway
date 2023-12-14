package com.railwayteam.railways.compat.tracks;

import com.railwayteam.railways.Config;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.mixin.AccessorIngredient_TagValue;
import com.railwayteam.railways.multiloader.CommonTags;
import com.railwayteam.railways.util.TextUtils;
import com.railwayteam.railways.util.Utils;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.railwayteam.railways.Railways.registrate;
import static com.railwayteam.railways.compat.tracks.TrackCompatUtils.buildCompatModels;
import static com.railwayteam.railways.compat.tracks.TrackCompatUtils.makeTrack;
import static com.railwayteam.railways.registry.CRItems.ITEM_INCOMPLETE_TRACK;
import static com.simibubi.create.content.trains.track.TrackMaterialFactory.make;

public class GenericTrackCompat {
    public final String modid;

    public GenericTrackCompat(String modid) {
        this.modid = modid;
    }

    protected final Map<String, TrackMaterial> MATERIALS = new HashMap<>();
    protected final Map<String, NonNullSupplier<? extends TrackBlock>> BLOCKS = new HashMap<>();

    public static boolean isDataGen() {
        return Utils.isEnvVarTrue("DATAGEN");
    }

    protected static boolean registerTracksAnywayGlobal() {
        return Config.REGISTER_MISSING_TRACKS.get();// || Utils.isDevEnv();
    }

    protected boolean registerTracksAnyway() {
        return registerTracksAnywayGlobal();
    }

    // If tracks/materials should still be registered if the base block is missing
    protected final boolean shouldRegisterMissing() {
        return isDataGen() || registerTracksAnyway();
    }

    public void register(String... names) {
        for (String name : names) {
            Optional<Block> baseBlock = Registry.BLOCK.getOptional(getSlabLocation(name));
            if (baseBlock.isEmpty()) {
                if (!shouldRegisterMissing()) continue; // skip if we shouldn't register tracks for missing base blocks
                if (isDataGen() || Utils.isDevEnv())
                    Railways.LOGGER.error("Failed to locate base block at "+getSlabLocation(name)+" for "+asResource(name));
            }
            TrackMaterial material = buildCompatModels(make(asResource(name))
                .lang(langName(name))
                .block(() -> BLOCKS.get(name))
                .particle(asResource("block/track/"+name+"/standard_track_crossing_"+name))
                .sleeper(baseBlock.map(Ingredient::of).orElseGet(() -> SoftIngredient.of(getSlabLocation(name))))
                .rails(getIngredientForRail())
            );
            MATERIALS.put(name, material);

            NonNullSupplier<TrackBlock> block = makeTrack(material);
            BLOCKS.put(name, block);

            ITEM_INCOMPLETE_TRACK.put(material, registrate().item("track_incomplete_" + modid + "_" + material.resourceName(), SequencedAssemblyItem::new)
                .model((c, p) -> p.generated(c, asResource("item/track_incomplete/track_incomplete_" + material.resourceName())))
                .lang("Incomplete " + material.langName + " Track")
                .register());
        }
    }

    protected String langName(String name) {
        return TextUtils.titleCaseConversion(name.replace('_', ' '));
    }

    protected ResourceLocation asResource(String path) {
        return new ResourceLocation(modid, path);
    }

    protected ResourceLocation getSlabLocation(String name) {
        return asResource(name+"_slab");
    }

    protected Ingredient getIngredientForRail() {
        return Ingredient.fromValues(Stream.of(
                AccessorIngredient_TagValue.railway$create(CommonTags.IRON_NUGGETS.tag),
                AccessorIngredient_TagValue.railway$create(CommonTags.ZINC_NUGGETS.tag)
        ));
    }
}
