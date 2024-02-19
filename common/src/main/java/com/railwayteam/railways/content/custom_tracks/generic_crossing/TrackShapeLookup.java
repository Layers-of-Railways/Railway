package com.railwayteam.railways.content.custom_tracks.generic_crossing;

import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class TrackShapeLookup {
    private static final Map<Couple<TrackShape>, TrackShape> MERGE = new HashMap<>();
    private static final Map<TrackShape, Couple<TrackShape>> UNMERGE = new HashMap<>();

    static {
        i(TrackShape.XO, TrackShape.ZO, TrackShape.CR_O);
        i(TrackShape.PD, TrackShape.ND, TrackShape.CR_D);

        i(TrackShape.XO, TrackShape.PD, TrackShape.CR_PDX);
        i(TrackShape.XO, TrackShape.ND, TrackShape.CR_NDX);

        i(TrackShape.ZO, TrackShape.PD, TrackShape.CR_PDZ);
        i(TrackShape.ZO, TrackShape.ND, TrackShape.CR_NDZ);
    }

    private static void i(TrackShape a, TrackShape b, TrackShape merged) {
        if (a.ordinal() > b.ordinal()) {
            TrackShape tmp = a;
            a = b;
            b = tmp;
        }

        MERGE.put(Couple.create(a, b), merged);
        UNMERGE.put(merged, Couple.create(a, b));
    }

    /**
     *
     * @param a supposed primary shape
     * @param b supposed secondary shape
     * @return (merged, flip) -> flip means that primary and secondary are reversed
     */
    public static @Nullable Pair<TrackShape, Boolean> getMerged(TrackShape a, TrackShape b) {
        Couple<TrackShape> key = Couple.create(a, b);
        if (MERGE.containsKey(key)) {
            return Pair.of(MERGE.get(key), false);
        }
        key = Couple.create(b, a);
        if (MERGE.containsKey(key)) {
            return Pair.of(MERGE.get(key), true);
        }

        return null;
    }

    public static @Nullable Couple<TrackShape> getUnmerged(TrackShape merged) {
        return UNMERGE.get(merged);
    }



    public record GenericCrossingData(Pair<TrackShape, Boolean> merged, TrackMaterial existingMaterial, TrackMaterial overlayMaterial) {}
}
