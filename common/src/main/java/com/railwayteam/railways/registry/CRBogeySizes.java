package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.simibubi.create.content.trains.bogey.BogeySizes;

public class CRBogeySizes {
    public static BogeySizes.BogeySize create(String name, float size) {
        return BogeySizes.addSize(Railways.asResource(name), size);
    }

    public static void register() {

    }
}