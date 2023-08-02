package com.railwayteam.railways.compat.tracks;

import com.railwayteam.railways.content.custom_tracks.CustomTrackBlockStateGenerator;
import dev.architectury.injectables.annotations.ExpectPlatform;

public abstract class NarrowGaugeCompatTrackBlockStateGenerator extends CustomTrackBlockStateGenerator {
    @ExpectPlatform
    public static NarrowGaugeCompatTrackBlockStateGenerator create() {
        throw new AssertionError();
    }
}


/*
model list

done: x_ortho
done: z_ortho
done: tie
done: segment left
done: segment right
done: teleport
done: diag
done: diag2
done: ascending
done: cross_ortho
done: cross_diag
done: cross_d1_xo
done: cross_d1_zo
done: cross_d2_xo
done: cross_d2_zo

 */