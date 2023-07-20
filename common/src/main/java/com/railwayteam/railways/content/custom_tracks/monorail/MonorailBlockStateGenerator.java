package com.railwayteam.railways.content.custom_tracks.monorail;

import com.railwayteam.railways.content.custom_tracks.CustomTrackBlockStateGenerator;
import dev.architectury.injectables.annotations.ExpectPlatform;

public abstract class MonorailBlockStateGenerator extends CustomTrackBlockStateGenerator {
    @ExpectPlatform
    public static MonorailBlockStateGenerator create() {
        throw new AssertionError();
    }
}
