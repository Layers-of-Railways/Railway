package com.railwayteam.railways.registry;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;

public class CRExtraDisplayTags {
    public static void register() {
        PonderRegistry.TAGS.forTag(AllPonderTags.DISPLAY_SOURCES)
            .add(AllBlocks.TRACK_SIGNAL)
            .add(CRBlocks.TRACK_COUPLER)
            .add(CRBlocks.ANDESITE_SWITCH)
            .add(CRBlocks.BRASS_SWITCH);
        PonderRegistry.TAGS.forTag(AllPonderTags.DISPLAY_TARGETS)
            .add(CRBlocks.SEMAPHORE);
    }
}
