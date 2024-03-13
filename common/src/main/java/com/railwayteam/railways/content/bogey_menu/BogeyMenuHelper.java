package com.railwayteam.railways.content.bogey_menu;

import com.railwayteam.railways.api.bogeymenu.entry.BogeyEntry;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.foundation.gui.widget.Indicator;

import java.util.HashMap;
import java.util.Map;

public class BogeyMenuHelper {
    private static final Map<BogeyEntry, Indicator.State[]> CACHED_COMPATS = new HashMap<>();

    public static Indicator.State[] getTrackCompat(BogeyEntry bogeyEntry) {
        return CACHED_COMPATS.computeIfAbsent(bogeyEntry, (k) -> new Indicator.State[] {
                styleFits(bogeyEntry, CRTrackMaterials.CRTrackType.NARROW_GAUGE),
                styleFits(bogeyEntry, TrackMaterial.TrackType.STANDARD),
                styleFits(bogeyEntry, CRTrackMaterials.CRTrackType.WIDE_GAUGE)
        });
    }

    private static Indicator.State styleFits(BogeyEntry bogeyEntry, TrackMaterial.TrackType trackType) {
        return CRBogeyStyles.styleFitsTrack(bogeyEntry.bogeyStyle(), trackType) ? Indicator.State.GREEN : Indicator.State.RED;
    }
}
