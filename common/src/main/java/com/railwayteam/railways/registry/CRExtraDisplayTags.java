package com.railwayteam.railways.registry;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;

public class CRExtraDisplayTags {
    public static void register() {
        PonderRegistry.TAGS.forTag(AllPonderTags.DISPLAY_SOURCES)
            .add(AllBlocks.TRACK_SIGNAL)
            .add(CRBlocks.TRACK_COUPLER);
        PonderRegistry.TAGS.forTag(AllPonderTags.DISPLAY_TARGETS)
            .add(CRBlocks.SEMAPHORE);
    }
}
