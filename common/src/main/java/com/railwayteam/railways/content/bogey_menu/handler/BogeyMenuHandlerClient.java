package com.railwayteam.railways.content.bogey_menu.handler;

import com.railwayteam.railways.api.bogeymenu.entry.BogeyEntry;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.simibubi.create.content.trains.bogey.BogeySizes.BogeySize;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.foundation.gui.widget.Indicator;
import com.simibubi.create.foundation.utility.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

;

public class BogeyMenuHandlerClient {
    private static final Map<BogeyStyle, List<Pair<BogeyStyle, BogeySize>>> CACHED_RENDER_CYCLES = new HashMap<>();
    private static final Map<BogeyEntry, Indicator.State[]> CACHED_COMPATS = new HashMap<>();

    public static @Nullable BogeySize getSize(BogeyStyle style) {
        for (BogeySize size : style.validSizes()) {
            return size;
        }
        return null;
    }

    public static List<Pair<BogeyStyle, BogeySize>> getRenderCycle(BogeyStyle style) {
         return CACHED_RENDER_CYCLES.computeIfAbsent(style, (s) -> {
            List<Pair<BogeyStyle, BogeySize>> cycle = new ArrayList<>();
            for (BogeySize size : style.validSizes()) {
                cycle.add(Pair.of(style, size));
            }

            for (BogeyStyle subStyle : CRBogeyStyles.getSubStyles(style)) {
                for (BogeySize size : subStyle.validSizes()) {
                    cycle.add(Pair.of(subStyle, size));
                }
            }

            return cycle;
        });
    }

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
