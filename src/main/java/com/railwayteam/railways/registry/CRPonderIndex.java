package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.ponder.TrainScenes;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;


public class CRPonderIndex {
    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(Railways.MODID);
    public static void register() {
        HELPER.forComponents(CRBlocks.SEMAPHORE)
                .addStoryBoard("semaphore", TrainScenes::signaling);
    }
}
