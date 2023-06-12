package com.railwayteam.railways.compat.tracks.mods;

import at.petrak.hexcasting.common.lib.HexBlocks;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.compat.tracks.LazyIngredient;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.resources.ResourceLocation;

import static com.railwayteam.railways.compat.tracks.TrackCompatUtils.buildCompatModels;
import static com.railwayteam.railways.compat.tracks.TrackCompatUtils.makeTrack;
import static com.railwayteam.railways.registry.CRItems.ITEM_INCOMPLETE_TRACK;
import static com.simibubi.create.content.trains.track.TrackMaterialFactory.make;

public class HexCastingTrackCompat {

    private static final CreateRegistrate REGISTRATE = Railways.registrate();

    private static BlockEntry<TrackBlock> _EDIFIED_TRACK; // private internal thing to let static loading work
    public static final BlockEntry<TrackBlock> EDIFIED_TRACK;

    public static final TrackMaterial EDIFIED = buildCompatModels(make(new ResourceLocation("hexcasting", "edified"))
        .lang("Edified")
        .block(() -> _EDIFIED_TRACK)
        .particle(new ResourceLocation("hexcasting", "block/akashic/planks1.png"))
        .sleeper(LazyIngredient.lazyOf(() -> HexBlocks.AKASHIC_SLAB)) // must lazy load this :(
    );

    static {
        _EDIFIED_TRACK = makeTrack(EDIFIED);
        EDIFIED_TRACK = _EDIFIED_TRACK;
    }

    public static void register() {
        Railways.LOGGER.info("Loading track compat for Hex Casting");
        for (TrackMaterial material : TrackMaterial.allFromMod("hexcasting")) {
            ITEM_INCOMPLETE_TRACK.put(material, REGISTRATE.item("track_incomplete_hexcasting_" + material.resourceName(), SequencedAssemblyItem::new)
                .model((c, p) -> p.generated(c, new ResourceLocation("hexcasting", "item/track_incomplete/track_incomplete_" + material.resourceName())))
                .lang("Incomplete " + material.langName + " Track")
                .register());
        }
    }
}
